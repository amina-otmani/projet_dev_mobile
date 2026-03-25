package com.example.projet_dev_mobile.ui.screens.editeurs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.repository.JeuxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditeurJeuxViewModel(
    private val editeurId: Int,
    editeurNom: String,
    private val jeuxRepository: JeuxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditeurJeuxUiState(editeurNom = editeurNom))
    val uiState: StateFlow<EditeurJeuxUiState> = _uiState.asStateFlow()

    init {
        fetchJeuxEditeur()
    }

    fun fetchJeuxEditeur() {
        _uiState.update { it.copy(isLoading = true, isError = false) }

        viewModelScope.launch {
            val jeux = jeuxRepository.getAllJeux()
            if (jeux != null) {
                _uiState.update {
                    it.copy(
                        jeux = jeux.filter { jeu -> jeu.editeur_id == editeurId },
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
