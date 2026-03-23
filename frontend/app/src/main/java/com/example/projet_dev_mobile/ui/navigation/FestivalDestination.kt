package com.example.projet_dev_mobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Festival
import androidx.compose.material.icons.filled.Groups
import androidx.compose.ui.graphics.vector.ImageVector

enum class FestivalDestination (
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    ACCUEIL("accueil", "Accueil", Icons.Default.Festival, "Accueil"),
    DASHBOARD("dashboard", "Dashboard", Icons.Default.Dashboard, "Dashboard"),
    EDITEURS("editeurs", "Editeurs", Icons.Default.Groups, "Editeurs"),
    JEUX("jeux", "Jeux", Icons.Default.Casino, "Jeux"),
    WORKFLOW("workflow", "Workflow", Icons.Default.Autorenew, "Workflow"),
    RESERVATIONS("reservations", "Reservations", Icons.Default.Event, "Reservations")
}