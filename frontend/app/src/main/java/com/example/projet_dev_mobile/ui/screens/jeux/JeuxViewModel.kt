package com.example.projet_dev_mobile.ui.screens.jeux

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.entity.enum.GameType
import com.example.projet_dev_mobile.data.repository.EditeursRepository
import com.example.projet_dev_mobile.data.repository.JeuxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JeuxViewModel(
    private val jeuxRepository: JeuxRepository,
    private val editeursRepository: EditeursRepository
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
                val jeuxAvecEditeur = jeux.map { jeu ->
                    val editeur = editeursRepository.getEditeurById(jeu.editeur_id)
                    jeu.copy(editeur = editeur)
                }
                _uiState.update {
                    it.copy(
                        jeux = jeuxAvecEditeur,
                        availableCategories = jeuxAvecEditeur.map { it.typeG }.distinct().sorted(),
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategoryChange(category: GameType?) {
        _uiState.update { it.copy(selectedCategory = category) }

    }
}
