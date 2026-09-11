package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.domain.QuantitativeEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiMentorRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val systemPrompt = """
        You are an elite, brutally honest trading mentor, risk manager, market researcher, trading psychologist, and quantitative analyst.
        Your student is a trader who lost approximately $10,000 in the markets and is rebuilding from the ground up as a disciplined, risk-first trader.
        
        CRITICAL RULES:
        1. No motivational hype. No guaranteed profits. Never tell them they can easily win the money back.
        2. Treat their previous $10,000 loss strictly as TUITION, not something to immediately recover.
        3. Never invent market data. Separate what is known from what is uncertain.
        4. When analyzing stocks vs crypto, respect their structural differences (liquidity, continuous 24/7 vs market sessions, leverage/perps, funding rates, macro vs tokenomics).
        5. Explain the difference between a good trade that lost money (followed rules, disciplined stop) and a bad trade that made money (gambling, no stop, lucky outcome bias).
        6. Act as an active CIRCUIT BREAKER whenever you detect:
           - Revenge trading (trying to get even after a loss)
           - Fear of missing out (FOMO / chasing extended candles)
           - Sizing up because of a recent loss
           - Moving stop losses away
           - Adding to losing positions (averaging down without a predefined plan)
           - Chasing social media pumps
           - Absence of an explicit structural invalidation point
        7. Treat CASH as a valid position. Treat WAITING as an active, disciplined decision.
        8. When evaluating a setup, audit the 22 key parameters:
           1. Market regime, 2. Asset, 3. Timeframe, 4. Thesis, 5. Evidence supporting, 6. Evidence contradicting,
           7. Key levels, 8. Entry condition, 9. Invalidation condition, 10. Stop placement logic, 11. Target logic,
           12. Position-sizing framework, 13. Hypothetical risk, 14. Hypothetical reward, 15. Risk-reward analysis,
           16. Catalyst risk, 17. Liquidity risk, 18. Volatility risk, 19. Emotional risk, 20. Alternative scenarios (Bull, Bear, Sideways),
           21. Reasons to reject the trade, 22. Post-trade review criteria.
        9. END EVERY ANALYSIS WITH:
           "Would I still take this trade if I had no previous losses to recover?"
           Followed by the final decision classification:
           A — Strongly defined setup
           B — Potential setup requiring confirmation
           C — Unclear setup
           D — Poor risk/reward
           F — Emotionally motivated or unacceptable risk
        10. Never confuse confidence with certainty. Your highest priority is preserving the trader's ability to participate in the market tomorrow.
    """.trimIndent()

    suspend fun auditTradeWithAI(
        userMessage: String,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiMentor", "API key not configured; using offline quantitative mentor engine.")
            return@withContext generateOfflineMentorResponse(userMessage)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // System instruction included as first exchange or systemInstruction
            for (msg in conversationHistory) {
                val role = if (msg.first == "user") "user" else "model"
                val partObj = JSONObject().put("text", msg.second)
                val contentObj = JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(partObj))
                contentsArray.put(contentObj)
            }

            // Current prompt with persona
            val promptText = "$systemPrompt\n\nTrader's Question or Trade Submission:\n$userMessage"
            val currentPartObj = JSONObject().put("text", promptText)
            val currentContentObj = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(currentPartObj))
            contentsArray.put(currentContentObj)

            val rootJson = JSONObject().put("contents", contentsArray)

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string()

            if (!response.isSuccessful || bodyString.isNullOrBlank()) {
                Log.w("GeminiMentor", "API call unsuccessful: ${response.code} $bodyString")
                return@withContext generateOfflineMentorResponse(userMessage)
            }

            val respJson = JSONObject(bodyString)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCand = candidates.getJSONObject(0)
                val content = firstCand.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return@withContext parts.getJSONObject(0).optString("text")
                }
            }

            generateOfflineMentorResponse(userMessage)
        } catch (e: Exception) {
            Log.e("GeminiMentor", "Failed to call Gemini API", e)
            generateOfflineMentorResponse(userMessage)
        }
    }

    private fun generateOfflineMentorResponse(prompt: String): String {
        val lower = prompt.lowercase()
        val isEmotional = lower.contains("recover") || lower.contains("make back") ||
                lower.contains("revenge") || lower.contains("lost") || lower.contains("quick") ||
                lower.contains("fomo") || lower.contains("all in")

        if (isEmotional) {
            return """
                ### 🛑 CIRCUIT BREAKER TRIGGERED: EMOTIONAL CONTAMINATION DETECTED

                **Brutal Truth:** You are thinking about the money you lost rather than the quality of the process.
                
                You lost approximately $10,000. That money is already paid as **market tuition**. If your subconscious motive right now is "getting it back," you are trading on tilt. 
                
                **The Mathematics of Loss Recovery:**
                • A 50% account loss requires a 100% gain just to break even.
                • Trying to force a 100% return quickly requires massive leverage, which guarantees a 100% chance of eventual total ruin.
                • Survival is the non-negotiable prerequisite for compounding.
                
                **Prescribed Action:**
                1. Stand down. Cash is an active, profitable position that loses zero dollars during market noise.
                2. Do not look at 1-minute or 5-minute charts today.
                3. Ask yourself honestly:
                
                > **"Would I still take this trade if I had no previous losses to recover?"**
                
                If the answer is no, this is revenge trading.
                
                **Final Decision Classification: F — Emotionally motivated or unacceptable risk.**
            """.trimIndent()
        }

        return """
            ### 📊 QUANTITATIVE & RISK AUDIT

            Let us look at this with surgical skepticism.

            **1. Structural Assessment:**
            • Market Regime: Assess whether the asset is in a clean higher-timeframe expansion or low-liquidity range chop.
            • Liquidity & Execution Friction: Always calculate slippage and exchange fees before determining viability.

            **2. Position Sizing & Invalidation:**
            • Professional standard: Maximum 1.0% account risk per trade.
            • Never enter without an objective price level where your thesis is proven 100% wrong.
            • If you cannot write down the exact invalidation price before entering, you have no trade.

            **3. Alternative Scenarios:**
            • **Works:** Hits target. Trail stops only after 1.5R.
            • **Fails:** Hits stop. Accept the loss instantly without re-entering.
            • **Chops:** Consolidates. Time stop kicks in.

            > **"Would I still take this trade if I had no previous losses to recover?"**

            **Final Decision Classification: B — Potential setup requiring confirmation.**
        """.trimIndent()
    }
}
