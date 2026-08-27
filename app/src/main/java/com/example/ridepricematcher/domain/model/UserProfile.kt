package com.example.ridepricematcher.domain.model

import com.example.ridepricematcher.ads.AdPolicy

data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String,
    val role: String = "user",
    val blocked: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    /**
     * The email check keeps the primary owner from being locked out of the
     * admin surface if an older profile row still has the default role.
     * Server-side RLS remains the authority for admin mutations.
     */
    val isAdmin: Boolean
        get() = role.equals("admin", ignoreCase = true) ||
            email.equals(AdPolicy.PRIMARY_ADMIN_EMAIL, ignoreCase = true)
}
