package com.example.projet_dev_mobile.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.repository.FestivalsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
@RequiresApi(Build.VERSION_CODES.O)
class FestivalHomeViewModel(private val festivalsRepository: FestivalsRepository) : ViewModel() {

    private val today = java.time.LocalDate.now()

    val homeUiState: StateFlow<FestivalHomeUiState> = festivalsRepository
        .getAllFestivalsStream()
        .map { festivals ->
            // Log pour vérifier si les données arrivent ici
            println("HOME_DEBUG: Festivals reçus = ${festivals.size}")

            FestivalHomeUiState(
                activeFestivals = festivals.filter {
                    parseSafeDate(it.date_fin).isAfter(today.minusDays(1))
                },
                pastFestivals = festivals.filter {
                    parseSafeDate(it.date_fin).isBefore(today)
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FestivalHomeUiState()
        )

    private fun parseSafeDate(dateString: String): java.time.LocalDate {
        return try {
            // Nettoyage pour gérer "2026-03-22T00:00:00.000Z" -> "2026-03-22"
            val cleanDate = dateString.substringBefore("T")
            java.time.LocalDate.parse(cleanDate)
        } catch (e: Exception) {
            java.time.LocalDate.now() // Valeur de secours
        }
    }
}