package com.example.projet_dev_mobile.data.repository

import com.example.projet_dev_mobile.data.entity.Festival
import kotlinx.coroutines.flow.Flow

interface FestivalsRepository {

    suspend fun addFestival(festival: Festival)

    suspend fun updateFestival(festival: Festival)

    suspend fun deleteFestival(festival: Festival)

    suspend fun deleteAllFestivals()

    fun getFestivalByIdStream(id: Int): Flow<Festival?>

    fun getAllFestivalsStream(): Flow<List<Festival>>

    fun getActiveFestivalsStream(today: Long): Flow<List<Festival>>

    fun getPastFestivalsStream(today: Long): Flow<List<Festival>>

    suspend fun upsertAll(festivals: List<Festival>)

}