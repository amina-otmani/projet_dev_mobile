package com.example.projet_dev_mobile.ui.screens.festival

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.entity.Festival
import com.example.projet_dev_mobile.data.repository.FestivalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FestivalDetailsUiState(
    val festival: Festival? = null,
    val isLoading: Boolean = true,
    val isError: Boolean = false
)

class FestivalDetailsViewModel(
    //savedStateHandle: SavedStateHandle,
    private val festivalId: Int,
    private val festivalsRepository : FestivalsRepository
    ) : ViewModel() {

    private val _uiState = MutableStateFlow(FestivalDetailsUiState())
    val uiState: StateFlow<FestivalDetailsUiState> = _uiState.asStateFlow()

    init {
        fetchFestival()
    }

    private fun fetchFestival() {
        viewModelScope.launch {
            try {
                val result = festivalsRepository.getFestivalById(festivalId)
                if (result != null) {
                    _uiState.value = _uiState.value.copy(festival = result, isLoading = false)
                }
                else {
                    _uiState.value = _uiState.value.copy(isError = true, isLoading = false)
                }
            }
            catch (e: Exception) {
                _uiState.update { it.copy(isError = true, isLoading = false) }
            }
        }
    }
}