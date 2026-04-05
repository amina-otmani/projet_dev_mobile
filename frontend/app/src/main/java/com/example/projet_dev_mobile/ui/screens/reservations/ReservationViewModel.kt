package com.example.projet_dev_mobile.ui.screens.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.repository.ReservationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReservationViewModel(
    private val repository: ReservationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()


    fun loadReservations(festivalId: Int, isOrganisateur: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.getReservations(festivalId, isOrganisateur)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, reservations = result.getOrDefault(emptyList()))
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message ?: "Erreur inconnue")
                }
            }
        }
    }

    fun deleteReservation(id: Int, festivalId: Int, isOrganisateur: Boolean) {
        viewModelScope.launch {
            val result = repository.deleteReservation(id)
            if (result.isSuccess) {
                loadReservations(festivalId, isOrganisateur)
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Échec de la suppression : ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }
}