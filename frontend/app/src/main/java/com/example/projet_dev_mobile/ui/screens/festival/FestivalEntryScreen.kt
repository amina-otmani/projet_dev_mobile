package com.example.projet_dev_mobile.ui.screens.festival

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projet_dev_mobile.data.entity.ZonePlan
import com.example.projet_dev_mobile.data.entity.ZoneTarifaire
import com.example.projet_dev_mobile.ui.screens.zoneTarifaire.ZoneTarifaireItem


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

            onAddZoneT = viewModel::addEmptyZoneT,
            onDeleteZoneT = viewModel::deleteZoneT,
            onUpdateZoneT = viewModel::updateZoneT,

            onAddZoneP = viewModel::addZPtoZT,
            onDeleteZoneP = viewModel::deleteZPfromZT,
            onUpdateZoneP = viewModel::updateZPinZT,

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

    onAddZoneT: () -> Unit,
    onDeleteZoneT: (Int) -> Unit,
    onUpdateZoneT: (Int, ZoneTarifaire) -> Unit,

    onAddZoneP: (Int) -> Unit,
    onDeleteZoneP: (Int, Int) -> Unit,
    onUpdateZoneP: (Int, Int, ZonePlan) -> Unit,

    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(8.dp)
    ) {
        FestivalInputForm(
            festivalDetails = festivalUiState.festivalDetails,
            onValueChange = onFestivalValueChange,

            onAddZoneT = onAddZoneT,
            onDeleteZoneT = onDeleteZoneT,
            onUpdateZoneT = onUpdateZoneT,

            onAddZoneP = onAddZoneP,
            onDeleteZoneP = onDeleteZoneP,
            onUpdateZoneP = onUpdateZoneP,

            modifier = Modifier.fillMaxWidth(),
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

    onAddZoneT: () -> Unit,
    onDeleteZoneT: (Int) -> Unit,
    onUpdateZoneT: (Int, ZoneTarifaire) -> Unit,

    onAddZoneP: (Int) -> Unit,
    onDeleteZoneP: (Int, Int) -> Unit,
    onUpdateZoneP: (Int, Int, ZonePlan) -> Unit,

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FestivalDatePicker(
                label = "Date de début",
                selectedDate = festivalDetails.date_debut,
                onDateSelected = { onValueChange(festivalDetails.copy(date_debut = it)) },
                modifier = Modifier.weight(1f)
            )
            FestivalDatePicker(
                label = "Date de fin",
                selectedDate = festivalDetails.date_fin,
                onDateSelected = { onValueChange(festivalDetails.copy(date_fin = it)) },
                modifier = Modifier.weight(1f)
            )
        }


        Spacer(modifier = Modifier.padding(16.dp))

        // PARTIE 2 form
        FormSelectionTitle("Détails des Tables")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = festivalDetails.stock_tables_petites.toString(),
                onValueChange = { onValueChange(festivalDetails.copy(stock_tables_petites = it.toIntOrNull() ?: 0)) },
                label = { Text("Petites") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enabled
            )
            OutlinedTextField(
                value = festivalDetails.stock_tables_grandes.toString(),
                onValueChange = { onValueChange(festivalDetails.copy(stock_tables_grandes = it.toIntOrNull() ?: 0)) },
                label = { Text("Grandes") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enabled
            )
            OutlinedTextField(
                value = festivalDetails.stock_tables_mairie.toString(),
                onValueChange = { onValueChange(festivalDetails.copy(stock_tables_mairie = it.toIntOrNull() ?: 0)) },
                label = { Text("Mairie") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enabled
            )
        }


        Spacer(modifier = Modifier.padding(16.dp))

        // PARTIE 3 form
        FormSelectionTitle("Zones Tarifaires")

        // affichage des zones déjà affichées
        festivalDetails.zonesTarifaires.forEachIndexed { index, zone ->
            ZoneTarifaireItem(
                zoneT = zone,
                indexZT = index,
                onDelete = { onDeleteZoneT(index) },
                onUpdate = { updatedZone -> onUpdateZoneT(index, updatedZone) },
                onAddZP = onAddZoneP,
                onDeleteZP = onDeleteZoneP,
                onUpdateZP = onUpdateZoneP
            )
        }
        TextButton(
            onClick = onAddZoneT,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Ajouter une zone tarifaire")
        }


        if (enabled) {
            Text(
                text = "*Champs obligatoires",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
