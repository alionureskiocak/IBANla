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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
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
import com.example.ibanla.domain.model.IbanItem

@Composable
fun IbanScreen(viewModel: IbanViewModel = hiltViewModel()) {

    println(getLogoById("123456789"))
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
            var selectedCategory by remember{mutableStateOf("")}

            if (showDialog){
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AlertDialog(
                        title = {},
                        text = {
                           Column(
                               modifier = Modifier.fillMaxSize()
                           ) {
                               TextField(value = ibanText, onValueChange = {ibanText = it},
                                   modifier = Modifier.padding(2.dp))
                               TextField(value = ownerText, onValueChange = {ownerText = it})

                               LazyColumn {
                                   items (categories) {
                                       Button(
                                           onClick = {
                                               viewModel.setCurrentCategory(it.categoryName)
                                           }
                                       ) {
                                           Text("Category")
                                           TODO("KATEGORİ EKLENMESİ TAMAMLANACAK")
                                       }
                                   }
                               }
                           }

                        },
                        onDismissRequest = {
                            showDialog = false
                        },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.addIban(
                                    IbanItem(
                                        0,
                                        ibanText,
                                        ownerText,
                                        getBankNameByIban(ibanText),
                                        currentCategory.id
                                    )
                                )
                            }) {
                                Text("Add IBAN")
                            }
                        },
                        dismissButton = {}
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
        "00001" -> R.drawable.ziraat
        "00006" -> R.drawable.isbankasi
        "00111" -> R.drawable.akbank
        "00062" -> R.drawable.qnb
        "00059" -> R.drawable.yapikredi
        "00017" -> R.drawable.halkbank
        "00010" -> R.drawable.vakifbank
        "00320" -> R.drawable.garanti
        else -> R.drawable.ic_launcher_background
    }
}

fun getBankNameByIban(iban : String) : String {
    val cleaned = iban.replace(" ", "")
    val code = cleaned.substring(4, 9)

    return when (code) {
        "00001" -> "Ziraat"
        "00006" -> "İş Bankası"
        "00111" -> "Akbank"
        "00062" -> "Qnb"
        "00059" -> "Yapı Kredi"
        "00017" -> "HalkBank"
        "00010" -> "VakıfBank"
        "00320" -> "Garanti"
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
    var isPressed by remember { mutableStateOf(false) }

    // Animation tanımlamaları Composable seviyesinde
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box {
            // Animated gradient background
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea).copy(alpha = 0.1f),
                            Color(0xFF764ba2).copy(alpha = 0.15f),
                            Color(0xFFf093fb).copy(alpha = 0.1f)
                        ),
                        start = Offset(size.width * offset, 0f),
                        end = Offset(size.width * (1 - offset), size.height)
                    ),
                    cornerRadius = CornerRadius(24.dp.toPx())
                )
            }

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Modern Label with glow effect
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Holographic accent
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Content with glassmorphism effect - DAHA BÜYÜK
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp) // 16dp'den 20dp'ye çıkarıldı
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = fullName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = iban,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp) // 8dp'den 12dp'ye
                        ) {
                            // Futuristic Copy Button - DAHA BÜYÜK
                            Box(
                                modifier = Modifier
                                    .size(52.dp) // 44dp'den 52dp'ye çıkarıldı
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                MaterialTheme.colorScheme.tertiaryContainer
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable { onCopyClick(iban) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy IBAN",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp) // 20dp'den 24dp'ye
                                )
                            }

                            // Bank Logo with neon border - DAHA BÜYÜK
                            Box(
                                modifier = Modifier
                                    .size(64.dp) // 48dp'den 64dp'ye çıkarıldı
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(16.dp) // 12dp'den 16dp'ye
                                    )
                                    .border(
                                        width = 2.5.dp, // 2dp'den 2.5dp'ye
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(8.dp) // 6dp'den 8dp'ye
                            ) {
                                Image(
                                    painter = painterResource(id = bankLogo),
                                    contentDescription = "Bank Logo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}