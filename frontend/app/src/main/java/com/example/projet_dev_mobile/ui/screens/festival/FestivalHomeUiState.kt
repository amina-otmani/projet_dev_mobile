package com.example.projet_dev_mobile.ui.screens.festival

import com.example.projet_dev_mobile.data.entity.Festival

data class FestivalHomeUiState(
    val activeFestivals: List<Festival> = emptyList(),
    val pastFestivals: List<Festival> = emptyList()
)
