package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(entity = Festival::class, parentColumns = ["id"], childColumns = ["festival_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("festival_id")]
)
data class ZoneTarifaire(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val festival_id: Int,
    val nom: String,
    val prix_table: Double,
    val prix_m2: Double
)