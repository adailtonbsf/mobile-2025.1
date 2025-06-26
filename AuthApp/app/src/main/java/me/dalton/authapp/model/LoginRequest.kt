package me.dalton.authapp.model

data class LoginRequest(
    val email: String,
    val password: String
)