package com.example.projet_dev_mobile.ui.navigation

import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.projet_dev_mobile.data.entity.enum.RoleType
import com.example.projet_dev_mobile.data.local.TokenManager
import com.example.projet_dev_mobile.data.network.RetrofitInstance
import com.example.projet_dev_mobile.ui.screens.login.LoginScreen
import com.example.projet_dev_mobile.ui.screens.pending.PendingApprovalScreen
import com.example.projet_dev_mobile.ui.screens.register.RegisterScreen
import kotlinx.coroutines.launch
import android.widget.Toast
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.projet_dev_mobile.ui.screens.home.FestivalHomeScreen
import com.example.projet_dev_mobile.ui.screens.festival.FestivalEntryScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_dev_mobile.ui.AppViewModelProvider
import com.example.projet_dev_mobile.ui.screens.admin.AdminScreen
import com.example.projet_dev_mobile.ui.screens.admin.AdminViewModel
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeurEntryScreen
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeursScreen
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeurJeuxScreen
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeurJeuxViewModel
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeursViewModel
import com.example.projet_dev_mobile.ui.screens.jeux.JeuDetailsScreen
import com.example.projet_dev_mobile.ui.screens.jeux.JeuDetailsViewModel
import com.example.projet_dev_mobile.ui.screens.jeux.JeuEntryScreen
import com.example.projet_dev_mobile.ui.screens.jeux.JeuxScreen




object LoginDestination
object RegisterDestination
object PendingApprovalDestination

// festival
object FestivalEntryDestination
data class FestivalDetailsDestination(val id: Int)

data class JeuDetailsDestination(val id: Int)
object JeuEntryDestination
data class EditeurDetailsDestination(val id: Int, val nom: String)
object EditeurEntryDestination

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val coroutineScope = rememberCoroutineScope()
    val api = remember { RetrofitInstance.getApiService(context) }

    val currentRole = remember { mutableStateOf(tokenManager.getRole()) }
    val isLoggedIn = currentRole.value != null
    val isPendingApproval = isLoggedIn && currentRole.value == RoleType.NO_ROLE

    val backStack = remember {
        mutableStateListOf<Any>().apply {
            if (isPendingApproval) {
                add(PendingApprovalDestination)
            } else if (isLoggedIn) {
                add(Destination.FESTIVAL)
            } else {
                add(LoginDestination)
            }
        }
    }

    val currentDestination = backStack.lastOrNull()
    val isFestivalScreen = backStack.isInFestivalContext() ||
            currentDestination == FestivalEntryDestination
    val isAuthScreen = currentDestination == LoginDestination ||
            currentDestination == RegisterDestination ||
            currentDestination == PendingApprovalDestination
    val hideChrome = isAuthScreen ||
            isFestivalScreen ||
            currentDestination == FestivalEntryDestination ||
            currentDestination is EditeurDetailsDestination

    Scaffold(
        topBar = {
            if (!isAuthScreen && !isFestivalScreen) {
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
                    actions = {
                        IconButton(onClick = {
                            tokenManager.clear()
                            backStack.clear()
                            backStack.add(LoginDestination)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Se déconnecter",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (!hideChrome) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Destination.entries

                            .filter { destination ->
                                if (destination == Destination.ADMIN) {
                                    currentRole.value == RoleType.ADMIN
                                }
                                else {
                                    true
                                }

                            }

                            .forEach { destination ->
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
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            // car FestivalDestination a deja un innerPadding
            modifier = if (isFestivalScreen) Modifier else Modifier.padding(innerPadding),
            entryProvider = { key ->
                when (key) {
                    LoginDestination -> NavEntry(key) {
                        LoginScreen(
                            onLoginSuccess = {
                                val role = tokenManager.getRole()
                                currentRole.value = role
                                backStack.clear()
                                if (role == RoleType.NO_ROLE) {
                                    backStack.add(PendingApprovalDestination)
                                } else {
                                    backStack.add(Destination.FESTIVAL)
                                }
                            },
                            onNavigateToRegister = {
                                backStack.add(RegisterDestination)
                            }
                        )
                    }
                    RegisterDestination -> NavEntry(key) {
                        RegisterScreen(
                            onRegisterSuccess = {
                                backStack.clear()
                                backStack.add(PendingApprovalDestination)
                            },
                            onNavigateToLogin = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                    PendingApprovalDestination -> NavEntry(key) {
                        PendingApprovalScreen(
                            onRefresh = {
                                coroutineScope.launch {
                                    try {
                                        val response = api.whoami()

                                        if (response.isSuccessful) {
                                            val userRole = response.body()?.user?.role

                                            if (userRole != null && userRole != RoleType.NO_ROLE) {
                                                tokenManager.saveRole(userRole)
                                                currentRole.value = userRole
                                                backStack.clear()
                                                backStack.add(Destination.FESTIVAL)
                                            }
                                        } else {
                                            if (response.code() == 401 || response.code() == 403) {
                                                // La session a expiré -> retour au login
                                                tokenManager.clear()
                                                backStack.clear()
                                                backStack.add(LoginDestination)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e("PendingApproval", "Erreur lors du refresh : ${e.message}")
                                        Toast.makeText(context, "Erreur réseau, impossible de vérifier le statut.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onLogout = {
                                tokenManager.clear()
                                currentRole.value = null
                                backStack.clear()
                                backStack.add(LoginDestination)
                            }
                        )
                    }
                    Destination.FESTIVAL -> NavEntry(key) {
                        FestivalHomeScreen(
                            onNavigateToEntry = { backStack.add(FestivalEntryDestination)},
                            onFestivalClick = { festivalId ->
                                backStack.add(FestivalDetailsDestination(festivalId))
                            }
                        )
                    }
                    is FestivalDetailsDestination -> {
                        val destination = key
                        NavEntry(destination) {
                            FestivalNavigation(
                                festivalId = destination.id,
                                onBack = { backStack.removeLastOrNull() }
                            )
                        }
                    }
                    FestivalEntryDestination -> NavEntry(key){
                        FestivalEntryScreen(
                            navigateBack = { backStack.removeLastOrNull() }
                        )
                    }
                    Destination.EDITEURS -> NavEntry(key) {
                        // accompagne le LaunchedEffect de EditeurScreen.
                        val editeursViewModel: EditeursViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        EditeursScreen(
                            viewModel = editeursViewModel,
                            onEditeurClick = { editeur ->
                                backStack.add(EditeurDetailsDestination(editeur.id, editeur.nom))
                            },
                            onNavigateToEntry = {
                                backStack.add(EditeurEntryDestination)
                            }
                        )
                    }
                    is EditeurDetailsDestination -> {
                        val destination = key
                        NavEntry(destination) {
                            val detailViewModel = viewModel<EditeurJeuxViewModel>(
                                key = "editeur_${destination.id}",
                                factory = AppViewModelProvider.editeurJeuxFactory(
                                    editeurId = destination.id,
                                    editeurNom = destination.nom
                                )
                            )
                            EditeurJeuxScreen(
                                viewModel = detailViewModel,
                                onJeuClick = { jeuId ->
                                    backStack.add(JeuDetailsDestination(jeuId))
                                }
                            )
                        }
                    }
                    EditeurEntryDestination -> NavEntry(key) {
                        EditeurEntryScreen(
                            navigateBack = { backStack.removeLastOrNull() }
                        )
                    }
                    Destination.JEUX -> NavEntry(key) {
                        JeuxScreen(
                            onJeuClick = { jeuId ->
                                backStack.add(JeuDetailsDestination(jeuId))
                            },
                            onNavigateToEntry = {
                                backStack.add(JeuEntryDestination)
                            }
                        )
                    }
                    is JeuDetailsDestination -> {
                        val destination = key
                        NavEntry(destination) {
                            val detailViewModel = viewModel<JeuDetailsViewModel>(
                                key = "jeu_${destination.id}",
                                factory = AppViewModelProvider.jeuDetailsFactory(destination.id)
                            )
                            JeuDetailsScreen(
                                viewModel = detailViewModel,
                                onBack = { backStack.removeLastOrNull() },
                                onEditeurClick = { editeurId, editeurNom ->
                                    backStack.add(EditeurDetailsDestination(editeurId, editeurNom))
                                }
                            )
                        }
                    }
                    JeuEntryDestination -> NavEntry(key) {
                        JeuEntryScreen(
                            navigateBack = { backStack.removeLastOrNull() }
                        )
                    }

                    Destination.ADMIN -> NavEntry(key) {
                        if (currentRole.value == RoleType.ADMIN) {
                            val adminViewModel: AdminViewModel = viewModel(
                                factory = AppViewModelProvider.Factory
                            )
                            AdminScreen(viewModel = adminViewModel)
                        }
                        else {
                            Text("Accès non autorisé")
                        }
                    }
                    else -> NavEntry(Unit) { Text("Unknown route") }
                }
            }
        )
    }
}
