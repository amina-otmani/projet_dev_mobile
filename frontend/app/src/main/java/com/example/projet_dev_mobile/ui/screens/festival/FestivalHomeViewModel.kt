package com.example.projet_dev_mobile.ui.screens.festival

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.repository.FestivalsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FestivalHomeViewModel(private val festivalsRepository: FestivalsRepository) : ViewModel() {

    // récupère la date du jour
    private val today = System.currentTimeMillis()

    // mise à jour des festivals sans que l'user n'ait à raffraichir
    // flux vers le haut, on attend des changements de la data base
    val homeUiState: StateFlow<FestivalHomeUiState> = festivalsRepository
        .getAllFestivalsStream()
        .map { festivals ->
            // On trie les données reçues du serveur en deux listes
            FestivalHomeUiState(
                activeFestivals = festivals.filter { it.date_fin >= today },
                pastFestivals = festivals.filter { it.date_fin < today }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FestivalHomeUiState()
        )
}