package com.example.ridepricematcher.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrases")
data class CachedPhraseEntity(
    @PrimaryKey val id: String,
    val languageId: String,
    val type: String,
    val phrase: String,
    val enabled: Boolean,
    val syncedAt: Long = System.currentTimeMillis()
)
