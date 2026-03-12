package com.example.projet_dev_mobile.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel() // On injecte automatiquement votre ViewModel
) {
    // Variables pour stocker ce que l'utilisateur tape (comme le [(ngModel)] en Angular)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observation de l'état (Chargement, Erreur, Succès)
    val uiState by viewModel.uiState.collectAsState()

    // Si l'état passe à Success, on déclenche la navigation vers Home
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }

    // Le design de la page (l'équivalent de flexbox direction column)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenue",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Champ Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Adresse email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Champ Mot de passe
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(), // Censure le texte par des "•••"
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Bouton de connexion ou indicateur de chargement
        if (uiState is LoginUiState.Loading) {
            CircularProgressIndicator() // Affiche la roue de chargement
        } else {
            Button(
                onClick = {
                    // Appel de la méthode login de votre ViewModel (qui tapera sur Retrofit)
                    viewModel.login(email, password)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                // On désactive le bouton si les champs sont vides
                enabled = email.isNotBlank() && password.isNotBlank()
            ) {
                Text("Se connecter")
            }
        }

        // Affichage des erreurs si Retrofit renvoie une erreur (401, 500, etc.)
        if (uiState is LoginUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (uiState as LoginUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}