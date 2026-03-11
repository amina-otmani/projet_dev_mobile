package com.example.projet_dev_mobile.data.entity.enum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RoleType {
    @SerialName("no-role") no_role,
    visiteur,
    organisateur_jeux,
    organisateur_reservations,
    admin
}