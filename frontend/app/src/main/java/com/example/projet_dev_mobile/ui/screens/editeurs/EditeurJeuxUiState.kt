package com.example.projet_dev_mobile.ui.screens.editeurs

import com.example.projet_dev_mobile.data.network.dto.JeuDto

data class EditeurJeuxUiState(
    val editeurNom: String,
    val jeux: List<JeuDto> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false
)
