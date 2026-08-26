package com.example.ridepricematcher.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "languages")
data class CachedLanguageEntity(
    @PrimaryKey val id: String,
    val locale: String,
    val displayName: String,
    val displayNameNative: String,
    val aliases: List<String>,
    val enabled: Boolean,
    val priceKeywords: List<String>,
    val distanceKeywords: List<String>,
    val durationKeywords: List<String>,
    val syncedAt: Long = System.currentTimeMillis()
)
