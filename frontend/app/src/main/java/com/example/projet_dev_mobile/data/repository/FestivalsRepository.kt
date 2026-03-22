package com.example.projet_dev_mobile.data.repository

import com.example.projet_dev_mobile.data.entity.Festival
import com.example.projet_dev_mobile.data.network.APIService
import com.example.projet_dev_mobile.data.network.dto.toDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FestivalsRepository(private val apiService: APIService) {

    fun getAllFestivalsStream(): Flow<List<Festival>> = flow {
        try {
            val response = apiService.getAllFestivals()
            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()
                emit(dtos.map { it.toEntity() })
            } else {
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    suspend fun getFestivalById(id: Int): Festival? {
        return try {
            val response = apiService.getFestivalById(id)
            if (response.isSuccessful) {
                response.body()?.toEntity()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addFestival(festival: Festival): Boolean {
        return try {
            val response = apiService.addFestival(festival.toDto())
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateFestival(festival: Festival): Boolean {
        return try {
            val response = apiService.updateFestival(festival.id, festival.toDto())
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