package com.example.projet_dev_mobile.ui.screens.jeux

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_dev_mobile.data.entity.enum.GameType
import com.example.projet_dev_mobile.data.network.dto.EditeurDto
import com.example.projet_dev_mobile.ui.AppViewModelProvider
import com.example.projet_dev_mobile.utils.FormSelectionTitle
import kotlinx.coroutines.launch

@Composable
fun JeuEntryScreen(
    navigateBack: () -> Unit,
    viewModel: JeuEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold() {
        innerPadding ->
        JeuEntryBody(
            jeuUiState = viewModel.jeuUiState,
            editeurs = viewModel.editeurs,
            onJeuValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveJeu()
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
fun JeuEntryBody(
    jeuUiState: JeuUiState,
    editeurs: List<EditeurDto>,
    onJeuValueChange: (JeuDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(8.dp)
    ) {
        JeuInputForm(
            jeuDetails = jeuUiState.jeuDetails,
            editeurs = editeurs,
            onValueChange = onJeuValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = jeuUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer")
        }
    }
}

@Composable
fun JeuInputForm(
    jeuDetails: JeuDetails,
    editeurs: List<EditeurDto>,
    modifier: Modifier = Modifier,
    onValueChange: (JeuDetails) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = jeuDetails.nom,
            onValueChange = { onValueChange(jeuDetails.copy(nom = it)) },
            label = { Text("Nom du jeu") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = enabled
        )
        GameTypeDropdown(
            selectedType = jeuDetails.typeG,
            onTypeSelected = { onValueChange(jeuDetails.copy(typeG = it)) },
            enabled = enabled
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = jeuDetails.age_min.toString(),
                onValueChange = { onValueChange(jeuDetails.copy(age_min = it.toIntOrNull() ?: 0)) },
                label = { Text("Âge min") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enabled
            )
            OutlinedTextField(
                value = jeuDetails.age_max.toString(),
                onValueChange = { onValueChange(jeuDetails.copy(age_max = it.toIntOrNull() ?: 0)) },
                label = { Text("Âge max") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = enabled
            )
        }

        Spacer(modifier = Modifier.padding(16.dp))

        FormSelectionTitle("Éditeur")
        EditeurDropdown(
            editeurs = editeurs,
            selectedEditeurId = jeuDetails.editeur_id,
            onEditeurSelected = { onValueChange(jeuDetails.copy(editeur_id = it)) }
        )

        Spacer(modifier = Modifier.padding(16.dp))
    }
}

// --- Dropdown pour GameType ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTypeDropdown(
    selectedType: GameType,
    onTypeSelected: (GameType) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it }
    ) {
        OutlinedTextField(
            value = selectedType.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Type de jeu") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            GameType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditeurDropdown(
    editeurs: List<EditeurDto>,
    selectedEditeurId: Int,
    onEditeurSelected: (Int) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedEditeur = editeurs.find { it.id == selectedEditeurId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it }
    ) {
        OutlinedTextField(
            value = selectedEditeur?.nom ?: "Sélectionner un éditeur",
            onValueChange = {},
            readOnly = true,
            label = { Text("Éditeur") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            editeurs.forEach { editeur ->
                DropdownMenuItem(
                    text = { Text(editeur.nom) },
                    onClick = {
                        onEditeurSelected(editeur.id)
                        expanded = false
                    }
                )
            }
        }
    }
}