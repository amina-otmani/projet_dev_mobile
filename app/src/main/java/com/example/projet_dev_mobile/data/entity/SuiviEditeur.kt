package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.projet_dev_mobile.data.entity.enum.EtatSuivi

@Entity(
    primaryKeys = ["festival_id", "editeur_id"],
    foreignKeys = [
        ForeignKey( entity = Festival::class, parentColumns = ["id"], childColumns = ["festival_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Editeur::class, parentColumns = ["id"], childColumns = ["editeur_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["responsable_id"])
    ],
    indices = [Index("editeur_id"), Index("responsable_id")]
)
data class SuiviEditeur(
    val festival_id: Int,
    val editeur_id: Int,
    val etat: EtatSuivi = EtatSuivi.PAS_CONTACTE,
    val compte_rendu: String? = null,
    val responsable_id: Int? = null,
    val dates_contact: String = "[]"
)
