package me.dalton.authapp.model

data class TokenResponse(
    val access_token: String,
    val token_type: String
)
