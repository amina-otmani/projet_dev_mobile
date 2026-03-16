package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(entity = ZoneTarifaire::class, parentColumns = ["id"], childColumns = ["zone_tarifaire_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("zone_tarifaire_id")]
)
data class ZonePlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val zone_tarifaire_id: Int,
    val nom: String,
    val nombre_tables: Int
)