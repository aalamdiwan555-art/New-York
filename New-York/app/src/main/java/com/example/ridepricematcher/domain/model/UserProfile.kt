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
    val isAdmin: Boolean get() = role == "admin"
}
