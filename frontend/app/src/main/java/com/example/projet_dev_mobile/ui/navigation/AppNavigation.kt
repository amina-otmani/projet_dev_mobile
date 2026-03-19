package com.example.projet_dev_mobile.ui.navigation

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_dev_mobile.ui.AppViewModelProvider
import com.example.projet_dev_mobile.ui.screens.festival.FestivalDetailsScreen
import com.example.projet_dev_mobile.ui.screens.festival.FestivalDetailsViewModel
import com.example.projet_dev_mobile.ui.screens.home.FestivalHomeScreen
import com.example.projet_dev_mobile.ui.screens.festival.FestivalEntryScreen




object LoginDestination
object RegisterDestination
object PendingApprovalDestination

// festival
object FestivalEntryDestination
object FestivalEditDestination
data class FestivalDetailsDestination(val id: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val coroutineScope = rememberCoroutineScope()
    val api = remember { RetrofitInstance.getApiService(context) }

    val currentRole = remember { tokenManager.getRole() }
    val isLoggedIn = remember { currentRole != null }
    val isPendingApproval = remember { isLoggedIn && currentRole == RoleType.NO_ROLE }

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

    val isAuthScreen = currentDestination == LoginDestination ||
            currentDestination == RegisterDestination ||
            currentDestination == PendingApprovalDestination

    Scaffold(
        topBar = {
            if (!isAuthScreen) {
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
            if (!isAuthScreen) {
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
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(innerPadding),
            entryProvider = { key ->
                when (key) {
                    LoginDestination -> NavEntry(key) {
                        LoginScreen(
                            onLoginSuccess = {
                                val role = tokenManager.getRole()
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
                        val destination = key  // capture the typed key outside NavEntry
                        NavEntry(destination) {
                            val viewModel: FestivalDetailsViewModel = viewModel(
                                key = "festival_${destination.id}",
                                factory = AppViewModelProvider.festivalDetailsFactory(destination.id)
                            )
                            FestivalDetailsScreen(
                                onBack = { backStack.removeLastOrNull() },
                                viewModel = viewModel
                            )
                        }
                    }
                    FestivalEntryDestination -> NavEntry(key){
                        FestivalEntryScreen(
                            navigateBack = { backStack.removeLastOrNull() }
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
