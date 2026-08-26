package com.example.ridepricematcher.data.repository

import com.example.ridepricematcher.data.remote.SupabaseClientProvider
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.AuditLog
import com.example.ridepricematcher.domain.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AdminRepository {

    private val postgrest = SupabaseClientProvider.postgrest

    suspend fun getAllUsers(): Result<List<UserProfile>> = withContext(Dispatchers.IO) {
        try {
            val result = postgrest.from("profiles")
                .select()
                .decodeList<ProfileDto>()
            Result.success(result.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(AppError.Server("Failed to load users", e.message ?: ""))
        }
    }

    suspend fun blockUser(userId: String, adminId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("profiles").update(
                buildJsonObject { put("blocked", true) }
            ) {
                filter { eq("id", userId) }
            }
            logAction(adminId, "block_user", userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.Server("Failed to block user", e.message ?: ""))
        }
    }

    suspend fun unblockUser(userId: String, adminId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("profiles").update(
                buildJsonObject { put("blocked", false) }
            ) {
                filter { eq("id", userId) }
            }
            logAction(adminId, "unblock_user", userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.Server("Failed to unblock user", e.message ?: ""))
        }
    }

    suspend fun grantAdFree(userId: String, adminId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("entitlements").upsert(
                buildJsonObject {
                    put("user_id", userId)
                    put("ad_free", true)
                }
            )
            logAction(adminId, "grant_ad_free", userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.Server("Failed to grant ad-free", e.message ?: ""))
        }
    }

    suspend fun revokeAdFree(userId: String, adminId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("entitlements").update(
                buildJsonObject { put("ad_free", false) }
            ) {
                filter { eq("user_id", userId) }
            }
            logAction(adminId, "revoke_ad_free", userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.Server("Failed to revoke ad-free", e.message ?: ""))
        }
    }

    suspend fun grantLifetime(userId: String, adminId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("entitlements").upsert(
                buildJsonObject {
                    put("user_id", userId)
                    put("lifetime", true)
                    put("type", "LIFETIME")
                }
            )
            logAction(adminId, "grant_lifetime", userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.Server("Failed to grant lifetime", e.message ?: ""))
        }
    }

    suspend fun getAuditLogs(): Result<List<AuditLog>> = withContext(Dispatchers.IO) {
        try {
            val result = postgrest.from("audit_logs")
                .select()
                .decodeList<AuditLogDto>()
            Result.success(result.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(AppError.Server("Failed to load audit logs", e.message ?: ""))
        }
    }

    private suspend fun logAction(adminId: String, action: String, targetId: String? = null) {
        try {
            postgrest.from("audit_logs").insert(
                buildJsonObject {
                    put("admin_user_id", adminId)
                    put("action", action)
                    targetId?.let { put("target_user_id", it) }
                }
            )
        } catch (_: Exception) {
            // Silent fail for audit logging
        }
    }

    @kotlinx.serialization.Serializable
    private data class ProfileDto(
        val id: String,
        val email: String,
        val display_name: String? = null,
        val role: String = "user",
        val blocked: Boolean = false,
        val created_at: String = "",
        val updated_at: String = ""
    ) {
        fun toDomain() = UserProfile(
            id = id,
            email = email,
            displayName = display_name ?: "",
            role = role,
            blocked = blocked,
            createdAt = created_at,
            updatedAt = updated_at
        )
    }

    @kotlinx.serialization.Serializable
    private data class AuditLogDto(
        val id: String,
        val admin_user_id: String,
        val action: String,
        val target_user_id: String? = null,
        val metadata: Map<String, String> = emptyMap(),
        val created_at: String = ""
    ) {
        fun toDomain() = AuditLog(
            id = id,
            adminUserId = admin_user_id,
            action = action,
            targetUserId = target_user_id,
            metadata = metadata,
            createdAt = created_at
        )
    }
}
