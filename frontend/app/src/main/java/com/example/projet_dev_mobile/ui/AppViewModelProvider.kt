package com.example.projet_dev_mobile.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.projet_dev_mobile.FestivalApplication
import com.example.projet_dev_mobile.ui.screens.admin.AdminViewModel
import com.example.projet_dev_mobile.ui.screens.dashboard.FestivalDetailsViewModel
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeurEntryViewModel
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeurJeuxViewModel
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeursViewModel
import com.example.projet_dev_mobile.ui.screens.festival.FestivalEntryViewModel
import com.example.projet_dev_mobile.ui.screens.home.FestivalHomeViewModel
import com.example.projet_dev_mobile.ui.screens.jeux.JeuDetailsViewModel
import com.example.projet_dev_mobile.ui.screens.jeux.JeuEntryViewModel
import com.example.projet_dev_mobile.ui.screens.jeux.JeuxViewModel
import com.example.projet_dev_mobile.ui.screens.reservations.ReservationFormViewModel
import com.example.projet_dev_mobile.ui.screens.reservations.ReservationViewModel
import com.example.projet_dev_mobile.ui.screens.workflow.WorkflowViewModel

object AppViewModelProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {
        // La "recette" pour créer le FestivalHomeViewModel
        initializer {
            FestivalHomeViewModel(
                // On va chercher l'instance unique du repository dans l'application
                festivalApplication().container.festivalsRepository
            )
        }

        initializer {
            FestivalEntryViewModel(
                festivalApplication().container.festivalsRepository
            )
        }

        initializer {
            JeuEntryViewModel(
                festivalApplication().container.jeuxRepository,
                festivalApplication().container.editeursRepository
            )
        }

        initializer {
            EditeurEntryViewModel(
                festivalApplication().container.editeursRepository
            )
        }


        initializer {
            EditeursViewModel(
                festivalApplication().container.editeursRepository,
                festivalApplication().container.jeuxRepository,
                festivalApplication().container.reservationsRepository
            )
        }

        initializer {
            JeuxViewModel(
                festivalApplication().container.jeuxRepository,
                festivalApplication().container.editeursRepository
            )
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

    fun editeurJeuxFactory(editeurId: Int) = viewModelFactory {
        initializer {
            EditeurJeuxViewModel(
                editeurId = editeurId,
                editeurRepository = festivalApplication().container.editeursRepository,
                jeuxRepository = festivalApplication().container.jeuxRepository
            )
        }
    }

    fun jeuDetailsFactory(jeuId: Int) = viewModelFactory {
        initializer {
            JeuDetailsViewModel(
                jeuId = jeuId,
                jeuxRepository = festivalApplication().container.jeuxRepository,
                editeurRepository = festivalApplication().container.editeursRepository
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

    fun editeursForFestivalFactory(festivalId: Int) = viewModelFactory {
        initializer {
            EditeursViewModel(
                editeursRepository = festivalApplication().container.editeursRepository,
                jeuxRepository = festivalApplication().container.jeuxRepository,
                reservationsRepository = festivalApplication().container.reservationsRepository,
                festivalId = festivalId
            )
        }
    }

    fun jeuxForFestivalFactory(festivalId: Int) = viewModelFactory {
        initializer {
            JeuxViewModel(
                jeuxRepository = festivalApplication().container.jeuxRepository,
                editeursRepository = festivalApplication().container.editeursRepository,
                reservationsRepository = festivalApplication().container.reservationsRepository,
                festivalId = festivalId
            )
        }
    }
}

// Petite fonction utilitaire pour accéder facilement au container de données
fun CreationExtras.festivalApplication(): FestivalApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FestivalApplication)