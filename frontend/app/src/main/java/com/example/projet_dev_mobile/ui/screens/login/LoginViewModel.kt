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

                    if (loginResponse != null) {
                        // On ne sauvegarde que si les tokens ont bien été trouvés
                        if (accessToken.isNotEmpty() && refreshToken.isNotEmpty()) {
                            tokenManager.saveTokens(accessToken, refreshToken)
                        }
                        tokenManager.saveRole(loginResponse.user.role)
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
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}