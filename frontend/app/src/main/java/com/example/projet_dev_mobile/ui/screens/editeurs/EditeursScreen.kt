package com.example.projet_dev_mobile.ui.screens.editeurs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_dev_mobile.data.network.dto.EditeurDto
import com.example.projet_dev_mobile.ui.AppViewModelProvider

@Composable
fun EditeursScreen(
    modifier: Modifier = Modifier,
    onEditeurClick: (EditeurDto) -> Unit = {},
    viewModel: EditeursViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.isError -> {
                Text(
                    text = "Erreur lors du chargement des editeurs",
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> EditeursContent(
                editeurs = uiState.filteredEditeurs,
                totalEditeurs = uiState.editeurs.size,
                jeuxParEditeur = uiState.jeuxParEditeur,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onEditeurClick = onEditeurClick
            )
        }
    }
}

@Composable
private fun EditeursContent(
    editeurs: List<EditeurDto>,
    totalEditeurs: Int,
    jeuxParEditeur: Map<Int, Int>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onEditeurClick: (EditeurDto) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Editeurs (${totalEditeurs})",
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

        if (editeurs.isEmpty()) {
            item {
                Text(
                    text = "Aucun editeur ne correspond a la recherche.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(editeurs, key = { it.id }) { editeur ->
            EditeurCard(
                editeur = editeur,
                jeuxCount = jeuxParEditeur[editeur.id] ?: 0,
                onClick = { onEditeurClick(editeur) })
        }
    }
}

@Composable
private fun EditeurCard(
    editeur: EditeurDto,
    jeuxCount: Int,
    onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = editeur.nom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            val contactCount = editeur.contacts.size
            Text(
                text = if (contactCount > 1) "$contactCount contacts" else "$contactCount contact",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = if (jeuxCount > 1) "$jeuxCount jeux" else "$jeuxCount jeu",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
