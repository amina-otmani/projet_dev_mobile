package com.example.projet_dev_mobile.ui.screens.editeurs

import com.example.projet_dev_mobile.data.network.dto.EditeurDto
import com.example.projet_dev_mobile.utils.matchesSearchQuery

data class EditeursUiState(
    val editeurs: List<EditeurDto> = emptyList(),
    val jeuxParEditeur: Map<Int, Int> = emptyMap(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isError: Boolean = false
) {
    val filteredEditeurs: List<EditeurDto>
        get() = if (searchQuery.isBlank()) {
            editeurs
        } else {
            editeurs.filter { it.nom.matchesSearchQuery(searchQuery)}
        }
}
