package com.example.ridepricematcher.domain.model

enum class EntitlementType {
    FREE, AD_REWARDED, PREMIUM, AD_FREE, LIFETIME, BLOCKED
}

data class Entitlement(
    val id: String,
    val userId: String,
    val type: EntitlementType = EntitlementType.FREE,
    val adFree: Boolean = false,
    val lifetime: Boolean = false,
    val subscriptionExpiresAt: String? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    fun isActive(): Boolean {
        if (blocked) return false
        if (lifetime) return true
        if (adFree) return true
        if (type == EntitlementType.PREMIUM) {
            subscriptionExpiresAt?.let {
                return try {
                    val expiry = java.time.Instant.parse(it)
                    expiry.isAfter(java.time.Instant.now())
                } catch (_: Exception) {
                    false
                }
            }
        }
        return type == EntitlementType.AD_REWARDED
    }

    val blocked: Boolean get() = type == EntitlementType.BLOCKED
}
