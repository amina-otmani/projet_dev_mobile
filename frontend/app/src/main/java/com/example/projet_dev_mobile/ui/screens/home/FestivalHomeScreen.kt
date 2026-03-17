package com.example.projet_dev_mobile.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.espresso.base.Default
import com.example.projet_dev_mobile.ui.AppViewModelProvider
import com.example.projet_dev_mobile.ui.screens.festival.FestivalCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FestivalHomeScreen(
    onNavigateToEntry: () -> Unit,
    viewModel: FestivalHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToEntry,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un festival")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Section Festivals Actifs
            if (uiState.activeFestivals.isNotEmpty()) {
                item { Text("Festivals à venir", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 16.dp)) }
                items(uiState.activeFestivals) { festival ->
                    FestivalCard(festival = festival)
                }
            }

            // Section Festivals Passés
            if (uiState.pastFestivals.isNotEmpty()) {
                item { Text("Archives", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 16.dp)) }
                items(uiState.pastFestivals) { festival ->
                    FestivalCard(
                        festival = festival,
                        modifier = Modifier.alpha(0.6f) // On grise un peu pour le côté "archive"
                    )
                }
            }
        }
    }
}