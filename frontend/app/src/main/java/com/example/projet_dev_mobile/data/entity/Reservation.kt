package com.example.projet_dev_mobile.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.projet_dev_mobile.data.entity.enum.EtatReservation
import com.example.projet_dev_mobile.data.entity.enum.TypeReservant

@Entity(
    foreignKeys = [
        ForeignKey(entity = Festival::class, parentColumns = ["id"], childColumns = ["festival_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Editeur::class, parentColumns = ["id"], childColumns = ["editeur_id"]),
    ],
    indices = [Index("festival_id"), Index("editeur_id")]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val festival_id: Int,
    val type: TypeReservant,
    val editeur_id: Int? = null,
    val autre_nom_reservant: String? = null,

    // STEP 1 : Données Logistiques Globales & Préférences
    val nombre_prises: Int = 0,
    val est_present: Boolean = true,
    val remise_generale: Double = 0.0,
    val preferences_tables: String? = null,

    // Cycle de vie
    val statut: EtatReservation = EtatReservation.PRESENT,
    val date_creation: Long = System.currentTimeMillis(),
    val date_facturation: Long? = null,
    val date_paiement: Long? = null
) {
    init {
        if (type == TypeReservant.Editeur) {
            require(editeur_id != null) { "editeur_id requis si type = EDITEUR" }
        } else {
            require(autre_nom_reservant != null) { "autre_nom_reservant requis si type != EDITEUR" }
        }
    }
}