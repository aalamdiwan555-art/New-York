package com.example.ridepricematcher.domain.model

import com.example.ridepricematcher.BuildConfig

/**
 * Resolves roles without making domain models depend on ads or repositories.
 * The admin address is supplied through build configuration and is blank by
 * default, so no personal address is embedded in source code.
 */
object UserRoleResolver {
    private val primaryAdminEmail: String
        get() = BuildConfig.ADMIN_EMAIL

    fun isAdmin(role: String, email: String): Boolean {
        return role.equals("admin", ignoreCase = true) || isAdminEmail(email)
    }

    fun isAdminEmail(email: String): Boolean {
        return primaryAdminEmail.isNotBlank() &&
            email.equals(primaryAdminEmail, ignoreCase = true)
    }
}