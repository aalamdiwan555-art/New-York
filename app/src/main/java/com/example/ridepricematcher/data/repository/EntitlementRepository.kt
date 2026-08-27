package com.example.ridepricematcher.data.repository

import com.example.ridepricematcher.data.local.dao.EntitlementDao
import com.example.ridepricematcher.data.local.entity.CachedEntitlementEntity
import com.example.ridepricematcher.data.remote.SupabaseClientProvider
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.Entitlement
import com.example.ridepricematcher.domain.model.EntitlementType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EntitlementRepository(
    private val entitlementDao: EntitlementDao
) {

    private val postgrest = SupabaseClientProvider.postgrest

    suspend fun getEntitlement(userId: String): Result<Entitlement> = withContext(Dispatchers.IO) {
        try {
            // Server is authoritative
            val remote = fetchRemoteEntitlement(userId)
            if (remote != null) {
                cacheEntitlement(remote)
                return@withContext Result.success(remote)
            }
            // Fallback to cache if offline
            val cached = entitlementDao.getEntitlement(userId)
            if (cached != null) {
                return@withContext Result.success(cached.toDomain())
            }
            Result.success(Entitlement(id = "", userId = userId, type = EntitlementType.FREE))
        } catch (e: Exception) {
            val cached = entitlementDao.getEntitlement(userId)
            if (cached != null) {
                Result.success(cached.toDomain())
            } else {
                Result.failure(AppError.Server("Failed to load entitlement", e.message ?: ""))
            }
        }
    }

    suspend fun recordAdReward(userId: String, provider: String, rewardId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                postgrest.from("rewarded_ad_rewards").insert(
                    buildJsonObject {
                        put("user_id", userId)
                        put("provider", provider)
                        put("provider_reward_id", rewardId)
                        put("reward_value", 1)
                    }
                )
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(AppError.Server("Failed to record reward", e.message ?: ""))
            }
        }

    suspend fun getAdRewardCount(userId: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val result = postgrest.from("rewarded_ad_rewards")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<RewardDto>()
            Result.success(result.size)
        } catch (e: Exception) {
            Result.failure(AppError.Server("Failed to load reward count", e.message ?: ""))
        }
    }

    private suspend fun fetchRemoteEntitlement(userId: String): Entitlement? {
        return try {
            val result = postgrest.from("entitlements")
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeSingleOrNull<EntitlementDto>()
            result?.toDomain()
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun cacheEntitlement(entitlement: Entitlement) {
        entitlementDao.insert(
            CachedEntitlementEntity(
                userId = entitlement.userId,
                type = entitlement.type.name,
                adFree = entitlement.adFree,
                lifetime = entitlement.lifetime,
                subscriptionExpiresAt = entitlement.subscriptionExpiresAt
            )
        )
    }

    private fun CachedEntitlementEntity.toDomain(): Entitlement {
        return Entitlement(
            id = "",
            userId = userId,
            type = try { EntitlementType.valueOf(type) } catch (_: Exception) { EntitlementType.FREE },
            adFree = adFree,
            lifetime = lifetime,
            subscriptionExpiresAt = subscriptionExpiresAt
        )
    }

    @kotlinx.serialization.Serializable
    private data class EntitlementDto(
        val id: String,
        val user_id: String,
        val type: String = "FREE",
        val ad_free: Boolean = false,
        val lifetime: Boolean = false,
        val subscription_expires_at: String? = null,
        val created_at: String = "",
        val updated_at: String = ""
    ) {
        fun toDomain() = Entitlement(
            id = id,
            userId = user_id,
            type = try { EntitlementType.valueOf(type) } catch (_: Exception) { EntitlementType.FREE },
            adFree = ad_free,
            lifetime = lifetime,
            subscriptionExpiresAt = subscription_expires_at,
            createdAt = created_at,
            updatedAt = updated_at
        )
    }

    @kotlinx.serialization.Serializable
    private data class RewardDto(val id: String)
}
