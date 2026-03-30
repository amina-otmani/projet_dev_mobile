package com.example.projet_dev_mobile.ui.screens.jeux

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.repository.JeuxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JeuDetailsViewModel(
    private val jeuId: Int,
    private val jeuxRepository: JeuxRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(JeuDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init { fetchJeu() }

    private fun fetchJeu() {
        viewModelScope.launch {
            val jeux = jeuxRepository.getAllJeux()
            val jeu = jeux?.find { it.id == jeuId }

            _uiState.value = if (jeu != null)
                JeuDetailsUiState(jeu = jeu, isLoading = false)
            else
                JeuDetailsUiState(isError = true, isLoading = false)
        }
    }
}