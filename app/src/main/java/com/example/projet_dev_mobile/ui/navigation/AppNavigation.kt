package com.example.projet_dev_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue


@Composable
fun AppNavigation() {
    var token by rememberSaveable { mutableStateOf<String?>(null) }
    val backStack = rememberSaveable { mutableStateListOf<Any>(Destination.HOME) }


}


