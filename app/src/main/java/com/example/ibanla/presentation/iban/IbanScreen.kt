package com.example.ibanla.presentation.iban

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun IbanScreen(viewModel: IbanViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsState()
    val allIbans = state.ibanList
    val categorizedIbans = state.categorizedIbanList
    val currentIban = state.currentIban

}