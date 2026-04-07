package com.example.projet_dev_mobile.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.projet_dev_mobile.data.entity.enum.RoleType
import com.example.projet_dev_mobile.ui.AppViewModelProvider
import com.example.projet_dev_mobile.ui.screens.dashboard.FestivalDetailsScreen
import com.example.projet_dev_mobile.ui.screens.dashboard.FestivalDetailsViewModel
import com.example.projet_dev_mobile.ui.screens.reservations.ReservationFormScreen
import com.example.projet_dev_mobile.ui.screens.reservations.ReservationFormViewModel
import com.example.projet_dev_mobile.ui.screens.reservations.ReservationViewModel
import com.example.projet_dev_mobile.ui.screens.reservations.ReservationsListScreen
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Destination spécifique pour le formulaire (permet de passer l'ID en paramètre)
// ---------------------------------------------------------------------------
data class ReservationFormDestination(val reservationId: Int? = null)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalNavigation(
    festivalId: Int,
    userRole: RoleType?,
    onBack: () -> Unit
) {
    val backStack = remember { mutableStateListOf<Any>(FestivalDestination.DASHBOARD) }
    val currentDestination = backStack.lastOrNull()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val viewModel: FestivalDetailsViewModel = viewModel(
        key = "festival_${festivalId}_${userRole?.name}",
        factory = AppViewModelProvider.festivalDetailsFactory(festivalId)
    )
    val uiState by viewModel.uiState.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet {
                        Text(
                            text = uiState.festival?.nom ?: "Festival #$festivalId",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                        HorizontalDivider()

                        FestivalDestination.entries.forEach { destination ->
                            // FIX VISIBILITÉ : Utilisation du paramètre userRole (source de vérité)
                            val isVisible = if (destination == FestivalDestination.RESERVATIONS) {
                                userRole == RoleType.ADMIN || userRole == RoleType.ORGANISATEUR_RESERVATIONS
                            } else true

                            if (isVisible) {
                                NavigationDrawerItem(
                                    icon = { Icon(destination.icon, destination.contentDescription) },
                                    label = { Text(destination.label) },
                                    selected = currentDestination == destination,
                                    onClick = {
                                        coroutineScope.launch { drawerState.close() }
                                        if (destination == FestivalDestination.ACCUEIL) {
                                            onBack()
                                        } else if (currentDestination != destination) {
                                            backStack.clear()
                                            backStack.add(destination)
                                        }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(uiState.festival?.nom ?: "Festival #$festivalId") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    if (backStack.size > 1) backStack.removeLastOrNull()
                                    else onBack()
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    coroutineScope.launch { drawerState.open() }
                                }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
                        onBack = {
                            if (backStack.size > 1) backStack.removeLastOrNull()
                            else onBack()
                        },
                        modifier = Modifier.padding(innerPadding),
                        entryProvider = { key ->
                            // DEBUT DU WHEN
                            when (key) {
                                FestivalDestination.DASHBOARD -> NavEntry(key) {
                                    FestivalDetailsScreen(viewModel = viewModel)
                                }

                                FestivalDestination.EDITEURS -> NavEntry(key) {
                                    Text("Éditeurs du festival $festivalId")
                                }

                                FestivalDestination.JEUX -> NavEntry(key) {
                                    Text("Jeux du festival $festivalId")
                                }

                                FestivalDestination.WORKFLOW -> NavEntry(key) {
                                    Text("Workflow du festival $festivalId")
                                }

                                FestivalDestination.RESERVATIONS -> NavEntry(key) {
                                    val hasEditRights = userRole == RoleType.ADMIN || userRole == RoleType.ORGANISATEUR_RESERVATIONS
                                    val reservationViewModel: ReservationViewModel = viewModel(factory = AppViewModelProvider.Factory)

                                    ReservationsListScreen(
                                        viewModel = reservationViewModel,
                                        festivalId = festivalId,
                                        isOrganisateur = hasEditRights,
                                        onNavigateToForm = { resaId -> backStack.add(ReservationFormDestination(resaId)) }
                                    )
                                }

                                is ReservationFormDestination -> NavEntry(key) {
                                    val formViewModel: ReservationFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
                                    ReservationFormScreen(
                                        viewModel = formViewModel,
                                        festivalId = festivalId,
                                        reservationIdToEdit = key.reservationId,
                                        onNavigateBack = { backStack.removeLastOrNull() }
                                    )
                                }

                                else -> NavEntry(Unit) { Text("Inconnu") }
                            }
                        }
                    )
                }
            }
        }
    }
}

fun List<Any>.isInFestivalContext(): Boolean =
    any { it is FestivalDetailsDestination }