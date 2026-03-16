package com.example.projet_dev_mobile.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.projet_dev_mobile.data.entity.Festival
import kotlinx.coroutines.flow.Flow

@Dao
interface FestivalDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFestival(festival: Festival)

    @Update
    suspend fun updateFestival(festival: Festival)

    @Delete
    suspend fun deleteFestival(festival: Festival)

    @Query("DELETE FROM festival")
    suspend fun deleteAllFestivals()

    @Query("SELECT * FROM festival ORDER BY date_debut ASC")
    fun getAllFestivals(): Flow<List<Festival>>

    @Query("SELECT * FROM festival WHERE id = :id")
    fun getFestivalById(id: Int): Flow<Festival?>

    @Query("SELECT * FROM festival WHERE date_fin >= :today ORDER BY date_debut ASC")
    fun getActiveFestivals(today: Long): Flow<List<Festival>>

    @Query("SELECT * FROM festival WHERE date_fin < :today ORDER BY date_debut DESC")
    fun getPastFestivals(today: Long): Flow<List<Festival>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(festivals: List<Festival>)
}