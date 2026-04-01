package com.example.projet_dev_mobile.ui.screens.editeurs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.projet_dev_mobile.data.entity.Personne
import com.example.projet_dev_mobile.data.network.dto.EditeurContactDto
import com.example.projet_dev_mobile.data.network.dto.EditeurDto
import com.example.projet_dev_mobile.data.repository.EditeursRepository

data class EditeurUiState(
    val editeurDetails: EditeurDetails = EditeurDetails(),
    val isEntryValid: Boolean = false
)

data class EditeurDetails(
    val id: Int = 0,
    val nom: String = "",
    val contacts: List<EditeurContactDto> = emptyList()
)

fun EditeurDetails.toEditeur(): EditeurDto = EditeurDto(
    id = id,
    nom = nom,
    contacts = contacts
)

class EditeurEntryViewModel(private val editeursRepository: EditeursRepository) : ViewModel() {
    var editeurUiState by mutableStateOf(EditeurUiState())
            private set

    fun updateUiState(details: EditeurDetails) {
        editeurUiState = EditeurUiState(
            editeurDetails = details,
            isEntryValid = validateInput(details)
        )
    }

    private fun validateInput(details: EditeurDetails): Boolean {
        return details.nom.isNotBlank()
    }

    suspend fun saveEditeur(): Boolean {
        if (validateInput(editeurUiState.editeurDetails)) {
            return editeursRepository.addEditeur(editeurUiState.editeurDetails.toEditeur())
        }
        return false
    }
}