package com.example.projet_dev_mobile.ui.screens.jeux

import android.R
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.projet_dev_mobile.data.entity.enum.GameType
import com.example.projet_dev_mobile.data.network.dto.AuteurDto
import com.example.projet_dev_mobile.data.network.dto.JeuDto
import com.example.projet_dev_mobile.data.repository.JeuxRepository

data class JeuUiState(
    val jeuDetails: JeuDetails = JeuDetails(),
    val isEntryValid: Boolean = false
)

data class JeuDetails(
    val id: Int = 0,
    val nom: String = "",
    val typeG: GameType = GameType.AVENTURE,
    val age_min: Int = 0,
    val age_max: Int = 99,
    val editeur_id: Int = 0,
    val nom_editeur: String = "",
    val auteurs: List<AuteurDto> = emptyList()
)

fun JeuDetails.toJeu(): JeuDto = JeuDto(
    id = id,
    nom = nom,
    typeG = typeG,
    age_min = age_min,
    age_max = age_max,
    editeur_id = editeur_id,
    nom_editeur = nom_editeur,
    auteurs = auteurs
)

class JeuEntryViewModel(private val jeuxRepository: JeuxRepository) : ViewModel() {
    var jeuUiState by mutableStateOf(JeuUiState())
        private set

    fun updateUiState(details: JeuDetails) {
        jeuUiState = JeuUiState(
            jeuDetails = details,
            isEntryValid = validateInput(details)
        )
    }

    private fun validateInput(details: JeuDetails): Boolean {
        return details.nom.isNotBlank() &&
                details.typeG != null &&
                details.editeur_id > 0 &&
                details.age_min >= 0 &&
                details.age_max > details.age_min
    }

    suspend fun saveJeu(): Boolean {
        if (validateInput(jeuUiState.jeuDetails)) {
            val jeu = jeuUiState.jeuDetails.toJeu()
            Log.d("JeuEntryViewModel", "Tentative d'ajout : $jeu")
            val result = jeuxRepository.addJeu(jeu)
            Log.d("JeuEntryViewModel", "Résultat : $result")
            return result
        }
        Log.d("JeuEntryViewModel", "Validation échouée : ${jeuUiState.jeuDetails}")
        return false
    }
}