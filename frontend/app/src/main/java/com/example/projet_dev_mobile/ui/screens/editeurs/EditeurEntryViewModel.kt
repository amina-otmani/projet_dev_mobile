package com.example.projet_dev_mobile.ui.screens.editeurs

import android.util.Log
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
    val contacts: List<ContactDetails> = emptyList()
)

data class ContactDetails(
    val id: Int = 0,
    val nom: String = "",
    val prenom: String = "",
    val email: String = "",
    val poste: String = ""
)

fun EditeurDetails.toEditeur(): EditeurDto = EditeurDto(
    id = id,
    nom = nom,
    contacts = contacts.map { contact ->
        EditeurContactDto(
            id = contact.id,
            nom = contact.nom,
            prenom = contact.prenom,
            email = contact.email,
            poste = contact.poste.ifBlank { null }
        )
    }
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

    // --- CONTACTS ---
    fun addEmptyContact() {
        val currentContacts = editeurUiState.editeurDetails.contacts
        updateUiState(editeurUiState.editeurDetails.copy(
            contacts = currentContacts + ContactDetails()
        ))
    }

    fun deleteContact(index: Int) {
        val currentContacts = editeurUiState.editeurDetails.contacts.toMutableList()
        currentContacts.removeAt(index)
        updateUiState(editeurUiState.editeurDetails.copy(contacts = currentContacts))
    }

    private fun validateInput(details: EditeurDetails): Boolean {
        return details.nom.isNotBlank()
    }

    suspend fun saveEditeur(): Boolean {
        if (validateInput(editeurUiState.editeurDetails)) {
            val editeur = editeurUiState.editeurDetails.toEditeur()
            Log.d("EditeurEntryViewModel", "Tentative d'ajout : $editeur")
            val result = editeursRepository.addEditeur(editeur)
            Log.d("EditeurEntryViewModel", "Résultat : $result")
            return result
        }
        Log.d("EditeurEntryViewModel", "Validation échouée : ${editeurUiState.editeurDetails}")
        return false
    }
}