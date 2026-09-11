package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.domain.QuantitativeEngine
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonInvalidation
import com.example.ui.theme.CyanQuantitative
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldDisciplined
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateSurfaceVariant
import java.util.Locale

@Composable
fun RiskCalculatorScreen(modifier: Modifier = Modifier) {
    var accountSizeInput by remember { mutableStateOf("10000") }
    var riskPercentInput by remember { mutableStateOf("1.0") }
    var entryPriceInput by remember { mutableStateOf("100.00") }
    var stopPriceInput by remember { mutableStateOf("96.00") }
    var targetPriceInput by remember { mutableStateOf("108.00") }

    val accountSize = accountSizeInput.toDoubleOrNull() ?: 10000.0
    val riskPercent = riskPercentInput.toDoubleOrNull() ?: 1.0
    val entryPrice = entryPriceInput.toDoubleOrNull() ?: 100.0
    val stopPrice = stopPriceInput.toDoubleOrNull() ?: 96.0
    val targetPrice = targetPriceInput.toDoubleOrNull() ?: 108.0

    val sizingResult = remember(accountSize, riskPercent, entryPrice, stopPrice, targetPrice) {
        QuantitativeEngine.calculatePositionSize(
            accountSize = accountSize,
            riskPercent = riskPercent,
            entryPrice = entryPrice,
            stopPrice = stopPrice,
            targetPrice = targetPrice
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "POSITION SIZING & RISK MANAGER",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Stop distance dictates size. Never size based on greed or excitement.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Interactive inputs
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "HYPOTHETICAL ACCOUNT & RISK ALLOCATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanQuantitative,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = accountSizeInput,
                            onValueChange = { accountSizeInput = it },
                            label = { Text("Account Balance ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("calc_account_size"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = riskPercentInput,
                            onValueChange = { riskPercentInput = it },
                            label = { Text("Risk per trade (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("calc_risk_percent"),
                            singleLine = true
                        )
                    }

                    // Risk Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("0.5", "1.0", "1.5", "2.0").forEach { preset ->
                            FilterChip(
                                selected = riskPercentInput == preset,
                                onClick = { riskPercentInput = preset },
                                label = { Text("$preset% Risk", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = SlateCardBorder)

                    Text(
                        text = "TRADE STRUCTURE PRICES",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanQuantitative,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = entryPriceInput,
                            onValueChange = { entryPriceInput = it },
                            label = { Text("Entry Price $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("calc_entry"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = stopPriceInput,
                            onValueChange = { stopPriceInput = it },
                            label = { Text("Stop Loss $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("calc_stop"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = targetPriceInput,
                            onValueChange = { targetPriceInput = it },
                            label = { Text("Target $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("calc_target"),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Calculation Output Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.8f))
                    .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .testTag("sizing_result_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "EXACT EXECUTION SIZING",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Max Dollar Risk", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("-$${String.format(Locale.US, "%.2f", sizingResult.riskAmountDollar)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CrimsonInvalidation)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Position Size (Units/Shares)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${String.format(Locale.US, "%.4f", sizingResult.positionSizeUnits)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    HorizontalDivider(color = SlateCardBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Capital Committed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${String.format(Locale.US, "%,.2f", sizingResult.capitalAllocatedDollar)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Stop Distance", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${String.format(Locale.US, "%.2f", sizingResult.stopDistancePercent)}% ($${String.format(Locale.US, "%.2f", sizingResult.stopDistanceDollar)})", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Reward/Risk", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${String.format(Locale.US, "%.2f", sizingResult.rewardRiskRatio)}R", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (sizingResult.rewardRiskRatio >= 2.0) EmeraldDisciplined else CrimsonInvalidation)
                        }
                    }

                    // Warnings if any
                    if (sizingResult.warnings.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        sizingResult.warnings.forEach { warn ->
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonInvalidation, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(warn, style = MaterialTheme.typography.bodySmall, color = CrimsonInvalidation)
                            }
                        }
                    }
                }
            }
        }

        // Risk of Ruin & Losing Streak Table
        item {
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
                        Icon(Icons.Default.Shield, contentDescription = null, tint = CyanQuantitative, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RISK OF RUIN & LOSING STREAK PROBABILITIES",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanQuantitative,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Even a profitable strategy with a 50% win rate experiences 6 to 10 consecutive losses across a sample of 200 trades. Here is what happens to your account during a 10-trade losing streak:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Streak simulation table
                    val riskLevels = listOf(
                        Triple("0.5% Risk", "-4.9% Drawdown", "Ultra-safe / Institutional"),
                        Triple("1.0% Risk", "-9.6% Drawdown", "Professional Standard"),
                        Triple("2.0% Risk", "-18.3% Drawdown", "Aggressive / Max Limit"),
                        Triple("5.0% Risk", "-40.1% Drawdown", "Critical Danger / High Ruin"),
                        Triple("10.0% Risk", "-65.1% Drawdown", "Guaranteed Account Ruin")
                    )

                    riskLevels.forEach { (level, dd, comment) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(level, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                Text(comment, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                dd,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (level.startsWith("10") || level.startsWith("5")) CrimsonInvalidation else if (level.startsWith("2")) GoldAccent else EmeraldDisciplined
                            )
                        }
                    }
                }
            }
        }

        // Daily / Weekly Loss Circuit Breaker Rules
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SYSTEMATIC CIRCUIT BREAKER LIMITS", style = MaterialTheme.typography.labelSmall, color = GoldAccent, fontWeight = FontWeight.Bold)
                    }
                    Text("• Maximum Daily Loss: 2.0% ($200 on a $10k account). Once hit, platform is locked. Step away.", style = MaterialTheme.typography.bodySmall)
                    Text("• Maximum Weekly Loss: 5.0% ($500). Once hit, trading is halted until the next Monday.", style = MaterialTheme.typography.bodySmall)
                    Text("• Max Trades per Session: 2 trades. Overtrading is simply churning commissions and amplifying noise.", style = MaterialTheme.typography.bodySmall)
                    Text("• Mandatory Lockout after 2 consecutive stop-outs: Zero exceptions.", style = MaterialTheme.typography.bodySmall, color = GoldAccent, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
