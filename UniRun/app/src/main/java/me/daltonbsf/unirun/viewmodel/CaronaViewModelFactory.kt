package me.daltonbsf.unirun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.CaronaRepository
import me.daltonbsf.unirun.data.ChatRepository

class CaronaViewModelFactory(
    private val caronaRepository: CaronaRepository,
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaronaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CaronaViewModel(caronaRepository, authRepository, chatRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}