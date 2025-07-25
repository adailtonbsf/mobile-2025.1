package me.daltonbsf.unirun.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.CaronaRepository
import me.daltonbsf.unirun.data.ChatRepository
import me.daltonbsf.unirun.data.UserPreferences

class CaronaViewModelFactory(
    private val caronaRepository: CaronaRepository,
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val userPreferences: UserPreferences,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaronaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CaronaViewModel(caronaRepository, authRepository, chatRepository, userPreferences, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}