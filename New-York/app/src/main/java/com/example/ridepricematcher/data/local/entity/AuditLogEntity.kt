package com.example.ridepricematcher.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val adminUserId: String,
    val action: String,
    val targetUserId: String?,
    val metadata: Map<String, String>,
    val createdAt: String,
    val syncedAt: Long = System.currentTimeMillis()
)
