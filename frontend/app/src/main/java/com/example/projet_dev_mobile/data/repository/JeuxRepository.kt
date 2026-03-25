package com.example.projet_dev_mobile.data.repository

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
}
