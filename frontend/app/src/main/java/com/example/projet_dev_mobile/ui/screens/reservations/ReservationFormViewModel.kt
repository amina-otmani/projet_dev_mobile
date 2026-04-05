package com.example.projet_dev_mobile.ui.screens.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.entity.ZoneTarifaire
import com.example.projet_dev_mobile.data.entity.enum.TypeReservant
import com.example.projet_dev_mobile.data.network.dto.EditeurDto
import com.example.projet_dev_mobile.data.network.dto.JeuDto
import com.example.projet_dev_mobile.data.network.dto.JeuReserveDto
import com.example.projet_dev_mobile.data.network.dto.LigneReservationDto
import com.example.projet_dev_mobile.data.network.dto.ReservationDto
import com.example.projet_dev_mobile.data.repository.EditeursRepository
import com.example.projet_dev_mobile.data.repository.FestivalsRepository
import com.example.projet_dev_mobile.data.repository.JeuxRepository
import com.example.projet_dev_mobile.data.repository.ReservationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReservationFormViewModel(
    private val repository: ReservationsRepository,
    private val editeursRepository: EditeursRepository,
    private val jeuxRepository: JeuxRepository,
    private val festivalsRepository: FestivalsRepository
) : ViewModel() {

    // --- NAVIGATION DU WIZARD ---
    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    // --- DONNÉES DE RÉFÉRENCE (Pour alimenter les menus déroulants) ---
    private val _editeurs = MutableStateFlow<List<EditeurDto>>(emptyList())
    val editeurs = _editeurs.asStateFlow()

    private val _zonesTarifaires = MutableStateFlow<List<ZoneTarifaire>>(emptyList())
    val zonesTarifaires = _zonesTarifaires.asStateFlow()

    private val _jeuxDisponibles = MutableStateFlow<List<JeuDto>>(emptyList())
    val jeuxDisponibles = _jeuxDisponibles.asStateFlow()

    // --- ÉTAT DU FORMULAIRE EN COURS ---

    // 1. Infos générales
    private val _reservationState = MutableStateFlow(ReservationDto(
        festival_id = 0,
        type = TypeReservant.AUTRE
    ))
    val reservationState: StateFlow<ReservationDto> = _reservationState.asStateFlow()

    // 2. Les lignes tarifaires (La facturation)
    private val _lignes = MutableStateFlow<List<LigneReservationDto>>(emptyList())
    val lignes = _lignes.asStateFlow()

    // 3. Les jeux réservés (La logistique / Placement)
    private val _lignesJeux = MutableStateFlow<List<JeuReserveDto>>(emptyList())
    val lignesJeux = _lignesJeux.asStateFlow()

    // --- INITIALISATION ---
    fun initForm(festivalId: Int, reservationId: Int?) {
        _currentStep.value = 1
        loadInitialData(festivalId)

        if (reservationId != null) {
            fetchReservationDetails(reservationId)
        } else {
            _reservationState.value = ReservationDto(festival_id = festivalId, type = TypeReservant.AUTRE)
            _lignes.value = emptyList()
            _lignesJeux.value = emptyList()
        }
    }

    private fun fetchReservationDetails(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getReservationDetails(id)
                if (response.isSuccess) {
                    val details = response.getOrNull()
                    if (details != null) {
                        _reservationState.value = details.reservation
                        _lignes.value = details.lignes
                        _lignesJeux.value = details.jeux
                    }
                }
            } catch (e: Exception) {
                // Gérer l'erreur
            }
        }
    }

    private fun loadInitialData(festivalId: Int) {
        viewModelScope.launch {
            // 1. Charger les éditeurs
            _editeurs.value = editeursRepository.getAllEditeurs() ?: emptyList()

            // 2. Charger les zones tarifaires du festival actuel
            val festival = festivalsRepository.getFestivalById(festivalId)
            if (festival != null) {
                _zonesTarifaires.value = festival.zonesTarifaires
            }

            // 3. Charger les jeux disponibles
            _jeuxDisponibles.value = jeuxRepository.getAllJeux() ?: emptyList()
        }
    }

    // --- NAVIGATION ---
    fun nextStep() {
        if (_currentStep.value < 3) _currentStep.value += 1
    }

    fun previousStep() {
        if (_currentStep.value > 1) _currentStep.value -= 1
    }

    // --- MISE À JOUR INFOS GÉNÉRALES ---
    fun updateNomReservant(nom: String) {
        _reservationState.update { it.copy(autre_nom_reservant = nom) }
    }

    fun updateTypeReservant(type: TypeReservant) {
        _reservationState.update { it.copy(type = type) }
    }

    fun updateEditeurId(editeurId: Int?) {
        _reservationState.update { it.copy(editeur_id = editeurId) }
    }

    // --- GESTION DES LIGNES TARIFAIRES (Step 1) ---
    fun addLigneTarifaire() {
        val newLine = LigneReservationDto(zone_tarifaire_id = 0, quantite = 1, prix_moment_reservation = 0.0, type_emplacement = "TABLE")
        _lignes.update { it + newLine }
    }

    fun updateLigneTarifaire(index: Int, updatedLigne: LigneReservationDto) {
        _lignes.update { list ->
            val mutableList = list.toMutableList()
            if (index in mutableList.indices) {
                mutableList[index] = updatedLigne
            }
            mutableList
        }
    }

    fun removeLigneTarifaire(index: Int) {
        _lignes.update { list -> list.filterIndexed { i, _ -> i != index } }
    }

    // --- GESTION DES JEUX (Step 2 & 3) ---
    fun addLigneJeu() {
        val newJeu = JeuReserveDto(jeu_id = 0, nb_exemplaires = 1, tables_occupees = 1)
        _lignesJeux.update { it + newJeu }
    }

    fun updateLigneJeu(index: Int, updatedJeu: JeuReserveDto) {
        _lignesJeux.update { list ->
            val mutableList = list.toMutableList()
            if (index in mutableList.indices) {
                mutableList[index] = updatedJeu
            }
            mutableList
        }
    }

    fun removeLigneJeu(index: Int) {
        _lignesJeux.update { list -> list.filterIndexed { i, _ -> i != index } }
    }

    // --- SAUVEGARDE FINALE ---
    fun submitReservation(onSuccess: () -> Unit, onError: (String) -> Unit) {

        val finalReservationToSend = _reservationState.value.copy(
            lignes = _lignes.value,
            jeux = _lignesJeux.value
        )

        viewModelScope.launch {
            val result = repository.saveReservation(finalReservationToSend)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Erreur lors de la sauvegarde")
            }
        }
    }
}