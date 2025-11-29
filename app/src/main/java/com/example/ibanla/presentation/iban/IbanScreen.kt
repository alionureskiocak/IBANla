package com.example.ibanla.presentation.iban

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.selects.select

@Composable
fun IbanScreen(viewModel: IbanViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsState()
    val allIbans = state.ibanList
    val categorizedIbans = state.categorizedIbanList
    val currentIban = state.currentIban


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        var selectedIndex by remember { mutableStateOf(0) }
        val titles = listOf("IBAN'larım","Diğer IBAN'lar")

        SingleChoiceSegmentedButtonRow {
            titles.forEachIndexed { index , item ->
                SegmentedButton(
                    selected = selectedIndex == index,
                    onClick = {
                        selectedIndex = index
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = titles.size)
                ){
                    Text(item)
                }
            }
        }

        if (selectedIndex == 0){
            Text("Benim ibanlar burda")
        }else{
            Text("Diğer ibanlar burda")
        }
    }

}