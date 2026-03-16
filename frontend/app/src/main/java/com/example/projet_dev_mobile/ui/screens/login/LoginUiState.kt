package com.example.projet_dev_mobile.ui.screens.login

sealed class LoginUiState {
    object Idle : LoginUiState() // État de base (rien ne se passe)
    object Loading : LoginUiState() // Le petit cercle qui tourne
    object Success : LoginUiState() // Connexion réussie !
    data class Error(val message: String) : LoginUiState() // Mot de passe incorrect, etc.
}