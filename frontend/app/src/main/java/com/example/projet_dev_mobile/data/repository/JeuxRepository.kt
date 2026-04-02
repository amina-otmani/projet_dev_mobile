package com.example.projet_dev_mobile.data.repository

import android.util.Log
import com.example.projet_dev_mobile.data.network.APIService
import com.example.projet_dev_mobile.data.network.dto.JeuDto

class JeuxRepository(private val apiService: APIService) {

    suspend fun getAllJeux(): List<JeuDto>? {
        return try {
            val response = apiService.getAllJeux()
            if (response.isSuccessful) {
                response.body().orEmpty()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun addJeu(jeu: JeuDto): Boolean {
        return try {
            val response = apiService.addJeu(jeu)
            if (!response.isSuccessful) {
                Log.e("JeuxRepository", "Erreur ${response.code()} : ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("JeuxRepository", "Exception : ${e.message}")
            false
        }
    }

    suspend fun deleteJeu(id: Int): Boolean {
        return try {
            val response = apiService.deleteJeu(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

}
