package com.example.projet_dev_mobile.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.projet_dev_mobile.data.local.TokenManager
import com.example.projet_dev_mobile.ui.screens.home.HomeScreen
import com.example.projet_dev_mobile.ui.screens.login.LoginScreen

object LoginDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current

    // 1. On mémorise le tokenManager pour ne pas surcharger la mémoire
    val tokenManager = remember { TokenManager(context) }

    // 2. On vérifie l'état de connexion uniquement au lancement
    val isLoggedIn = remember { !tokenManager.getRole().isNullOrEmpty() }

    // 3. Utiliser remember au lieu de rememberSaveable
    val backStack = remember {
        mutableStateListOf<Any>(if (isLoggedIn) Destination.FESTIVAL else LoginDestination)
    }

    val currentDestination = backStack.lastOrNull()

    // On crée un booléen pour savoir si on est sur la page de connexion
    val isLoginScreen = currentDestination == LoginDestination

    // 4. UN SEUL Scaffold pour toute l'appli.
    // S'il s'agit de la page de login, on laisse les Top/Bottom bar vides !
    Scaffold(
        topBar = {
            if (!isLoginScreen) { // Affiche la topBar seulement si on n'est pas sur le login
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("Gestion des Festivals") },
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
            }
        },
        bottomBar = {
            if (!isLoginScreen) { // Affiche la navbar seulement si on n'est pas sur le login
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
                                    if (currentDestination != destination) {
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
            }
        },
    ) { innerPadding ->
        // Le Padding (innerPadding) s'adaptera automatiquement à 0 si on cache les barres
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(innerPadding),
            entryProvider = { key ->
                when (key) {
                    LoginDestination -> NavEntry(key) {
                        LoginScreen(
                            onLoginSuccess = {
                                backStack.clear()
                                backStack.add(Destination.FESTIVAL)
                            }
                        )
                    }
                    Destination.FESTIVAL -> NavEntry(key) {
                        HomeScreen(
                            onLogout = {
                                tokenManager.clear()
                                backStack.clear()
                                backStack.add(LoginDestination)
                            }
                        )
                    }
                    Destination.EDITEURS -> NavEntry(key) { Text("Liste des editeurs") }
                    Destination.JEUX -> NavEntry(key) { Text("Liste des jeux") }
                    Destination.ADMIN -> NavEntry(key) { Text("Pannel Admin") }
                    else -> NavEntry(Unit) { Text("Unknown route") }
                }
            }
        )
    }
}