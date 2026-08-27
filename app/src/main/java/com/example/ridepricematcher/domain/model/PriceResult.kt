package com.example.ridepricematcher.domain.model

data class PriceResult(
    val amount: Double,
    val currency: String,
    val confidence: Float,
    val sourceText: String,
    val timestamp: Long = System.currentTimeMillis()
)
