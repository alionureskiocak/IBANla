package com.example.ibanla.presentation.iban

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    val selectedTab = state.currentTab
    var showIbanDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    var clickedForNewIban by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val showTick by viewModel.showTick.collectAsState()

    TODO("Card'a tıklayınca hangi iban entity'sine tıkladığımı bilmiyorum bunu bulup gerekli yerlere" +
            "yazdırmam lazım")


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showIbanDialog = true
                    clickedForNewIban = true
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

            val selectedIndex = when(selectedTab){
                IbanTab.MY -> 0
                IbanTab.OTHER -> 1
            }
            var showFirst = selectedTab == IbanTab.MY
            val titles = listOf("IBAN'larım", "Diğer IBAN'lar")


            if (showIbanDialog) {
                IbanDialogScreen(
                    clickedForNewIban = clickedForNewIban,
                    categories = categories,
                    showFirst = showFirst,
                    viewModel = viewModel,
                    onDismiss = {
                        showIbanDialog = false
                    },
                    onCategoryAdded = {
                        showCategoryDialog = true
                        showIbanDialog = false
                    })
            }
            if (showCategoryDialog){
                CategoryDialog(
                    onDismiss = {
                        showIbanDialog = true
                        showCategoryDialog = false
                    },
                    onCategoryAdded = {
                        viewModel.addCategory(
                            CategoryEntity(
                                categoryName = it
                            )
                        )
                    }
                )
            }

            SingleChoiceSegmentedButtonRow {
                titles.forEachIndexed { index, item ->
                    SegmentedButton(
                        selected = selectedIndex == index,
                        onClick = {
                            viewModel.onTabSelected(
                                if (index == 0) IbanTab.MY else IbanTab.OTHER
                            )

                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = titles.size
                        )
                    ) {
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
                if (it) {

                    LazyColumn {
                        items(myIbansWithCategory) { (ibanItem, category) ->
                            IbanCard(
                                label = category.categoryName,
                                fullName = ibanItem.ownerName,
                                iban = ibanItem.iban,
                                showTick = showTick,
                                onCardClick = {
                                    showIbanDialog = true
                                    clickedForNewIban = false
                                },
                                onCopyClick = { iban ->
                                    clipboardManager.setText(
                                        AnnotatedString(iban)
                                    )
                                    Toast.makeText(context,"Copied",Toast.LENGTH_SHORT).show()
                                    viewModel.startCopiedTimer()
                                }
                            )
                        }

                    }

                } else {
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
                                    showTick = showTick,
                                    onCardClick = {
                                        showIbanDialog = true
                                        clickedForNewIban = false
                                    },
                                    onCopyClick = { iban ->
                                        clipboardManager.setText(
                                            AnnotatedString(iban)
                                        )
                                        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                                        viewModel.startCopiedTimer()
                                    })
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IbanDialogScreen(
    clickedForNewIban : Boolean,
    categories: List<CategoryEntity>,
    showFirst: Boolean,
    viewModel: IbanViewModel,
    onDismiss: () -> Unit,
    onCategoryAdded : () -> Unit,
) {

    var ibanText by remember { mutableStateOf("") }
    var ownerText by remember { mutableStateOf("") }

    var ibanUpdateText by remember { mutableStateOf(ibanText) }
    var ownerUpdateText by remember { mutableStateOf(ownerText) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    Column(

    ) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = if (clickedForNewIban)"Yeni IBAN Ekle" else "IBAN Bilgileri",
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
                                label = { Text(category.categoryName, textAlign = TextAlign.Center) }
                            )

                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        FilterChip(
                            selected = false,
                            label = { Text(text = "Kategori Ekle") },
                            onClick = {
                                onCategoryAdded()
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = (clickedForNewIban && selectedCategory?.id != null && ibanText.length >= 26)
                            || selectedCategory?.id != null && ibanText.length >= 26 && (
                                    ibanText != ibanUpdateText || ownerText != ownerUpdateText
                                    )
                    ,
                    onClick = {
                        onDismiss()
                        if (clickedForNewIban){
                            viewModel.addIban(
                                IbanItem(
                                    id = 0,
                                    iban = ibanText,
                                    ownerName = ownerText,
                                    bankName = getBankNameByIban(ibanText),
                                    categoryId = selectedCategory?.id!!
                                )
                            )
                        }else{
                            TODO("viewmodel.update çağrılacak ama daha oluşturulmadı")
                        }

                    }
                ) {
                    Text(if(clickedForNewIban)"Ekle" else "Güncelle")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("İptal")
                }
            }
        )

    }
}


fun getLogoById(iban: String): Int {
    val cleaned = iban.replace(" ", "")
    val code = cleaned.substring(4, 9)

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

fun getBankNameByIban(iban: String): String {
    val cleaned = iban.replace(" ", "")
    val code = cleaned.substring(4, 9)

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
    showTick : Boolean,
    onCardClick : () -> Unit,
    onCopyClick: (String) -> Unit
) {
    val bankLogo = getLogoById(iban)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable{
                onCardClick()
            }
        ,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(bankLogo),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.width(12.dp))


            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = iban.chunked(4).joinToString(" "),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }


            Icon(
                imageVector =if (!showTick) Icons.Default.ContentCopy else Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onCopyClick(iban) },
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    onCategoryAdded: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Yeni Kategori Ekle",
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
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Kategori adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Örnek: Aile, İş, Arkadaşlar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                enabled = categoryName.isNotBlank(),
                onClick = {
                    onCategoryAdded(categoryName.trim())
                    onDismiss()
                }
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun IbanDetailsDialog(
    categories: List<CategoryEntity>,
    ibanText : String,
    ownerText : String,
    category : CategoryEntity,
    onUpdate : (IbanItem) -> Unit,
    onDelete : (IbanItem) -> Unit,
    onDismiss: () -> Unit,
    onCategoryAdded : () -> Unit
) {

    var iban by remember { mutableStateOf(ibanText) }
    var owner by remember { mutableStateOf(ownerText) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    // değişken tut, ibanText iban'a eşitse ve diğer durumda, değişiklik yok demek update butonu disabled
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "IBAN Detayları",
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
                    value = iban,
                    onValueChange = { iban = it },
                    label = { Text("IBAN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = owner,
                    onValueChange = { owner = it },
                    label = { Text("İsim") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
                            label = { Text(category.categoryName, textAlign = TextAlign.Center) }
                        )

                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                )
                {
                    FilterChip(
                        selected = false,
                        label = { Text(text = "Kategori Ekle") },
                        onClick = {
                            onCategoryAdded()
                        }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )

}

