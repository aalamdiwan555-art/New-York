package com.example.ridepricematcher.data.repository

import com.example.ridepricematcher.data.remote.SupabaseClientProvider
import com.example.ridepricematcher.ads.AdPolicy
import com.example.ridepricematcher.RidePriceMatcherApplication
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.UserProfile
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    private val auth = SupabaseClientProvider.auth
    private val postgrest = SupabaseClientProvider.postgrest

    suspend fun signUp(email: String, password: String, name: String): Result<UserProfile> =
        withContext(Dispatchers.IO) {
            try {
                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = buildJsonObject {
                        put("display_name", name)
                    }
                }
                val user = auth.currentUserOrNull()
                    ?: return@withContext Result.failure(
                        AppError.Auth("Signup failed", "No user returned after signup")
                    )
                createProfile(user.id, email, name)
                Result.success(mapToProfile(user, name))
            } catch (e: Exception) {
                Result.failure(mapAuthError(e))
            }
        }

    suspend fun signIn(email: String, password: String): Result<UserProfile> =
        withContext(Dispatchers.IO) {
            try {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                val user = auth.currentUserOrNull()
                    ?: return@withContext Result.failure(
                        AppError.Auth("Login failed", "Invalid credentials")
                    )
                val profile = fetchProfile(user.id).getOrElse { error ->
                    return@withContext Result.failure(error)
                }
                if (profile?.blocked == true) {
                    auth.signOut()
                    return@withContext Result.failure(AppError.Blocked())
                }
                val effectiveProfile = profile ?: mapToProfile(user, "")
                AdPolicy.setUser(RidePriceMatcherApplication.instance, effectiveProfile)
                Result.success(effectiveProfile)
            } catch (e: Exception) {
                Result.failure(mapAuthError(e))
            }
        }

    suspend fun sendPasswordReset(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                auth.resetPasswordForEmail(email)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(mapAuthError(e))
            }
        }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            AdPolicy.clear(RidePriceMatcherApplication.instance)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.Auth("Logout failed", e.message ?: "Unknown error"))
        }
    }

    fun isAuthenticated(): Boolean = auth.currentUserOrNull() != null

    fun currentUserId(): String? = auth.currentUserOrNull()?.id

    suspend fun getCurrentProfile(): Result<UserProfile> = withContext(Dispatchers.IO) {
        val user = auth.currentUserOrNull()
            ?: return@withContext Result.failure(AppError.Auth("Not signed in", "No active session"))
        val profile = fetchProfile(user.id).getOrElse { error ->
            return@withContext Result.failure(error)
        } ?: mapToProfile(user, "")
        AdPolicy.setUser(RidePriceMatcherApplication.instance, profile)
        Result.success(profile)
    }

    suspend fun refreshSession(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.refreshCurrentSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.Auth("Session expired", e.message ?: "Unknown error"))
        }
    }

    private suspend fun createProfile(userId: String, email: String, name: String) {
        try {
            postgrest.from("profiles").insert(
                buildJsonObject {
                    put("id", userId)
                    put("email", email)
                    put("display_name", name)
                    put("role", if (email.equals(AdPolicy.PRIMARY_ADMIN_EMAIL, ignoreCase = true)) "admin" else "user")
                    put("blocked", false)
                }
            )
        } catch (e: Exception) {
            val message = e.message.orEmpty()
            val isDuplicate = message.contains("duplicate", ignoreCase = true) ||
                message.contains("already exists", ignoreCase = true) ||
                message.contains("23505")
            if (!isDuplicate) throw e
        }
    }

    private suspend fun fetchProfile(userId: String): Result<UserProfile?> {
        return try {
            val result = postgrest.from("profiles")
                .select(columns = Columns.list("id", "email", "display_name", "role", "blocked", "created_at", "updated_at")) {
                    filter { eq("id", userId) }
                }
                .decodeSingleOrNull<UserProfileDto>()
            Result.success(result?.toDomain())
        } catch (e: Exception) {
            Result.failure(
                AppError.Server("Unable to load your profile. Please try again.", e.message ?: "Profile lookup failed")
            )
        }
    }

    private fun mapToProfile(user: UserInfo, name: String): UserProfile {
        return UserProfile(
            id = user.id,
            email = user.email ?: "",
            displayName = name,
            role = "user",
            blocked = false
        )
    }

    private fun mapAuthError(e: Exception): AppError {
        val message = e.message ?: "Unknown error"
        return when {
            message.contains("Invalid login", ignoreCase = true) ->
                AppError.Auth("Invalid email or password", message)
            message.contains("User not found", ignoreCase = true) ->
                AppError.Auth("Account not found", message)
            message.contains("Email not confirmed", ignoreCase = true) ->
                AppError.Auth("Please confirm your email first", message)
            message.contains("rate limit", ignoreCase = true) ->
                AppError.Auth("Too many attempts. Please try again later.", message)
            message.contains("network", ignoreCase = true) ->
                AppError.Network("No internet connection", message)
            else -> AppError.Auth("Authentication failed", message)
        }
    }

    @kotlinx.serialization.Serializable
    private data class UserProfileDto(
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
}
