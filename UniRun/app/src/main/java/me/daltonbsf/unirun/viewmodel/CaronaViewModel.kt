package me.daltonbsf.unirun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.CaronaRepository
import me.daltonbsf.unirun.data.ChatRepository
import me.daltonbsf.unirun.model.Carona

class CaronaViewModel(
    private val caronaRepository: CaronaRepository,
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _caronaCreationStatus = MutableStateFlow<Boolean?>(null)
    val caronaCreationStatus = _caronaCreationStatus.asStateFlow()

    private val _availableCaronas = MutableStateFlow<List<Carona>>(emptyList())
    val availableCaronas = _availableCaronas.asStateFlow()

    init {
        loadAvailableCaronas()
    }

    fun loadAvailableCaronas() {
        viewModelScope.launch {
            val allCaronas = caronaRepository.getAllCaronas()
            val currentUser = authRepository.getCurrentUser()?.uid
            if (currentUser != null) {
                _availableCaronas.value = allCaronas.filter { it.creator != currentUser }
            } else {
                _availableCaronas.value = allCaronas
            }
        }
    }

    fun createCarona(
        startLocation: LatLng?,
        destLocation: LatLng?,
        startLocationName: String,
        destLocationName: String,
        dateTimeMillis: Long,
        seats: Int,
        additionalInfo: String
    ) {
        viewModelScope.launch {
            val creatorId = authRepository.getCurrentUser()?.uid
            if (creatorId == null || startLocation == null || destLocation == null) {
                _caronaCreationStatus.value = false
                return@launch
            }

            val groupName = "$startLocationName ➜ $destLocationName"
            val chatId = chatRepository.createGroupChatRoom(groupName, listOf(creatorId))

            val newCarona = Carona(
                creator = creatorId,
                chatId = chatId,
                information = additionalInfo,
                departureDate = dateTimeMillis.toString(),
                origin = "${startLocation.latitude},${startLocation.longitude}",
                destiny = "${destLocation.latitude},${destLocation.longitude}",
                originName = startLocationName,
                destinyName = destLocationName,
                seatsAvailable = seats
            )

            val success = caronaRepository.createCarona(newCarona)
            _caronaCreationStatus.value = success
        }
    }

    fun resetCreationStatus() {
        _caronaCreationStatus.value = null
    }
}