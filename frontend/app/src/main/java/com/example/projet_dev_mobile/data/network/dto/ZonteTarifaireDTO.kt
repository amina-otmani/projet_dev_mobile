package com.example.projet_dev_mobile.data.network.dto

import com.example.projet_dev_mobile.data.entity.ZoneTarifaire
import kotlinx.serialization.Serializable
import com.example.projet_dev_mobile.data.network.dto.ZonePlanDTO


@Serializable
data class ZoneTarifaireDTO(
    val id: Int,
    val nom: String,
    val prixTable: Double,
    val prixM: Double,
    val zonesPlan: List<ZonePlanDTO> = emptyList(),

    // Champs optionnels calculés par l'API
    val nbTotalTables: Int? = 0,
    val nbTablesLibres: Int? = 0
) {
    fun toEntity(): ZoneTarifaire {
        return ZoneTarifaire(
            id = this.id,
            nom = this.nom,
            prixTable = this.prixTable,
            prixM2 = this.prixM,
            zonesPlan = this.zonesPlan.map { it.toEntity() }
        )
    }
}

fun ZoneTarifaire.toDto(): ZoneTarifaireDTO {
    return ZoneTarifaireDTO(
        id = this.id,
        nom = this.nom,
        prixTable = this.prixTable,
        prixM = this.prixM2,
        zonesPlan = this.zonesPlan.map { it.toDto() }
    )
}
