package com.example.ridepricematcher.domain.model

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
        get() = UserRoleResolver.isAdmin(role, email)
}
