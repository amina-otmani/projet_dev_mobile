package com.example.projet_dev_mobile.ui.screens.editeurs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet_dev_mobile.ui.AppViewModelProvider
import com.example.projet_dev_mobile.ui.screens.jeux.JeuInputForm
import kotlinx.coroutines.launch

@Composable
fun EditeurEntryScreen(
    navigateBack: () -> Unit,
    viewModel: EditeursViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold() {
        innerPadding ->
        EditeurEntryBody(
            editeurUiState = viewModel.editeurUiState,
            onEditeurValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveEditeur()
                    navigateBack()
                }
            },
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
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(8.dp)
    ) {
        EditeurInputForm(
            jeuDetails = editeurUiState.editeurDetails,
            onValueChange = onEditeurValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = editeurUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer")
        }
    }
}

@Composable
fun EditeurInputForm(
    EditeurDetails: EditeurDetails,
    modifier: Modifier = Modifier,
    onValueChange: (EditeurDetails) -> Unit = {},
    enabled: Boolean = true
) {



}