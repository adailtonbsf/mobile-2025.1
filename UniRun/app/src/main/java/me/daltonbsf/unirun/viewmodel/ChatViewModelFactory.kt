package me.daltonbsf.unirun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.ChatRepository
import me.daltonbsf.unirun.data.UserPreferences

class ChatViewModelFactory(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRepository, authRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}