package com.example.projet_dev_mobile.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class SuiviDto(
    val editeur_id: Int,
    val editeur_nom: String,
    val etat: String,
    val compte_rendu: String? = null,
    val responsable_id: Int? = null,
    val responsable_nom: String? = null,
    val dates_contact: List<String> = emptyList(),
    val nb_jeux: Int,
    val reservation_id: Int? = null
)

@Serializable
data class PrendreContactRequest(
    val festival_id: Int,
    val editeur_id: Int
)

@Serializable
data class UpdateSuiviRequest(
    val festival_id: Int,
    val editeur_id: Int,
    val etat: String,
    val compte_rendu: String? = null,
    val responsable_id: Int? = null
)
