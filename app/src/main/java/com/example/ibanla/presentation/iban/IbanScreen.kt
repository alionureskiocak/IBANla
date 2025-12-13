package com.example.ibanla.presentation.iban

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ibanla.R
import com.example.ibanla.data.model.CategoryEntity
import com.example.ibanla.domain.model.IbanItem

@Composable
fun IbanScreen(viewModel: IbanViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsState()
    val allIbans = state.ibanList
    val categorizedIbans = state.categorizedIbanList
    val categories by viewModel.categories.collectAsState()
    val currentIban = state.currentIban
    val currentCategory = state.currentCategory
    println(categories)
    val myIbans by viewModel.myIbans.collectAsState()
    val myIbansWithCategory by viewModel.myIbansWithCategory.collectAsState()
    println(myIbans)
    val otherIbans by viewModel.categorizedIbans.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var selectedIndex by remember { mutableStateOf(0) }
            var showFirst by remember { mutableStateOf(true) }
            val titles = listOf("IBAN'larım","Diğer IBAN'lar")
            var ibanText by remember { mutableStateOf("") }
            var ownerText by remember {mutableStateOf("")}
            var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }

            if (showDialog){
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Text(
                                text = "Yeni IBAN Ekle",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                OutlinedTextField(
                                    value = ibanText,
                                    onValueChange = { ibanText = it },
                                    label = { Text("IBAN") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = ownerText,
                                    onValueChange = { ownerText = it },
                                    label = { Text("İsim") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(
                                    text = "Kategori Seç",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 100.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.heightIn(max = 200.dp)
                                ) {
                                    items(categories) { category ->
                                        val isSelected = selectedCategory?.id == category.id

                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                selectedCategory = category
                                            },
                                            label = { Text(category.categoryName) }
                                        )

                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                enabled = selectedCategory?.id != null && ibanText.length >= 26,
                                onClick = {
                                    showDialog = false
                                    viewModel.addIban(
                                        IbanItem(
                                            id = 0,
                                            iban = ibanText,
                                            ownerName = ownerText,
                                            bankName = getBankNameByIban(ibanText),
                                            categoryId = selectedCategory?.id!!
                                        )
                                    )
                                }
                            ) {
                                Text("Ekle")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("İptal")
                            }
                        }
                    )

                }
            }

            SingleChoiceSegmentedButtonRow {
                titles.forEachIndexed { index , item ->
                    SegmentedButton(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            showFirst = !showFirst
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = titles.size)
                    ){
                        Text(item)
                    }
                }
            }


            AnimatedContent(
                targetState = showFirst,
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        ),
                        initialOffsetX = {
                            if (targetState) -600 else 600
                        }
                    ) + fadeIn() togetherWith slideOutHorizontally(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        ),
                        targetOffsetX = {
                            if (targetState) 600 else -600
                        }
                    )
                }
            ) {
                if (it){

                    LazyColumn {
                        items(myIbansWithCategory) { (ibanItem, category) ->
                            IbanCard(
                                label = category.categoryName,
                                fullName = ibanItem.ownerName,
                                iban = ibanItem.iban,
                            ) {}
                        }

                    }

                }
                else{
                    LazyColumn {
                        otherIbans.forEach { (category, ibans) ->
                            item {
                                Text(
                                    text = category.categoryName,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            items(ibans) { ibanItem ->
                                viewModel.getCategoryById(ibanItem.categoryId)
                                IbanCard(
                                    label = currentCategory.categoryName,
                                    fullName = ibanItem.ownerName,
                                    iban = ibanItem.iban,
                                ) { }
                            }

                        }
                    }
                }
            }
        }
    }


}


fun getLogoById(iban : String) : Int{
    val cleaned = iban.replace(" ","")
    val code = cleaned.substring(4,9)

    return when (code) {
        "00010" -> R.drawable.ziraat
        "00064" -> R.drawable.isbankasi
        "00111" -> R.drawable.qnb
        "00046" -> R.drawable.akbank
        "00067" -> R.drawable.yapikredi
        "00012" -> R.drawable.halkbank
        "00015" -> R.drawable.vakifbank
        "00062" -> R.drawable.garanti
        else -> R.drawable.ic_launcher_background
    }
}

fun getBankNameByIban(iban : String) : String {
    val cleaned = iban.replace(" ", "")
    val code = cleaned.substring(4,9)

    return when (code) {
        "00010" -> "Ziraat"
        "00064" -> "İş Bankası"
        "00111" -> "Qnb"
        "00046" -> "Akbank"
        "00067" -> "Yapı Kredi"
        "00012" -> "HalkBank"
        "00015" -> "VakıfBank"
        "00062" -> "Garanti"
        else -> ""
    }
}
@Composable
fun IbanCard(
    label: String,
    fullName: String,
    iban: String,
    onCopyClick: (String) -> Unit
) {
    val bankLogo = getLogoById(iban)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /* -------- BIG LOGO -------- */
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = bankLogo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            /* -------- TEXT AREA -------- */
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = fullName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = iban,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            /* -------- ACTION -------- */
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy IBAN",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onCopyClick(iban) }
            )
        }
    }
}

