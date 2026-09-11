package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.data.db.AppDatabase
import com.example.data.repository.GeminiMentorRepository
import com.example.data.repository.TradeRepository
import com.example.ui.TradingMentorApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val tradeRepository = TradeRepository(database.tradeDao())
        val mentorRepository = GeminiMentorRepository()

        setContent {
            MyApplicationTheme {
                TradingMentorApp(
                    tradeRepository = tradeRepository,
                    mentorRepository = mentorRepository
                )
            }
        }
    }
}
