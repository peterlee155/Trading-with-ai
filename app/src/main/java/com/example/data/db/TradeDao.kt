package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MistakeLog
import com.example.data.model.TradeLog
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trade_logs ORDER BY timestamp DESC")
    fun getAllTrades(): Flow<List<TradeLog>>

    @Query("SELECT * FROM trade_logs WHERE isTuitionTrade = 1 ORDER BY timestamp ASC")
    fun getTuitionTrades(): Flow<List<TradeLog>>

    @Query("SELECT * FROM trade_logs WHERE isTuitionTrade = 0 ORDER BY timestamp DESC")
    fun getActiveTrades(): Flow<List<TradeLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrades(trades: List<TradeLog>)

    @Update
    suspend fun updateTrade(trade: TradeLog)

    @Delete
    suspend fun deleteTrade(trade: TradeLog)

    @Query("SELECT COUNT(*) FROM trade_logs")
    suspend fun getTradeCount(): Int

    // Mistake logs
    @Query("SELECT * FROM mistake_logs ORDER BY timestamp DESC")
    fun getAllMistakes(): Flow<List<MistakeLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: MistakeLog): Long

    @Delete
    suspend fun deleteMistake(mistake: MistakeLog)
}
