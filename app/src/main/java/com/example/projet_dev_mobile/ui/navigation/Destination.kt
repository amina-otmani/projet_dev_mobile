package com.example.projet_dev_mobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination (
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    FESTIVAL("festival", "Festival", Icons.Default.Home, "Festival"),
    EDITEURS("editeurs", "Editeurs", Icons.Default.Person, "Editeurs"),
    JEUX("jeux", "Jeux", Icons.Default.Home, "Jeux")
}