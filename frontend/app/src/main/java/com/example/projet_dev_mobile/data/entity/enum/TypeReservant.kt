package com.example.projet_dev_mobile.data.entity.enum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
enum class TypeReservant {
    @SerialName("Editeur") EDITEUR,
    @SerialName("Boutique") BOUTIQUE,
    @SerialName("Association") ASSOCIATION,
    @SerialName("Prestataire") PRESTATAIRE,
    @SerialName("Autre") AUTRE
}