package com.example.data.repository

import com.example.data.db.TradeDao
import com.example.data.model.MistakeLog
import com.example.data.model.TradeLog
import com.example.domain.SampleTuitionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TradeRepository(private val tradeDao: TradeDao) {

    val allTrades: Flow<List<TradeLog>> = tradeDao.getAllTrades()
    val tuitionTrades: Flow<List<TradeLog>> = tradeDao.getTuitionTrades()
    val activeTrades: Flow<List<TradeLog>> = tradeDao.getActiveTrades()
    val allMistakes: Flow<List<MistakeLog>> = tradeDao.getAllMistakes()

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        val count = tradeDao.getTradeCount()
        if (count == 0) {
            tradeDao.insertTrades(SampleTuitionData.initialTuitionTrades)
            for (m in SampleTuitionData.initialMistakes) {
                tradeDao.insertMistake(m)
            }
        }
    }

    suspend fun addTrade(trade: TradeLog): Long = withContext(Dispatchers.IO) {
        tradeDao.insertTrade(trade)
    }

    suspend fun updateTrade(trade: TradeLog) = withContext(Dispatchers.IO) {
        tradeDao.updateTrade(trade)
    }

    suspend fun deleteTrade(trade: TradeLog) = withContext(Dispatchers.IO) {
        tradeDao.deleteTrade(trade)
    }

    suspend fun addMistake(mistake: MistakeLog): Long = withContext(Dispatchers.IO) {
        tradeDao.insertMistake(mistake)
    }

    suspend fun deleteMistake(mistake: MistakeLog) = withContext(Dispatchers.IO) {
        tradeDao.deleteMistake(mistake)
    }
}
