package com.example.projet_dev_mobile.ui.screens.festival

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

import androidx.lifecycle.ViewModel
import com.example.projet_dev_mobile.data.entity.Festival
import com.example.projet_dev_mobile.data.entity.ZonePlan
import com.example.projet_dev_mobile.data.entity.ZoneTarifaire
import com.example.projet_dev_mobile.data.repository.FestivalsRepository

class FestivalEntryViewModel(private val festivalsRepository: FestivalsRepository) : ViewModel() {

    // --- ZONE TARIFAIRE ---
    fun addEmptyZoneT() {
        val currentZones = festivalUiState.festivalDetails.zonesTarifaires
        val newZone = ZoneTarifaire(nom = "", prixTable = 0.0, prixM2 = 0.0)

        updateUiState(festivalUiState.festivalDetails.copy(
            zonesTarifaires = currentZones + newZone
        ))
    }
    fun deleteZoneT(index: Int) {
        val currentZones = festivalUiState.festivalDetails.zonesTarifaires.toMutableList()
        currentZones.removeAt(index)

        updateUiState(festivalUiState.festivalDetails.copy(
            zonesTarifaires = currentZones
        ))
    }
    fun updateZoneT(index: Int, updatedZoneT: ZoneTarifaire) {
        val currentZT = festivalUiState.festivalDetails.zonesTarifaires.toMutableList()
        currentZT[index] = updatedZoneT

        updateUiState(festivalUiState.festivalDetails.copy(
            zonesTarifaires = currentZT
        ))
    }


    // --- ZONE PLAN ---
    fun addZPtoZT(indexZT: Int) {
        val currentZT = festivalUiState.festivalDetails.zonesTarifaires.toMutableList()
        val zone = currentZT[indexZT]

        val newZP = ZonePlan(nom = "", nbTables = 0)
        currentZT[indexZT] = zone.copy(zonesPlan = zone.zonesPlan + newZP)

        updateUiState(festivalUiState.festivalDetails.copy(zonesTarifaires = currentZT))
    }
    fun updateZPinZT(indexZT: Int, indexZP: Int, updatedZP: ZonePlan) {
        val currentZT = festivalUiState.festivalDetails.zonesTarifaires.toMutableList()
        val zone = currentZT[indexZT]
        val currentZPs = zone.zonesPlan.toMutableList()

        currentZPs[indexZP] = updatedZP
        currentZT[indexZT] = zone.copy(zonesPlan = currentZPs)

        updateUiState(festivalUiState.festivalDetails.copy(zonesTarifaires = currentZT))
    }
    fun deleteZPfromZT(indexZT: Int, indexZP: Int) {
        val currentZT = festivalUiState.festivalDetails.zonesTarifaires.toMutableList()
        val zone = currentZT[indexZT]
        val currentZPs = zone.zonesPlan.toMutableList()

        currentZPs.removeAt(indexZP)
        currentZT[indexZT] = zone.copy(zonesPlan = currentZPs)

        updateUiState(festivalUiState.festivalDetails.copy(zonesTarifaires = currentZT))
    }

    var festivalUiState by mutableStateOf(FestivalUiState())
        private set

    fun updateUiState(details: FestivalDetails) {
        festivalUiState = FestivalUiState(
            festivalDetails = details,
            isEntryValid = validateInput(details))
    }

    private fun validateInput(uiState: FestivalDetails): Boolean {
        return uiState.nom.isNotBlank() && uiState.date_debut.isNotBlank() && uiState.date_fin.isNotBlank()
    }

    suspend fun saveFestival(): Boolean {
        if (validateInput(festivalUiState.festivalDetails)) {
            return festivalsRepository.addFestival(festivalUiState.festivalDetails.toFestival())
        }
        return false
    }
}

data class FestivalUiState(
    val festivalDetails: FestivalDetails = FestivalDetails(),
    val isEntryValid: Boolean = false
)

data class FestivalDetails(
    val id: Int = 0,
    val nom: String = "",
    val date_debut: String = "",
    val date_fin: String = "",

    val stock_tables_petites: Int = 0,
    val stock_tables_grandes: Int = 0,
    val stock_tables_mairie: Int = 0,

    val zonesTarifaires: List<ZoneTarifaire> = emptyList()
)

fun FestivalDetails.toFestival(): Festival = Festival(
    id = id,
    nom = nom,
    date_debut = date_debut,
    date_fin = date_fin,

    stock_tables_mairie = stock_tables_mairie,
    stock_tables_petites = stock_tables_petites,
    stock_tables_grandes = stock_tables_grandes,

    zonesTarifaires = zonesTarifaires
)