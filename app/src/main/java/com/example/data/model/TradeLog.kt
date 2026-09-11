package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_logs")
data class TradeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbol: String,
    val marketType: String, // "STOCKS" or "CRYPTO"
    val timestamp: Long = System.currentTimeMillis(),
    val timeframe: String = "4H",
    val marketRegime: String = "Range Bound",
    val thesis: String,
    val entryPrice: Double,
    val stopLossPrice: Double,
    val targetPrice: Double,
    val positionSize: Double,
    val accountSizeAtEntry: Double = 10000.0,
    val riskAmount: Double,
    val riskPercent: Double,
    val rewardRiskRatio: Double,
    val exitPrice: Double? = null,
    val pnl: Double? = null,
    val pnlR: Double? = null,
    val holdingPeriodHours: Double? = null,
    val classification: String = "Valid Setup",
    val emotionalState: String = "Disciplined",
    val isGoodTrade: Boolean = true, // Good trade = followed rules, regardless of outcome
    val rootCause: String = "None / Disciplined",
    val notes: String = "",
    val isTuitionTrade: Boolean = false // Flag for initial $10k loss history
)

@Entity(tableName = "mistake_logs")
data class MistakeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val mistakeType: String,
    val triggerCondition: String,
    val costDollars: Double,
    val emotionalState: String,
    val ruleBroken: String,
    val correctiveAction: String
)
