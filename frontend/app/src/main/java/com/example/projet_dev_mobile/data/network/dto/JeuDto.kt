package com.example.projet_dev_mobile.data.network.dto

import com.example.projet_dev_mobile.data.entity.enum.GameType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JeuDto(
    val id: Int,
    val nom: String,
    @SerialName("typeg")
    val typeG: GameType,
    val age_min: Int,
    val age_max: Int,
    // ce n'est pas une bonne pratique mais cet attribut est nécessaire pour le requête GET /jeux
    val editeur_id: Int,
    // utile pour GET /editeurs/{id}/jeux
    val editeur: EditeurDto? = null,
    val auteurs: List<AuteurDto>? = emptyList()
)

@Serializable
data class AuteurDto(
    val id: Int,
    val nom: String,
    val prenom: String
)