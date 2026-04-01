package com.example.projet_dev_mobile.data.network

import com.example.projet_dev_mobile.data.entity.Festival
import com.example.projet_dev_mobile.data.entity.enum.RoleType
import com.example.projet_dev_mobile.data.network.dto.EditeurDto
import com.example.projet_dev_mobile.data.network.dto.LoginRequest
import com.example.projet_dev_mobile.data.network.dto.LoginResponse
import com.example.projet_dev_mobile.data.network.dto.JeuDto
import  com.example.projet_dev_mobile.data.network.dto.FestivalDto
import com.example.projet_dev_mobile.data.network.dto.UserDto
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface APIService {

    // --- AUTHENTIFICATION ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/whoami")
    suspend fun whoami(): Response<LoginResponse>

    // --- FESTIVAL ---

    @GET("festivals")
    suspend fun getAllFestivals(): Response<List<FestivalDto>>

    @GET("festivals/{id}")
    suspend fun getFestivalById(@Path("id") id: Int): Response<FestivalDto>

    @POST("festivals")
    suspend fun addFestival(@Body festival: FestivalDto): Response<Unit>

    @PUT("festivals/{id}")
    suspend fun updateFestival(@Path("id") id: Int, @Body festival: FestivalDto): Response<Unit>

    @DELETE("festivals/{id}")
    suspend fun deleteFestival(@Path("id") id: Int): Response<Unit>

    // --- EDITEURS ---

    @GET("editeurs")
    suspend fun getAllEditeurs(): Response<List<EditeurDto>>

    // --- PANNEL ADMIN ---
    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>

    @PUT("users/{id}/role")
    suspend fun updateUserRole(
        @Path("id") userId: Int,
        @Body roleUpdate: RoleUpdateClick
    ): Response<UserDto>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<Unit>


    // --- JEUX ---
    @GET("jeux")
    suspend fun getAllJeux(): Response<List<JeuDto>>

    @GET("festivals/{id}/jeux")
    suspend fun getJeuxByFestival(@Path("id") festivalId: Int): Response<List<JeuDto>>

    @GET("editeurs/{id}/jeux")
    suspend fun getJeuxByEditeur(@Path("id") editeurId: Int): Response<List<JeuDto>>

    @POST("jeux")
    suspend fun addJeu(@Body jeu: JeuDto): Response<JeuDto>
}

@Serializable
data class RoleUpdateClick(
    val role: RoleType
)


