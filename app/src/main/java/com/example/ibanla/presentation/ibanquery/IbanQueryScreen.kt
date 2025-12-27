package com.example.ibanla.presentation.ibanquery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun IbanQueryScreen(
    viewModel: QueryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var ibanText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "IBAN Sorgula",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = ibanText,
            onValueChange = { ibanText = it },
            label = { Text("IBAN") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Monospace
            )
        )

        Button(
            onClick = {
                viewModel.onEvent(
                    QueryEvent.FindBank(ibanText)
                )
            },
            enabled = ibanText.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sorgula")
        }

        // Sonuç Kartı
        if (state.bankInfo.name.isNotBlank()) {
            BankResultCard(
                bankName = state.bankInfo.name,
                logoRes = state.bankInfo.logo
            )
        }
    }
}

@Composable
fun BankResultCard(
    bankName: String,
    logoRes: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = logoRes),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = bankName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
