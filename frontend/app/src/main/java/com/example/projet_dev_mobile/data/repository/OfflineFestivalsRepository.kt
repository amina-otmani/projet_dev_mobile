package com.example.projet_dev_mobile.data.repository

import com.example.projet_dev_mobile.data.dao.FestivalDao
import com.example.projet_dev_mobile.data.entity.Festival
import kotlinx.coroutines.flow.Flow

class OfflineFestivalsRepository(private val festivalDao: FestivalDao) : FestivalsRepository {
    override suspend fun addFestival(festival: Festival) = festivalDao.addFestival(festival)

    override suspend fun updateFestival(festival: Festival) = festivalDao.updateFestival(festival)

    override suspend fun deleteFestival(festival: Festival) = festivalDao.deleteFestival(festival)

    override suspend fun deleteAllFestivals() = festivalDao.deleteAllFestivals()

    override fun getFestivalByIdStream(id: Int): Flow<Festival?> = festivalDao.getFestivalById(id)

    override fun getAllFestivalsStream(): Flow<List<Festival>> = festivalDao.getAllFestivals()

    override fun getActiveFestivalsStream(today: Long): Flow<List<Festival>> = festivalDao.getActiveFestivals(today)

    override fun getPastFestivalsStream(today: Long): Flow<List<Festival>> = festivalDao.getPastFestivals(today)

    override suspend fun upsertAll(festivals: List<Festival>) = festivalDao.upsertAll(festivals)

}