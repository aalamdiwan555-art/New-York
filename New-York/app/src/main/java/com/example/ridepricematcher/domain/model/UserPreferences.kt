package com.example.ridepricematcher.domain.model

data class UserPreferences(
    val userId: String,
    val minimumPrice: Double? = null,
    val maximumPrice: Double? = null,
    val selectedLanguages: List<String> = emptyList(),
    val matchingEnabled: Boolean = false,
    val updatedAt: String = ""
)
