package com.example.projet_dev_mobile.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.network.RetrofitInstance
import com.example.projet_dev_mobile.data.network.dto.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// On utilise AndroidViewModel pour avoir accès au Contexte (nécessaire pour TokenManager/Retrofit)
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // L'état initial est "Idle" (En attente)
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, mdp: String) {
        // 1. On passe l'interface en mode "Chargement" (le bouton disparaît, la roue tourne)
        _uiState.value = LoginUiState.Loading

        // 2. On lance l'appel réseau dans une Coroutine (en arrière-plan)
        viewModelScope.launch {
            try {
                // Récupération de ton API configurée plus tôt
                val api = RetrofitInstance.getApiService(getApplication())

                // Création de l'objet à envoyer au backend
                val request = LoginRequest(login = email, password = mdp)

                // Appel réseau POST
                val response = api.login(request)

                if (response.isSuccessful) {
                    // Si code 200 OK -> Succès ! Le LoginScreen va basculer sur Home
                    _uiState.value = LoginUiState.Success
                } else {
                    // Si erreur (ex: 401 Unauthorized) -> Affichage du message d'erreur
                    _uiState.value = LoginUiState.Error("Identifiants incorrects")
                }
            } catch (e: Exception) {
                // Si le serveur est éteint, Docker planté, ou pas de Wifi
                _uiState.value = LoginUiState.Error("Erreur de connexion : ${e.localizedMessage}")
            }
        }
    }
}
