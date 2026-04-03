package com.example.projet_dev_mobile.data.repository

import android.util.Log
import com.example.projet_dev_mobile.data.network.APIService
import com.example.projet_dev_mobile.data.network.dto.EditeurDto
import com.example.projet_dev_mobile.data.network.dto.JeuDto

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

    suspend fun getEditeurById(id: Int): EditeurDto? {
        return try {
            val response = apiService.getEditeurById(id)
            if (response.isSuccessful) response.body() else null
        } catch (_: Exception) { null }
    }

    suspend fun getJeuxByEditeur(id: Int): List<JeuDto>? {
        return try {
            val response = apiService.getJeuxByEditeur(id)
            if (response.isSuccessful) response.body() else null
        } catch (_: Exception) { null }
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

    suspend fun deleteEditeur(id: Int): Boolean {
        return try {
            val response = apiService.deleteEditeur(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

}
