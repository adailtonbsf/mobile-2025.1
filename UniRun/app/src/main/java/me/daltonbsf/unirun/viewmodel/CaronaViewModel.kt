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

    private val _selectedCarona = MutableStateFlow<Carona?>(null)
    val selectedCarona = _selectedCarona.asStateFlow()

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

    fun loadCaronaDetails(caronaId: String) {
        viewModelScope.launch {
            _selectedCarona.value = caronaRepository.getCaronaById(caronaId)
        }
    }

    fun joinCarona(carona: Carona, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.uid
            if (userId == null) {
                onResult(false)
                return@launch
            }

            val joinedCarona = caronaRepository.joinCarona(carona.id, userId)
            if (joinedCarona) {
                val addedToChat = chatRepository.addUserToGroupChat(carona.chatId, userId)
                if (addedToChat) {
                    loadCaronaDetails(carona.id) // Recarrega os dados para atualizar a UI
                }
                onResult(addedToChat)
            } else {
                onResult(false)
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

            val participants = listOf(creatorId)
            val groupName = "$startLocationName ➜ $destLocationName"
            val chatId = chatRepository.createGroupChatRoom(groupName, participants)

            val newCarona = Carona(
                creator = creatorId,
                chatId = chatId,
                information = additionalInfo,
                departureDate = dateTimeMillis.toString(),
                origin = "${startLocation.latitude},${startLocation.longitude}",
                destiny = "${destLocation.latitude},${destLocation.longitude}",
                originName = startLocationName,
                destinyName = destLocationName,
                seatsAvailable = seats,
                participants = participants
            )

            val success = caronaRepository.createCarona(newCarona)
            _caronaCreationStatus.value = success
        }
    }

    fun resetCreationStatus() {
        _caronaCreationStatus.value = null
    }

    fun findCaronaByChatId(chatId: String, onResult: (Carona?) -> Unit) {
        viewModelScope.launch {
            val carona = caronaRepository.getCaronaByChatId(chatId)
            onResult(carona)
        }
    }

    fun leaveCarona(carona: Carona, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.uid
            if (userId == null) {
                onResult(false)
                return@launch
            }

            val leftCarona = caronaRepository.leaveCarona(carona.id, userId)
            if (leftCarona) {
                val removedFromChat = chatRepository.removeUserFromGroupChat(carona.chatId, userId)
                onResult(removedFromChat)
            } else {
                onResult(false)
            }
        }
    }

    fun cancelCarona(carona: Carona, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val chatDeleted = chatRepository.deleteChat(carona.chatId)
            val caronaCancelled = caronaRepository.cancelCarona(carona.id)
            onResult(chatDeleted && caronaCancelled)
        }
    }
}