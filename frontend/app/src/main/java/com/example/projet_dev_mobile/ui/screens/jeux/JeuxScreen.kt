package com.example.projet_dev_mobile.ui.screens.jeux

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_dev_mobile.data.entity.enum.GameType
import com.example.projet_dev_mobile.data.network.dto.JeuDto
import com.example.projet_dev_mobile.ui.AppViewModelProvider

@Composable
fun JeuxScreen(
    modifier: Modifier = Modifier,
    onJeuClick: (Int) -> Unit,
    onNavigateToEntry: () -> Unit,
    viewModel: JeuxViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToEntry,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un jeu")
            }
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()

                uiState.isError -> {
                    Text(
                        text = "Erreur lors du chargement des jeux",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> JeuxContent(
                    allJeux = uiState.jeux,
                    jeux = uiState.filteredJeux,
                    searchQuery = uiState.searchQuery,
                    selectedCategory = uiState.selectedCategory,
                    categories = uiState.availableCategories,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onCategoryChange = viewModel::onCategoryChange,
                    onJeuClick = onJeuClick
                )
            }
        }
    }
}

@Composable
private fun JeuxContent(
    allJeux: List<JeuDto>,
    jeux: List<JeuDto>,
    searchQuery: String,
    selectedCategory: GameType?,
    categories: List<GameType>,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (GameType?) -> Unit,
    onJeuClick: (Int) -> Unit
) {


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Jeux (${allJeux.size})",
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
                label = { Text("Rechercher un jeu") }
            )
        }

        item {
            FilterDropdown(
                label = "Categorie",
                selectedValue = selectedCategory,
                options = categories,
                allLabel = "Toutes",
                onValueSelected = onCategoryChange
            )
        }

        if (jeux.isEmpty()) {
            item {
                Text(
                    text = "Aucun jeu ne correspond aux filtres.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(jeux, key = { it.id }) { jeu ->
            JeuCard(
                jeu = jeu,
                onClick = { onJeuClick(jeu.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    selectedValue: GameType?,
    options: List<GameType>,
    allLabel: String,
    onValueSelected: (GameType?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedValue?.name ?: allLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(allLabel) },
                onClick = {
                    onValueSelected(null)
                    expanded = false
                }
            )

            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun JeuCard(
    jeu: JeuDto,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = jeu.nom,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Editeur: ${jeu.editeur!!.nom}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Categorie: ${jeu.typeG}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
