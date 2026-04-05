package com.example.projet_dev_mobile.ui.screens.workflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_dev_mobile.data.entity.enum.RoleType
import com.example.projet_dev_mobile.data.local.TokenManager
import com.example.projet_dev_mobile.data.network.dto.SuiviDto

@Composable
fun WorkflowScreen(
    modifier: Modifier = Modifier,
    viewModel: WorkflowViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val canEdit = remember {
        val role = TokenManager(context).getRole()
        role == RoleType.ORGANISATEUR_RESERVATIONS || role == RoleType.ADMIN
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.isError && uiState.suivis.isEmpty() -> {
                Text(
                    text = "Erreur lors du chargement du workflow",
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> WorkflowContent(
                suivis = uiState.filteredSuivis,
                total = uiState.suivis.size,
                searchQuery = uiState.searchQuery,
                selectedEtatFilter = uiState.selectedEtatFilter,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onEtatFilterChange = viewModel::onEtatFilterChange,
                onPrendreContact = viewModel::prendreContact,
                onChangerEtat = viewModel::changerEtat,
                canEdit = canEdit
            )
        }
    }
}

@Composable
private fun WorkflowContent(
    suivis: List<SuiviDto>,
    total: Int,
    searchQuery: String,
    selectedEtatFilter: String,
    onSearchQueryChange: (String) -> Unit,
    onEtatFilterChange: (String) -> Unit,
    onPrendreContact: (Int) -> Unit,
    onChangerEtat: (Int, String) -> Unit,
    canEdit: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Workflow de suivi des editeurs ($total)",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Rechercher un editeur") }
            )
        }

        item {
            WorkflowFilterDropdown(
                selectedEtat = selectedEtatFilter,
                onEtatSelected = onEtatFilterChange
            )
        }

        if (suivis.isEmpty()) {
            item {
                Text(
                    text = "Aucun editeur a afficher avec ce filtre.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(suivis, key = { it.editeur_id }) { suivi ->
            SuiviCard(
                suivi = suivi,
                canEdit = canEdit,
                onPrendreContact = onPrendreContact,
                onChangerEtat = onChangerEtat
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkflowFilterDropdown(
    selectedEtat: String,
    onEtatSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = WorkflowViewModel.etatLabels[selectedEtat] ?: selectedEtat,
            onValueChange = {},
            readOnly = true,
            label = { Text("Filtrer par etat") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                ),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WorkflowViewModel.etatsDisponibles.forEach { etat ->
                DropdownMenuItem(
                    text = { Text(WorkflowViewModel.etatLabels[etat] ?: etat) },
                    onClick = {
                        onEtatSelected(etat)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SuiviCard(
    suivi: SuiviDto,
    canEdit: Boolean,
    onPrendreContact: (Int) -> Unit,
    onChangerEtat: (Int, String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = suivi.editeur_nom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            EtatDropdown(
                selectedEtat = suivi.etat,
                enabled = canEdit,
                onEtatSelected = { onChangerEtat(suivi.editeur_id, it) }
            )

            Text(
                text = "Dates de contact: ${formatDatesContact(suivi.dates_contact)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Nb jeux: ${suivi.nb_jeux}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Responsable: ${suivi.responsable_nom ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            val actionLabel = when (suivi.etat) {
                WorkflowViewModel.ETAT_PAS_CONTACTE -> "Prendre contact"
                WorkflowViewModel.ETAT_CONTACTE -> "Relancer"
                else -> null
            }

            if (actionLabel != null) {
                Button(
                    onClick = { onPrendreContact(suivi.editeur_id) },
                    enabled = canEdit
                ) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EtatDropdown(
    selectedEtat: String,
    enabled: Boolean,
    onEtatSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                expanded = !expanded
            }
        }
    ) {
        OutlinedTextField(
            value = WorkflowViewModel.etatLabels[selectedEtat] ?: selectedEtat,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Etat") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = enabled
                ),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WorkflowViewModel.etatsDisponibles
                .filter { it != WorkflowViewModel.ETAT_TOUS }
                .forEach { etat ->
                    DropdownMenuItem(
                        text = { Text(WorkflowViewModel.etatLabels[etat] ?: etat) },
                        onClick = {
                            onEtatSelected(etat)
                            expanded = false
                        }
                    )
                }
        }
    }
}

private fun formatDatesContact(dates: List<String>): String {
    if (dates.isEmpty()) return "Aucun contact"

    return dates.joinToString(separator = ", ") { date ->
        if (date.length >= 10) {
            val isoDate = date.take(10)
            val parts = isoDate.split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else date
        } else {
            date
        }
    }
}
