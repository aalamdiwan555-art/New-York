package com.example.ridepricematcher.domain.model

data class DetectionResult(
    val detected: Boolean,
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f,
    val confidence: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val source: String = "",
    val language: String = "",
    val matchedText: String = "",
    val matchedPrice: PriceResult? = null
)
