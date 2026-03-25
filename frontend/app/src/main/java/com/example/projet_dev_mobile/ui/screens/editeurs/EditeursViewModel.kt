package com.example.projet_dev_mobile.ui.screens.editeurs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.repository.EditeursRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditeursViewModel(
    private val editeursRepository: EditeursRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditeursUiState())
    val uiState: StateFlow<EditeursUiState> = _uiState.asStateFlow()

    init {
        fetchEditeurs()
    }

    fun fetchEditeurs() {
        _uiState.update { it.copy(isLoading = true, isError = false) }

        viewModelScope.launch {
            val editeurs = editeursRepository.getAllEditeurs()
            if (editeurs != null) {
                _uiState.update {
                    it.copy(
                        editeurs = editeurs,
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
