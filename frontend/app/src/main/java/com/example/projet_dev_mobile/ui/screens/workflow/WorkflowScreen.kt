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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_dev_mobile.R
import com.example.projet_dev_mobile.data.entity.enum.EtatSuivi
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
            uiState.hasLoadError && uiState.suivis.isEmpty() -> {
                Text(
                    text = stringResource(R.string.workflow_load_error),
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> WorkflowContent(
                suivis = uiState.filteredSuivis,
                total = uiState.suivis.size,
                searchQuery = uiState.searchQuery,
                selectedEtatFilter = uiState.selectedEtatFilter,
                showLoadErrorBanner = uiState.hasLoadError && uiState.suivis.isNotEmpty(),
                showMutationErrorBanner = uiState.hasMutationError,
                onDismissMutationError = viewModel::clearMutationError,
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
    selectedEtatFilter: EtatSuivi?,
    showLoadErrorBanner: Boolean,
    showMutationErrorBanner: Boolean,
    onDismissMutationError: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onEtatFilterChange: (EtatSuivi?) -> Unit,
    onPrendreContact: (Int) -> Unit,
    onChangerEtat: (Int, EtatSuivi) -> Unit,
    canEdit: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.workflow_title, total),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (showLoadErrorBanner) {
            item {
                Text(
                    text = stringResource(R.string.workflow_load_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (showMutationErrorBanner) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.workflow_update_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = onDismissMutationError) {
                        Text(stringResource(R.string.workflow_ok))
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(R.string.workflow_search_label)) }
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
                    text = stringResource(R.string.workflow_no_data),
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
    selectedEtat: EtatSuivi?,
    onEtatSelected: (EtatSuivi?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedEtatLabel(selectedEtat),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.workflow_filter_state_label)) },
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
            DropdownMenuItem(
                text = { Text(stringResource(R.string.workflow_state_all)) },
                onClick = {
                    onEtatSelected(null)
                    expanded = false
                }
            )

            WorkflowViewModel.etatsDisponibles.forEach { etat ->
                DropdownMenuItem(
                    text = { Text(selectedEtatLabel(etat)) },
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
    onChangerEtat: (Int, EtatSuivi) -> Unit
) {
    val currentEtat = suiviEtatOrDefault(suivi.etat)

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
                selectedEtat = currentEtat,
                enabled = canEdit,
                onEtatSelected = { onChangerEtat(suivi.editeur_id, it) }
            )

            Text(
                text = stringResource(
                    R.string.workflow_dates_contact,
                    formatDatesContact(
                        dates = suivi.dates_contact,
                        noContactLabel = stringResource(R.string.workflow_no_contact)
                    )
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.workflow_nb_jeux, suivi.nb_jeux),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(
                        R.string.workflow_responsable,
                        suivi.responsable_nom ?: stringResource(R.string.workflow_responsable_unknown)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            val actionLabel = when (currentEtat) {
                EtatSuivi.PAS_CONTACTE -> stringResource(R.string.workflow_action_contact)
                EtatSuivi.CONTACTE -> stringResource(R.string.workflow_action_relance)
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
    selectedEtat: EtatSuivi,
    enabled: Boolean,
    onEtatSelected: (EtatSuivi) -> Unit
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
            value = selectedEtatLabel(selectedEtat),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(stringResource(R.string.workflow_state_label)) },
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
                .forEach { etat ->
                    DropdownMenuItem(
                        text = { Text(selectedEtatLabel(etat)) },
                        onClick = {
                            onEtatSelected(etat)
                            expanded = false
                        }
                    )
                }
        }
    }
}

private fun formatDatesContact(dates: List<String>, noContactLabel: String): String {
    if (dates.isEmpty()) return noContactLabel

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

@Composable
private fun selectedEtatLabel(etat: EtatSuivi?): String {
    return when (etat) {
        null -> stringResource(R.string.workflow_state_all)
        EtatSuivi.PAS_CONTACTE -> stringResource(R.string.workflow_state_pas_contacte)
        EtatSuivi.CONTACTE -> stringResource(R.string.workflow_state_contacte)
        EtatSuivi.DISCUSSION -> stringResource(R.string.workflow_state_discussion)
        EtatSuivi.REFUS -> stringResource(R.string.workflow_state_refus)
        EtatSuivi.CONFIRME -> stringResource(R.string.workflow_state_confirme)
    }
}

private fun suiviEtatOrDefault(rawEtat: String): EtatSuivi {
    return runCatching { EtatSuivi.valueOf(rawEtat) }.getOrDefault(EtatSuivi.PAS_CONTACTE)
}
