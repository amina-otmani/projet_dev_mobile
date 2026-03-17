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
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
class FestivalHomeViewModel(private val festivalsRepository: FestivalsRepository) : ViewModel() {

    private val today = Instant.now()

    val homeUiState: StateFlow<FestivalHomeUiState> = festivalsRepository
        .getAllFestivalsStream()
        .map { festivals ->
            FestivalHomeUiState(
                activeFestivals = festivals.filter {
                    Instant.parse(it.date_fin).isAfter(today)
                },
                pastFestivals = festivals.filter {
                    Instant.parse(it.date_fin).isBefore(today)
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FestivalHomeUiState()
        )
}