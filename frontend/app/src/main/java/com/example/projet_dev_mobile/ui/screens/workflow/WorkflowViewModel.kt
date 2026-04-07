package com.example.projet_dev_mobile.ui.screens.workflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.entity.enum.EtatSuivi
import com.example.projet_dev_mobile.data.network.dto.SuiviDto
import com.example.projet_dev_mobile.data.repository.SuiviRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkflowViewModel(
    private val festivalId: Int,
    private val suiviRepository: SuiviRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkflowUiState())
    val uiState: StateFlow<WorkflowUiState> = _uiState.asStateFlow()

    init {
        fetchSuivis()
    }

    fun fetchSuivis() {
        _uiState.update { it.copy(isLoading = true, hasLoadError = false) }

        viewModelScope.launch {
            val suivis = suiviRepository.getSuivisByFestival(festivalId)
            if (suivis != null) {
                _uiState.update {
                    it.copy(
                        suivis = suivis,
                        filteredSuivis = applyFilters(
                            suivis = suivis,
                            etatFilter = it.selectedEtatFilter,
                            searchQuery = it.searchQuery
                        ),
                        isLoading = false,
                        hasLoadError = false,
                        hasMutationError = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, hasLoadError = true) }
            }
        }
    }

    fun onEtatFilterChange(etat: EtatSuivi?) {
        _uiState.update {
            it.copy(
                selectedEtatFilter = etat,
                filteredSuivis = applyFilters(
                    suivis = it.suivis,
                    etatFilter = etat,
                    searchQuery = it.searchQuery
                )
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredSuivis = applyFilters(
                    suivis = it.suivis,
                    etatFilter = it.selectedEtatFilter,
                    searchQuery = query
                )
            )
        }
    }

    fun prendreContact(editeurId: Int) {
        viewModelScope.launch {
            val success = suiviRepository.prendreContact(
                festivalId = festivalId,
                editeurId = editeurId
            )

            if (success) {
                fetchSuivis()
            } else {
                _uiState.update { it.copy(hasMutationError = true) }
            }
        }
    }

    fun changerEtat(editeurId: Int, nouvelEtat: EtatSuivi) {
        viewModelScope.launch {
            val success = suiviRepository.updateSuivi(
                festivalId = festivalId,
                editeurId = editeurId,
                etat = nouvelEtat
            )

            if (success) {
                fetchSuivis()
            } else {
                _uiState.update { it.copy(hasMutationError = true) }
            }
        }
    }

    fun clearMutationError() {
        _uiState.update { it.copy(hasMutationError = false) }
    }

    private fun applyFilters(
        suivis: List<SuiviDto>,
        etatFilter: EtatSuivi?,
        searchQuery: String
    ): List<SuiviDto> {
        val normalizedQuery = searchQuery.trim()

        return suivis.filter { suivi ->
            val etatMatch = etatFilter == null || suivi.etat == etatFilter.name
            val searchMatch = normalizedQuery.isBlank() ||
                suivi.editeur_nom.contains(normalizedQuery, ignoreCase = true)

            etatMatch && searchMatch
        }
    }

    companion object {
        val etatsDisponibles: List<EtatSuivi> = EtatSuivi.entries
    }
}
