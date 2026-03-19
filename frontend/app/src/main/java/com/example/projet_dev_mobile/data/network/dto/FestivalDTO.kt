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
    val nbTablesMairie: Int = 0
    // TODO Amina : Il faut que tu crées cette classe ZoneTarifaireDto
    // val zonesTarifaires: List<ZoneTarifaireDto> = emptyList()
) {
    fun toEntity(): Festival {
        return Festival(
            id = this.id,
            nom = this.nom,
            date_debut = this.date_debut,
            date_fin = this.date_fin,
            stock_tables_petites = this.nbTablesPetites,
            stock_tables_grandes = this.nbTablesGrandes,
            stock_tables_mairie = this.nbTablesMairie
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
        nbTablesMairie = this.stock_tables_mairie
    )
}

//Pour t'aider à créer tes deux autres DTOs, voici ce que le backend NodeJS va t'envoyer (et ce qu'il attend en retour lors d'un POST/PUT).
// Assure-toi que tes variables Kotlin aient exactement ces noms-là :
//Pour ZoneTarifaireDto :
//
//id (Int)
//
//nom (String)
//
//prixTable (Double)
//
//prixM (Double)
//
//zonesPlan (List<ZonePlanDto>)
//
//(Optionnels, calculés par l'API pour l'affichage : nbTotalTables et nbTablesLibres)
//
//Pour ZonePlanDto :
//
//id (Int)
//
//nom (String)
//
//nbTables (Int)