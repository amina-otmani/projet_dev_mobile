package com.example.projet_dev_mobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Festival
import androidx.compose.material.icons.filled.Groups
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination (
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    FESTIVAL("festival", "Festival", Icons.Default.Festival, "Festival"),
    EDITEURS("editeurs", "Editeurs", Icons.Default.Groups, "Editeurs"),
    JEUX("jeux", "Jeux", Icons.Default.Casino, "Jeux"),
    ADMIN("admin", "Admin", Icons.Default.AdminPanelSettings, "Admin")
}