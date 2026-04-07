package com.example.projet_dev_mobile.ui.screens.reservations

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
import kotlin.math.floor

class ReservationFormViewModel(
    private val repository: ReservationsRepository,
    private val editeursRepository: EditeursRepository,
    private val jeuxRepository: JeuxRepository,
    private val festivalsRepository: FestivalsRepository
) : ViewModel() {

    // --- CONSTANTES ---
    private val RATIO_M2_TABLE = 4.0

    // --- NAVIGATION DU WIZARD ---
    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    // --- GESTION DES ERREURS ---
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

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
        type = TypeReservant.AUTRE,
        nombre_prises = 0,
        remise_generale = 0.0,
        est_present = true
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
        clearErrorMessage()
        loadInitialData(festivalId)

        if (reservationId != null) {
            fetchReservationDetails(reservationId)
        } else {
            _reservationState.value = ReservationDto(
                festival_id = festivalId,
                type = TypeReservant.AUTRE,
                nombre_prises = 0,
                remise_generale = 0.0,
                est_present = true
            )
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
                    } else {
                        _errorMessage.value = "Les détails de la réservation sont introuvables."
                    }
                } else {
                    _errorMessage.value = response.exceptionOrNull()?.message ?: "Erreur lors de la récupération de la réservation."
                }
            } catch (e: Exception) {
                // Gestion de l'erreur (ex: problème réseau, timeout)
                _errorMessage.value = "Erreur réseau ou inattendue : ${e.localizedMessage}"
            }
        }
    }

    private fun loadInitialData(festivalId: Int) {
        viewModelScope.launch {
            try {
                // 1. Charger les éditeurs
                _editeurs.value = editeursRepository.getAllEditeurs() ?: emptyList()

                // 2. Charger les zones tarifaires du festival actuel
                val festival = festivalsRepository.getFestivalById(festivalId)
                if (festival != null) {
                    _zonesTarifaires.value = festival.zonesTarifaires
                }

                // 3. Charger les jeux disponibles
                _jeuxDisponibles.value = jeuxRepository.getAllJeux() ?: emptyList()
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors du chargement des données de référence : ${e.localizedMessage}"
            }
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

    fun updateNombrePrises(prises: Int) {
        _reservationState.update { it.copy(nombre_prises = prises) }
    }

    fun updateEstPresent(estPresent: Boolean) {
        _reservationState.update { it.copy(est_present = estPresent) }
    }

    fun updatePreferencesTables(prefs: String) {
        _reservationState.update { it.copy(preferences_tables = prefs) }
    }

    // --- LOGIQUE DE VALIDATION DES TABLES ---
    fun getTotalTablesReserved(): Double {
        return _lignes.value.sumOf { ligne ->
            if (ligne.type_emplacement == "M2") {
                ligne.quantite / RATIO_M2_TABLE
            } else {
                ligne.quantite.toDouble()
            }
        }
    }

    fun getTotalTablesUsed(): Double {
        return _lignesJeux.value.sumOf { ligne ->
            ligne.nb_exemplaires * ligne.tables_occupees
        }
    }

    fun hasTablesExceeded(): Boolean {
        return getTotalTablesUsed() > floor(getTotalTablesReserved())
    }

    // --- GESTION DES LIGNES TARIFAIRES (Step 1) ---
    fun addLigneTarifaire() {
        val firstZone = _zonesTarifaires.value.firstOrNull()

        val defaultPrice = firstZone?.prix_table ?: 0.0

        val newLine = LigneReservationDto(
            zone_tarifaire_id = firstZone?.id ?: 0,
            quantite = 1,
            prix_moment_reservation = defaultPrice,
            type_emplacement = "TABLE"
        )
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
        val newJeu = JeuReserveDto(jeu_id = 0, nb_exemplaires = 1, tables_occupees = 1.0)
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
        val lignesValides = _lignes.value.filter { it.zone_tarifaire_id != 0 }
        val jeuxValides = _lignesJeux.value.filter { it.jeu_id != 0 }

        val finalReservationToSend = _reservationState.value.copy(
            lignes = lignesValides,
            jeux = jeuxValides
        )

        viewModelScope.launch {
            try {
                val result = repository.saveReservation(finalReservationToSend)
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    onError(result.exceptionOrNull()?.message ?: "Erreur lors de la sauvegarde")
                }
            } catch (e: Exception) {
                onError("Erreur réseau : ${e.localizedMessage}")
            }
        }
    }
}