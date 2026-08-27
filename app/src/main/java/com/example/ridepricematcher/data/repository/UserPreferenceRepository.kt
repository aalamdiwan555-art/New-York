package com.example.ridepricematcher.data.repository

import com.example.ridepricematcher.data.local.dao.UserPreferenceDao
import com.example.ridepricematcher.data.local.entity.CachedUserPreferenceEntity
import com.example.ridepricematcher.data.remote.SupabaseClientProvider
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UserPreferenceRepository(
    private val preferenceDao: UserPreferenceDao
) {

    private val postgrest = SupabaseClientProvider.postgrest

    suspend fun getPreferences(userId: String): Result<UserPreferences> = withContext(Dispatchers.IO) {
        try {
            val remote = postgrest.from("user_preferences")
                .select { filter { eq("user_id", userId) } }
                .decodeSingleOrNull<PreferenceDto>()

            if (remote != null) {
                val domain = remote.toDomain()
                cachePreferences(domain)
                return@withContext Result.success(domain)
            }

            val cached = preferenceDao.getPreferences(userId)
            if (cached != null) {
                return@withContext Result.success(cached.toDomain())
            }

            Result.success(UserPreferences(userId = userId))
        } catch (e: Exception) {
            val cached = preferenceDao.getPreferences(userId)
            if (cached != null) {
                Result.success(cached.toDomain())
            } else {
                Result.failure(AppError.Network("Failed to load preferences", e.message ?: ""))
            }
        }
    }

    suspend fun savePreferences(preferences: UserPreferences): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("user_preferences").upsert(
                buildJsonObject {
                    put("user_id", preferences.userId)
                    preferences.minimumPrice?.let { put("minimum_price", it) }
                    preferences.maximumPrice?.let { put("maximum_price", it) }
                    put(
                        "selected_languages",
                        JsonArray(preferences.selectedLanguages.map(::JsonPrimitive))
                    )
                    put("matching_enabled", preferences.matchingEnabled)
                }
            )
            cachePreferences(preferences)
            Result.success(Unit)
        } catch (e: Exception) {
            cachePreferences(preferences)
            Result.failure(AppError.Network("Failed to save preferences", e.message ?: ""))
        }
    }

    private suspend fun cachePreferences(preferences: UserPreferences) {
        preferenceDao.insert(
            CachedUserPreferenceEntity(
                userId = preferences.userId,
                minimumPrice = preferences.minimumPrice,
                maximumPrice = preferences.maximumPrice,
                selectedLanguages = preferences.selectedLanguages,
                matchingEnabled = preferences.matchingEnabled
            )
        )
    }

    private fun CachedUserPreferenceEntity.toDomain(): UserPreferences {
        return UserPreferences(
            userId = userId,
            minimumPrice = minimumPrice,
            maximumPrice = maximumPrice,
            selectedLanguages = selectedLanguages,
            matchingEnabled = matchingEnabled
        )
    }

    @kotlinx.serialization.Serializable
    private data class PreferenceDto(
        val user_id: String,
        val minimum_price: Double? = null,
        val maximum_price: Double? = null,
        val selected_languages: List<String> = emptyList(),
        val matching_enabled: Boolean = false,
        val updated_at: String = ""
    ) {
        fun toDomain() = UserPreferences(
            userId = user_id,
            minimumPrice = minimum_price,
            maximumPrice = maximum_price,
            selectedLanguages = selected_languages,
            matchingEnabled = matching_enabled,
            updatedAt = updated_at
        )
    }
}
