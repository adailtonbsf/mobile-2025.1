package me.dalton.authapp.remote

import me.dalton.authapp.model.LoginRequest
import me.dalton.authapp.model.TokenResponse
import me.dalton.authapp.model.UserCreate
import me.dalton.authapp.model.UserOut
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("register")
    suspend fun register(@Body userCreate: UserCreate): Response<UserOut>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @GET("me")
    suspend fun me(@Header("Authorization") token: String): Response<UserOut>
}