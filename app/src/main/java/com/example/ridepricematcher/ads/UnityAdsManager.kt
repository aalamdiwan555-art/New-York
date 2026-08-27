package com.example.ridepricematcher.ads

import android.app.Activity
import android.content.Context
import com.example.ridepricematcher.BuildConfig
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds

/**
 * Small lifecycle-safe wrapper around Unity's rewarded placement.
 *
 * Rewards are granted by SubscriptionViewModel only after Unity reports a
 * completed show, never when the user merely taps the button.
 */
object UnityAdsManager {
    const val REWARDED_PLACEMENT = "rewardedVideo"

    private var initialized = false
    private var loading = false
    private var loadAfterInitialization = false

    fun initialize(context: Context) {
        if (initialized || UnityAds.isInitialized()) return

        UnityAds.initialize(
            context.applicationContext,
            BuildConfig.UNITY_GAME_ID,
            BuildConfig.DEBUG,
            object : IUnityAdsInitializationListener {
                override fun onInitializationComplete() {
                    initialized = true
                    if (loadAfterInitialization) {
                        loadAfterInitialization = false
                        loadRewardedAd()
                    }
                }

                override fun onInitializationFailed(
                    error: UnityAds.UnityAdsInitializationError,
                    message: String
                ) {
                    initialized = false
                    loading = false
                }
            }
        )
    }

    fun loadRewardedAd(
        onLoaded: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (!initialized && !UnityAds.isInitialized()) {
            loadAfterInitialization = true
            return
        }

        if (UnityAds.isReady(REWARDED_PLACEMENT)) {
            onLoaded()
            return
        }

        if (loading) return
        loading = true
        UnityAds.load(REWARDED_PLACEMENT, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String) {
                loading = false
                onLoaded()
            }

            override fun onUnityAdsFailedToLoad(
                placementId: String,
                error: UnityAds.UnityAdsLoadError,
                message: String
            ) {
                loading = false
                onError(message)
            }
        })
    }

    fun showRewardedAd(
        activity: Activity,
        onRewarded: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!UnityAds.isReady(REWARDED_PLACEMENT)) {
            loadRewardedAd(onError = onError)
            return
        }

        UnityAds.show(activity, REWARDED_PLACEMENT, object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                placementId: String,
                error: UnityAds.UnityAdsShowError,
                message: String
            ) {
                onError(message)
                loadRewardedAd()
            }

            override fun onUnityAdsShowStart(placementId: String) = Unit

            override fun onUnityAdsShowClick(placementId: String) = Unit

            override fun onUnityAdsShowComplete(
                placementId: String,
                state: UnityAds.UnityAdsShowCompletionState
            ) {
                if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                    onRewarded()
                }
                loadRewardedAd()
            }
        })
    }
}