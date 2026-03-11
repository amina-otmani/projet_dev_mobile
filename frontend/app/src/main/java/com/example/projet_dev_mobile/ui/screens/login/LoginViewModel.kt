package com.example.projet_dev_mobile.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet_dev_mobile.data.local.TokenManager
import com.example.projet_dev_mobile.data.network.RetrofitInstance
import com.example.projet_dev_mobile.data.network.dto.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val apiService = RetrofitInstance.getApiService(application)
    private val tokenManager = TokenManager(application)

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val request = LoginRequest(
                    login = _uiState.value.email,
                    password = _uiState.value.password
                )

                val response = apiService.login(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        tokenManager.saveTokens(it.token, it.refreshToken)
                        onSuccess()
                    }
                } else {
                    _uiState.value =
                        _uiState.value.copy(errorMessage = "Login ou mot de passe incorrect")
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(errorMessage = "Erreur : ${e.localizedMessage}")
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}