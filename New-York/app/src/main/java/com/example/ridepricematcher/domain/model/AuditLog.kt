package com.example.ridepricematcher.domain.model

data class AuditLog(
    val id: String,
    val adminUserId: String,
    val action: String,
    val targetUserId: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: String = ""
)
