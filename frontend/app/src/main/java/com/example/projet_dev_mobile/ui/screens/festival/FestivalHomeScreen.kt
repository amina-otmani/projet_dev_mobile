package com.example.projet_dev_mobile.ui.screens.festival

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.projet_dev_mobile.ui.AppViewModelProvider

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FestivalHomeScreen(
    viewModel: FestivalHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // On s'abonne à l'état du ViewModel
    val uiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        topBar = { }
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