package com.example.projet_dev_mobile.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.projet_dev_mobile.data.local.TokenManager
import com.example.projet_dev_mobile.ui.screens.home.HomeScreen
import com.example.projet_dev_mobile.ui.screens.login.LoginScreen

// On crée une destination spécifique pour le Login (hors de votre enum Destination)
// pour qu'elle n'apparaisse pas dans la BottomBar
object LoginDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    // 1. Récupération du contexte et vérification du token
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val isLoggedIn = !tokenManager.getToken().isNullOrEmpty()

    // 2. Initialisation du BackStack (si connecté -> FESTIVAL, sinon -> Login)
    val backStack = rememberSaveable {
        mutableStateListOf<Any>(if (isLoggedIn) Destination.FESTIVAL else LoginDestination)
    }

    val currentDestination = backStack.lastOrNull()

    // 3. Routage principal : On cache le Scaffold si on est sur l'écran de connexion
    if (currentDestination == LoginDestination) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    LoginDestination -> NavEntry(key) {
                        LoginScreen(
                            onLoginSuccess = {
                                // Quand le login réussit, on vide l'historique et on va sur Festival
                                backStack.clear()
                                backStack.add(Destination.FESTIVAL)
                            }
                        )
                    }
                    else -> NavEntry(Unit) { Text("Unknown Route") }
                }
            }
        )
    } else {
        // 4. Interface de l'application une fois connecté (avec barre de navigation)
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text("Gestion des Festivals")
                    },
                    navigationIcon = {
                        if (backStack.size > 1) {
                            IconButton(onClick = { backStack.removeLastOrNull() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Retour"
                                )
                            }
                        }
                    },
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Destination.entries.forEach { destination ->
                            NavigationBarItem(
                                selected = currentDestination == destination,
                                onClick = {
                                    // On évite d'ajouter la page si on est déjà dessus
                                    if (currentDestination != destination) {
                                        // On vide pour éviter un empilement infini des onglets (comportement standard)
                                        backStack.clear()
                                        backStack.add(destination)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.contentDescription
                                    )
                                },
                                label = { Text(destination.label) }
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                modifier = Modifier.padding(innerPadding),
                entryProvider = { key ->
                    when (key) {
                        Destination.FESTIVAL -> NavEntry(key) {
                            HomeScreen(
                                onLogout = {
                                    tokenManager.clear()
                                    backStack.clear()
                                    backStack.add(LoginDestination)
                                }
                            )
                        }

                        Destination.EDITEURS -> NavEntry(key) {
                            Text("Liste des editeurs")
                        }

                        Destination.JEUX -> NavEntry(key) {
                            Text("Liste des jeux")
                        }

                        Destination.ADMIN -> NavEntry(key) {
                            Text("Pannel Admin")
                        }

                        else -> NavEntry(Unit) { Text("Unknown route") }
                    }
                }
            )
        }
    }
}
