package com.example.projet_dev_mobile.data.repository

import android.util.Log
import com.example.projet_dev_mobile.data.network.APIService
import com.example.projet_dev_mobile.data.network.dto.EditeurDto

class EditeursRepository(private val apiService: APIService) {

    suspend fun getAllEditeurs(): List<EditeurDto>? {
        return try {
            val response = apiService.getAllEditeurs()
            if (response.isSuccessful) {
                response.body().orEmpty()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun addEditeur(editeur: EditeurDto): Boolean {
        return try {
            val response = apiService.addEditeur(editeur)
            if (!response.isSuccessful) {
                Log.e("EditeursRepository", "Erreur ${response.code()} : ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("EditeursRepository", "Exception : ${e.message}")
            false
        }
    }
}
