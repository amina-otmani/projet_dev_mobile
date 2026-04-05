package com.example.projet_dev_mobile.data.network

import com.example.projet_dev_mobile.data.entity.Festival
import com.example.projet_dev_mobile.data.network.dto.EditeurDto
import com.example.projet_dev_mobile.data.network.dto.LoginRequest
import com.example.projet_dev_mobile.data.network.dto.LoginResponse
import com.example.projet_dev_mobile.data.network.dto.JeuDto
import  com.example.projet_dev_mobile.data.network.dto.FestivalDto
import com.example.projet_dev_mobile.data.network.dto.ReservationDto
import com.example.projet_dev_mobile.data.network.dto.ReservationDetailsResponse
import com.example.projet_dev_mobile.data.network.dto.ReservationResponse
import com.example.projet_dev_mobile.data.network.dto.StatutRequest
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

    // --- ZONE TARIFAIRE ---



    // --- JEUX ---
    @GET("jeux")
    suspend fun getAllJeux(): Response<List<JeuDto>>

    // Récupérer les jeux d'un festival précis
    @GET("festivals/{id}/jeux")
    suspend fun getJeuxByFestival(@Path("id") festivalId: Int): Response<List<JeuDto>>

    // Récupérer les jeux d'un éditeur précis
    @GET("editeurs/{id}/jeux")
    suspend fun getJeuxByEditeur(@Path("id") editeurId: Int): Response<List<JeuDto>>

    // --- RESERVATIONS ---

    // Lecture Publique (Visiteurs)
    @GET("reservations/festival/{id}/public")
    suspend fun getPublicReservations(@Path("id") festivalId: Int): Response<List<ReservationDto>>

    // Lecture Gestion (Organisateurs)
    @GET("reservations/festival/{id}")
    suspend fun getReservationsByFestival(@Path("id") festivalId: Int): Response<List<ReservationDto>>

    // Détail complet d'une réservation
    @GET("reservations/{id}/details")
    suspend fun getReservationDetails(@Path("id") id: Int): Response<ReservationDetailsResponse> // À créer si besoin

    // Création
    @POST("reservations")
    suspend fun createReservation(@Body reservation: ReservationDto): Response<ReservationResponse>

    // Mise à jour complète
    @PUT("reservations/{id}")
    suspend fun updateReservation(@Path("id") id: Int, @Body reservation: ReservationDto): Response<Unit>

    // Changement de statut
    @PUT("reservations/{id}/statut")
    suspend fun updateReservationStatut(@Path("id") id: Int, @Body request: StatutRequest): Response<ReservationDto>

    // Suppression
    @DELETE("reservations/{id}")
    suspend fun deleteReservation(@Path("id") id: Int): Response<Unit>
}
