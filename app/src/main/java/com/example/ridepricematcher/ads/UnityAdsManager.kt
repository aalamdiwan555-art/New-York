package com.example.ridepricematcher.ads

import android.app.Activity
import android.content.Context
import com.example.ridepricematcher.BuildConfig
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions

object UnityAdsManager {
    const val REWARDED_PLACEMENT = "rewardedVideo"

    private var initialized = false
    private var loading = false
    private var loadAfterInitialization = false
    private var isAdLoaded = false

    fun initialize(context: Context) {
        if (initialized || UnityAds.isInitialized) return

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
        // v4 me isInitialized property hai, function nahi
        if (!initialized && !UnityAds.isInitialized) {
            loadAfterInitialization = true
            return
        }

        if (isAdLoaded) {
            onLoaded()
            return
        }

        if (loading) return
        loading = true
        
        UnityAds.load(REWARDED_PLACEMENT, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String) {
                loading = false
                isAdLoaded = true
                onLoaded()
            }
            override fun onUnityAdsFailedToLoad(
                placementId: String,
                error: UnityAds.UnityAdsLoadError,
                message: String
            ) {
                loading = false
                isAdLoaded = false
                onError(message)
            }
        })
    }

    fun showRewardedAd(
        activity: Activity,
        onRewarded: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isAdLoaded) {
            onError("Ad not loaded yet")
            loadRewardedAd(onError = onError)
            return
        }

        UnityAds.show(activity, REWARDED_PLACEMENT, UnityAdsShowOptions(), object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                placementId: String,
                error: UnityAds.UnityAdsShowError,
                message: String
            ) {
                isAdLoaded = false
                onError(message)
                loadRewardedAd()
            }
            override fun onUnityAdsShowStart(placementId: String) {
                isAdLoaded = false
            }
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
