package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Editeur_Contact",
    primaryKeys = ["editeur_id", "contact_id"],
    foreignKeys = [
        ForeignKey(entity = Editeur::class, parentColumns = ["id"], childColumns = ["editeur_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Personne::class, parentColumns = ["id"], childColumns = ["contact_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("editeur_id"), Index("contact_id")]
)
data class EditeurContact(
    val editeur_id: Int,
    val contact_id: Int,
    val est_contact_principal: Boolean = false,
    val poste: String? = null
)