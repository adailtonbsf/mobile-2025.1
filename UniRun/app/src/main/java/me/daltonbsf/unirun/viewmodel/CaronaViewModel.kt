package me.daltonbsf.unirun.viewmodel

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Application
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.CaronaRepository
import me.daltonbsf.unirun.data.ChatRepository
import me.daltonbsf.unirun.data.UserPreferences
import me.daltonbsf.unirun.model.Carona
import me.daltonbsf.unirun.util.NotificationUtils

class CaronaViewModel(
    private val caronaRepository: CaronaRepository,
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val userPreferences: UserPreferences,
    application: Application
) : AndroidViewModel(application) {

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
            caronaRepository.finishPastCaronas()

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

    @RequiresPermission(POST_NOTIFICATIONS)
    fun joinCarona(carona: Carona, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            val userName = authRepository.getUserName()
            if (user == null || userName == null) {
                onResult(false)
                return@launch
            }

            val joinedCarona = caronaRepository.joinCarona(carona.id, user.uid)
            if (joinedCarona) {
                val addedToChat = chatRepository.addUserToGroupChat(carona.chatId, user.uid)
                if (addedToChat) {
                    loadCaronaDetails(carona.id)
                    sendNotificationIfEnabled(
                        title = "Novo participante!",
                        message = "$userName entrou na carona para ${carona.destinyName}."
                    )
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

    @RequiresPermission(POST_NOTIFICATIONS)
    fun leaveCarona(carona: Carona, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            val userName = authRepository.getUserName()
            if (user == null || userName == null) {
                onResult(false)
                return@launch
            }

            val leftCarona = caronaRepository.leaveCarona(carona.id, user.uid)
            if (leftCarona) {
                val removedFromChat = chatRepository.removeUserFromGroupChat(carona.chatId, user.uid)
                if(removedFromChat) {
                    sendNotificationIfEnabled(
                        title = "Um participante saiu!",
                        message = "$userName saiu da carona para ${carona.destinyName}."
                    )
                }
                onResult(removedFromChat)
            } else {
                onResult(false)
            }
        }
    }

    @RequiresPermission(POST_NOTIFICATIONS)
    fun cancelCarona(carona: Carona, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val chatDeleted = chatRepository.deleteChat(carona.chatId)
            val caronaCancelled = caronaRepository.cancelCarona(carona.id)
            val success = chatDeleted && caronaCancelled
            if (success) {
                sendNotificationIfEnabled(
                    title = "Carona Cancelada",
                    message = "A carona de ${carona.originName} para ${carona.destinyName} foi cancelada."
                )
            }
            onResult(success)
        }
    }

    @RequiresPermission(POST_NOTIFICATIONS)
    private suspend fun sendNotificationIfEnabled(title: String, message: String) {
        val allNotificationsEnabled = userPreferences.getPreference(UserPreferences.ALL_NOTIFICATIONS_KEY, "true").first().toBoolean()
        val groupNotificationsEnabled = userPreferences.getPreference(UserPreferences.GROUP_ENTRY_EXIT_NOTIFICATIONS_KEY, "true").first().toBoolean()

        if (allNotificationsEnabled && groupNotificationsEnabled) {
            NotificationUtils.showNotification(getApplication(), title, message)
        }
    }
}