package com.example.projet_dev_mobile.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class EditeurDto(
    val id: Int,
    val nom: String,
    val created_at: String? = null,
    val contacts: List<EditeurContactDto> = emptyList(),
)

@Serializable
data class EditeurContactDto(
    val id: Int,
    val nom: String,
    val prenom: String,
    val email: String,
    val poste: String? = null
)
