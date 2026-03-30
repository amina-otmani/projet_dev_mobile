package com.example.projet_dev_mobile.ui.screens.jeux

import com.example.projet_dev_mobile.data.network.dto.JeuDto
import com.example.projet_dev_mobile.utils.matchesSearchQuery

data class JeuxUiState(
    val jeux: List<JeuDto> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val availableCategories: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false
) {
    val filteredJeux: List<JeuDto>
        get() = jeux.filter { jeu ->
            val matchQuery = searchQuery.isBlank() ||
                jeu.nom.matchesSearchQuery(searchQuery)

            val matchCategory = selectedCategory.isNullOrBlank() ||
                    jeu.typeG == selectedCategory

            matchQuery && matchCategory
        }
}
