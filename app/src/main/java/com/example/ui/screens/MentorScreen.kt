package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.GeminiMentorRepository
import com.example.domain.AuditAnalysisResult
import com.example.domain.QuantitativeEngine
import com.example.ui.components.CircuitBreakerBanner
import com.example.ui.components.DisciplineMantraCard
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonInvalidation
import com.example.ui.theme.CyanQuantitative
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldDisciplined
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDim
import com.example.ui.theme.GoldLight
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.launch
import java.util.Locale

data class ChatMessage(
    val sender: String, // "user" or "mentor"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun MentorScreen(
    mentorRepository: GeminiMentorRepository,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: 22-Step Auditor, 1: AI Mentor Chat

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        DisciplineMantraCard()
        Spacer(modifier = Modifier.height(12.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SlateSurfaceVariant.copy(alpha = 0.4f),
            contentColor = GoldAccent,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, SlateCardBorder, RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("22-Step Auditor", fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Brutal Mentor AI", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            TradeAuditorView(mentorRepository)
        } else {
            MentorChatView(mentorRepository)
        }
    }
}

@Composable
fun TradeAuditorView(mentorRepository: GeminiMentorRepository) {
    var symbol by remember { mutableStateOf("BTC/USDT") }
    var marketType by remember { mutableStateOf("CRYPTO") } // "STOCKS" or "CRYPTO"
    var timeframe by remember { mutableStateOf("4H") }
    var marketRegime by remember { mutableStateOf("Range Bound / Liquidity Sweep") }
    var thesis by remember { mutableStateOf("Bullish divergence at higher-timeframe support after liquidity sweep.") }
    var supportingEvidence by remember { mutableStateOf("4H volume spike at $60,000 support, open interest flushed.") }
    var contradictingEvidence by remember { mutableStateOf("BTC dominance is rising; altcoins showing weakness.") }
    var entryPrice by remember { mutableStateOf("60500") }
    var stopPrice by remember { mutableStateOf("59200") }
    var targetPrice by remember { mutableStateOf("63800") }
    var accountSize by remember { mutableStateOf("10000") }
    var riskPercent by remember { mutableStateOf("1.0") }
    var emotionalState by remember { mutableStateOf("Calm & Objective") }
    var hasInvalidation by remember { mutableStateOf(true) }
    var catalystNotes by remember { mutableStateOf("FOMC rate decision in 4 days.") }
    var isSeekingLossRecovery by remember { mutableStateOf(false) }

    var auditResult by remember { mutableStateOf<AuditAnalysisResult?>(null) }
    var isAuditing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "RIGOROUS 22-STEP PRE-TRADE AUDIT",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAccent,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Test your setup before risking capital. Capital preservation is your non-negotiable duty.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Market Type selector & Symbol
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = { Text("Symbol / Asset") },
                    modifier = Modifier.weight(1f).testTag("input_symbol"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = SlateCardBorder
                    )
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text("Market Type", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = marketType == "CRYPTO",
                            onClick = { marketType = "CRYPTO" },
                            label = { Text("CRYPTO") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                        FilterChip(
                            selected = marketType == "STOCKS",
                            onClick = { marketType = "STOCKS" },
                            label = { Text("STOCKS") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        }

        // Timeframe & Regime
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = timeframe,
                    onValueChange = { timeframe = it },
                    label = { Text("Timeframe (e.g. 15m, 4H)") },
                    modifier = Modifier.weight(1f).testTag("input_timeframe"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = marketRegime,
                    onValueChange = { marketRegime = it },
                    label = { Text("Market Regime") },
                    modifier = Modifier.weight(1.5f).testTag("input_regime"),
                    singleLine = true
                )
            }
        }

        // Prices: Entry, Stop, Target
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = entryPrice,
                    onValueChange = { entryPrice = it },
                    label = { Text("Entry ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("input_entry"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = stopPrice,
                    onValueChange = { stopPrice = it },
                    label = { Text("Stop Loss ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("input_stop"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = targetPrice,
                    onValueChange = { targetPrice = it },
                    label = { Text("Target ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("input_target"),
                    singleLine = true
                )
            }
        }

        // Account size & Risk %
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = accountSize,
                    onValueChange = { accountSize = it },
                    label = { Text("Account Size ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("input_account_size"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = riskPercent,
                    onValueChange = { riskPercent = it },
                    label = { Text("Risk % (Standard: 1.0%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).testTag("input_risk_percent"),
                    singleLine = true
                )
            }
        }

        // Thesis & Supporting Evidence
        item {
            OutlinedTextField(
                value = thesis,
                onValueChange = { thesis = it },
                label = { Text("Thesis (Measurable hypothesis)") },
                modifier = Modifier.fillMaxWidth().testTag("input_thesis"),
                minLines = 2
            )
        }

        // Contradicting Evidence (Critical for confirmation bias elimination)
        item {
            OutlinedTextField(
                value = contradictingEvidence,
                onValueChange = { contradictingEvidence = it },
                label = { Text("Contradicting Evidence (Stress-test your idea)") },
                placeholder = { Text("What evidence argues AGAINST taking this trade?") },
                modifier = Modifier.fillMaxWidth().testTag("input_contradicting"),
                minLines = 2
            )
        }

        // Emotional check & Invalidation switches
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Explicit Structural Invalidation Defined?", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text("Do you know the exact price where the thesis is 100% dead?", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = hasInvalidation,
                            onCheckedChange = { hasInvalidation = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldDisciplined)
                        )
                    }

                    HorizontalDivider(color = SlateCardBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Am I motivated by recovering previous losses?", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isSeekingLossRecovery) CrimsonInvalidation else MaterialTheme.colorScheme.onSurface)
                            Text("Honesty check: Are you trading to 'make back' prior losses?", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isSeekingLossRecovery,
                            onCheckedChange = { isSeekingLossRecovery = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CrimsonInvalidation)
                        )
                    }
                }
            }
        }

        // Audit Button
        item {
            Button(
                onClick = {
                    isAuditing = true
                    val ep = entryPrice.toDoubleOrNull() ?: 0.0
                    val sp = stopPrice.toDoubleOrNull() ?: 0.0
                    val tp = targetPrice.toDoubleOrNull() ?: 0.0
                    val acc = accountSize.toDoubleOrNull() ?: 10000.0
                    val rp = riskPercent.toDoubleOrNull() ?: 1.0

                    auditResult = QuantitativeEngine.auditTradeSetup(
                        symbol = symbol,
                        marketType = marketType,
                        timeframe = timeframe,
                        marketRegime = marketRegime,
                        thesis = thesis,
                        supportingEvidence = supportingEvidence,
                        contradictingEvidence = contradictingEvidence,
                        entryPrice = ep,
                        stopPrice = sp,
                        targetPrice = tp,
                        accountSize = acc,
                        riskPercent = rp,
                        emotionalState = emotionalState,
                        hasDefinedInvalidation = hasInvalidation,
                        catalystNotes = catalystNotes,
                        isSeekingLossRecovery = isSeekingLossRecovery
                    )
                    isAuditing = false
                },
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("button_run_audit"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = Color.Black
                )
            ) {
                if (isAuditing) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                } else {
                    Icon(Icons.Default.Shield, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AUDIT SETUP RIGOROUSLY (22-POINT RUBRIC)", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Audit Result Display
        auditResult?.let { res ->
            item {
                AuditResultCard(res)
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AuditResultCard(result: AuditAnalysisResult) {
    val gradeColor = when (result.grade) {
        "A" -> EmeraldDisciplined
        "B" -> CyanQuantitative
        "C" -> GoldAccent
        "D" -> CrimsonInvalidation.copy(alpha = 0.8f)
        else -> CrimsonInvalidation // F
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateSurfaceVariant.copy(alpha = 0.8f))
            .border(2.dp, gradeColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
            .testTag("audit_result_container")
    ) {
        // Grade & Rationale Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FINAL SETUP CLASSIFICATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Grade: ${result.grade}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = gradeColor
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(gradeColor.copy(alpha = 0.2f))
                    .border(1.dp, gradeColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = when (result.grade) {
                        "A" -> "STRONGLY DEFINED"
                        "B" -> "NEEDS CONFIRMATION"
                        "C" -> "UNCLEAR SETUP"
                        "D" -> "POOR R:R"
                        else -> "REJECTED / HAZARDOUS"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = gradeColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = result.gradeRationale,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )

        // Circuit Breaker if applicable
        if (result.circuitBreakerTriggered) {
            Spacer(modifier = Modifier.height(12.dp))
            CircuitBreakerBanner(isTriggered = true, reason = result.circuitBreakerReason)
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = SlateCardBorder)
        Spacer(modifier = Modifier.height(14.dp))

        // Position Sizing Breakdown
        Text(
            text = "MATHEMATICAL POSITION SIZING",
            style = MaterialTheme.typography.labelSmall,
            color = GoldAccent,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Max Dollar Risk", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("-$${String.format(Locale.US, "%.2f", result.positionSizing.riskAmountDollar)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = CrimsonInvalidation)
            }
            Column {
                Text("Position Size", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${String.format(Locale.US, "%.4f", result.positionSizing.positionSizeUnits)} units", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Reward / Risk", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${String.format(Locale.US, "%.2f", result.positionSizing.rewardRiskRatio)}R", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (result.positionSizing.rewardRiskRatio >= 2.0) EmeraldDisciplined else CrimsonInvalidation)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = SlateCardBorder)
        Spacer(modifier = Modifier.height(14.dp))

        // Mandatory Tuition Question Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateSurfaceVariant)
                .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "MANDATORY CLOSING PROBE:",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${result.mandatoryTuitionQuestion}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = result.tuitionAnswerAnalysis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 22-Step Detailed Breakdown
        Text(
            text = "22-POINT STRUCTURAL DECONSTRUCTION",
            style = MaterialTheme.typography.labelSmall,
            color = CyanQuantitative,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            result.structured22Steps.forEach { (title, body) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SlateSurfaceVariant.copy(alpha = 0.4f))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = body,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MentorChatView(mentorRepository: GeminiMentorRepository) {
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    sender = "mentor",
                    text = "I am your risk manager, quantitative analyst, and trading psychologist. You lost $10,000. We treat that strictly as tuition. I will not flatter you, validate FOMO, or tolerate revenge trading. What setup, emotion, or hypothesis do you want audited today?"
                )
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { msg ->
                val isMentor = msg.sender == "mentor"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMentor) Arrangement.Start else Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isMentor) SlateSurfaceVariant
                                else GoldAccent.copy(alpha = 0.15f)
                            )
                            .border(
                                1.dp,
                                if (isMentor) SlateCardBorder else GoldAccent,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isMentor) Icons.Default.Shield else Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = if (isMentor) GoldAccent else CyanQuantitative,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isMentor) "TRADING MENTOR & RISK MANAGER" else "YOU",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMentor) GoldAccent else CyanQuantitative,
                                    letterSpacing = 0.8.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateSurfaceVariant)
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = GoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Auditing thesis and testing risk parameters...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick prompt chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = false,
                onClick = {
                    inputText = "I feel like I need to make back my losses quickly with high leverage. What should I do?"
                },
                label = { Text("Loss Recovery Tilt", fontSize = 11.sp) }
            )
            FilterChip(
                selected = false,
                onClick = {
                    inputText = "Audit my thought: TSLA has dropped 15%, should I buy calls now because it's 'cheap'?"
                },
                label = { Text("Catching Knife Setup", fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask mentor or submit trade idea...") },
                modifier = Modifier.weight(1f).testTag("chat_input_field"),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = SlateCardBorder
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val prompt = inputText.trim()
                    if (prompt.isNotBlank() && !isLoading) {
                        val userMsg = ChatMessage(sender = "user", text = prompt)
                        chatMessages = chatMessages + userMsg
                        inputText = ""
                        isLoading = true

                        scope.launch {
                            val history = chatMessages.map { it.sender to it.text }
                            val response = mentorRepository.auditTradeWithAI(prompt, history)
                            chatMessages = chatMessages + ChatMessage(sender = "mentor", text = response)
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.size(48.dp).testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send to mentor",
                    tint = GoldAccent
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
