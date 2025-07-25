package me.daltonbsf.unirun.viewmodel

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.ChatRepository
import me.daltonbsf.unirun.data.UserPreferences
import me.daltonbsf.unirun.model.Chat
import me.daltonbsf.unirun.model.Message
import me.daltonbsf.unirun.util.NotificationUtils

class ChatViewModel(
    private val repository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList = _chatList.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    @RequiresPermission(POST_NOTIFICATIONS)
    fun loadUserChats(context: Context) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            repository.getUserChats(userId).collect { chats ->
                val sortedChats = chats.sortedByDescending { it.lastMessage?.timestamp }
                val previousChatList = _chatList.value

                sortedChats.forEach { newChat ->
                    val oldChat = previousChatList.find { it.id == newChat.id }
                    val newLastMessage = newChat.lastMessage
                    val oldLastMessage = oldChat?.lastMessage

                    if (newLastMessage != null &&
                        newLastMessage.timestamp != oldLastMessage?.timestamp &&
                        newLastMessage.senderId != userId) {
                        viewModelScope.launch {
                            val allNotificationsEnabled = userPreferences.getPreference(UserPreferences.ALL_NOTIFICATIONS_KEY, "true").first().toBoolean()
                            if (allNotificationsEnabled) {
                                val title: String
                                val message: String
                                if (newChat.type == "group") {
                                    val caronaNotificationsEnabled = userPreferences.getPreference(UserPreferences.CARONA_NOTIFICATIONS_KEY, "true").first().toBoolean()
                                    if (caronaNotificationsEnabled) {
                                        title = newChat.groupName ?: "Nova Mensagem de Grupo"
                                        message = "${newLastMessage.senderName}: ${newLastMessage.content}"
                                        NotificationUtils.showNotification(context, title, message)
                                    }
                                } else {
                                    val userNotificationsEnabled = userPreferences.getPreference(UserPreferences.USER_NOTIFICATIONS_KEY, "true").first().toBoolean()
                                    if (userNotificationsEnabled) {
                                        title = newLastMessage.senderName
                                        message = newLastMessage.content
                                        NotificationUtils.showNotification(context, title, message)
                                    }
                                }
                            }
                        }
                    }
                }
                _chatList.value = sortedChats
            }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            repository.getMessages(chatId).collect {
                _messages.value = it
            }
        }
    }

    fun sendMessage(chatId: String, content: String) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            val userName = authRepository.getUserName()
            if (user != null && userName != null) {
                val message = Message(
                    senderId = user.uid,
                    senderName = userName,
                    timestamp = System.currentTimeMillis(),
                    content = content
                )
                repository.sendMessage(chatId, message)
            }
        }
    }

    suspend fun getChatTitle(chat: Chat): String {
        val currentUserId = authRepository.getCurrentUser()?.uid ?: return "Chat"
        return if (chat.type == "group") {
            chat.groupName ?: "Grupo"
        } else {
            val otherUserId = chat.participants.firstOrNull { it != currentUserId }
            if (otherUserId != null) {
                authRepository.getUserData(otherUserId)?.name ?: "Usuário"
            } else {
                "Chat"
            }
        }
    }

    fun createPrivateChat(otherUserId: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUser()?.uid
            if (currentUserId != null) {
                val chatId = repository.createPrivateChatRoom(currentUserId, otherUserId)
                onResult(chatId)
            }
        }
    }
}