package com.example.projet_dev_mobile.ui.screens.reservations

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projet_dev_mobile.data.entity.ZonePlan
import com.example.projet_dev_mobile.data.entity.ZoneTarifaire
import com.example.projet_dev_mobile.data.entity.enum.TypeReservant
import com.example.projet_dev_mobile.data.network.dto.JeuDto
import com.example.projet_dev_mobile.data.network.dto.JeuReserveDto
import com.example.projet_dev_mobile.data.network.dto.LigneReservationDto
import com.example.projet_dev_mobile.data.network.dto.ReservationDto

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

    // Ajout de l'observation de l'erreur
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current

    // Gestion de l'alerte de dépassement
    var showTablesWarningDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(festivalId, reservationIdToEdit) {
        viewModel.initForm(festivalId, reservationIdToEdit)
    }

    // Affichage des erreurs via Toast
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    if (showTablesWarningDialog) {
        AlertDialog(
            onDismissRequest = { showTablesWarningDialog = false },
            title = { Text("Attention : Dépassement") },
            text = { Text("Vous utilisez plus de tables pour vos jeux que vous n'en avez réservé en zone tarifaire. Voulez-vous continuer quand même ?") },
            confirmButton = {
                TextButton(onClick = {
                    showTablesWarningDialog = false
                    pendingAction?.invoke()
                }) { Text("Continuer") }
            },
            dismissButton = {
                TextButton(onClick = { showTablesWarningDialog = false }) { Text("Annuler") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Étape $currentStep / 3") })
        },
        bottomBar = {
            BottomNavigationBar(
                currentStep = currentStep,
                onPrevious = { viewModel.previousStep() },
                onSavePartial = {
                    viewModel.submitReservation(
                        onSuccess = {
                            Toast.makeText(context, "Brouillon sauvegardé", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        },
                        onError = { msg -> Toast.makeText(context, "Erreur : $msg", Toast.LENGTH_LONG).show() }
                    )
                },
                onNext = {
                    if (currentStep == 2 && viewModel.hasTablesExceeded()) {
                        pendingAction = { viewModel.nextStep() }
                        showTablesWarningDialog = true
                    } else {
                        viewModel.nextStep()
                    }
                },
                onSubmit = {
                    val submitAction = {
                        viewModel.submitReservation(
                            onSuccess = {
                                Toast.makeText(context, "Réservation validée avec succès", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            },
                            onError = { msg -> Toast.makeText(context, "Erreur : $msg", Toast.LENGTH_LONG).show() }
                        )
                    }

                    if (viewModel.hasTablesExceeded()) {
                        pendingAction = submitAction
                        showTablesWarningDialog = true
                    } else {
                        submitAction()
                    }
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
                progress = { currentStep / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

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
    val zonesTarifaires by viewModel.zonesTarifaires.collectAsState()
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
                modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
                    modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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

        Spacer(modifier = Modifier.height(16.dp))

        // 3. PRÉSENCE PHYSIQUE (Seulement pour Éditeur)
        if (state.type == TypeReservant.EDITEUR) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = state.est_present,
                    onCheckedChange = { viewModel.updateEstPresent(it) }
                )
                Text("Présent physiquement au festival")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 4. NOMBRE DE PRISES ÉLECTRIQUES
        OutlinedTextField(
            value = state.nombre_prises.toString(),
            onValueChange = { viewModel.updateNombrePrises(it.toIntOrNull() ?: 0) },
            label = { Text("Nombre de prises électriques") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 5. PRÉFÉRENCES TABLES
        OutlinedTextField(
            value = state.preferences_tables ?: "",
            onValueChange = { viewModel.updatePreferencesTables(it) },
            label = { Text("Commentaire - Préférences des tables") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text("Emplacements Réservés", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        lignes.forEachIndexed { index, ligne ->
            LigneTarifaireCard(
                index = index,
                ligne = ligne,
                zonesTarifaires = zonesTarifaires,
                onUpdate = { updatedLigne -> viewModel.updateLigneTarifaire(index, updatedLigne) },
                onRemove = { viewModel.removeLigneTarifaire(index) }
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LigneTarifaireCard(
    index: Int,
    ligne: LigneReservationDto,
    zonesTarifaires: List<ZoneTarifaire>,
    onUpdate: (LigneReservationDto) -> Unit,
    onRemove: () -> Unit
) {
    var zoneExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val currentZone = zonesTarifaires.find { it.id == ligne.zone_tarifaire_id }
    val zoneName = currentZone?.nom ?: "Sélectionner une zone"

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sélection de la Zone Tarifaire
                ExposedDropdownMenuBox(
                    expanded = zoneExpanded,
                    onExpandedChange = { zoneExpanded = !zoneExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = zoneName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Zone Tarifaire") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = zoneExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = zoneExpanded,
                        onDismissRequest = { zoneExpanded = false }
                    ) {
                        zonesTarifaires.forEach { zone ->
                            DropdownMenuItem(
                                text = { Text("${zone.nom} (T:${zone.prix_table}€ / M2:${zone.prix_m2}€)") },
                                onClick = {
                                    val newPrice = if (ligne.type_emplacement == "TABLE") zone.prix_table else zone.prix_m2
                                    onUpdate(ligne.copy(
                                        zone_tarifaire_id = zone.id,
                                        prix_moment_reservation = newPrice
                                    ))
                                    zoneExpanded = false
                                }
                            )
                        }
                    }
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, "Supprimer", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Sélection du Type (TABLE ou M2)
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                    modifier = Modifier.weight(1.2f)
                ) {
                    OutlinedTextField(
                        value = ligne.type_emplacement,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        listOf("TABLE", "M2").forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    val newPrice = if (type == "TABLE") currentZone?.prix_table else currentZone?.prix_m2
                                    onUpdate(ligne.copy(
                                        type_emplacement = type,
                                        prix_moment_reservation = newPrice ?: ligne.prix_moment_reservation
                                    ))
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Saisie Quantité
                OutlinedTextField(
                    value = ligne.quantite.toString(),
                    onValueChange = {
                        val newQte = it.toIntOrNull() ?: 1
                        onUpdate(ligne.copy(quantite = newQte))
                    },
                    label = { Text("Qté") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.8f)
                )

                // Saisie Prix Unitaire
                OutlinedTextField(
                    value = ligne.prix_moment_reservation.toString(),
                    onValueChange = {
                        val newPrice = it.toDoubleOrNull() ?: 0.0
                        onUpdate(ligne.copy(prix_moment_reservation = newPrice))
                    },
                    label = { Text("Prix U.") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun Step2Jeux(viewModel: ReservationFormViewModel) {
    val lignesJeux by viewModel.lignesJeux.collectAsState()
    val jeuxDisponibles by viewModel.jeuxDisponibles.collectAsState()

    val totalReserved = viewModel.getTotalTablesReserved()
    val totalUsed = viewModel.getTotalTablesUsed()
    val hasExceeded = viewModel.hasTablesExceeded()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Jeux Exposés", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Affichage du compteur d'utilisation des tables
        Text(
            text = "Utilisation : $totalUsed / ${kotlin.math.floor(totalReserved).toInt()} tables réservées",
            color = if (hasExceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
    val selectedJeuName = jeuxDisponibles.find { it.id == ligne.jeu_id }?.nom ?: "Sélectionner un jeu"

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
                    value = ligne.tables_occupees.toString(),
                    onValueChange = { newValue ->
                        val newTables = newValue.toDoubleOrNull() ?: 1.0
                        onUpdate(ligne.copy(tables_occupees = newTables))
                    },
                    label = { Text("Tables/Exemplaire") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun Step3Placement(viewModel: ReservationFormViewModel) {
    val lignesJeux by viewModel.lignesJeux.collectAsState()
    val zonesTarifaires by viewModel.zonesTarifaires.collectAsState()
    val jeuxDisponibles by viewModel.jeuxDisponibles.collectAsState()

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
    val selectedZoneName = zonesPlans.find { it.id == ligne.zone_plan_id }?.nom ?: "Non placé"

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = jeuName, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            val nbTablesTotales = (ligne.nb_exemplaires * ligne.tables_occupees).toInt()
            Text(text = "Nécessite $nbTablesTotales table(s)", style = MaterialTheme.typography.bodySmall)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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
    onSavePartial: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentStep > 1) {
            OutlinedButton(onClick = onPrevious) { Text("Précédent") }
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (currentStep < 3) {
                OutlinedButton(onClick = onSavePartial) { Text("Sauvegarder") }
                Button(onClick = onNext) { Text("Suivant") }
            } else {
                Button(onClick = onSubmit) { Text("Valider") }
            }
        }
    }
}