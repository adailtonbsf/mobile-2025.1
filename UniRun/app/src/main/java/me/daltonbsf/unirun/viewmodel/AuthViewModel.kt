package me.daltonbsf.unirun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.LoginStatus
import me.daltonbsf.unirun.data.UserPreferences
import me.daltonbsf.unirun.model.User
import java.time.LocalDate

class AuthViewModel(
    private val repository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    var loginResult: ((Boolean) -> Unit)? = null
    var registerResult: ((Boolean) -> Unit)? = null
    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    init {
        viewModelScope.launch {
            // Tenta carregar o usuário do cache primeiro
            _user.value = userPreferences.getUser().firstOrNull()
            // Se logado, busca dados atualizados da rede em segundo plano
            if (isUserLoggedIn()) {
                loadCurrentUser()
            }
        }
    }

    // Função para buscar dados da rede e atualizar o cache
    private suspend fun updateAndCacheUser() {
        val firebaseUser = repository.getCurrentUser()
        if (firebaseUser != null) {
            val userModel = User(
                uid = firebaseUser.uid,
                email = repository.getUserEmail(),
                name = repository.getUserName() ?: "",
                username = repository.getUserUsername() ?: "",
                phone = repository.getUserPhone() ?: "",
                profileImageURL = repository.getUserProfileImage(),
                bio = repository.getUserBio() ?: "",
                registrationDate = repository.getUserRegistrationDate(),
                offeredRidesCount = repository.getUserOfferedRidesCount(),
                requestedRidesCount = repository.getUserRequestedRidesCount()
            )
            _user.value = userModel
            userPreferences.saveUser(userModel) // Salva no DataStore
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            updateAndCacheUser()
        }
    }

    fun login(email: String, password: String, onResult: (LoginStatus) -> Unit) {
        viewModelScope.launch {
            val status = repository.login(email, password)
            if (status == LoginStatus.SUCCESS) {
                // Atualiza o usuário e o cache após o login
                updateAndCacheUser()
            }
            onResult(status)
        }
    }

    fun sendEmailVerification(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.sendEmailVerification()
            onResult(success)
        }
    }

    fun resetPassword(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.resetPassword(email)
            onResult(success)
        }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        username: String,
        phone: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.register(
                email, password, name, username, phone
            )
            if (success) {
                repository.sendEmailVerification()
                repository.logout()
            }
            onResult(success)
        }
    }

    suspend fun validateRegisterFields(
        email: String,
        password: String,
        confirmPassword: String,
        name: String,
        username: String,
        phone: String
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors["email"] = "E-mail inválido"
        } else if (repository.isEmailAlreadyInUse(email)) {
            errors["email"] = "E-mail já cadastrado"
        }
        // TODO: VALIDAR EMAIL INSTITUCIONAL
        if (password.length < 6) {
            errors["password"] = "Senha deve ter pelo menos 6 caracteres"
        }
        if (password != confirmPassword) {
            errors["confirmPassword"] = "Senhas não coincidem"
        }
        if (name.isBlank()) {
            errors["name"] = "Nome obrigatório"
        } else if (!name.trim().contains(' ')) {
            errors["name"] = "Por favor, digite seu nome completo"
        }
        if (username.isBlank()) {
            errors["username"] = "Usuário obrigatório"
        } else if (!username.matches("^[a-zA-Z0-9._-]+$".toRegex())) {
            errors["username"] = "Use apenas letras, números e os caracteres . _ -"
        } else if (repository.isUsernameAlreadyInUse(username)) {
            errors["username"] = "Usuário já cadastrado"
        }
        if (phone.isBlank()) {
            errors["phone"] = "Telefone obrigatório"
        }
        return errors
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            userPreferences.clearUser() // Limpa o cache no logout
            _user.value = null
        }
    }

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    suspend fun updateUserProfileImage(imageBytes: ByteArray, fileName: String): Boolean {
        val success = repository.updateUserProfileImage(imageBytes, fileName)
        if (success) {
            loadCurrentUser()
        }
        return success
    }

    suspend fun updateUserProfile(user: User): Boolean {
        val results = mutableListOf<Boolean>()
        user.name.let { results.add(repository.updateUserName(it)) }
        user.username.let { results.add(repository.updateUserUsername(it)) }
        user.phone.let { results.add(repository.updateUserPhone(it)) }
        user.bio.let { results.add(repository.updateUserBio(it)) }
        val allSuccess = results.all { it }
        if (allSuccess) {
            loadCurrentUser()
        }
        return allSuccess
    }

    suspend fun updateUserOfferedRidesCount(count: Int): Boolean {
        val success = repository.updateUserOfferedRidesCount(count)
        if (success) {
            loadCurrentUser()
        }
        return success
    }

    suspend fun updateUserRequestedRidesCount(count: Int): Boolean {
        val success = repository.updateUserRequestedRidesCount(count)
        if (success) {
            loadCurrentUser()
        }
        return success
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            if (query.length > 2) {
                _searchResults.value = repository.searchUsers(query)
            } else {
                _searchResults.value = emptyList()
            }
        }
    }

    suspend fun getUserData(userId: String): User? {
        return repository.getUserData(userId)
    }
}