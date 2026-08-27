package com.example.ridepricematcher.domain.model

sealed class MatchResult {
    data class Success(
        val price: PriceResult,
        val rule: PriceRule,
        val acceptancePhrase: String,
        val language: String,
        val confidence: Float,
        val timestamp: Long = System.currentTimeMillis()
    ) : MatchResult()

    data class NoMatch(val reason: String) : MatchResult()
    data class Error(val exception: Throwable) : MatchResult()
}
