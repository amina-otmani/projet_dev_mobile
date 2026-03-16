package com.example.projet_dev_mobile.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.projet_dev_mobile.FestivalApplication
import com.example.projet_dev_mobile.ui.screens.festival.FestivalHomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // La "recette" pour créer le FestivalHomeViewModel
        initializer {
            FestivalHomeViewModel(
                // On va chercher l'instance unique du repository dans l'application
                festivalApplication().container.festivalsRepository
            )
        }

        // Tu ajouteras ici les recettes pour tes autres ViewModels plus tard
    }
}

// Petite fonction utilitaire pour accéder facilement au container de données
fun CreationExtras.festivalApplication(): FestivalApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FestivalApplication)