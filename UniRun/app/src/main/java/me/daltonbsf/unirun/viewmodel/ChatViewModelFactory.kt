package me.daltonbsf.unirun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.ChatRepository

class ChatViewModelFactory(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}