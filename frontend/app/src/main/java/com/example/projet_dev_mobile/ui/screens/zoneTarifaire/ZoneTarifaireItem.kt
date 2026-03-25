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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.projet_dev_mobile.data.entity.ZonePlan
import com.example.projet_dev_mobile.data.entity.ZoneTarifaire

@Composable
fun ZoneTarifaireItem(
    zoneT: ZoneTarifaire,
    indexZT: Int,

    onDelete: () -> Unit,
    onUpdate: (ZoneTarifaire) -> Unit,

    onAddZP: (Int) -> Unit,
    onDeleteZP: (Int, Int) -> Unit,
    onUpdateZP: (Int, Int, ZonePlan) -> Unit,
) {
    OutlinedCard(
    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
    colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8F9FA))
    )
    {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Nouvelle zone tarifaire", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red)
                }
            }

            OutlinedTextField(
                value = zoneT.nom,
                onValueChange = { onUpdate(zoneT.copy(nom = it)) },
                label = { Text("Nom de la zone tarifaire") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = if (zoneT.prix_table == 0.0) "" else zoneT.prix_table.toString(),
                    onValueChange = { newValue: String ->
                        val sanitizedValue = newValue.replace(',', '.')
                        val price = sanitizedValue.toDoubleOrNull() ?: 0.0
                        onUpdate(zoneT.copy(prix_table = price))
                    },
                    label = { Text("Prix Table (€)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                OutlinedTextField(
                    value = if (zoneT.prix_m2 == 0.0) "" else zoneT.prix_m2.toString(),
                    onValueChange = { newValue: String ->
                        val sanitizedValue = newValue.replace(',', '.')
                        val priceM = sanitizedValue.toDoubleOrNull() ?: 0.0
                        onUpdate(zoneT.copy(prix_m2 = priceM))
                    },
                    label = { Text("Prix m² (€)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            // --- ZONE PLAN ---
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Zones Plans", style = MaterialTheme.typography.titleSmall)

            zoneT.zonesPlan.forEachIndexed { indexPlan, plan ->
                ZonePlanItem(
                    zone = plan,
                    onDelete = { onDeleteZP(indexZT, indexPlan) },
                    onUpdate = { updatedPlan -> onUpdateZP(indexZT, indexPlan, updatedPlan) }
                )
            }

            TextButton(
                onClick = { onAddZP(indexZT) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Ajouter un plan à cette zone")
            }
        }
    }
}

data class ZoneTarifaireDetails(
    val id: Int = 0,
    val nom: String = "",
    val prix_table: Double = 0.0,
    val prix_m2: Double = 0.0
)