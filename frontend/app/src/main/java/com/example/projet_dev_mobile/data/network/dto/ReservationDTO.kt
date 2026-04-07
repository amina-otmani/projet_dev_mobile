package com.example.projet_dev_mobile.data.network.dto

import com.example.projet_dev_mobile.data.entity.enum.EtatReservation
import com.example.projet_dev_mobile.data.entity.enum.TypeReservant
import kotlinx.serialization.Serializable

@Serializable
data class ReservationDto(
    val id: Int? = null,
    val festival_id: Int,
    val type: TypeReservant,
    val editeur_id: Int? = null,
    val autre_nom_reservant: String? = null,
    val nom_reservant: String? = null,
    val nombre_prises: Int,
    val remise_generale: Double,
    val est_present: Boolean,
    val preferences_tables: String? = null,
    val statut: EtatReservation? = EtatReservation.PRESENT,
    val date_creation: String? = null,
    val date_facturation: String? = null,
    val date_paiement: String? = null,
    val lignes: List<LigneReservationDto> = emptyList(),
    val jeux: List<JeuReserveDto> = emptyList(),
    val total_a_payer: String? = null
)

@Serializable
data class LigneReservationDto(
    val id: Int? = null,
    val zone_tarifaire_id: Int,
    val quantite: Int,
    val prix_moment_reservation: Double,
    val type_emplacement: String
)

@Serializable
data class JeuReserveDto(
    val id: Int? = null,
    val jeu_id: Int,
    val nb_exemplaires: Int,
    val tables_occupees: Double,
    val type_table: String? = null,
    val zone_plan_id: Int? = null,
    val jeu_nom: String? = null,
    val salle_nom: String? = null
)

@Serializable
data class ReservationResponse(
    val message: String,
    val id: Int
)

@Serializable
data class ReservationDetailsResponse(
    val reservation: ReservationDto,
    val lignes: List<LigneReservationDto>,
    val jeux: List<JeuReserveDto>
)

@Serializable
data class StatutRequest(
    val statut: String
)