package com.example.projet_dev_mobile.ui.screens.workflow

import com.example.projet_dev_mobile.data.entity.enum.EtatSuivi
import com.example.projet_dev_mobile.data.network.dto.SuiviDto

data class WorkflowUiState(
    val suivis: List<SuiviDto> = emptyList(),
    val filteredSuivis: List<SuiviDto> = emptyList(),
    val searchQuery: String = "",
    val selectedEtatFilter: EtatSuivi? = null,
    val isLoading: Boolean = true,
    val hasLoadError: Boolean = false,
    val hasMutationError: Boolean = false
)
