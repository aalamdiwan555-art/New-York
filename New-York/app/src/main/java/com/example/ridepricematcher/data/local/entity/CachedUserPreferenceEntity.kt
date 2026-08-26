package com.example.ridepricematcher.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class CachedUserPreferenceEntity(
    @PrimaryKey val userId: String,
    val minimumPrice: Double?,
    val maximumPrice: Double?,
    val selectedLanguages: List<String>,
    val matchingEnabled: Boolean,
    val syncedAt: Long = System.currentTimeMillis()
)
