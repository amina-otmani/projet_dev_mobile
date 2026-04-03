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

class EditeurJeuxViewModel(
    private val editeurId: Int,
    private val editeurRepository: EditeursRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditeurJeuxUiState())
    val uiState: StateFlow<EditeurJeuxUiState> = _uiState.asStateFlow()

    init {
        fetchJeuxEditeur()
    }

    fun fetchJeuxEditeur() {
        _uiState.update { it.copy(isLoading = true, isError = false) }

        viewModelScope.launch {
            val editeurDeffered = async { editeurRepository.getEditeurById(editeurId) }
            val jeuxDeffered = async { editeurRepository.getJeuxByEditeur(editeurId) }

            val editeur = editeurDeffered.await()
            val jeux = jeuxDeffered.await()

            if (editeur != null && jeux != null) {
                _uiState.update {
                    it.copy(
                        editeurNom = editeur.nom,
                        jeux = jeux,
                        isLoading = false,
                        isError = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }
}
