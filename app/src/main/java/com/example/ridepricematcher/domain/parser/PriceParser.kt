package com.example.ridepricematcher.domain.parser

import com.example.ridepricematcher.domain.model.PriceResult
import java.util.regex.Pattern

class PriceParser {

    companion object {
        // Currency patterns for Indian context - raw strings avoid escape issues
        private val CURRENCY_PATTERNS = listOf(
            Pattern.compile("""(?i)(?:\b(?:Rs\.?|INR|रु\.?)|₹)\s*([0-9,]+(?:\.[0-9]+)?)"""),
            Pattern.compile("""(?i)\b([0-9,]+(?:\.[0-9]+)?)\s*(?:Rs\.?|INR|₹|रु\.?|रुपये)"""),
            Pattern.compile("""(?i)\b(?:price|fare|cost|amount)\s*[:=]?\s*([0-9,]+(?:\.[0-9]+)?)"""),
            Pattern.compile("""(?i)\b([0-9,]+(?:\.[0-9]+)?)\s*(?:रुपये|रु\.?)"""),
        )

        private val PRICE_KEYWORDS = listOf(
            "fare", "price", "cost", "amount", "total", "estimate", "रुपये", "रु", "मूल्य",
            "ದರ", "ಬೆಲೆ", "విలువ", "விலை", "দাম", "मूल्य", "വില"
        )

        private val NUMBER_PATTERN = Pattern.compile("""([0-9,]+(?:\.[0-9]+)?)""")
    }

    fun parse(text: String): PriceResult? {
        if (text.isBlank()) return null

        val normalized = normalizeText(text)
        var bestResult: PriceResult? = null
        var bestConfidence = 0f

        for (pattern in CURRENCY_PATTERNS) {
            val matcher = pattern.matcher(normalized)
            while (matcher.find()) {
                val rawAmount = matcher.group(1)?.replace(",", "") ?: continue
                val amount = rawAmount.toDoubleOrNull() ?: continue
                val contextStart = (matcher.start() - 20).coerceAtLeast(0)
                val contextEnd = (matcher.end() + 20).coerceAtMost(normalized.length)
                val context = normalized.substring(contextStart, contextEnd)

                val confidence = calculateConfidence(amount, context, matcher.group())
                if (confidence > bestConfidence) {
                    bestConfidence = confidence
                    bestResult = PriceResult(
                        amount = amount,
                        currency = detectCurrency(context),
                        confidence = confidence,
                        sourceText = matcher.group()
                    )
                }
            }
        }

        // Fallback: look for numbers near price keywords
        if (bestResult == null) {
            bestResult = findPriceNearKeywords(normalized)
        }

        return bestResult
    }

    private fun normalizeText(text: String): String {
        return text
            .trim()
            .replace(Regex("""\s+"""), " ")
            .replace("\u200B", "") // zero-width space
            .replace("\u00A0", " ") // non-breaking space
    }

    private fun detectCurrency(context: String): String {
        return when {
            context.contains("₹") || context.contains("Rs") ||
            context.contains("रु") || context.contains("रुपये") -> "INR"
            context.contains("INR", ignoreCase = true) -> "INR"
            else -> "INR" // Default for Indian context
        }
    }

    private fun calculateConfidence(amount: Double, context: String, sourceText: String): Float {
        var confidence = 0.5f

        // Boost if near price keywords
        if (PRICE_KEYWORDS.any { context.contains(it, ignoreCase = true) }) {
            confidence += 0.2f
        }

        // Boost for reasonable fare amounts (Rs.10 to Rs.50,000)
        if (amount in 10.0..50000.0) {
            confidence += 0.15f
        }

        // Boost for clear currency symbol
        if (sourceText.contains("₹") || sourceText.contains("Rs")) {
            confidence += 0.1f
        }

        // Penalize very large or very small numbers
        if (amount < 5.0 || amount > 100000.0) {
            confidence -= 0.2f
        }

        return confidence.coerceIn(0f, 1f)
    }

    private fun findPriceNearKeywords(text: String): PriceResult? {
        var bestResult: PriceResult? = null
        var bestConfidence = 0f

        for (keyword in PRICE_KEYWORDS) {
            var index = text.indexOf(keyword, ignoreCase = true)
            while (index >= 0) {
                val windowStart = (index - 30).coerceAtLeast(0)
                val windowEnd = (index + keyword.length + 30).coerceAtMost(text.length)
                val window = text.substring(windowStart, windowEnd)
                val matcher = NUMBER_PATTERN.matcher(window)

                while (matcher.find()) {
                    val rawAmount = matcher.group(1)?.replace(",", "") ?: continue
                    val amount = rawAmount.toDoubleOrNull() ?: continue
                    val numberStart = windowStart + matcher.start()
                    val numberEnd = windowStart + matcher.end()
                    val before = text.substring((numberStart - 20).coerceAtLeast(0), numberStart)
                    val after = text.substring(numberEnd, (numberEnd + 20).coerceAtMost(text.length))

                    val belongsToMetric =
                        Regex("(?i)(?:km|kms|kilometers?|miles?|min|mins|minutes?)\\s*$").containsMatchIn(before) ||
                            Regex("(?i)^\\s*(?:km|kms|kilometers?|miles?|min|mins|minutes?)\\b").containsMatchIn(after) ||
                            Regex("(?i)(?:trip\\s*id|rating|surge|multiplier)\\s*$").containsMatchIn(before)
                    if (belongsToMetric) continue

                    val distance = if (numberStart >= index + keyword.length) {
                        numberStart - (index + keyword.length)
                    } else {
                        index - numberEnd
                    }.coerceAtLeast(0)
                    var confidence = 0.4f
                    if (amount in 10.0..50000.0) confidence += 0.15f
                    if (numberStart >= index + keyword.length) confidence += 0.1f
                    if (distance <= 12) confidence += 0.1f
                    confidence -= (distance / 60f).coerceAtMost(0.2f)
                    confidence = confidence.coerceIn(0f, 1f)

                    if (confidence > bestConfidence) {
                        bestConfidence = confidence
                        bestResult = PriceResult(
                            amount = amount,
                            currency = "INR",
                            confidence = confidence,
                            sourceText = matcher.group()
                        )
                    }
                }
                index = text.indexOf(keyword, index + keyword.length, ignoreCase = true)
            }
        }

        return bestResult
    }

    fun parseMultiple(text: String): List<PriceResult> {
        val results = mutableListOf<PriceResult>()
        val seen = mutableSetOf<Double>()

        for (pattern in CURRENCY_PATTERNS) {
            val matcher = pattern.matcher(normalizeText(text))
            while (matcher.find()) {
                val rawAmount = matcher.group(1)?.replace(",", "") ?: continue
                val amount = rawAmount.toDoubleOrNull() ?: continue
                if (seen.add(amount)) {
                    results.add(
                        PriceResult(
                            amount = amount,
                            currency = detectCurrency(matcher.group()),
                            confidence = calculateConfidence(amount, matcher.group(), matcher.group()),
                            sourceText = matcher.group()
                        )
                    )
                }
            }
        }
        return results.sortedByDescending { it.confidence }
    }
}
