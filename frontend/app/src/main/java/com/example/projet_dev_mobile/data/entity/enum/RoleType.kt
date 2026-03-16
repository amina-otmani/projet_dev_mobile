package com.example.projet_dev_mobile.data.entity.enum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RoleType {
    @SerialName("no-role") NO_ROLE,
    @SerialName("visiteur") VISITEUR,
    @SerialName("organisateur_jeux") ORGANISATEUR_JEUX,
    @SerialName("organisateur_reservations") ORGANISATEUR_RESERVATIONS,
    @SerialName("admin") ADMIN
}