package com.example.projet_dev_mobile.ui.screens.editeurs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.repository.EditeursRepository
import com.example.projet_dev_mobile.data.repository.JeuxRepository
import com.example.projet_dev_mobile.data.repository.ReservationsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.emptySet

class EditeursViewModel(
    private val editeursRepository: EditeursRepository,
    private val jeuxRepository: JeuxRepository,
    private val reservationsRepository: ReservationsRepository,
    private val festivalId: Int? = null
    ) : ViewModel() {

    private val _uiState = MutableStateFlow(EditeursUiState())
    val uiState: StateFlow<EditeursUiState> = _uiState.asStateFlow()

    init {
        fetchEditeurs()
    }

    fun fetchEditeurs() {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            val tousLesEditeurs = editeursRepository.getAllEditeurs()

            val editeursFiltres = if (festivalId != null) {
                val reservations = reservationsRepository.getReservations(festivalId, false).getOrNull()
                val editeurIdsAvecResa = reservations
                    ?.mapNotNull { it.editeur_id }
                    ?.toSet()
                    ?: emptySet()

                tousLesEditeurs?.filter { it.id in editeurIdsAvecResa }
            } else {
                tousLesEditeurs
            }

            if (editeursFiltres != null) {
                _uiState.update { it.copy(editeurs = editeursFiltres, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
