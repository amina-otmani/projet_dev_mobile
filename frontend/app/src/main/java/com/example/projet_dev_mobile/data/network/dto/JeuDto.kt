package com.example.projet_dev_mobile.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JeuDto(
    val id: Int,
    val nom: String,
    @SerialName("typeg")
    val typeG: String,
    val age_min: Int,
    val age_max: Int,
    val editeur_id: Int,
    val nom_editeur: String,
    val auteurs: List<AuteurDto> = emptyList()
)

@Serializable
data class AuteurDto(
    val id: Int,
    val nom: String,
    val prenom: String
)
