package com.example.projet_dev_mobile.ui.screens.dashboard

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projet_dev_mobile.data.entity.Festival
import com.example.projet_dev_mobile.data.entity.ZoneTarifaire

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: FestivalDetailsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    FestivalDetailsBody(
        uiState = uiState,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
private fun FestivalDetailsBody(
    uiState: FestivalDetailsUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> { CircularProgressIndicator() }
            uiState.isError -> {
                Text(
                    text = "Erreur lors du chargement des détails du festival",
                    color = MaterialTheme.colorScheme.error
                )
            }
            uiState.festival != null -> { FestivalDetailsContent(festival = uiState.festival) }
        }
    }
}

@Composable
private fun FestivalDetailsContent(
    festival: Festival,
    modifier: Modifier = Modifier
) {
    val dateDebut = festival.date_debut.substringBefore("T")
    val dateFin = festival.date_fin.substringBefore("T")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Dates
        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Dates :", fontWeight = FontWeight.Bold)
                Text("$dateDebut – $dateFin")
            }
        }

        // Stock tables
        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Nombre total de tables en stock :", fontWeight = FontWeight.Bold)
                Text("${festival.totalTables}")
            }
            Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp)) {
                Text("• Petites : ${festival.stock_tables_petites}")
                Text("• Grandes : ${festival.stock_tables_grandes}")
                Text("• Mairie : ${festival.stock_tables_mairie}")
            }
        }

        // Capacité zones
        val totalTablesZones = festival.zonesTarifaires
            .flatMap { it.zonesPlan }
            .sumOf { it.nombre_tables }
        val tablesRestantes = festival.totalTables - totalTablesZones

        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Capacité totale des zones :", fontWeight = FontWeight.Bold)
                Text("$totalTablesZones")
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Tables restantes à vendre :", fontWeight = FontWeight.Bold)
                Text("$tablesRestantes")
            }
        }

        // Zones tarifaires
        if (festival.zonesTarifaires.isNotEmpty()) {
            item {
                HorizontalDivider()
                Text(
                    "Zones tarifaires",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(festival.zonesTarifaires) { zone ->
                ZoneTarifaireCard(zone = zone)
            }
        }
    }
}

@Composable
private fun ZoneTarifaireCard(zone: ZoneTarifaire) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(zone.nom, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            val totalTables = zone.zonesPlan.sumOf { it.nombre_tables }
            InfoRow("Nombre total de tables :", "$totalTables")
            InfoRow("Prix d'une table :", "${zone.prix_table} €")
            InfoRow("Prix au m² :", "${zone.prix_m2} €")

            if (zone.zonesPlan.isNotEmpty()) {
                Text("Zones plan :", fontWeight = FontWeight.Bold)
                zone.zonesPlan.forEach { zonePlan ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(zonePlan.nom)
                            InfoRow("Nombre tables :", "${zonePlan.nombre_tables}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, fontWeight = FontWeight.Bold)
        Text(value)
    }
}