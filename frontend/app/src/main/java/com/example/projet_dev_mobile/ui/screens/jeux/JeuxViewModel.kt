package com.example.projet_dev_mobile.ui.screens.jeux

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.network.dto.JeuDto
import com.example.projet_dev_mobile.data.repository.JeuxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JeuxViewModel(
    private val jeuxRepository: JeuxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JeuxUiState())
    val uiState: StateFlow<JeuxUiState> = _uiState.asStateFlow()

    init {
        fetchJeux()
    }

    fun fetchJeux() {
        _uiState.update { it.copy(isLoading = true, isError = false) }

        viewModelScope.launch {
            val jeux = jeuxRepository.getAllJeux()
            if (jeux != null) {
                val categories = jeux.map { it.typeG }.distinct().sorted()

                _uiState.update {
                    it.copy(
                        jeux = jeux,
                        filteredJeux = applyFilters(
                            jeux = jeux,
                            query = it.searchQuery,
                            category = it.selectedCategory
                        ),
                        availableCategories = categories,
                        isLoading = false,
                        isError = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredJeux = applyFilters(
                    jeux = it.jeux,
                    query = query,
                    category = it.selectedCategory
                )
            )
        }
    }

    fun onCategoryChange(category: String?) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                filteredJeux = applyFilters(
                    jeux = it.jeux,
                    query = it.searchQuery,
                    category = category
                )
            )
        }
    }

    private fun applyFilters(
        jeux: List<JeuDto>,
        query: String,
        category: String?
    ): List<JeuDto> {
        val normalizedQuery = query.trim()

        return jeux.filter { jeu ->
            val matchQuery = normalizedQuery.isBlank() ||
                jeu.nom.contains(normalizedQuery, ignoreCase = true) ||
                jeu.nom_editeur.contains(normalizedQuery, ignoreCase = true) ||
                jeu.typeG.contains(normalizedQuery, ignoreCase = true)

            val matchCategory = category.isNullOrBlank() || jeu.typeG == category

            matchQuery && matchCategory
        }
    }
}
