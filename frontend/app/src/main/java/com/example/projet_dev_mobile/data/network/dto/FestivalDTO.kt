package com.example.projet_dev_mobile.data.network.dto

import kotlinx.serialization.Serializable
import com.example.projet_dev_mobile.data.entity.Festival

@Serializable
data class FestivalDto(
    val id: Int = 0,
    val nom: String,
    val date_debut: String,
    val date_fin: String,

    val nbTablesPetites: Int = 0,
    val nbTablesGrandes: Int = 0,
    val nbTablesMairie: Int = 0,
    val zonesTarifaires: List<ZoneTarifaireDTO> = emptyList()
) {
    fun toEntity(): Festival {
        return Festival(
            id = this.id,
            nom = this.nom,
            date_debut = this.date_debut,
            date_fin = this.date_fin,
            stock_tables_petites = this.nbTablesPetites,
            stock_tables_grandes = this.nbTablesGrandes,
            stock_tables_mairie = this.nbTablesMairie,
            zonesTarifaires = this.zonesTarifaires.map { it.toEntity() }
        )
    }
}

// Fonction d'extension pour faire l'inverse (Entity -> DTO) lors d'un POST/PUT
fun Festival.toDto(): FestivalDto {
    return FestivalDto(
        id = this.id,
        nom = this.nom,
        date_debut = this.date_debut,
        date_fin = this.date_fin,
        nbTablesPetites = this.stock_tables_petites,
        nbTablesGrandes = this.stock_tables_grandes,
        nbTablesMairie = this.stock_tables_mairie,
        zonesTarifaires = this.zonesTarifaires.map { it.toDto() }
    )
}