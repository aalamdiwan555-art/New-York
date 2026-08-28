package com.example.ridepricematcher.ads

import android.content.Context
import com.example.ridepricematcher.domain.model.Entitlement
import com.example.ridepricematcher.domain.model.UserProfile

/**
 * One policy for every ad surface in the app.
 *
 * Login/signup have no active user, so they never show ads. The policy is also
 * cached for services that run outside an Activity, such as the overlay.
 */
object AdPolicy {
    private const val PREFS = "ad_policy"
    private const val ACTIVE_USER = "active_user"
    private const val ADMIN = "admin"
    private const val AD_FREE = "ad_free"
    private const val LIFETIME = "lifetime"
    private const val BLOCKED = "blocked"

    fun setUser(context: Context, profile: UserProfile, entitlement: Entitlement? = null) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(ACTIVE_USER, true)
            .putBoolean(ADMIN, profile.isAdmin)
            .putBoolean(AD_FREE, entitlement?.adFree == true)
            .putBoolean(LIFETIME, entitlement?.lifetime == true)
            .putBoolean(BLOCKED, profile.blocked || entitlement?.blocked == true)
            .apply()
    }

    fun updateEntitlement(context: Context, entitlement: Entitlement) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(AD_FREE, entitlement.adFree)
            .putBoolean(LIFETIME, entitlement.lifetime)
            .putBoolean(BLOCKED, entitlement.blocked)
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    fun shouldShowAds(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return preferences.getBoolean(ACTIVE_USER, false) &&
            !preferences.getBoolean(ADMIN, false) &&
            !preferences.getBoolean(AD_FREE, false) &&
            !preferences.getBoolean(LIFETIME, false) &&
            !preferences.getBoolean(BLOCKED, false)
    }

    fun shouldShowAds(profile: UserProfile?, entitlement: Entitlement?): Boolean {
        return profile != null &&
            !profile.isAdmin &&
            !profile.blocked &&
            entitlement?.adFree != true &&
            entitlement?.lifetime != true &&
            entitlement?.blocked != true
    }
}