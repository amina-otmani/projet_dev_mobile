package com.example.projet_dev_mobile.data.network

import com.example.projet_dev_mobile.data.network.dto.LoginRequest
import com.example.projet_dev_mobile.data.network.dto.LoginResponse
import com.example.projet_dev_mobile.data.network.dto.JeuDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/whoami")
    suspend fun whoami(): Response<LoginResponse>

    // --- JEUX ---
    @GET("jeux")
    suspend fun getAllJeux(): Response<List<JeuDto>>

    // Si tu as besoin de récupérer les jeux d'un festival précis
    @GET("festivals/{id}/jeux")
    suspend fun getJeuxByFestival(@Path("id") festivalId: Int): Response<List<JeuDto>>

    // Si tu as besoin de récupérer les jeux d'un éditeur précis
    @GET("editeurs/{id}/jeux")
    suspend fun getJeuxByEditeur(@Path("id") editeurId: Int): Response<List<JeuDto>>
}
