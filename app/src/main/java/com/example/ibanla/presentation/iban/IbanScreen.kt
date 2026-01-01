package com.example.ibanla.presentation.iban

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ibanla.R
import com.example.ibanla.domain.model.Category
import com.example.ibanla.domain.model.IbanItem
import com.example.ibanla.domain.model.IbanWithCategory

@Composable
fun IbanScreen(viewModel: IbanViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsState()
    val categories = state.categories
    val currentIban = state.currentIban
    val currentCategory = state.currentCategory
    val myIbans = state.myIbans
    val otherIbansWithCategory = state.categorizedIbans
    val showTick = state.showTick
    val selectedTab = state.currentTab

    var showIbanDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var clickedForNewIban by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                is IbanEffect.ShowToast ->
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showIbanDialog = true
                    clickedForNewIban = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // ✅ Premium Header (göze hitap eden kısım)
            val totalCount = myIbans.size + otherIbansWithCategory.values.sumOf { it.size }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Kayıtlı IBAN'lar",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$totalCount kayıt",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Dialoglar (senin akışın aynen)
            val showFirst = selectedTab == IbanTab.MY

            if (showIbanDialog) {
                IbanDialogScreen(
                    clickedForNewIban = clickedForNewIban,
                    currentIban = currentIban,
                    currentCategory = currentCategory,
                    categories = categories,
                    showFirst = showFirst,
                    viewModel = viewModel,
                    onDismiss = { showIbanDialog = false },
                    onCategoryAdded = {
                        showCategoryDialog = true
                        showIbanDialog = false
                    }
                )
            }

            if (showCategoryDialog) {
                CategoryDialog(
                    onDismiss = {
                        showIbanDialog = true
                        showCategoryDialog = false
                    },
                    onCategoryAdded = {
                        viewModel.onEvent(
                            IbanEvent.AddCategory(Category(id = -1, name = it))
                        )
                    }
                )
            }

            // Segmented
            val selectedIndex = when (selectedTab) {
                IbanTab.MY -> 0
                IbanTab.OTHER -> 1
            }
            val titles = listOf("IBAN'larım", "Diğer IBAN'lar")

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                titles.forEachIndexed { index, item ->
                    SegmentedButton(
                        selected = selectedIndex == index,
                        onClick = {
                            viewModel.onEvent(
                                IbanEvent.TabSelected(if (index == 0) IbanTab.MY else IbanTab.OTHER)
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

            Spacer(Modifier.height(12.dp))

            // Animasyonlu geçiş (senin mevcut kurgun)
            AnimatedContent(
                targetState = showFirst,
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(
                            durationMillis = 450,
                            easing = FastOutSlowInEasing
                        ),
                        initialOffsetX = { if (targetState) -600 else 600 }
                    ) + fadeIn() togetherWith slideOutHorizontally(
                        animationSpec = tween(
                            durationMillis = 450,
                            easing = FastOutSlowInEasing
                        ),
                        targetOffsetX = { if (targetState) 600 else -600 }
                    )
                },
                label = "tabAnim"
            ) { isMyTab ->

                if (isMyTab) {

                    // ✅ Empty State (premium hissi)
                    if (myIbans.isEmpty()) {
                        EmptyIbanState(
                            onAddClick = {
                                showIbanDialog = true
                                clickedForNewIban = true
                            }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 96.dp)
                        ) {
                            items(myIbans) { ibanItem ->
                                val category = categories.first { it.id == 1000 }

                                IbanCard(
                                    ibanItem = ibanItem,
                                    category = category,
                                    showTick = showTick,
                                    onCardClick = { ibanWithCategory ->
                                        val selectedIban = ibanWithCategory.ibanItem
                                        viewModel.onEvent(IbanEvent.IbanSelected(selectedIban, category))
                                        viewModel.onEvent(IbanEvent.CategorySelected(category))
                                        showIbanDialog = true
                                        clickedForNewIban = false
                                    },
                                    onCopyClick = { iban ->
                                        clipboardManager.setText(AnnotatedString(iban))
                                        viewModel.onEvent(IbanEvent.CopyIban(iban))
                                    }
                                )
                            }
                        }
                    }

                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        otherIbansWithCategory.forEach { (category, ibans) ->
                            item {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            items(ibans) { ibanItem ->
                                IbanCard(
                                    ibanItem = ibanItem,
                                    category = category,
                                    showTick = showTick,
                                    onCardClick = { ibanWithCategory ->
                                        val selectedIban = ibanWithCategory.ibanItem
                                        viewModel.onEvent(IbanEvent.IbanSelected(selectedIban, category))
                                        viewModel.onEvent(IbanEvent.CategorySelected(category))
                                        showIbanDialog = true
                                        clickedForNewIban = false
                                    },
                                    onCopyClick = { iban ->
                                        clipboardManager.setText(AnnotatedString(iban))
                                        viewModel.onEvent(IbanEvent.CopyIban(iban))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyIbanState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Eğer bu drawable sende yoksa kendi ikonunu koy ya da bu Image'ı kaldır.
        Image(
            painter = painterResource(R.drawable.unknown),
            contentDescription = null,
            modifier = Modifier.size(160.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Henüz IBAN eklemedin",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "IBAN ekleyerek hızlıca kopyalayabilir\nve kategorilere ayırabilirsin.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.height(20.dp))

        Button(onClick = onAddClick) {
            Text("IBAN Ekle")
        }
    }
}

@Composable
fun IbanDialogScreen(
    clickedForNewIban: Boolean,
    currentIban: IbanItem,
    currentCategory: Category,
    categories: List<Category>,
    showFirst: Boolean,
    viewModel: IbanViewModel,
    onDismiss: () -> Unit,
    onCategoryAdded: () -> Unit,
) {

    var ibanText by remember { mutableStateOf("") }
    var ownerText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedCategoryChanged by remember { mutableStateOf(false) }

    LaunchedEffect(currentIban.id) {
        if (!clickedForNewIban) {
            ibanText = currentIban.iban
            ownerText = currentIban.ownerName
            selectedCategory = currentCategory
        } else {
            ibanText = ""
            ownerText = ""
            selectedCategory = null
            selectedCategoryChanged = false
        }
    }

    var ibanUpdateText by remember { mutableStateOf(ibanText) }
    var ownerUpdateText by remember { mutableStateOf(ownerText) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = if (clickedForNewIban) "Yeni IBAN Ekle" else "IBAN Bilgileri",
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
                                if (selectedCategory != category && !clickedForNewIban) {
                                    selectedCategoryChanged = true
                                }
                                selectedCategory = category
                            },
                            label = { Text(category.name, textAlign = TextAlign.Center) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilterChip(
                        selected = false,
                        label = { Text(text = "Yeni Kategori +") },
                        onClick = { onCategoryAdded() }
                    )
                }
            }
        },
        confirmButton = {

            if (!clickedForNewIban) {
                TextButton(
                    onClick = {
                        viewModel.onEvent(IbanEvent.DeleteIban(currentIban))
                        onDismiss()
                    }
                ) {
                    Text("Sil")
                }
            }

            Button(
                enabled =
                    (clickedForNewIban && selectedCategory?.id != null && ibanText.length >= 26)
                            || (selectedCategory?.id != null && ibanText.length >= 26 &&
                            (ibanText != ibanUpdateText || ownerText != ownerUpdateText))
                            || selectedCategoryChanged,
                onClick = {
                    onDismiss()
                    if (clickedForNewIban) {
                        viewModel.onEvent(
                            IbanEvent.AddIban(
                                IbanItem(
                                    id = 0,
                                    iban = ibanText,
                                    ownerName = ownerText,
                                    bankName = getBankNameByIban(ibanText),
                                    categoryId = selectedCategory?.id!!
                                )
                            )
                        )
                    } else {
                        viewModel.onEvent(
                            IbanEvent.UpdateIban(
                                IbanItem(
                                    currentIban.id,
                                    ibanText,
                                    ownerText,
                                    getBankNameByIban(ibanText),
                                    selectedCategory!!.id
                                )
                            )
                        )
                    }
                }
            ) {
                Text(if (clickedForNewIban) "Ekle" else "Güncelle")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("İptal")
            }
        }
    )
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
    ibanItem: IbanItem,
    category: Category,
    showTick: Boolean,
    onCardClick: (IbanWithCategory) -> Unit,
    onCopyClick: (String) -> Unit
) {
    val bankLogo = getLogoById(ibanItem.iban)

    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing))
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current
            ) {
                onCardClick(IbanWithCategory(ibanItem, category))
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ÜST SATIR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 12.dp, top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {

                    // ✅ LOGO daha sola + daha küçük
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(bankLogo),
                            contentDescription = null,
                            modifier = Modifier.size(52.dp)
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = ibanItem.ownerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // ✅ IBAN tek satır + taşarsa ellipsis
                        Text(
                            text = ibanItem.iban.replace(" ", ""),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                letterSpacing = 0.6.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // COPY BUTTON
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                        .clickable { onCopyClick(ibanItem.iban) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (!showTick) Icons.Default.ContentCopy else Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // ALT SATIR (kategori + banka) - ✅ padding küçüldü => kart daha kısa
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 78.dp,
                        end = 56.dp,
                        top = 6.dp,
                        bottom = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (!ibanItem.bankName.isNullOrEmpty()) {
                    Text(
                        text = "• ${ibanItem.bankName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
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
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
