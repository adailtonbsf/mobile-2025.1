package me.daltonbsf.unirun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.ChatRepository
import me.daltonbsf.unirun.model.Chat
import me.daltonbsf.unirun.model.Message

class ChatViewModel(private val chatRepository: ChatRepository, private val authRepository: AuthRepository): ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList: StateFlow<List<Chat>> = _chatList

    init {
        loadUserChats()
    }

    private fun loadUserChats() {
        val userId = authRepository.getCurrentUser()?.uid
        if (userId != null) {
            viewModelScope.launch {
                chatRepository.getUserChats(userId).collect { chats ->
                    _chatList.value = chats
                }
            }
        }
    }

    fun loadMessages(chatRoomId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(chatRoomId).collect {
                _messages.value = it
            }
        }
    }

    suspend fun sendMessage(chatRoomId: String, text: String) {
        val senderId = authRepository.getCurrentUser()?.uid ?: return
        val message = Message(
            senderId = senderId,
            senderName = authRepository.getUserName() ?: "Unknown",
            timestamp = System.currentTimeMillis(),
            content = text
        )
        chatRepository.sendMessage(chatRoomId, message)
    }

    fun createPrivateChat(otherUserId: String, onResult: (String) -> Unit) {
        val currentUserId = authRepository.getCurrentUser()?.uid
        if (currentUserId != null) {
            val chatId = chatRepository.createPrivateChatRoom(currentUserId, otherUserId)
            onResult(chatId)
        }
    }

    suspend fun getChatTitle(chat: Chat): String {
        if (chat.type == "private") {
            val currentUserId = authRepository.getCurrentUser()?.uid
            val otherUserId = chat.participants.firstOrNull { it != currentUserId }
            return if (otherUserId != null) {
                val otherUser = authRepository.getUserData(otherUserId)
                otherUser?.name ?: "Usuário"
            } else {
                "Usuário Desconhecido"
            }
        }
        return chat.groupName ?: "Grupo"
    }
}