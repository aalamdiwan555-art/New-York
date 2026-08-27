package com.example.ridepricematcher.domain.matcher

import com.example.ridepricematcher.domain.model.*
import com.example.ridepricematcher.domain.parser.PriceParser
import com.example.ridepricematcher.domain.parser.TextNormalizer

class MatchingEngine(
    private val priceParser: PriceParser = PriceParser(),
    private val phraseMatcher: AcceptancePhraseMatcher = AcceptancePhraseMatcher(),
    private val normalizer: TextNormalizer = TextNormalizer()
) {

    fun process(
        text: String,
        priceRule: PriceRule,
        languages: List<LanguageConfig>
    ): MatchResult {
        try {
            val normalizedText = normalizer.normalize(text)

            // Parse price
            val priceResult = priceParser.parse(normalizedText)
                ?: return MatchResult.NoMatch("No price detected in text")

            // Check price rule match
            if (!priceRule.matches(priceResult)) {
                return MatchResult.NoMatch(
                    "Price ₹${priceResult.amount} does not match configured rule"
                )
            }

            // Match acceptance phrase
            val phraseMatch = phraseMatcher.match(normalizedText, languages)
                ?: return MatchResult.NoMatch("No acceptance phrase detected")

            // Calculate overall confidence
            val overallConfidence = (priceResult.confidence + phraseMatch.confidence) / 2

            if (overallConfidence < 0.6f) {
                return MatchResult.NoMatch("Confidence too low: $overallConfidence")
            }

            return MatchResult.Success(
                price = priceResult,
                rule = priceRule,
                acceptancePhrase = phraseMatch.phrase,
                language = phraseMatch.language,
                confidence = overallConfidence
            )

        } catch (e: Exception) {
            return MatchResult.Error(e)
        }
    }
}
