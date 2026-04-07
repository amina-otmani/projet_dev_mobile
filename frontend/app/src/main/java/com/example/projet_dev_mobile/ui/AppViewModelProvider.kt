package com.example.projet_dev_mobile.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.projet_dev_mobile.FestivalApplication
import com.example.projet_dev_mobile.ui.screens.admin.AdminViewModel
import com.example.projet_dev_mobile.ui.screens.dashboard.FestivalDetailsViewModel
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeurJeuxViewModel
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeursViewModel
import com.example.projet_dev_mobile.ui.screens.festival.FestivalEntryViewModel
import com.example.projet_dev_mobile.ui.screens.home.FestivalHomeViewModel
import com.example.projet_dev_mobile.ui.screens.jeux.JeuxViewModel
import com.example.projet_dev_mobile.ui.screens.reservations.ReservationFormViewModel
import com.example.projet_dev_mobile.ui.screens.reservations.ReservationViewModel
import com.example.projet_dev_mobile.ui.screens.workflow.WorkflowViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            FestivalHomeViewModel(festivalApplication().container.festivalsRepository)
        }

        initializer {
            FestivalEntryViewModel(festivalApplication().container.festivalsRepository)
        }

        initializer {
            EditeursViewModel(festivalApplication().container.editeursRepository)
        }

        initializer {
            JeuxViewModel(festivalApplication().container.jeuxRepository)
        }

        initializer {
            ReservationViewModel(
                festivalApplication().container.reservationsRepository
            )
        }

        initializer {
            AdminViewModel(
                festivalApplication().container.adminRepository
            )
        }

        initializer {
            ReservationFormViewModel(
                repository = festivalApplication().container.reservationsRepository,
                editeursRepository = festivalApplication().container.editeursRepository,
                jeuxRepository = festivalApplication().container.jeuxRepository,
                festivalsRepository = festivalApplication().container.festivalsRepository
            )
        }
    }

    fun festivalDetailsFactory(festivalId: Int) = viewModelFactory {
        initializer {
            FestivalDetailsViewModel(
                festivalId = festivalId,
                festivalsRepository = festivalApplication().container.festivalsRepository,
                tokenManager = festivalApplication().container.tokenManager
            )
        }
    }

    fun editeurJeuxFactory(editeurId: Int, editeurNom: String) = viewModelFactory {
        initializer {
            EditeurJeuxViewModel(
                editeurId = editeurId,
                editeurNom = editeurNom,
                jeuxRepository = festivalApplication().container.jeuxRepository
            )
        }
    }

    fun workflowFactory(festivalId: Int) = viewModelFactory {
        initializer {
            WorkflowViewModel(
                festivalId = festivalId,
                suiviRepository = festivalApplication().container.suiviRepository
            )
        }
    }
}

fun CreationExtras.festivalApplication(): FestivalApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FestivalApplication)