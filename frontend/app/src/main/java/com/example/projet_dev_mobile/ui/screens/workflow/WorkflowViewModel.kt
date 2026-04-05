package com.example.projet_dev_mobile.ui.screens.workflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        _uiState.update { it.copy(isLoading = true, isError = false) }

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
                        isError = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    fun onEtatFilterChange(etat: String) {
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
                _uiState.update { it.copy(isError = true) }
            }
        }
    }

    fun changerEtat(editeurId: Int, nouvelEtat: String) {
        viewModelScope.launch {
            val success = suiviRepository.updateSuivi(
                festivalId = festivalId,
                editeurId = editeurId,
                etat = nouvelEtat
            )

            if (success) {
                fetchSuivis()
            } else {
                _uiState.update { it.copy(isError = true) }
            }
        }
    }

    private fun applyFilters(
        suivis: List<SuiviDto>,
        etatFilter: String,
        searchQuery: String
    ): List<SuiviDto> {
        val normalizedQuery = searchQuery.trim()

        return suivis.filter { suivi ->
            val etatMatch = etatFilter == ETAT_TOUS || suivi.etat == etatFilter
            val searchMatch = normalizedQuery.isBlank() ||
                suivi.editeur_nom.contains(normalizedQuery, ignoreCase = true)

            etatMatch && searchMatch
        }
    }

    companion object {
        const val ETAT_TOUS = "TOUS"

        const val ETAT_PAS_CONTACTE = "PAS_CONTACTE"
        const val ETAT_CONTACTE = "CONTACTE"
        const val ETAT_DISCUSSION = "DISCUSSION"
        const val ETAT_REFUS = "REFUS"
        const val ETAT_CONFIRME = "CONFIRME"

        val etatsDisponibles = listOf(
            ETAT_TOUS,
            ETAT_PAS_CONTACTE,
            ETAT_CONTACTE,
            ETAT_DISCUSSION,
            ETAT_REFUS,
            ETAT_CONFIRME
        )

        val etatLabels = mapOf(
            ETAT_TOUS to "Tous",
            ETAT_PAS_CONTACTE to "Pas contacte",
            ETAT_CONTACTE to "Contacte",
            ETAT_DISCUSSION to "Discussion en cours",
            ETAT_REFUS to "Refus / Absent",
            ETAT_CONFIRME to "Confirme / Present"
        )
    }
}
