package com.example.projet_dev_mobile.data.network.dto

import com.example.projet_dev_mobile.data.entity.ZoneTarifaire
import kotlinx.serialization.Serializable

@Serializable
data class ZoneTarifaireDTO(
    val id: Int,
    val nom: String,
    val prixTable: Double,
    val prixM2: Double,
    val zonesPlan: List<ZonePlanDTO> = emptyList(),

    val nbTotalTables: Int? = 0,
    val nbTablesLibres: Int? = 0
) {
    fun toEntity(): ZoneTarifaire {
        return ZoneTarifaire(
            id = this.id,
            nom = this.nom,
            prix_table = this.prixTable,
            prix_m2 = this.prixM2,
            zonesPlan = this.zonesPlan.map { it.toEntity() }
        )
    }
}

fun ZoneTarifaire.toDto(): ZoneTarifaireDTO {
    return ZoneTarifaireDTO(
        id = this.id,
        nom = this.nom,
        prixTable = this.prix_table,
        prixM2 = this.prix_m2,
        zonesPlan = this.zonesPlan.map { it.toDto() }
    )
}
