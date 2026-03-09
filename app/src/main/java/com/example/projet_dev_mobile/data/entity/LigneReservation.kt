package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.projet_dev_mobile.data.entity.enum.TypeEmplacement

@Entity(
    foreignKeys = [
        ForeignKey(entity = Reservation::class, parentColumns = ["id"], childColumns = ["reservation_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ZoneTarifaire::class, parentColumns = ["id"], childColumns = ["zone_tarifaire_id"])
    ],
    indices = [Index("reservation_id"), Index("zone_tarifaire_id")]
)
data class LigneReservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val reservation_id: Int,
    val zone_tarifaire_id: Int,

    val type_emplacement: TypeEmplacement,
    val quantite: Int,
    val prix_moment_reservation: Double
)
