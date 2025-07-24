package me.daltonbsf.unirun.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
            "id" to chatRoomRef.id,
            "groupName" to groupName,
            "participants" to participants,
            "type" to "group"
        )
        chatRoomRef.set(chatRoom)
        return chatRoomRef.id
    }
}