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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.TradeRepository
import com.example.domain.QuantitativeEngine
import com.example.ui.components.MetricCard
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
fun AnalyticsScreen(
    tradeRepository: TradeRepository,
    modifier: Modifier = Modifier
) {
    val trades by tradeRepository.allTrades.collectAsState(initial = emptyList())
    val metrics = remember(trades) { QuantitativeEngine.calculateMetrics(trades) }
    val recoveryTable = remember { QuantitativeEngine.getLossRecoveryMathTable() }

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
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "QUANTITATIVE EXPECTANCY & RISK METRICS",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Mathematical audit of your historical performance & root cause breakdown.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Primary KPIs Grid (Win Rate, Expectancy, Profit Factor, Max Drawdown)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Win Rate",
                    value = "${String.format(Locale.US, "%.1f", metrics.winRatePercent)}%",
                    subtitle = "${metrics.winningTrades}W / ${metrics.losingTrades}L",
                    valueColor = if (metrics.winRatePercent >= 50.0) EmeraldDisciplined else GoldAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Expectancy",
                    value = "${if (metrics.expectancyR >= 0) "+" else ""}${String.format(Locale.US, "%.2f", metrics.expectancyR)}R",
                    subtitle = "${if (metrics.expectancyDollar >= 0) "+$" else "-$"}${String.format(Locale.US, "%.0f", kotlin.math.abs(metrics.expectancyDollar))} / trade",
                    valueColor = if (metrics.expectancyR > 0) EmeraldDisciplined else CrimsonInvalidation,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Profit Factor",
                    value = String.format(Locale.US, "%.2f", metrics.profitFactor),
                    subtitle = "Gains / Losses",
                    valueColor = if (metrics.profitFactor >= 1.5) EmeraldDisciplined else CrimsonInvalidation,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Max Drawdown",
                    value = "-$${String.format(Locale.US, "%,.0f", metrics.maxDrawdownDollar)}",
                    subtitle = "${String.format(Locale.US, "%.1f", metrics.maxDrawdownPercent)}% peak-to-trough",
                    valueColor = CrimsonInvalidation,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Secondary Metrics (Avg Win, Avg Loss, Risk % per trade, Process Quality)
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
                        text = "TRADE STATISTICAL DISTRIBUTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanQuantitative,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Average Winner", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("+$${String.format(Locale.US, "%,.2f", metrics.averageWinner)} (+${String.format(Locale.US, "%.2f", metrics.averageWinnerR)}R)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = EmeraldDisciplined)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Average Loser", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("-$${String.format(Locale.US, "%,.2f", metrics.averageLoser)} (-${String.format(Locale.US, "%.2f", metrics.averageLoserR)}R)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = CrimsonInvalidation)
                        }
                    }
                    HorizontalDivider(color = SlateCardBorder)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Avg Account Risk / Trade", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${String.format(Locale.US, "%.1f", metrics.averageRiskPercent)}% (Standard: 1.0%)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (metrics.averageRiskPercent > 2.0) CrimsonInvalidation else EmeraldDisciplined)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Process Compliance", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${String.format(Locale.US, "%.1f", metrics.goodTradePercent)}% Good Trades", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (metrics.goodTradePercent >= 80.0) EmeraldDisciplined else GoldAccent)
                        }
                    }
                }
            }
        }

        // Loss Root Cause Breakdown
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.6f))
                    .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
                    .testTag("root_cause_breakdown")
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ROOT CAUSE BREAKDOWN (WHERE LOSSES OCCURRED)",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    if (metrics.rootCauses.isEmpty()) {
                        Text("No trades recorded yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        val maxCount = metrics.rootCauses.values.maxOrNull()?.toFloat() ?: 1f
                        metrics.rootCauses.entries.sortedByDescending { it.value }.forEach { (cause, count) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = cause,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (cause.startsWith("None")) EmeraldDisciplined else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "$count trades (${String.format(Locale.US, "%.0f", (count.toFloat() / metrics.totalTrades) * 100)}%)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                LinearProgressIndicator(
                                    progress = { count / maxCount },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = if (cause.startsWith("None")) EmeraldDisciplined else CrimsonInvalidation,
                                    trackColor = SlateCardBorder
                                )
                            }
                        }
                    }
                }
            }
        }

        // Loss Recovery Mathematics (Asymmetry of Drawdowns)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateSurfaceVariant.copy(alpha = 0.6f))
                    .border(1.dp, CrimsonInvalidation.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
                    .testTag("loss_recovery_math_card")
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dangerous, contentDescription = null, tint = CrimsonInvalidation, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "THE MATHEMATICS OF LOSS RECOVERY",
                            style = MaterialTheme.typography.labelSmall,
                            color = CrimsonInvalidation,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Why you cannot 'rush' to make back your $10,000: Losses compound exponentially against you. Survival is the absolute prerequisite for compounding.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Table of Drawdowns vs Required Gain
                    recoveryTable.forEach { (loss, reqGain) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "-$loss% Drawdown",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (loss >= 50) CrimsonInvalidation else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Requires +${String.format(Locale.US, "%.1f", reqGain)}% to break even",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (loss >= 50) CrimsonInvalidation else GoldAccent
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
