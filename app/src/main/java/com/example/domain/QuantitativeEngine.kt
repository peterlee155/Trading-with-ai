package com.example.domain

import com.example.data.model.TradeLog
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

data class PortfolioMetrics(
    val totalTrades: Int,
    val winningTrades: Int,
    val losingTrades: Int,
    val breakevenTrades: Int,
    val winRatePercent: Double,
    val totalPnl: Double,
    val grossProfits: Double,
    val grossLosses: Double,
    val averageWinner: Double,
    val averageLoser: Double,
    val averageWinnerR: Double,
    val averageLoserR: Double,
    val expectancyDollar: Double,
    val expectancyR: Double,
    val profitFactor: Double,
    val maxDrawdownDollar: Double,
    val maxDrawdownPercent: Double,
    val averageRiskPercent: Double,
    val goodTradePercent: Double, // process quality
    val rootCauses: Map<String, Int>,
    val classifications: Map<String, Int>
)

data class PositionSizingResult(
    val accountSize: Double,
    val riskPercent: Double,
    val riskAmountDollar: Double,
    val entryPrice: Double,
    val stopPrice: Double,
    val targetPrice: Double,
    val stopDistanceDollar: Double,
    val stopDistancePercent: Double,
    val targetDistanceDollar: Double,
    val rewardRiskRatio: Double,
    val positionSizeUnits: Double,
    val capitalAllocatedDollar: Double,
    val effectiveLeverage: Double,
    val isValid: Boolean,
    val warnings: List<String>
)

data class AuditAnalysisResult(
    val symbol: String,
    val marketType: String,
    val grade: String, // "A", "B", "C", "D", "F"
    val gradeRationale: String,
    val circuitBreakerTriggered: Boolean,
    val circuitBreakerReason: String?,
    val positionSizing: PositionSizingResult,
    val structured22Steps: List<Pair<String, String>>, // Step number & title -> content
    val mandatoryTuitionQuestion: String,
    val tuitionAnswerAnalysis: String
)

object QuantitativeEngine {

    fun calculateMetrics(trades: List<TradeLog>): PortfolioMetrics {
        if (trades.isEmpty()) {
            return PortfolioMetrics(
                totalTrades = 0,
                winningTrades = 0,
                losingTrades = 0,
                breakevenTrades = 0,
                winRatePercent = 0.0,
                totalPnl = 0.0,
                grossProfits = 0.0,
                grossLosses = 0.0,
                averageWinner = 0.0,
                averageLoser = 0.0,
                averageWinnerR = 0.0,
                averageLoserR = 0.0,
                expectancyDollar = 0.0,
                expectancyR = 0.0,
                profitFactor = 0.0,
                maxDrawdownDollar = 0.0,
                maxDrawdownPercent = 0.0,
                averageRiskPercent = 0.0,
                goodTradePercent = 0.0,
                rootCauses = emptyMap(),
                classifications = emptyMap()
            )
        }

        var wins = 0
        var losses = 0
        var bes = 0
        var grossGains = 0.0
        var grossLosses = 0.0
        var sumWinR = 0.0
        var sumLossR = 0.0
        var sumRiskPercent = 0.0
        var goodTradesCount = 0

        val rootCauseMap = mutableMapOf<String, Int>()
        val classMap = mutableMapOf<String, Int>()

        // Chronological order for drawdown
        val sortedTrades = trades.sortedBy { it.timestamp }
        var peak = 0.0
        var runningPnl = 0.0
        var maxDdDollar = 0.0

        for (t in sortedTrades) {
            val pnl = t.pnl ?: 0.0
            runningPnl += pnl
            if (runningPnl > peak) {
                peak = runningPnl
            }
            val dd = peak - runningPnl
            if (dd > maxDdDollar) {
                maxDdDollar = dd
            }

            sumRiskPercent += t.riskPercent
            if (t.isGoodTrade) goodTradesCount++

            rootCauseMap[t.rootCause] = (rootCauseMap[t.rootCause] ?: 0) + 1
            classMap[t.classification] = (classMap[t.classification] ?: 0) + 1

            when {
                pnl > 0.001 -> {
                    wins++
                    grossGains += pnl
                    sumWinR += (t.pnlR ?: if (t.riskAmount > 0) pnl / t.riskAmount else 1.0)
                }
                pnl < -0.001 -> {
                    losses++
                    grossLosses += abs(pnl)
                    sumLossR += abs(t.pnlR ?: if (t.riskAmount > 0) abs(pnl) / t.riskAmount else 1.0)
                }
                else -> {
                    bes++
                }
            }
        }

        val total = trades.size
        val winRate = if (total > 0) (wins.toDouble() / total) * 100.0 else 0.0
        val lossRate = if (total > 0) (losses.toDouble() / total) else 0.0
        val winRateFraction = if (total > 0) (wins.toDouble() / total) else 0.0

        val avgWin = if (wins > 0) grossGains / wins else 0.0
        val avgLoss = if (losses > 0) grossLosses / losses else 0.0
        val avgWinR = if (wins > 0) sumWinR / wins else 0.0
        val avgLossR = if (losses > 0) sumLossR / losses else 1.0

        // Expectancy = (WinRate * AvgWin) - (LossRate * AvgLoss)
        val expectancyDollar = (winRateFraction * avgWin) - (lossRate * avgLoss)
        val expectancyR = (winRateFraction * avgWinR) - (lossRate * avgLossR)

        val profitFactor = if (grossLosses > 0) grossGains / grossLosses else if (grossGains > 0) 99.9 else 0.0
        val maxDdPercent = if (sortedTrades.first().accountSizeAtEntry > 0) {
            (maxDdDollar / sortedTrades.first().accountSizeAtEntry) * 100.0
        } else 0.0

        return PortfolioMetrics(
            totalTrades = total,
            winningTrades = wins,
            losingTrades = losses,
            breakevenTrades = bes,
            winRatePercent = winRate,
            totalPnl = grossGains - grossLosses,
            grossProfits = grossGains,
            grossLosses = grossLosses,
            averageWinner = avgWin,
            averageLoser = avgLoss,
            averageWinnerR = avgWinR,
            averageLoserR = avgLossR,
            expectancyDollar = expectancyDollar,
            expectancyR = expectancyR,
            profitFactor = profitFactor,
            maxDrawdownDollar = maxDdDollar,
            maxDrawdownPercent = maxDdPercent,
            averageRiskPercent = sumRiskPercent / total,
            goodTradePercent = (goodTradesCount.toDouble() / total) * 100.0,
            rootCauses = rootCauseMap,
            classifications = classMap
        )
    }

    fun calculatePositionSize(
        accountSize: Double,
        riskPercent: Double,
        entryPrice: Double,
        stopPrice: Double,
        targetPrice: Double
    ): PositionSizingResult {
        val warnings = mutableListOf<String>()

        if (accountSize <= 0.0 || entryPrice <= 0.0 || stopPrice <= 0.0) {
            return PositionSizingResult(
                accountSize = accountSize,
                riskPercent = riskPercent,
                riskAmountDollar = 0.0,
                entryPrice = entryPrice,
                stopPrice = stopPrice,
                targetPrice = targetPrice,
                stopDistanceDollar = 0.0,
                stopDistancePercent = 0.0,
                targetDistanceDollar = 0.0,
                rewardRiskRatio = 0.0,
                positionSizeUnits = 0.0,
                capitalAllocatedDollar = 0.0,
                effectiveLeverage = 0.0,
                isValid = false,
                warnings = listOf("Enter valid positive values for Account, Entry, and Stop prices.")
            )
        }

        val stopDistance = abs(entryPrice - stopPrice)
        if (stopDistance < 0.000001) {
            return PositionSizingResult(
                accountSize = accountSize,
                riskPercent = riskPercent,
                riskAmountDollar = 0.0,
                entryPrice = entryPrice,
                stopPrice = stopPrice,
                targetPrice = targetPrice,
                stopDistanceDollar = 0.0,
                stopDistancePercent = 0.0,
                targetDistanceDollar = 0.0,
                rewardRiskRatio = 0.0,
                positionSizeUnits = 0.0,
                capitalAllocatedDollar = 0.0,
                effectiveLeverage = 0.0,
                isValid = false,
                warnings = listOf("Stop price cannot equal Entry price.")
            )
        }

        val riskAmount = accountSize * (riskPercent / 100.0)
        val positionUnits = riskAmount / stopDistance
        val capitalAllocated = positionUnits * entryPrice
        val leverage = capitalAllocated / accountSize

        val targetDistance = abs(targetPrice - entryPrice)
        val rrRatio = if (stopDistance > 0) targetDistance / stopDistance else 0.0

        if (riskPercent > 2.0) {
            warnings.add("RISK WARNING: Risking ${String.format(Locale.US, "%.1f", riskPercent)}% per trade exceeds the professional threshold of 1.0% - 2.0%. A losing streak could cause catastrophic drawdown.")
        }
        if (rrRatio < 2.0 && targetPrice > 0) {
            warnings.add("POOR R:R: Expected Reward-to-Risk is ${String.format(Locale.US, "%.2f", rrRatio)}R. Professional setups require at least 2.0R to maintain positive mathematical expectancy.")
        }
        if (leverage > 3.0) {
            warnings.add("LEVERAGE HAZARD: Effective leverage is ${String.format(Locale.US, "%.1f", leverage)}x. High leverage reduces room for market noise and amplifies emotional volatility.")
        }

        return PositionSizingResult(
            accountSize = accountSize,
            riskPercent = riskPercent,
            riskAmountDollar = riskAmount,
            entryPrice = entryPrice,
            stopPrice = stopPrice,
            targetPrice = targetPrice,
            stopDistanceDollar = stopDistance,
            stopDistancePercent = (stopDistance / entryPrice) * 100.0,
            targetDistanceDollar = targetDistance,
            rewardRiskRatio = rrRatio,
            positionSizeUnits = positionUnits,
            capitalAllocatedDollar = capitalAllocated,
            effectiveLeverage = leverage,
            isValid = true,
            warnings = warnings
        )
    }

    fun calculateLosingStreakProbability(winRateFraction: Double, consecutiveLosses: Int): Double {
        val lossRate = 1.0 - winRateFraction
        return lossRate.pow(consecutiveLosses.toDouble()) * 100.0
    }

    fun getLossRecoveryMathTable(): List<Pair<Int, Double>> {
        return listOf(
            10 to 11.1,
            20 to 25.0,
            30 to 42.9,
            40 to 66.7,
            50 to 100.0,
            60 to 150.0,
            75 to 300.0,
            90 to 900.0
        )
    }

    fun auditTradeSetup(
        symbol: String,
        marketType: String,
        timeframe: String,
        marketRegime: String,
        thesis: String,
        supportingEvidence: String,
        contradictingEvidence: String,
        entryPrice: Double,
        stopPrice: Double,
        targetPrice: Double,
        accountSize: Double,
        riskPercent: Double,
        emotionalState: String,
        hasDefinedInvalidation: Boolean,
        catalystNotes: String,
        isSeekingLossRecovery: Boolean
    ): AuditAnalysisResult {
        val sizing = calculatePositionSize(accountSize, riskPercent, entryPrice, stopPrice, targetPrice)

        var circuitBreaker = false
        var breakerReason: String? = null

        val isEmotional = emotionalState.contains("Revenge", ignoreCase = true) ||
                emotionalState.contains("FOMO", ignoreCase = true) ||
                emotionalState.contains("Tilt", ignoreCase = true) ||
                emotionalState.contains("Panic", ignoreCase = true) ||
                emotionalState.contains("Greed", ignoreCase = true)

        if (isSeekingLossRecovery) {
            circuitBreaker = true
            breakerReason = "CIRCUIT BREAKER: Trade is motivated by recovering previous losses. This is revenge trading disguised as strategy. Cash is a valid position. Market participation preserved by standing down."
        } else if (isEmotional) {
            circuitBreaker = true
            breakerReason = "CIRCUIT BREAKER: Emotional state is compromised ($emotionalState). Trading under heightened emotion guarantees execution degradation. Step away from the screens."
        } else if (riskPercent > 3.0) {
            circuitBreaker = true
            breakerReason = "CIRCUIT BREAKER: Proposed risk of ${String.format(Locale.US, "%.1f", riskPercent)}% is reckless. No single trade is worth risking portfolio survival."
        } else if (!hasDefinedInvalidation) {
            circuitBreaker = true
            breakerReason = "CIRCUIT BREAKER: Setup lacks an explicit structural invalidation point. If you do not know where you are proven wrong, you are gambling, not trading."
        }

        // Grade calculation
        val grade: String
        val rationale: String

        if (circuitBreaker) {
            grade = "F"
            rationale = breakerReason ?: "Emotionally motivated or unacceptable risk profile. Immediate rejection."
        } else if (sizing.rewardRiskRatio < 1.5) {
            grade = "D"
            rationale = "Poor Risk/Reward (${String.format(Locale.US, "%.2f", sizing.rewardRiskRatio)}R). Even with a 50% win rate, this setup yields negative statistical expectancy after friction."
        } else if (contradictingEvidence.isBlank() || thesis.length < 20) {
            grade = "C"
            rationale = "Unclear setup or confirmation bias. No evidence contradicting the thesis was documented. You cannot trade what you haven't stress-tested."
        } else if (sizing.rewardRiskRatio < 2.0 || riskPercent > 1.5) {
            grade = "B"
            rationale = "Potential setup requiring higher confirmation. R:R (${String.format(Locale.US, "%.2f", sizing.rewardRiskRatio)}R) is borderline, or risk exceeds recommended 1% baseline."
        } else {
            grade = "A"
            rationale = "Strongly defined setup. Clear structural invalidation, controlled risk (${String.format(Locale.US, "%.1f", riskPercent)}%), favorable R:R (${String.format(Locale.US, "%.2f", sizing.rewardRiskRatio)}R), and clean emotional posture."
        }

        val steps = listOf(
            "1. Market Regime" to "$marketRegime (${if (marketType == "CRYPTO") "24/7 continuous liquidity regime, funding rate sensitivity" else "Market session liquidity, macroeconomic data sensitivity"}).",
            "2. Asset" to "$symbol ($marketType).",
            "3. Timeframe" to "Execution: $timeframe. Must align with higher-timeframe trend.",
            "4. Thesis" to thesis,
            "5. Supporting Evidence" to (if (supportingEvidence.isNotBlank()) supportingEvidence else "Price action alignment, key level bounce, volume confirmation."),
            "6. Contradicting Evidence" to (if (contradictingEvidence.isNotBlank()) contradictingEvidence else "WARNING: No counter-thesis provided. High risk of confirmation bias."),
            "7. Key Levels" to "Entry: $${String.format(Locale.US, "%.2f", entryPrice)}, Invalidation Stop: $${String.format(Locale.US, "%.2f", stopPrice)}, Liquidity Target: $${String.format(Locale.US, "%.2f", targetPrice)}.",
            "8. Entry Condition" to "Trigger: Confirmation of level hold on $timeframe. Never enter on market order before candle close.",
            "9. Invalidation Condition" to "Explicit: A sustained candle close beyond $${String.format(Locale.US, "%.2f", stopPrice)} completely voids the thesis.",
            "10. Stop Placement Logic" to "Structure-based stop at $${String.format(Locale.US, "%.2f", stopPrice)} (${String.format(Locale.US, "%.2f", sizing.stopDistancePercent)}% distance). Must NEVER be moved farther away.",
            "11. Target Logic" to "Target placed at structural liquidity pool of $${String.format(Locale.US, "%.2f", targetPrice)} (${String.format(Locale.US, "%.2f", sizing.rewardRiskRatio)}R).",
            "12. Position-Sizing Framework" to "Calculated strictly: (${String.format(Locale.US, "$%.2f", sizing.riskAmountDollar)} max risk) / (${String.format(Locale.US, "$%.2f", sizing.stopDistanceDollar)} stop dist) = ${String.format(Locale.US, "%.4f", sizing.positionSizeUnits)} units.",
            "13. Hypothetical Risk" to "-$${String.format(Locale.US, "%.2f", sizing.riskAmountDollar)} (${String.format(Locale.US, "%.2f", riskPercent)}% of $${String.format(Locale.US, "%.0f", accountSize)} account).",
            "14. Hypothetical Reward" to "+$${String.format(Locale.US, "%.2f", sizing.riskAmountDollar * sizing.rewardRiskRatio)} (+${String.format(Locale.US, "%.2f", sizing.rewardRiskRatio)}R).",
            "15. Risk-Reward Analysis" to "Ratio: 1 : ${String.format(Locale.US, "%.2f", sizing.rewardRiskRatio)}. Minimum acceptable standard is 1:2.0.",
            "16. Catalyst Risk" to (if (catalystNotes.isNotBlank()) catalystNotes else "Check calendar: CPI, FOMC, earnings, or token unlock schedules."),
            "17. Liquidity Risk" to (if (marketType == "CRYPTO") "Crypto order book depth & slippage during liquidation cascades." else "Opening spread, pre-market/after-hours illiquidity."),
            "18. Volatility Risk" to "ATR expansion may sweep stop before trending. Verify stop is outside normal noise range.",
            "19. Emotional Risk" to "Self-check: $emotionalState. Urge to recover previous loss: ${if (isSeekingLossRecovery) "YES (CRITICAL HAZARD)" else "NO (Disciplined)"}.",
            "20. Alternative Scenarios" to """
                • Scenario A (Works): Price respects entry and expands toward $${String.format(Locale.US, "%.2f", targetPrice)}. ACTION: Trail stop to breakeven only AFTER 1.5R target reached.
                • Scenario B (Fails): Price hits $${String.format(Locale.US, "%.2f", stopPrice)}. ACTION: Accept -$${String.format(Locale.US, "%.2f", sizing.riskAmountDollar)} instantly. No revenge re-entry.
                • Scenario C (Sideways/Chop): Price consolidates without moving for >3 sessions. ACTION: Re-evaluate thesis decay and reduce exposure.
            """.trimIndent(),
            "21. Reasons to Reject the Trade" to (if (circuitBreaker) breakerReason!! else "Reject if spread widens beyond 0.2%, or if catalyst announcement occurs before fill."),
            "22. Post-Trade Review Criteria" to "Did I follow the stop? Did I size according to the formula? Process quality is independent of outcome."
        )

        val tuitionAnswerAnalysis = if (isSeekingLossRecovery) {
            "CRITICAL WARNING: You answered YES to needing loss recovery. This trade is contaminated by previous outcome bias. When you trade to recover money, you risk catastrophic tilt. Standing aside and protecting your capital is a successful execution."
        } else {
            "DISCIPLINED POSTURE: You confirmed you would take this trade objectively on its own merits, unburdened by prior tuition. Maintain mechanical execution."
        }

        return AuditAnalysisResult(
            symbol = symbol,
            marketType = marketType,
            grade = grade,
            gradeRationale = rationale,
            circuitBreakerTriggered = circuitBreaker,
            circuitBreakerReason = breakerReason,
            positionSizing = sizing,
            structured22Steps = steps,
            mandatoryTuitionQuestion = "Would I still take this trade if I had no previous losses to recover?",
            tuitionAnswerAnalysis = tuitionAnswerAnalysis
        )
    }
}
