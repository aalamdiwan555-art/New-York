package com.example.ridepricematcher.data.repository

import com.example.ridepricematcher.data.remote.SupabaseClientProvider
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.FeatureFlags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeatureFlagRepository {
    private val postgrest = SupabaseClientProvider.postgrest

    suspend fun getFeatureFlags(): Result<FeatureFlags> = withContext(Dispatchers.IO) {
        try {
            val remote = postgrest.from("feature_flags")
                .select()
                .decodeSingleOrNull<FeatureFlagsDto>()
            Result.success(remote?.toDomain() ?: FeatureFlags())
        } catch (e: Exception) {
            Result.failure(
                AppError.Network("Failed to load feature flags", e.message ?: "Unknown error")
            )
        }
    }

    @kotlinx.serialization.Serializable
    private data class FeatureFlagsDto(
        val text_detection_enabled: Boolean = true,
        val overlay_enabled: Boolean = true,
        val screen_capture_enabled: Boolean = false,
        val rewarded_ads_enabled: Boolean = true,
        val subscription_enabled: Boolean = true,
        val maintenance_mode: Boolean = false,
        val new_language_enabled: Boolean = false
    ) {
        fun toDomain() = FeatureFlags(
            textDetectionEnabled = text_detection_enabled,
            overlayEnabled = overlay_enabled,
            screenCaptureEnabled = screen_capture_enabled,
            rewardedAdsEnabled = rewarded_ads_enabled,
            subscriptionEnabled = subscription_enabled,
            maintenanceMode = maintenance_mode,
            newLanguageEnabled = new_language_enabled
        )
    }
}