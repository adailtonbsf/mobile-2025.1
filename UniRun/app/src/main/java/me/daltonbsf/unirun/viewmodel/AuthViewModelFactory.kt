package me.daltonbsf.unirun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.UserPreferences

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val userPreferences: UserPreferences // Adicione aqui
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, userPreferences) as T // Passe para o ViewModel
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}