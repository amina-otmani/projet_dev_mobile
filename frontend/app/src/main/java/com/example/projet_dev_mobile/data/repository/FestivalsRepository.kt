package com.example.projet_dev_mobile.data.repository

import com.example.projet_dev_mobile.data.entity.Festival
import com.example.projet_dev_mobile.data.network.APIService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FestivalsRepository(private val apiService: APIService) {

    fun getAllFestivalsStream(): Flow<List<Festival>> = flow {
        try {
            val response = apiService.getAllFestivals()
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getFestivalById(id: Int): Festival? {
        return try {
            val response = apiService.getFestivalById(id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addFestival(festival: Festival): Boolean {
        return try {
            val response = apiService.addFestival(festival)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateFestival(festival: Festival): Boolean {
        return try {
            val id = festival.id ?: return false
            val response = apiService.updateFestival(id, festival)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteFestival(id: Int): Boolean {
        return try {
            val response = apiService.deleteFestival(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

}