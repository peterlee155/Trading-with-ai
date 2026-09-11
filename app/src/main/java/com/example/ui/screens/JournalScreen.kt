package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TradeLog
import com.example.data.repository.TradeRepository
import com.example.ui.components.TradeCard
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonInvalidation
import com.example.ui.theme.CyanQuantitative
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldDisciplined
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDim
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateSurfaceVariant
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun JournalScreen(
    tradeRepository: TradeRepository,
    modifier: Modifier = Modifier
) {
    val trades by tradeRepository.allTrades.collectAsState(initial = emptyList())
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val filteredTrades = remember(trades, selectedFilter) {
        when (selectedFilter) {
            "ALL" -> trades
            "TUITION" -> trades.filter { it.isTuitionTrade }
            "GOOD_PROCESS" -> trades.filter { it.isGoodTrade }
            "BAD_PROCESS" -> trades.filter { !it.isGoodTrade }
            "FOMO" -> trades.filter { it.classification.contains("FOMO", ignoreCase = true) }
            "REVENGE" -> trades.filter { it.classification.contains("Revenge", ignoreCase = true) }
            "MOVED_STOP" -> trades.filter { it.classification.contains("Stop", ignoreCase = true) }
            "VALID" -> trades.filter { it.classification == "Valid Setup" }
            else -> trades
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GoldAccent,
                contentColor = Color.Black,
                modifier = Modifier.testTag("fab_add_trade")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Trade")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Outcome vs Process Philosophy Card
            OutcomeBiasEducationalCard()

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips Horizontal Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All (${trades.size})") }
                )
                FilterChip(
                    selected = selectedFilter == "TUITION",
                    onClick = { selectedFilter = "TUITION" },
                    label = { Text("Tuition History ($10k)") },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(14.dp)) }
                )
                FilterChip(
                    selected = selectedFilter == "GOOD_PROCESS",
                    onClick = { selectedFilter = "GOOD_PROCESS" },
                    label = { Text("Good Process") },
                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldDisciplined, modifier = Modifier.size(14.dp)) }
                )
                FilterChip(
                    selected = selectedFilter == "BAD_PROCESS",
                    onClick = { selectedFilter = "BAD_PROCESS" },
                    label = { Text("Bad Process") },
                    leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonInvalidation, modifier = Modifier.size(14.dp)) }
                )
                FilterChip(
                    selected = selectedFilter == "REVENGE",
                    onClick = { selectedFilter = "REVENGE" },
                    label = { Text("Revenge Trades") }
                )
                FilterChip(
                    selected = selectedFilter == "FOMO",
                    onClick = { selectedFilter = "FOMO" },
                    label = { Text("FOMO Trades") }
                )
                FilterChip(
                    selected = selectedFilter == "MOVED_STOP",
                    onClick = { selectedFilter = "MOVED_STOP" },
                    label = { Text("Moved Stop Loss") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredTrades.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trades match this filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTrades, key = { it.id }) { trade ->
                        TradeCard(
                            trade = trade,
                            onDelete = { t ->
                                scope.launch { tradeRepository.deleteTrade(t) }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTradeDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newTrade ->
                scope.launch {
                    tradeRepository.addTrade(newTrade)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun OutcomeBiasEducationalCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateSurfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = CyanQuantitative,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PROCESS QUALITY VS. OUTCOME BIAS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = CyanQuantitative,
                    letterSpacing = 0.8.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• A GOOD TRADE THAT LOST: Followed setup, sized properly (<=1%), respected stop loss without flinching. Taking planned losses is the standard operating expense of an edge.\n• A BAD TRADE THAT WON: Gambled, oversized, removed stop loss, and got lucky on a random bounce. This reinforces lethal habits and leads to eventual liquidation.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun AddTradeDialog(
    onDismiss: () -> Unit,
    onSave: (TradeLog) -> Unit
) {
    var symbol by remember { mutableStateOf("") }
    var marketType by remember { mutableStateOf("STOCKS") }
    var timeframe by remember { mutableStateOf("1D") }
    var thesis by remember { mutableStateOf("") }
    var entryPrice by remember { mutableStateOf("") }
    var stopPrice by remember { mutableStateOf("") }
    var targetPrice by remember { mutableStateOf("") }
    var exitPrice by remember { mutableStateOf("") }
    var pnl by remember { mutableStateOf("") }
    var accountSize by remember { mutableStateOf("10000") }
    var riskPercent by remember { mutableStateOf("1.0") }
    var classification by remember { mutableStateOf("Valid Setup") }
    var emotionalState by remember { mutableStateOf("Calm & Objective") }
    var isGoodTrade by remember { mutableStateOf(true) }
    var rootCause by remember { mutableStateOf("None / Disciplined") }
    var notes by remember { mutableStateOf("") }

    val classifications = listOf(
        "Valid Setup",
        "Invalid Setup",
        "Impulsive Trade",
        "Revenge Trade",
        "FOMO Trade",
        "Oversized Trade",
        "News-driven Trade",
        "Unclear Thesis",
        "Premature Exit",
        "Late Exit"
    )

    val rootCauses = listOf(
        "None / Disciplined",
        "Poor Strategy",
        "Poor Execution",
        "Poor Risk Management",
        "Excessive Leverage",
        "Overtrading",
        "Emotional Decision",
        "Insufficient Research"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Document Trade in Journal", fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = symbol,
                            onValueChange = { symbol = it },
                            label = { Text("Symbol (e.g. NVDA, BTC)") },
                            modifier = Modifier.weight(1.2f).testTag("dialog_symbol"),
                            singleLine = true
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Market", style = MaterialTheme.typography.labelSmall)
                            Row {
                                FilterChip(
                                    selected = marketType == "STOCKS",
                                    onClick = { marketType = "STOCKS" },
                                    label = { Text("Stock", fontSize = 10.sp) }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                FilterChip(
                                    selected = marketType == "CRYPTO",
                                    onClick = { marketType = "CRYPTO" },
                                    label = { Text("Crypto", fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = entryPrice,
                            onValueChange = { entryPrice = it },
                            label = { Text("Entry $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = stopPrice,
                            onValueChange = { stopPrice = it },
                            label = { Text("Stop $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = targetPrice,
                            onValueChange = { targetPrice = it },
                            label = { Text("Target $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = exitPrice,
                            onValueChange = { exitPrice = it },
                            label = { Text("Exit Price $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = pnl,
                            onValueChange = { pnl = it },
                            label = { Text("Realized P&L ($)") },
                            placeholder = { Text("-250.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("dialog_pnl"),
                            singleLine = true
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = thesis,
                        onValueChange = { thesis = it },
                        label = { Text("Thesis & Rationale") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }

                item {
                    Text("Classification", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        classifications.forEach { c ->
                            FilterChip(
                                selected = classification == c,
                                onClick = { classification = c },
                                label = { Text(c, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    Text("Primary Root Cause", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rootCauses.forEach { rc ->
                            FilterChip(
                                selected = rootCause == rc,
                                onClick = { rootCause = rc },
                                label = { Text(rc, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isGoodTrade,
                            onCheckedChange = { isGoodTrade = it },
                            colors = CheckboxDefaults.colors(checkedColor = EmeraldDisciplined)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Good Trade (Process Adherence)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text("Did you strictly obey your plan, stop loss, and position size?", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = emotionalState,
                        onValueChange = { emotionalState = it },
                        label = { Text("Emotional State (e.g. Calm, FOMO, Revenge, Tilt)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Post-Mortem Lessons & Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (symbol.isNotBlank()) {
                        val ep = entryPrice.toDoubleOrNull() ?: 0.0
                        val sp = stopPrice.toDoubleOrNull() ?: 0.0
                        val tp = targetPrice.toDoubleOrNull() ?: 0.0
                        val exp = exitPrice.toDoubleOrNull()
                        val pnlVal = pnl.toDoubleOrNull() ?: 0.0
                        val acc = accountSize.toDoubleOrNull() ?: 10000.0
                        val rp = riskPercent.toDoubleOrNull() ?: 1.0

                        val stopDist = abs(ep - sp)
                        val targetDist = abs(tp - ep)
                        val rr = if (stopDist > 0) targetDist / stopDist else 0.0
                        val riskAmt = acc * (rp / 100.0)
                        val posSize = if (stopDist > 0) riskAmt / stopDist else 0.0
                        val pnlR = if (riskAmt > 0) pnlVal / riskAmt else 0.0

                        val trade = TradeLog(
                            symbol = symbol.uppercase(),
                            marketType = marketType,
                            timeframe = timeframe,
                            thesis = if (thesis.isNotBlank()) thesis else "No thesis provided.",
                            entryPrice = ep,
                            stopLossPrice = sp,
                            targetPrice = tp,
                            exitPrice = exp,
                            positionSize = posSize,
                            accountSizeAtEntry = acc,
                            riskAmount = riskAmt,
                            riskPercent = rp,
                            rewardRiskRatio = rr,
                            pnl = pnlVal,
                            pnlR = pnlR,
                            classification = classification,
                            emotionalState = emotionalState,
                            isGoodTrade = isGoodTrade,
                            rootCause = rootCause,
                            notes = notes,
                            isTuitionTrade = false
                        )
                        onSave(trade)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                modifier = Modifier.testTag("dialog_save_button")
            ) {
                Text("Save to Journal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
