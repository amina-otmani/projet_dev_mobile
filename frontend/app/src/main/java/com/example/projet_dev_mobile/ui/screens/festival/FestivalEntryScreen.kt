package com.example.projet_dev_mobile.ui.screens.festival

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_dev_mobile.ui.AppViewModelProvider
import kotlinx.coroutines.launch


@Composable
fun FestivalEntryScreen(
    navigateBack: () -> Unit,
    viewModel: FestivalEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { /* Votre TopBar peut être gérée ici ou dans AppNavigation */ }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.festivalUiState.festivalDetails.nom,
                onValueChange = { viewModel.updateUiState(viewModel.festivalUiState.festivalDetails.copy(nom = it)) },
                label = { Text("Nom du festival") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.festivalUiState.festivalDetails.date_debut,
                onValueChange = { viewModel.updateUiState(viewModel.festivalUiState.festivalDetails.copy(date_debut = it)) },
                label = { Text("Date de début (AAAA-MM-JJ)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.festivalUiState.festivalDetails.date_fin,
                onValueChange = { viewModel.updateUiState(viewModel.festivalUiState.festivalDetails.copy(date_fin = it)) },
                label = { Text("Date de fin (AAAA-MM-JJ)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (viewModel.saveFestival()) navigateBack()
                    }
                },
                enabled = viewModel.festivalUiState.isEntryValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer")
            }
        }
    }
}