package com.example.projet_dev_mobile.ui.screens.festival

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_dev_mobile.ui.AppViewModelProvider
import com.example.projet_dev_mobile.utils.FestivalDatePicker
import com.example.projet_dev_mobile.utils.FormSelectionTitle
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.projet_dev_mobile.R


@Composable
fun FestivalEntryScreen(
    navigateBack: () -> Unit,
    viewModel: FestivalEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { /* Votre TopBar peut être gérée ici ou dans AppNavigation */ }
    ) { innerPadding ->

        FestivalEntryBody(
            festivalUiState = viewModel.festivalUiState,
            onFestivalValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveFestival()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )

    }
}

@Composable
fun FestivalEntryBody(
    festivalUiState: FestivalUiState,
    onFestivalValueChange: (FestivalDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(8.dp)
    ) {
        FestivalInputForm(
            festivalDetails = festivalUiState.festivalDetails,
            onValueChange = onFestivalValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = festivalUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer")
        }
    }
}

@Composable
fun FestivalInputForm(
    festivalDetails: FestivalDetails,
    modifier: Modifier = Modifier,
    onValueChange: (FestivalDetails) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // PARTIE 1 form
        FormSelectionTitle("Informations Générales")
        OutlinedTextField(
            value = festivalDetails.nom,
            onValueChange = { onValueChange(festivalDetails.copy(nom = it)) },
            label = { Text("Nom du festival") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = enabled
        )
        FestivalDatePicker(
            label = "Date de début",
            selectedDate = festivalDetails.date_debut,
            onDateSelected = { onValueChange(festivalDetails.copy(date_debut = it)) }
        )
        FestivalDatePicker(
            label = "Date de fin",
            selectedDate = festivalDetails.date_fin,
            onDateSelected = { onValueChange(festivalDetails.copy(date_fin = it)) }
        )

        Spacer(modifier = Modifier.padding(16.dp))

        // PARTIE 2 form
        FormSelectionTitle("Détails des Tables")
        OutlinedTextField(
            value = festivalDetails.stock_tables_petites.toString(),
            onValueChange = { onValueChange(festivalDetails.copy(stock_tables_petites = it.toIntOrNull() ?: 0)) },
            label = { Text("Nombre de petites tables") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )
        OutlinedTextField(
            value = festivalDetails.stock_tables_grandes.toString(),
            onValueChange = { onValueChange(festivalDetails.copy(stock_tables_petites = it.toIntOrNull() ?: 0)) },
            label = { Text("Nombre de grandes tables") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )
        OutlinedTextField(
            value = festivalDetails.stock_tables_mairie.toString(),
            onValueChange = { onValueChange(festivalDetails.copy(stock_tables_petites = it.toIntOrNull() ?: 0)) },
            label = { Text("Nombre de tables mairie") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )

        Spacer(modifier = Modifier.padding(16.dp))

        // PARTIE 3 form
        FormSelectionTitle("Zones Tarifaires")



        if (enabled) {
            Text(
                text = "*Champs obligatoires",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
