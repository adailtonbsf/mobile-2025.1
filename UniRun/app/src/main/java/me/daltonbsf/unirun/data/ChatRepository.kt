package me.daltonbsf.unirun.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import me.daltonbsf.unirun.model.Chat
import me.daltonbsf.unirun.model.Message

class ChatRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getUserChats(userId: String): Flow<List<Chat>> = callbackFlow {
        val listener = firestore.collection("chats")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val chats = snapshots?.map { document ->
                    val chat = document.toObject(Chat::class.java)
                    chat.copy(id = document.id) // Mapeia o ID do documento
                } ?: emptyList()
                trySend(chats).isSuccess
            }
        awaitClose { listener.remove() }
    }

    fun getMessages(chatRoomId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("chats").document(chatRoomId)
            .collection("messages").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val messages = snapshots?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages).isSuccess
            }
        awaitClose { listener.remove() }
    }

    fun sendMessage(chatRoomId: String, message: Message) {
        val chatRef = firestore.collection("chats").document(chatRoomId)

        chatRef.collection("messages").add(message)
            .addOnSuccessListener {
                chatRef.update("lastMessage", message)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun createPrivateChatRoom(userId1: String, userId2: String): String{
        val participants = listOf(userId1, userId2).sorted()
        val chatRoomId = participants.joinToString("_")
        val chatRoom = mapOf(
            "participants" to participants,
            "type" to "private"
        )
        firestore.collection("chats").document(chatRoomId).set(chatRoom)
        return chatRoomId
    }

    fun createGroupChatRoom(groupName: String, participants: List<String>): String {
        val chatRoomRef = firestore.collection("chats").document()
        val chatRoom = mapOf(
            "groupName" to groupName,
            "participants" to participants,
            "type" to "group"
        )
        chatRoomRef.set(chatRoom)
        return chatRoomRef.id
    }

    suspend fun addUserToGroupChat(chatId: String, userId: String): Boolean {
        return try {
            firestore.collection("chats").document(chatId)
                .update("participants", FieldValue.arrayUnion(userId))
                .await()
            true
        } catch (e: Exception) {
            Log.e("ChatRepository", "Erro ao adicionar usuário ao chat", e)
            false
        }
    }

    suspend fun removeUserFromGroupChat(chatId: String, userId: String): Boolean {
        return try {
            firestore.collection("chats").document(chatId)
                .update("participants", FieldValue.arrayRemove(userId))
                .await()
            true
        } catch (e: Exception) {
            Log.e("ChatRepository", "Erro ao remover usuário do chat", e)
            false
        }
    }

    suspend fun deleteChat(chatId: String): Boolean {
        return try {
            val chatRef = firestore.collection("chats").document(chatId)
            val messagesSnapshot = chatRef.collection("messages").get().await()

            firestore.runBatch { batch ->
                for (document in messagesSnapshot.documents) {
                    batch.delete(document.reference)
                }
                batch.delete(chatRef)
            }.await()
            true
        } catch (e: Exception) {
            Log.e("ChatRepository", "Erro ao deletar o chat: ${e.message}", e)
            false
        }
    }
}