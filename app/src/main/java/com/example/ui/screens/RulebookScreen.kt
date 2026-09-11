package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MistakeLog
import com.example.data.repository.TradeRepository
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonInvalidation
import com.example.ui.theme.CyanQuantitative
import com.example.ui.theme.EmeraldDisciplined
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDim
import com.example.ui.theme.PurpleRegime
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateSurfaceVariant
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun RulebookScreen(
    tradeRepository: TradeRepository,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    // 0: Stocks vs Crypto, 1: Checklists, 2: Backtesting Rigor, 3: Mistake Journal

    val tabTitles = listOf("Stocks vs Crypto", "Checklists", "Backtesting Rigor", "Mistake Journal")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                FilterChip(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    label = { Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAccent,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> StocksVsCryptoView()
            1 -> ChecklistsView()
            2 -> BacktestingGuideView()
            3 -> MistakeJournalView(tradeRepository)
        }
    }
}

@Composable
fun StocksVsCryptoView() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.6f))
                    .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "MARKET COMPARISON: STOCKS VS. CRYPTOCURRENCY",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Where they behave identically: Both are auction environments governed by liquidity pools, supply/demand imbalances, human greed, and panic. Price follows the path of least resistance to sweep liquidity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        val dimensions = listOf(
            Triple(
                "Liquidity & Market Depth",
                "STOCKS: Centralized exchanges (NYSE, NASDAQ). Regulated designated market makers provide continuous quoting. Deep institutional order books with predictable spread.",
                "CRYPTO: Fragmented across dozens of off-shore exchanges & DEXes. Illiquid order books susceptible to 10%-20% cascade slippage during stop-run events."
            ),
            Triple(
                "Volatility & Circuit Breakers",
                "STOCKS: Regulated circuit breakers (Limit-Up / Limit-Down pauses, market-wide halts). Daily ATR is generally bounded.",
                "CRYPTO: Zero circuit breakers. Unconstrained 24/7 volatility. Liquidations trigger cascading mechanical selling into empty order books."
            ),
            Triple(
                "Market Hours & Gaps",
                "STOCKS: Structured sessions (9:30 AM - 4:00 PM EST). Overnight and weekend gaps carry discontinuous jump risk. Pre-market has low liquidity.",
                "CRYPTO: 24/7/365 continuous trading. No weekend closing, but notorious for low-volume weekend 'fakeout' wicks engineered by market makers."
            ),
            Triple(
                "Leverage & Derivatives",
                "STOCKS: Reg T standard 2x-4x margin. Regulated options markets with clear delta/gamma mechanics and expiry dates.",
                "CRYPTO: Perpetual swaps with up to 50x-100x leverage. Funding rates force longs or shorts to pay interest, creating squeeze mechanics."
            ),
            Triple(
                "News Sensitivity & Catalysts",
                "STOCKS: Scheduled SEC 10-Q filings, earnings calendars, FOMC statements, CPI, NFP. High accountability and audited balance sheets.",
                "CRYPTO: Token unlock schedules (insider dilution), regulatory enforcement actions, exchange hack rumors, protocol exploits, and whale wallet movements."
            ),
            Triple(
                "Custody & Exchange Risk",
                "STOCKS: SIPC insured brokerage accounts. Assets are legally segregated in custody clearing houses (DTCC).",
                "CRYPTO: Extreme counterparty risk on centralized exchanges (FTX-style insolvencies). Smart contract bugs. Self-custody carries operational risk."
            )
        )

        items(dimensions) { (title, stockDesc, cryptoDesc) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CyanQuantitative)
                    Text(stockDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    HorizontalDivider(color = SlateCardBorder)
                    Text(cryptoDesc, style = MaterialTheme.typography.bodySmall, color = PurpleRegime)
                }
            }
        }

        // Indicators Myth section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.6f))
                    .border(1.dp, CrimsonInvalidation.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonInvalidation, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "THE MYTH OF TECHNICAL INDICATORS AS PREDICTION MACHINES",
                            style = MaterialTheme.typography.labelSmall,
                            color = CrimsonInvalidation,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Technical indicators (RSI, MACD, Bollinger Bands, Stochastics) are NOT magical crystal balls. Every indicator is merely a derivative mathematical calculation of past price and volume. RSI is simply the ratio of average up closes to average down closes. It contains zero future information.\n\nUse indicators ONLY when they provide a measurable, falsifiable threshold (e.g. 2 standard deviation band expansion) within a higher-timeframe thesis. Never buy because an indicator is 'oversold.'",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun ChecklistsView() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val checklists = listOf(
            "1. Pre-Trade Checklist (State & Environment)" to listOf(
                "Am I physically rested, calm, and detached from prior outcomes?",
                "Is this trade motivated by wanting to make back recent losses? (If YES, STOP)",
                "Is there a major macroeconomic event (CPI, FOMC) in the next 30 minutes?",
                "Have I already hit my 2 trades/day session limit? (If YES, STOP)",
                "Is cash my best position right now?"
            ),
            "2. Entry Checklist (Setup Validation)" to listOf(
                "Is the market regime clearly identified (Trending vs Range bound)?",
                "Is there an explicit, objective structural invalidation price?",
                "Is the Reward-to-Risk ratio at least 2.0R to the first structural liquidity pool?",
                "Has position size been mathematically calculated from stop distance (<=1% account risk)?",
                "Has a hard stop loss order been entered simultaneously with the entry?"
            ),
            "3. Position-Management Checklist" to listOf(
                "Rule #1: NEVER move a stop loss farther away under any circumstance.",
                "Rule #2: Do not average down into a losing position.",
                "Rule #3: Trail stop to breakeven ONLY after price has achieved >=1.5R target.",
                "Rule #4: Hands off the mouse. Let the math and the market do the work."
            ),
            "4. Exit Checklist" to listOf(
                "Did price hit the planned invalidation stop? Accept loss immediately without tilt.",
                "Did price reach the planned liquidity target? Scale out per plan.",
                "Did market regime fundamentally break before target? Close without hesitation."
            ),
            "5. Post-Trade Review Checklist" to listOf(
                "Did I execute the plan regardless of whether it won or lost?",
                "If it won, did I break rules? (A bad trade that made money is hazardous).",
                "If it lost, was it a good trade that took planned business risk?",
                "Document screenshots, thesis, and emotional state in the tuition journal."
            )
        )

        items(checklists) { (title, items) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GoldAccent)
                    Spacer(modifier = Modifier.height(8.dp))
                    items.forEach { item ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", color = GoldAccent, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun BacktestingGuideView() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.6f))
                    .border(1.dp, CyanQuantitative.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = CyanQuantitative, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "QUANTITATIVE BACKTESTING & BIAS ELIMINATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanQuantitative,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Why strategies that look brilliant historically fail miserably in live trading. You must understand statistical biases before risking real money.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        val biases = listOf(
            Triple(
                "Overfitting (Curve Fitting)",
                "Adjusting parameters (e.g. RSI length = 13.5 with 200 EMA) until the backtest looks perfect.",
                "REALITY: Overfitting models historical random noise, not true market signal. Test wide parameter bands. If strategy only works on one specific setting, it is 100% overfit."
            ),
            Triple(
                "Look-Ahead Bias",
                "Accidentally referencing price data that was not yet known at the precise time of the simulated entry.",
                "REALITY: E.g., using the daily bar's closing price or low before the candle actually closed. Live execution requires waiting for real-time confirmation."
            ),
            Triple(
                "Survivorship Bias",
                "Testing only the companies or tokens that are successful and traded today.",
                "REALITY: Omits bankrupt companies (Enron, Lehman) or collapsed crypto tokens (LUNA, FTX). Severely inflates historical win rates."
            ),
            Triple(
                "Slippage & Transaction Costs",
                "Assuming every order is filled perfectly at the exact signal price with zero friction.",
                "REALITY: Bid-ask spreads, taker fees (0.05%-0.1%), exchange funding rates, and market impact devour 30%-60% of apparent theoretical edge."
            ),
            Triple(
                "Walk-Forward Validation",
                "Institutional validation method: Optimize rules on in-sample data (2020-2022), then test untouched on out-of-sample data (2023-2024).",
                "REALITY: If out-of-sample performance collapses, the strategy has zero predictive validity."
            )
        )

        items(biases) { (title, def, reality) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GoldAccent)
                    Text(def, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(color = SlateCardBorder)
                    Text(reality, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Paper trading graduation criteria
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, EmeraldDisciplined.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("PAPER TRADING GRADUATION MILESTONES", style = MaterialTheme.typography.labelSmall, color = EmeraldDisciplined, fontWeight = FontWeight.Bold)
                    Text("Before increasing real-money position size, achieve:", style = MaterialTheme.typography.bodySmall)
                    Text("1. Minimum 50 documented trades in the journal.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    Text("2. Positive mathematical expectancy (>0.25R).", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    Text("3. Zero instances of moving a stop loss farther away.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    Text("4. Zero instances of revenge trading or position sizing >1%.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun MistakeJournalView(tradeRepository: TradeRepository) {
    val mistakes by tradeRepository.allMistakes.collectAsState(initial = emptyList())
    var showAddMistakeDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RECURRENT MISTAKE JOURNAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = CrimsonInvalidation,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Identify behavioral leaks so you never repeat the $10,000 tuition mistakes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddMistakeDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonInvalidation, contentColor = Color.White),
                    modifier = Modifier.testTag("button_log_mistake")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Mistake", fontSize = 12.sp)
                }
            }
        }

        items(mistakes, key = { it.id }) { mistake ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, CrimsonInvalidation.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mistake.mistakeType,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CrimsonInvalidation
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Cost: -$${String.format(Locale.US, "%,.2f", mistake.costDollars)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = CrimsonInvalidation
                            )
                            IconButton(
                                onClick = { scope.launch { tradeRepository.deleteMistake(mistake) } },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Text("Trigger: ${mistake.triggerCondition}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    Text("Emotional State: ${mistake.emotionalState}", style = MaterialTheme.typography.labelSmall, color = GoldAccent)
                    HorizontalDivider(color = SlateCardBorder)
                    Text("Rule Broken: ${mistake.ruleBroken}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Corrective Action: ${mistake.correctiveAction}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = EmeraldDisciplined)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }

    if (showAddMistakeDialog) {
        AddMistakeDialog(
            onDismiss = { showAddMistakeDialog = false },
            onSave = { newM ->
                scope.launch {
                    tradeRepository.addMistake(newM)
                    showAddMistakeDialog = false
                }
            }
        )
    }
}

@Composable
fun AddMistakeDialog(
    onDismiss: () -> Unit,
    onSave: (MistakeLog) -> Unit
) {
    var mistakeType by remember { mutableStateOf("") }
    var triggerCondition by remember { mutableStateOf("") }
    var costDollars by remember { mutableStateOf("") }
    var emotionalState by remember { mutableStateOf("") }
    var ruleBroken by remember { mutableStateOf("") }
    var correctiveAction by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Recurrent Mistake", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = mistakeType,
                    onValueChange = { mistakeType = it },
                    label = { Text("Mistake Type (e.g. Moved Stop)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = costDollars,
                    onValueChange = { costDollars = it },
                    label = { Text("Estimated Cost ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = emotionalState,
                    onValueChange = { emotionalState = it },
                    label = { Text("Emotional State (e.g. Fear of loss)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = triggerCondition,
                    onValueChange = { triggerCondition = it },
                    label = { Text("Trigger / Circumstance") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ruleBroken,
                    onValueChange = { ruleBroken = it },
                    label = { Text("Rule Broken") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = correctiveAction,
                    onValueChange = { correctiveAction = it },
                    label = { Text("Permanent Corrective Action") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mistakeType.isNotBlank()) {
                        val m = MistakeLog(
                            mistakeType = mistakeType,
                            triggerCondition = triggerCondition,
                            costDollars = costDollars.toDoubleOrNull() ?: 0.0,
                            emotionalState = emotionalState,
                            ruleBroken = ruleBroken,
                            correctiveAction = correctiveAction
                        )
                        onSave(m)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonInvalidation, contentColor = Color.White)
            ) {
                Text("Log to Journal")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
