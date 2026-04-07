package com.example.projet_dev_mobile.ui.screens.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.derivedStateOf
import com.example.projet_dev_mobile.data.entity.enum.RoleType
import com.example.projet_dev_mobile.data.network.dto.UserDto

@Composable
fun AdminScreen(viewModel: AdminViewModel) {

    val pendingUsers by remember {
        derivedStateOf { viewModel.users.filter { it.role == RoleType.NO_ROLE } }
    }

    val activeUsers by remember {
        derivedStateOf { viewModel.users.filter { it.role != RoleType.NO_ROLE } }
    }

    if (viewModel.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // --- SELECTION EN ATTENTE ---
            if (pendingUsers.isNotEmpty()) {
                item {
                    Text(
                        "En attente de validation (${pendingUsers.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF856404)
                    )
                }
                items(pendingUsers) { user ->
                    PendingUserRow(
                        user,
                        onAccept = { role -> viewModel.updateUserRole(user.id, role) },
                        onRefuse = { viewModel.deleteUser(user.id) }
                    )
                }
                item { HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp)) }
            }

            // --- SECTION USERS ACTIFS
            item {
                Text(
                    "Utilisateurs Actifs (${activeUsers.size})",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            items(activeUsers) { user ->
                ActiveUserRow(
                    user,
                    onRoleChange = { newRole -> viewModel.updateUserRole(user.id, newRole) },
                    onDelete = { viewModel.deleteUser(user.id) }
                )
            }
        }
    }
}
@Composable
fun PendingUserRow(user: UserDto, onAccept: (RoleType) -> Unit, onRefuse: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(user.login, Modifier.weight(1f), fontWeight = FontWeight.Bold)

            // Menu pour choisir le rôle et valider
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) { Text("Valider") }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    RoleType.entries.filter { it != RoleType.NO_ROLE }.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.name) },
                            onClick = { onAccept(role); expanded = false }
                        )
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onRefuse) {
                Icon(Icons.Default.Delete, contentDescription = "Refuser", tint = Color.Red)
            }
        }
    }
}



@Composable
fun ActiveUserRow(user: UserDto, onRoleChange: (RoleType) -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val isAdmin = user.role == RoleType.ADMIN

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.login, fontWeight = FontWeight.Bold)
                Text(user.role.name, style = MaterialTheme.typography.bodySmall)
            }

            // Sélecteur de rôle
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    enabled = !isAdmin
                ) {
                    Text("Modifier")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    RoleType.entries.filter { it != RoleType.NO_ROLE }.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.name) },
                            onClick = {
                                onRoleChange(role)
                                expanded = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                enabled = !isAdmin
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = if (isAdmin) Color.Gray else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}