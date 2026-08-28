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
import java.time.Instant
import java.time.temporal.ChronoUnit

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
                
                // Get the updated reward count
                val countResult = getAdRewardCount(userId)
                if (countResult.isSuccess) {
                    val count = countResult.getOrThrow()
                    if (count > 0 && count % 20 == 0) {
                        grantPremium(userId, 1)
                    }
                }
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(AppError.Server("Failed to record reward", e.message ?: ""))
            }
        }

    suspend fun getAdRewardCount(userId: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val oneDayAgo = Instant.now().minus(24, ChronoUnit.HOURS).toString()
            val result = postgrest.from("rewarded_ad_rewards")
                .select {
                    filter {
                        eq("user_id", userId)
                        gte("created_at", oneDayAgo)
                    }
                }
                .decodeList<RewardDto>()
            Result.success(result.size)
        } catch (e: Exception) {
            Result.failure(AppError.Server("Failed to load reward count", e.message ?: ""))
        }
    }

    suspend fun grantPremium(userId: String, days: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentDto = postgrest.from("entitlements")
                .select { filter { eq("user_id", userId) } }
                .decodeSingleOrNull<EntitlementDto>()
            
            val now = Instant.now()
            val currentExpires = currentDto?.subscription_expires_at?.let {
                try { Instant.parse(it) } catch (_: Exception) { null }
            }
            
            val baseTime = if (currentExpires != null && currentExpires.isAfter(now)) {
                currentExpires
            } else {
                now
            }
            val newExpires = baseTime.plus(days.toLong(), ChronoUnit.DAYS).toString()
            
            postgrest.from("entitlements").upsert(
                buildJsonObject {
                    put("user_id", userId)
                    put("type", "PREMIUM")
                    put("ad_free", true)
                    put("subscription_expires_at", newExpires)
                }
            )
            
            // Update cache
            cacheEntitlement(
                Entitlement(
                    id = currentDto?.id ?: "",
                    userId = userId,
                    type = EntitlementType.PREMIUM,
                    adFree = true,
                    subscriptionExpiresAt = newExpires
                )
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.Server("Failed to grant premium", e.message ?: ""))
        }
    }

    private suspend fun fetchRemoteEntitlement(userId: String): Entitlement? {
        val result = postgrest.from("entitlements")
            .select {
                filter { eq("user_id", userId) }
            }
            .decodeSingleOrNull<EntitlementDto>()
        return result?.toDomain()
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
