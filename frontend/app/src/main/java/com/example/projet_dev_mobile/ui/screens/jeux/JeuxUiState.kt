package com.example.projet_dev_mobile.ui.screens.jeux

import com.example.projet_dev_mobile.data.network.dto.JeuDto

data class JeuxUiState(
    val jeux: List<JeuDto> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false
)
