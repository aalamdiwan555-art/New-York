package com.example.ridepricematcher.domain.matcher

import com.example.ridepricematcher.domain.model.LanguageConfig
import com.example.ridepricematcher.domain.parser.TextNormalizer

class AcceptancePhraseMatcher(
    private val normalizer: TextNormalizer = TextNormalizer(),
    private val config: MatchConfig = MatchConfig()
) {
    data class MatchConfig(
        val defaultFuzzyThreshold: Int = 2,
        val fuzzyThresholdsByLanguage: Map<String, Int> = emptyMap()
    ) {
        init {
            require(defaultFuzzyThreshold >= 0) { "defaultFuzzyThreshold must be non-negative" }
            require(fuzzyThresholdsByLanguage.values.all { it >= 0 }) {
                "language fuzzy thresholds must be non-negative"
            }
        }
        fun thresholdFor(language: String): Int =
            fuzzyThresholdsByLanguage[language] ?: defaultFuzzyThreshold
    }

    data class Match(val phrase: String, val language: String, val confidence: Float)

    fun match(text: String, languages: List<LanguageConfig>): Match? {
        var bestMatch: Match? = null
        var bestConfidence = 0f
        for (lang in languages.filter { it.enabled }) {
            val normalizedText = normalizer.normalizeForMatching(text, lang.locale)
            val threshold = config.thresholdFor(lang.locale)
            for (phrase in lang.acceptancePhrases) {
                val normalizedPhrase = normalizer.normalizeForMatching(phrase, lang.locale)
                val confidence = calculateMatchConfidence(normalizedText, normalizedPhrase, threshold)
                if (confidence > bestConfidence && confidence >= 0.75f) {
                    bestConfidence = confidence
                    bestMatch = Match(phrase, lang.locale, confidence)
                }
            }
        }
        return bestMatch
    }

    private fun calculateMatchConfidence(text: String, phrase: String, fuzzyThreshold: Int): Float {
        val textLower = text.lowercase()
        val phraseLower = phrase.lowercase()
        if (textLower == phraseLower) return 1.0f
        if (textLower.contains(phraseLower)) return 0.95f
        if (phraseLower.contains(textLower) && textLower.length >= 3) return 0.9f
        val wordPattern = Regex("\\b" + Regex.escape(phraseLower) + "\\b")
        if (wordPattern.containsMatchIn(textLower)) return 0.92f
        val distance = levenshteinDistance(textLower, phraseLower)
        val maxLen = maxOf(textLower.length, phraseLower.length)
        if (maxLen > 0 && distance <= fuzzyThreshold) {
            return (1.0 - (distance.toDouble() / maxLen)).toFloat()
        }
        return 0f
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                dp[i][j] = if (s1[i - 1] == s2[j - 1]) dp[i - 1][j - 1] else minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + 1
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
