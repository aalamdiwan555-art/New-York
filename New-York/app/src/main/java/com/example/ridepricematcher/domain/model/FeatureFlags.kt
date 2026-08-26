package com.example.ridepricematcher.domain.model

data class FeatureFlags(
    val textDetectionEnabled: Boolean = true,
    val overlayEnabled: Boolean = true,
    val screenCaptureEnabled: Boolean = false,
    val rewardedAdsEnabled: Boolean = true,
    val subscriptionEnabled: Boolean = true,
    val maintenanceMode: Boolean = false,
    val newLanguageEnabled: Boolean = false
)
