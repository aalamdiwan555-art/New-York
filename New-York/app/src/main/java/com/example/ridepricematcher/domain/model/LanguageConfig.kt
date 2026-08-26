package com.example.ridepricematcher.domain.model

data class LanguageConfig(
    val id: String,
    val locale: String,
    val displayName: String,
    val displayNameNative: String,
    val acceptancePhrases: List<String>,
    val priceKeywords: List<String>,
    val distanceKeywords: List<String>,
    val durationKeywords: List<String>,
    val aliases: List<String>,
    val enabled: Boolean = true
)
