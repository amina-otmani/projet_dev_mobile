package com.example.projet_dev_mobile.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.local.TokenManager
import com.example.projet_dev_mobile.data.network.RetrofitInstance
import com.example.projet_dev_mobile.data.network.dto.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val tokenManager = TokenManager(application)

    fun login(email: String, mdp: String) {
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val api = RetrofitInstance.getApiService(getApplication())
                val request = LoginRequest(login = email, password = mdp)
                val response = api.login(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse != null) {
                        // On sauvegarde les tokens et les infos utilisateur
                        tokenManager.saveTokens(loginResponse.token, loginResponse.refreshToken)
                        tokenManager.saveRole(loginResponse.user.role.name)
                        tokenManager.saveLogin(loginResponse.user.login)
                    }

                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.Error("Identifiants incorrects")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Erreur de connexion : ${e.localizedMessage}")
            }
        }
    }
}
