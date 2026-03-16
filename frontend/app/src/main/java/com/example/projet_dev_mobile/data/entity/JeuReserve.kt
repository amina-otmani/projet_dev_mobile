package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.projet_dev_mobile.data.entity.enum.TailleTable

@Entity(
    foreignKeys = [
        ForeignKey(entity = Reservation::class, parentColumns = ["id"], childColumns = ["reservation_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Jeu::class, parentColumns = ["id"], childColumns = ["jeu_id"]),
        ForeignKey(entity = ZonePlan::class, parentColumns = ["id"], childColumns = ["zone_plan_id"])
    ],
    indices = [Index("reservation_id"), Index("jeu_id"), Index("zone_plan_id")]
)
data class JeuReserve(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val reservation_id: Int,
    val jeu_id: Int,

    // Consommation d'espace (1 = Une table entière, 0.5 = Partage)
    val tables_occupees: Double = 1.0,
    val nb_exemplaires: Int = 1,

    // Placement physique
    val zone_plan_id: Int? = null,
    // Type de table souhaité
    val type_table: TailleTable = TailleTable.PETITE,
    val est_recu: Boolean = false
)
