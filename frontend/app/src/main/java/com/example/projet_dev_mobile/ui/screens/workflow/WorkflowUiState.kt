package com.example.projet_dev_mobile.ui.screens.workflow

import com.example.projet_dev_mobile.data.network.dto.SuiviDto

data class WorkflowUiState(
    val suivis: List<SuiviDto> = emptyList(),
    val filteredSuivis: List<SuiviDto> = emptyList(),
    val searchQuery: String = "",
    val selectedEtatFilter: String = WorkflowViewModel.ETAT_TOUS,
    val isLoading: Boolean = true,
    val isError: Boolean = false
)
