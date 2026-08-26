package com.example.ridepricematcher.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entitlements")
data class CachedEntitlementEntity(
    @PrimaryKey val userId: String,
    val type: String,
    val adFree: Boolean,
    val lifetime: Boolean,
    val subscriptionExpiresAt: String?,
    val syncedAt: Long = System.currentTimeMillis()
)
