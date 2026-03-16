package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "Auteurs_Jeux",
    primaryKeys = ["jeu_id", "auteur_id"],
    foreignKeys = [
        ForeignKey(entity = Jeu::class, parentColumns = ["id"], childColumns = ["jeu_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Personne::class, parentColumns = ["id"], childColumns = ["auteur_id"])
    ],
    indices = [Index("jeu_id"), Index("auteur_id")])
data class AuteursJeux(
    val jeu_id: Int,
    val auteur_id: Int
)
