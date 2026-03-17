package com.example.projet_dev_mobile.ui.screens.festival

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

import androidx.lifecycle.ViewModel
import com.example.projet_dev_mobile.data.entity.Festival
import com.example.projet_dev_mobile.data.repository.FestivalsRepository

class FestivalEntryViewModel(private val festivalsRepository: FestivalsRepository) : ViewModel() {
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
    val date_fin: String = ""
)

fun FestivalDetails.toFestival(): Festival = Festival(
    id = id,
    nom = nom,
    date_debut = date_debut,
    date_fin = date_fin
)