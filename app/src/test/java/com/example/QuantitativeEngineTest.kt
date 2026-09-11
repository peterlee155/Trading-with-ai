package com.example

import com.example.domain.QuantitativeEngine
import com.example.domain.SampleTuitionData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuantitativeEngineTest {

    @Test
    fun testPositionSizingCalculation() {
        val result = QuantitativeEngine.calculatePositionSize(
            accountSize = 10000.0,
            riskPercent = 1.0, // $100 risk
            entryPrice = 100.0,
            stopPrice = 95.0,  // $5 stop distance
            targetPrice = 110.0 // $10 target distance -> 2.0 R:R
        )

        assertEquals(100.0, result.riskAmountDollar, 0.01)
        assertEquals(5.0, result.stopDistanceDollar, 0.01)
        assertEquals(20.0, result.positionSizeUnits, 0.01) // 100 / 5 = 20 units
        assertEquals(2000.0, result.capitalAllocatedDollar, 0.01) // 20 * 100 = $2,000
        assertEquals(2.0, result.rewardRiskRatio, 0.01)
        assertTrue(result.warnings.isEmpty())
    }

    @Test
    fun testPositionSizingExcessiveRiskWarning() {
        val result = QuantitativeEngine.calculatePositionSize(
            accountSize = 10000.0,
            riskPercent = 5.0, // Dangerous 5% risk
            entryPrice = 100.0,
            stopPrice = 90.0,
            targetPrice = 105.0 // Only 0.5 R:R
        )

        assertTrue(result.warnings.any { it.contains("exceeds the professional threshold", ignoreCase = true) })
        assertTrue(result.warnings.any { it.contains("POOR R:R", ignoreCase = true) })
    }

    @Test
    fun testCircuitBreakerTriggeredOnRevengeTrading() {
        val audit = QuantitativeEngine.auditTradeSetup(
            symbol = "BTC/USDT",
            marketType = "CRYPTO",
            timeframe = "5m",
            marketRegime = "Chop",
            thesis = "Need to make back yesterday's losses quickly",
            supportingEvidence = "Price dropped a lot",
            contradictingEvidence = "Downtrend intact",
            entryPrice = 60000.0,
            stopPrice = 58000.0,
            targetPrice = 64000.0,
            accountSize = 10000.0,
            riskPercent = 1.0,
            emotionalState = "Anxious",
            hasDefinedInvalidation = true,
            catalystNotes = "None",
            isSeekingLossRecovery = true // Explicitly triggered!
        )

        assertEquals("F", audit.grade)
        assertTrue(audit.circuitBreakerTriggered)
        assertTrue(audit.circuitBreakerReason?.contains("revenge trading", ignoreCase = true) == true)
        assertTrue(audit.mandatoryTuitionQuestion.contains("Would I still take this trade"))
    }

    @Test
    fun testCircuitBreakerTriggeredOnMissingInvalidation() {
        val audit = QuantitativeEngine.auditTradeSetup(
            symbol = "AAPL",
            marketType = "STOCKS",
            timeframe = "1D",
            marketRegime = "Trending",
            thesis = "Good company with consistent earnings",
            supportingEvidence = "Earnings beat",
            contradictingEvidence = "Market extended",
            entryPrice = 180.0,
            stopPrice = 170.0,
            targetPrice = 200.0,
            accountSize = 10000.0,
            riskPercent = 1.0,
            emotionalState = "Calm",
            hasDefinedInvalidation = false, // Critical failure
            catalystNotes = "None",
            isSeekingLossRecovery = false
        )

        assertEquals("F", audit.grade)
        assertTrue(audit.circuitBreakerTriggered)
        assertTrue(audit.circuitBreakerReason?.contains("invalidation", ignoreCase = true) == true)
    }

    @Test
    fun testGradeAForDisciplinedSetup() {
        val audit = QuantitativeEngine.auditTradeSetup(
            symbol = "MSFT",
            marketType = "STOCKS",
            timeframe = "4H",
            marketRegime = "Bullish Expansion above 200 EMA",
            thesis = "Pullback into key daily demand zone after high volume breakout",
            supportingEvidence = "Volume contraction during pullback, institutional bid at support",
            contradictingEvidence = "VIX ticking up slightly",
            entryPrice = 400.0,
            stopPrice = 390.0, // $10 risk
            targetPrice = 430.0, // $30 reward -> 3.0 R:R
            accountSize = 10000.0,
            riskPercent = 1.0,
            emotionalState = "Calm & Detached",
            hasDefinedInvalidation = true,
            catalystNotes = "Earnings not for 45 days",
            isSeekingLossRecovery = false
        )

        assertEquals("A", audit.grade)
        assertFalse(audit.circuitBreakerTriggered)
    }

    @Test
    fun testTuitionDataMetricsCalculation() {
        val sampleTrades = SampleTuitionData.initialTuitionTrades
        val metrics = QuantitativeEngine.calculateMetrics(sampleTrades)

        assertTrue(metrics.totalTrades > 0)
        assertEquals(0, metrics.winningTrades)
        assertEquals(5, metrics.losingTrades)
        assertTrue(metrics.totalPnl < 0) // Represents the ~$10,000 tuition loss
        assertTrue(metrics.grossLosses >= 10000.0)
        assertTrue(metrics.rootCauses.isNotEmpty())
    }

    @Test
    fun testLossRecoveryMathTable() {
        val table = QuantitativeEngine.getLossRecoveryMathTable()
        val fiftyPercentLoss = table.firstOrNull { it.first == 50 }
        val seventyFivePercentLoss = table.firstOrNull { it.first == 75 }

        assertEquals(100.0, fiftyPercentLoss?.second ?: 0.0, 0.1) // 50% loss requires 100% gain
        assertEquals(300.0, seventyFivePercentLoss?.second ?: 0.0, 0.1) // 75% loss requires 300% gain
    }
}
