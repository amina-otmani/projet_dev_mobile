package com.example.projet_dev_mobile.ui.screens.editeurs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.getColumnIndex
import com.example.projet_dev_mobile.ui.AppViewModelProvider
import com.example.projet_dev_mobile.ui.screens.jeux.JeuInputForm
import com.example.projet_dev_mobile.utils.FormSelectionTitle
import kotlinx.coroutines.launch

@Composable
fun EditeurEntryScreen(
    navigateBack: () -> Unit,
    viewModel: EditeurEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold() {
        innerPadding ->
        EditeurEntryBody(
            editeurUiState = viewModel.editeurUiState,
            onEditeurValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    val success = viewModel.saveEditeur()
                    if (success) {
                        navigateBack()
                    }
                }
            },
            onAddContact = viewModel::addEmptyContact,
            onDeleteContact = viewModel::deleteContact,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun EditeurEntryBody(
    editeurUiState: EditeurUiState,
    onEditeurValueChange: (EditeurDetails) -> Unit,
    onSaveClick: () -> Unit,
    onAddContact: () -> Unit,
    onDeleteContact: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(8.dp)
    ) {
        EditeurInputForm(
            editeurDetails = editeurUiState.editeurDetails,
            onValueChange = onEditeurValueChange,
            onAddContact = onAddContact,
            onDeleteContact = onDeleteContact,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer")
        }
    }
}

@Composable
fun EditeurInputForm(
    editeurDetails: EditeurDetails,
    modifier: Modifier = Modifier,
    onValueChange: (EditeurDetails) -> Unit = {},
    onAddContact: () -> Unit,
    onDeleteContact: (Int) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FormSelectionTitle("Informations Générales")
        OutlinedTextField(
            value = editeurDetails.nom,
            onValueChange = { onValueChange(editeurDetails.copy(nom = it)) },
            label = { Text("Nom de l'éditeur") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.padding(16.dp))

        FormSelectionTitle("Contacts")

        editeurDetails.contacts.forEachIndexed { index, contact ->
            ContactItem(
                contact = contact,
                onDelete = { onDeleteContact(index) }
            )
        }

        TextButton(
            onClick = onAddContact,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width((8.dp)))
            Text("Ajouter un contact")
        }
    }
}

// sous composant pour le contact
@Composable
fun ContactItem(
    contact: ContactDetails,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text("Contact", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer contact")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = contact.prenom,
                    onValueChange = {},
                    label = { Text("Prénom") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = contact.nom,
                    onValueChange = {},
                    label = { Text("Nom") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            OutlinedTextField(
                value = contact.email,
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            OutlinedTextField(
                value = contact.poste,
                onValueChange = {},
                label = { Text("Téléphone") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }
    }
}