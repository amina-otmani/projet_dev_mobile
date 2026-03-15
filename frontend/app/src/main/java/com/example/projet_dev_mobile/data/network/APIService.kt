package com.example.projet_dev_mobile.data.network

import com.example.projet_dev_mobile.data.network.dto.LoginRequest
import com.example.projet_dev_mobile.data.network.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface APIService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/whoami")
    suspend fun whoami(): Response<LoginResponse>
}
