package com.example.projet_dev_mobile.data.network.dto

import com.example.projet_dev_mobile.data.entity.ZonePlan
import kotlinx.serialization.Serializable

@Serializable
data class ZonePlanDTO(
    val id: Int,
    val nom: String,
    val nbTables: Int
) {
    fun toEntity(): ZonePlan {
        return ZonePlan(
            id = this.id,
            nom = this.nom,
            nombre_tables = this.nbTables
        )
    }
}

fun ZonePlan.toDto(): ZonePlanDTO {
    return ZonePlanDTO(
        id = this.id,
        nom = this.nom,
        nbTables = this.nombre_tables
    )
}