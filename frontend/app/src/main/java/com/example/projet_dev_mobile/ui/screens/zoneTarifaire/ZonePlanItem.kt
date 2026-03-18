package com.example.projet_dev_mobile.ui.screens.zoneTarifaire


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projet_dev_mobile.data.entity.ZonePlan
import com.example.projet_dev_mobile.data.entity.ZoneTarifaire

@Composable
fun ZonePlanItem(
    zone: ZonePlan,
    onDelete: () -> Unit,
    onUpdate: (ZonePlan) -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8F9FA))
    )
    {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nouvelle zone plan", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp))
            {
                OutlinedTextField(
                    value = zone.nom,
                    onValueChange = { onUpdate(zone.copy(nom = it)) },
                    label = { Text(
                        text = "Nom de la zone plan",
                        style = MaterialTheme.typography.labelSmall
                    ) },
                    modifier = Modifier.weight(3f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = if (zone.nbTables == 0) "" else zone.nbTables.toString(),
                    onValueChange = { newValue: String ->
                        val nb = newValue.toIntOrNull() ?: 0
                        onUpdate(zone.copy(nbTables = nb))
                    },
                    label = { Text(
                        text = "Nb de tables",
                        style = MaterialTheme.typography.labelSmall
                    ) },
                    modifier = Modifier.weight(1.8f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red)
                }
            }
        }
    }
}

data class ZonePlanDetails(
    val id: Int = 0,
    val nom: String = "",
    val nombre_tables: Int = 0
)