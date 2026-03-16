package com.example.projet_dev_mobile.ui.screens.register

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

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val tokenManager = TokenManager(application)

    fun register(email: String, mdp: String) {
        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            try {
                val api = RetrofitInstance.getApiService(getApplication())
                val request = LoginRequest(login = email, password = mdp)
                val response = api.register(request)

                if (response.isSuccessful) {
                    val user = response.body()?.user

                    // Récupération des tokens comme pour le login
                    val cookies = response.headers().values("Set-Cookie")
                    var accessToken = ""
                    var refreshToken = ""

                    cookies.forEach { cookie ->
                        if (cookie.startsWith("access_token=")) {
                            accessToken = cookie.substringAfter("access_token=").substringBefore(";")
                        } else if (cookie.startsWith("refresh_token=")) {
                            refreshToken = cookie.substringAfter("refresh_token=").substringBefore(";")
                        }
                    }

                    if (accessToken.isNotEmpty() && refreshToken.isNotEmpty()) {
                        tokenManager.saveTokens(accessToken, refreshToken)
                    }

                    if (user != null) {
                        tokenManager.saveRole(user.role)
                        tokenManager.saveLogin(user.login)
                    }
                    _uiState.value = RegisterUiState.Success
                } else {
                    when (response.code()) {
                        409 -> _uiState.value = RegisterUiState.Error("Ce login est déjà utilisé.")
                        400 -> _uiState.value = RegisterUiState.Error("Champs manquants ou invalides.")
                        else -> _uiState.value = RegisterUiState.Error("Erreur lors de l'inscription.")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RegisterUiState.Error("Erreur réseau : ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}