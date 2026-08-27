package com.example.ridepricematcher.domain.matcher

import com.example.ridepricematcher.domain.model.LanguageConfig
import com.example.ridepricematcher.domain.parser.TextNormalizer

class AcceptancePhraseMatcher(
    private val normalizer: TextNormalizer = TextNormalizer()
) {

    data class Match(
        val phrase: String,
        val language: String,
        val confidence: Float
    )

    fun match(text: String, languages: List<LanguageConfig>): Match? {
        val normalizedText = normalizer.normalize(text)
        var bestMatch: Match? = null
        var bestConfidence = 0f

        for (lang in languages.filter { it.enabled }) {
            for (phrase in lang.acceptancePhrases) {
                val normalizedPhrase = normalizer.normalizeForMatching(phrase, lang.locale)
                val confidence = calculateMatchConfidence(normalizedText, normalizedPhrase)
                if (confidence > bestConfidence && confidence >= 0.75f) {
                    bestConfidence = confidence
                    bestMatch = Match(phrase, lang.locale, confidence)
                }
            }
        }

        return bestMatch
    }

    private fun calculateMatchConfidence(text: String, phrase: String): Float {
        val textLower = text.lowercase()
        val phraseLower = phrase.lowercase()

        // Exact match
        if (textLower == phraseLower) return 1.0f

        // Contains match
        if (textLower.contains(phraseLower)) return 0.95f
        if (phraseLower.contains(textLower) && textLower.length >= 3) return 0.9f

        // Word boundary match
        val wordPattern = Regex("\\b${Regex.escape(phraseLower)}\\b")
        if (wordPattern.containsMatchIn(textLower)) return 0.92f

        // Fuzzy match for OCR noise tolerance
        val distance = levenshteinDistance(textLower, phraseLower)
        val maxLen = maxOf(textLower.length, phraseLower.length)
        if (maxLen > 0) {
            val similarity = 1.0 - (distance.toDouble() / maxLen)
            if (similarity > 0.7) {
                return similarity.toFloat()
            }
        }

        return 0f
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                dp[i][j] = if (s1[i - 1] == s2[j - 1]) {
                    dp[i - 1][j - 1]
                } else {
                    minOf(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1,
                        dp[i - 1][j - 1] + 1
                    )
                }
            }
        }
        return dp[s1.length][s2.length]
    }
}
