package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.projet_dev_mobile.data.entity.enum.GameType

@Entity(
    foreignKeys = [
        ForeignKey(entity = Editeur::class, parentColumns = ["id"], childColumns = ["editeur_id"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("editeur_id")]
)
data class Jeu(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val editeur_id: Int,
    val nom: String,
    val typeG: GameType,
    val age_min: Int,
    val age_max: Int
)
