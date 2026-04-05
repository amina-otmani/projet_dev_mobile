package com.example.projet_dev_mobile.ui.screens.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projet_dev_mobile.data.entity.enum.TypeReservant
import com.example.projet_dev_mobile.data.network.dto.ReservationDto
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.projet_dev_mobile.data.entity.ZonePlan
import com.example.projet_dev_mobile.data.network.dto.JeuDto
import com.example.projet_dev_mobile.data.network.dto.JeuReserveDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationFormScreen(
    viewModel: ReservationFormViewModel,
    festivalId: Int,
    reservationIdToEdit: Int? = null,
    onNavigateBack: () -> Unit
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val reservationState by viewModel.reservationState.collectAsState()

    LaunchedEffect(reservationIdToEdit) {
        viewModel.initForm(festivalId, reservationIdToEdit)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Étape $currentStep / 3") })
        },
        bottomBar = {
            BottomNavigationBar(
                currentStep = currentStep,
                onPrevious = { viewModel.previousStep() },
                onNext = { viewModel.nextStep() },
                onSubmit = {
                    viewModel.submitReservation(
                        onSuccess = { onNavigateBack() },
                        onError = { /* Afficher un Snackbar d'erreur */ }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LinearProgressIndicator(
                progress = currentStep / 3f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Changement d'écran selon l'étape
            when (currentStep) {
                1 -> Step1Informations(reservationState, viewModel)
                2 -> Step2Jeux(viewModel)
                3 -> Step3Placement(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Informations(
    state: ReservationDto,
    viewModel: ReservationFormViewModel
) {
    val lignes by viewModel.lignes.collectAsState()
    val editeurs by viewModel.editeurs.collectAsState()

    var editeurExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Informations Générales", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // 1. TYPE DE RÉSERVANT
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded }
        ) {
            OutlinedTextField(
                value = state.type.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Type de réservant") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                TypeReservant.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            viewModel.updateTypeReservant(type)
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. NOM DE LA STRUCTURE
        if (state.type == TypeReservant.EDITEUR) {
            // Cas Éditeur : Menu déroulant
            val currentEditeurNom = editeurs.find { it.id == state.editeur_id }?.nom
                ?: state.nom_reservant
                ?: "Sélectionner un éditeur"

            ExposedDropdownMenuBox(
                expanded = editeurExpanded,
                onExpandedChange = { editeurExpanded = !editeurExpanded }
            ) {
                OutlinedTextField(
                    value = currentEditeurNom,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Éditeur") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = editeurExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = editeurExpanded,
                    onDismissRequest = { editeurExpanded = false }
                ) {
                    editeurs.forEach { editeur ->
                        DropdownMenuItem(
                            text = { Text(editeur.nom) },
                            onClick = {
                                viewModel.updateEditeurId(editeur.id)
                                editeurExpanded = false
                            }
                        )
                    }
                }
            }
        } else {
            OutlinedTextField(
                value = state.autre_nom_reservant ?: "",
                onValueChange = { viewModel.updateNomReservant(it) },
                label = { Text("Nom de la structure (Asso / Boutique / Autre)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // 3. LIGNES TARIFAIRES (Facturation)
        Text("Emplacements Réservés", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        lignes.forEachIndexed { index, ligne ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Zone ID: ${ligne.zone_tarifaire_id}", modifier = Modifier.weight(1f))
                    Text("Qté: ${ligne.quantite}", modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = { viewModel.removeLigneTarifaire(index) }) {
                        Icon(Icons.Default.Delete, "Supprimer", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { viewModel.addLigneTarifaire() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ajouter un emplacement")
        }
    }
}


// ====================================================================
// ÉTAPE 2 : LOGISTIQUE (Choix des jeux)
// ====================================================================
@Composable
fun Step2Jeux(viewModel: ReservationFormViewModel) {
    val lignesJeux by viewModel.lignesJeux.collectAsState()
    val jeuxDisponibles by viewModel.jeuxDisponibles.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Jeux Exposés", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (jeuxDisponibles.isEmpty()) {
            Text("Chargement des jeux ou aucun jeu disponible...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            lignesJeux.forEachIndexed { index, ligne ->
                LigneJeuCard(
                    index = index,
                    ligne = ligne,
                    jeuxDisponibles = jeuxDisponibles,
                    onUpdate = { updatedLigne -> viewModel.updateLigneJeu(index, updatedLigne) },
                    onRemove = { viewModel.removeLigneJeu(index) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { viewModel.addLigneJeu() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un jeu")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ajouter un jeu")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LigneJeuCard(
    index: Int,
    ligne: JeuReserveDto,
    jeuxDisponibles: List<JeuDto>,
    onUpdate: (JeuReserveDto) -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Retrouver le nom du jeu actuellement sélectionné pour l'affichage
    val selectedJeuName = jeuxDisponibles.find { it.id == ligne.jeu_id }?.nom ?: "Sélectionner un jeu"

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Menu déroulant pour le choix du jeu
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedJeuName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jeu n°${index + 1}") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        jeuxDisponibles.forEach { jeu ->
                            DropdownMenuItem(
                                text = { Text(jeu.nom) },
                                onClick = {
                                    onUpdate(ligne.copy(jeu_id = jeu.id ?: 0))
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quantités et tables
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ligne.nb_exemplaires.toString(),
                    onValueChange = {
                        val newNb = it.toIntOrNull() ?: 1
                        onUpdate(ligne.copy(nb_exemplaires = newNb))
                    },
                    label = { Text("Exemplaires") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = ligne.tables_occupees,
                    onValueChange = { newValue ->
                        onUpdate(ligne.copy(tables_occupees = newValue))
                    },
                    label = { Text("Tables/Exemplaire") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


// ====================================================================
// ÉTAPE 3 : PLACEMENT (Assignation aux Zones de Plan)
// ====================================================================
@Composable
fun Step3Placement(viewModel: ReservationFormViewModel) {
    val lignesJeux by viewModel.lignesJeux.collectAsState()
    val zonesTarifaires by viewModel.zonesTarifaires.collectAsState()
    val jeuxDisponibles by viewModel.jeuxDisponibles.collectAsState()

    // On extrait toutes les "Zones de Plan" (salles/espaces physiques) disponibles dans le festival
    val allZonesPlans = remember(zonesTarifaires) {
        zonesTarifaires.flatMap { it.zonesPlan }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Placement (Zones)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (lignesJeux.isEmpty()) {
            Text("Aucun jeu ajouté à l'étape précédente.", color = MaterialTheme.colorScheme.error)
        } else if (allZonesPlans.isEmpty()) {
            Text("Aucune zone de plan n'a été créée pour ce festival.", color = MaterialTheme.colorScheme.error)
        } else {
            Text("Assignez un espace physique à chaque jeu :", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            lignesJeux.forEachIndexed { index, ligne ->
                // On retrouve le nom du jeu pour l'afficher clairement
                val jeuName = jeuxDisponibles.find { it.id == ligne.jeu_id }?.nom ?: "Jeu sans nom"

                LignePlacementCard(
                    jeuName = jeuName,
                    ligne = ligne,
                    zonesPlans = allZonesPlans,
                    onUpdate = { updatedLigne -> viewModel.updateLigneJeu(index, updatedLigne) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LignePlacementCard(
    jeuName: String,
    ligne: JeuReserveDto,
    zonesPlans: List<ZonePlan>,
    onUpdate: (JeuReserveDto) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Retrouver le nom de la zone actuellement sélectionnée
    val selectedZoneName = zonesPlans.find { it.id == ligne.zone_plan_id }?.nom ?: "Non placé"

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = jeuName, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            val nbTablesTotales = (ligne.nb_exemplaires * (ligne.tables_occupees.toDoubleOrNull() ?: 0.0)).toInt()
            Text(
                text = "Nécessite $nbTablesTotales table(s)",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedZoneName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Emplacement (Salle)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    zonesPlans.forEach { zone ->
                        DropdownMenuItem(
                            text = { Text("${zone.nom} (${zone.nombre_tables} tables max)") },
                            onClick = {
                                onUpdate(ligne.copy(zone_plan_id = zone.id))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentStep: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentStep > 1) {
            OutlinedButton(onClick = onPrevious) { Text("Précédent") }
        } else {
            Spacer(modifier = Modifier.width(8.dp)) // Pour garder l'alignement
        }

        if (currentStep < 3) {
            Button(onClick = onNext) { Text("Suivant") }
        } else {
            Button(onClick = onSubmit) { Text("Valider") }
        }
    }
}