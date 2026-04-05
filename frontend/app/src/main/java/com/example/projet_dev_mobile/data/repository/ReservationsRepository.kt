package com.example.projet_dev_mobile.data.repository

import com.example.projet_dev_mobile.data.network.dto.ReservationDto
import com.example.projet_dev_mobile.data.network.dto.ReservationDetailsResponse
import com.example.projet_dev_mobile.data.network.APIService
import retrofit2.Response

class ReservationsRepository(private val apiService: APIService) {

    suspend fun getReservations(festivalId: Int, isOrganisateur: Boolean): Result<List<ReservationDto>> {
        return try {
            val response = if (isOrganisateur) {
                apiService.getReservationsByFestival(festivalId)
            } else {
                apiService.getPublicReservations(festivalId)
            }

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Erreur API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReservationDetails(id: Int): Result<ReservationDetailsResponse> {
        return try {
            val response = apiService.getReservationDetails(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur lors de la récupération des détails"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveReservation(reservation: ReservationDto): Result<Unit> {
        return try {
            if (reservation.id != null) {
                // UPDATE
                val response = apiService.updateReservation(reservation.id, reservation)
                if (response.isSuccessful) Result.success(Unit)
                else Result.failure(Exception("Erreur Update: ${response.code()}"))
            } else {
                // CREATE
                val response = apiService.createReservation(reservation)
                if (response.isSuccessful) Result.success(Unit)
                else Result.failure(Exception("Erreur Création: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReservation(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteReservation(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Erreur lors de la suppression : ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}