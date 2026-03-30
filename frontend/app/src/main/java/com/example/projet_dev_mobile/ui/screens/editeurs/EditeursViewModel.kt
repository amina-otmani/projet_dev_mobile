package com.example.projet_dev_mobile.ui.screens.editeurs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.repository.EditeursRepository
import com.example.projet_dev_mobile.data.repository.JeuxRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditeursViewModel(
    private val editeursRepository: EditeursRepository,
    private val jeuxRepository: JeuxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditeursUiState())
    val uiState: StateFlow<EditeursUiState> = _uiState.asStateFlow()

    init {
        fetchEditeurs()
    }

    fun fetchEditeurs() {
        _uiState.update { it.copy(isLoading = true, isError = false) }

        viewModelScope.launch {
            val editeursDeferred = async { editeursRepository.getAllEditeurs() }
            val jeuxDeferred = async { jeuxRepository.getAllJeux() }

            val editeurs = editeursDeferred.await()
            val jeux = jeuxDeferred.await()

            if (editeurs != null) {
                val jeuxParEditeur = jeux
                    ?.groupingBy { it.editeur_id }
                    ?.eachCount()
                    ?: emptyMap()

                _uiState.update {
                    it.copy(
                        editeurs = editeurs,
                        filteredEditeurs = applyFilter(editeurs, it.searchQuery),
                        jeuxParEditeur = jeuxParEditeur,
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
                filteredEditeurs = applyFilter(it.editeurs, query)
            )
        }
    }

    private fun applyFilter(editeurs: List<com.example.projet_dev_mobile.data.network.dto.EditeurDto>, query: String): List<com.example.projet_dev_mobile.data.network.dto.EditeurDto> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return editeurs

        return editeurs.filter { editeur ->
            editeur.nom.contains(normalizedQuery, ignoreCase = true) ||
                editeur.contacts.any { contact ->
                    contact.nom.contains(normalizedQuery, ignoreCase = true) ||
                        contact.prenom.contains(normalizedQuery, ignoreCase = true) ||
                        contact.email.contains(normalizedQuery, ignoreCase = true)
                }
        }
    }
}
