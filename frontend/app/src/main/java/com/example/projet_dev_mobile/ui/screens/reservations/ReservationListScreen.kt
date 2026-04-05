package com.example.projet_dev_mobile.ui.screens.reservation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projet_dev_mobile.data.network.dto.ReservationDto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsListScreen(
    viewModel: ReservationViewModel,
    festivalId: Int,
    isOrganisateur: Boolean,
    onNavigateToForm: (Int?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // État pour gérer la boîte de dialogue de confirmation
    var reservationToDelete by remember { mutableStateOf<Int?>(null) }

    // --- DIALOGUE DE CONFIRMATION ---
    if (reservationToDelete != null) {
        AlertDialog(
            onDismissRequest = { reservationToDelete = null },
            title = { Text("Confirmer la suppression") },
            text = { Text("Voulez-vous vraiment supprimer cette réservation ? Cette action est irréversible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        reservationToDelete?.let { id ->
                            viewModel.deleteReservation(id, festivalId, isOrganisateur)
                        }
                        reservationToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Supprimer") }
            },
            dismissButton = {
                TextButton(onClick = { reservationToDelete = null }) { Text("Annuler") }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            // 🔒 SÉCURITÉ : Le bouton "Créer" n'est dessiné que pour les organisateurs/admins
            if (isOrganisateur) {
                FloatingActionButton(onClick = { onNavigateToForm(null) }) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter Réservation")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()

                uiState.errorMessage != null -> Text(
                    text = "Erreur : ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error
                )

                uiState.reservations.isEmpty() -> Text("Aucune réservation ou exposant trouvé.")

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.reservations) { reservation ->
                            ReservationCard(
                                reservation = reservation,
                                isOrganisateur = isOrganisateur,
                                onClick = { if (isOrganisateur) onNavigateToForm(reservation.id) },
                                onDelete = { reservationToDelete = reservation.id }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationCard(
    reservation: ReservationDto,
    isOrganisateur: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(if (isOrganisateur) Modifier.clickable { onClick() } else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val nom = reservation.nom_reservant ?: reservation.autre_nom_reservant ?: "Exposant inconnu"
                Text(text = nom, style = MaterialTheme.typography.titleMedium)
                Text(text = "Type : ${reservation.type}", style = MaterialTheme.typography.bodySmall)
            }

            if (isOrganisateur) {
                Row {
                    // Icône Modifier (Crayon)
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Icône Supprimer (Poubelle)
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}