package com.example.projet_dev_mobile.ui

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

object AppViewModelProvider {
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
                festivalApplication().container.jeuxRepository
            )
        }

        initializer {
            JeuxViewModel(
                festivalApplication().container.jeuxRepository,
                festivalApplication().container.editeursRepository
            )
        }

        initializer {
            AdminViewModel(
                festivalApplication().container.adminRepository
            )
        }

        /*
        initializer {
            FestivalDetailsViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                festivalsRepository = festivalApplication().container.festivalsRepository
            )
        }

         */
    }
    fun festivalDetailsFactory(festivalId: Int) = viewModelFactory {
        initializer {
            FestivalDetailsViewModel(
                festivalId = festivalId,
                festivalsRepository = festivalApplication().container.festivalsRepository
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
}

// Petite fonction utilitaire pour accéder facilement au container de données
fun CreationExtras.festivalApplication(): FestivalApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FestivalApplication)