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
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeursScreen
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeurJeuxScreen
import com.example.projet_dev_mobile.ui.screens.editeurs.EditeurJeuxViewModel
import com.example.projet_dev_mobile.ui.screens.jeux.JeuxScreen


object LoginDestination
object RegisterDestination
object PendingApprovalDestination

// festival
object FestivalEntryDestination
data class FestivalDetailsDestination(val id: Int)
data class EditeurDetailsDestination(val id: Int, val nom: String)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AppNavigation() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val api = remember { RetrofitInstance.getApiService(context) }

    var currentRole by remember { mutableStateOf(tokenManager.getRole()) }

    val backStack = remember {
        mutableStateListOf<Any>().apply {
            val role = tokenManager.getRole()
            if (role == RoleType.NO_ROLE) {
                add(PendingApprovalDestination)
            } else if (role != null) {
                add(Destination.FESTIVAL)
            } else {
                add(LoginDestination)
            }
        }
    }

    val isLoggedIn = currentRole != null
    val isPendingApproval = isLoggedIn && currentRole == RoleType.NO_ROLE

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
                            currentRole = null
                            backStack.clear()
                            backStack.add(LoginDestination)
                        }){
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
                                    currentRole == RoleType.ADMIN
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
            modifier = if (isFestivalScreen) Modifier else Modifier.padding(innerPadding),
            entryProvider = { key ->
                when (key) {
                    LoginDestination -> NavEntry(key) {
                        LoginScreen(
                            onLoginSuccess = {
                                val role = tokenManager.getRole()
                                currentRole = role
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
                                                currentRole = userRole
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
                                currentRole = null
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
                                userRole = currentRole,
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
                        EditeursScreen(
                            onEditeurClick = { editeur ->
                                backStack.add(EditeurDetailsDestination(editeur.id, editeur.nom))
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
                            EditeurJeuxScreen(viewModel = detailViewModel)
                        }
                    }
                    Destination.JEUX -> NavEntry(key) { JeuxScreen() }

                    Destination.ADMIN -> NavEntry(key) {
                        if (currentRole == RoleType.ADMIN) {
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