package com.example.projet_dev_mobile.ui.screens.editeurs

import com.example.projet_dev_mobile.data.network.dto.EditeurDto

data class EditeursUiState(
    val editeurs: List<EditeurDto> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false
)
