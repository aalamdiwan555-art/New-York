package com.example.ridepricematcher.ads

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import com.example.ridepricematcher.BuildConfig
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

object UnityAdsManager {
    const val REWARDED_PLACEMENT = "rewardedVideo"
    const val INTERSTITIAL_PLACEMENT = "video"
    const val BANNER_PLACEMENT = "banner"

    private var initialized = false
    private var initializationRequested = false
    private var rewardedLoading = false
    private var interstitialLoading = false
    private var rewardedLoaded = false
    private var interstitialLoaded = false
    private var lastInterstitialAt = 0L
    private val initializationCallbacks = mutableListOf<() -> Unit>()
    private val rewardedCallbacks = mutableListOf<RewardedCallbacks>()

    private data class RewardedCallbacks(
        val onLoaded: () -> Unit,
        val onError: (String) -> Unit
    )

    fun initialize(context: Context, onInitialized: () -> Unit = {}) {
        if (initialized || UnityAds.isInitialized) {
            initialized = true
            onInitialized()
            return
        }

        initializationCallbacks += onInitialized
        if (initializationRequested) return
        initializationRequested = true

        UnityAds.initialize(
            context.applicationContext,
            BuildConfig.UNITY_GAME_ID,
            BuildConfig.DEBUG,
            object : IUnityAdsInitializationListener {
                override fun onInitializationComplete() {
                    initialized = true
                    initializationCallbacks.toList().forEach { it() }
                    initializationCallbacks.clear()
                    loadRewardedAd()
                    loadInterstitialAd()
                }
                override fun onInitializationFailed(
                    error: UnityAds.UnityAdsInitializationError,
                    message: String
                ) {
                    initialized = false
                    initializationRequested = false
                    rewardedLoading = false
                    interstitialLoading = false
                    initializationCallbacks.clear()
                    val callbacks = rewardedCallbacks.toList()
                    rewardedCallbacks.clear()
                    callbacks.forEach { it.onError(message) }
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
            rewardedCallbacks += RewardedCallbacks(onLoaded, onError)
            return
        }

        if (rewardedLoaded) {
            onLoaded()
            return
        }

        rewardedCallbacks += RewardedCallbacks(onLoaded, onError)
        if (rewardedLoading) return
        rewardedLoading = true
        
        UnityAds.load(REWARDED_PLACEMENT, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String) {
                rewardedLoading = false
                rewardedLoaded = true
                val callbacks = rewardedCallbacks.toList()
                rewardedCallbacks.clear()
                callbacks.forEach { it.onLoaded() }
            }
            override fun onUnityAdsFailedToLoad(
                placementId: String,
                error: UnityAds.UnityAdsLoadError,
                message: String
            ) {
                rewardedLoading = false
                rewardedLoaded = false
                val callbacks = rewardedCallbacks.toList()
                rewardedCallbacks.clear()
                callbacks.forEach { it.onError(message) }
            }
        })
    }

    private fun loadInterstitialAd() {
        if (!initialized && !UnityAds.isInitialized) return
        if (interstitialLoaded || interstitialLoading) return
        interstitialLoading = true
        UnityAds.load(INTERSTITIAL_PLACEMENT, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String) {
                interstitialLoading = false
                interstitialLoaded = true
            }

            override fun onUnityAdsFailedToLoad(
                placementId: String,
                error: UnityAds.UnityAdsLoadError,
                message: String
            ) {
                interstitialLoading = false
                interstitialLoaded = false
            }
        })
    }

    fun createBanner(activity: Activity): BannerView? {
        if (!initialized && !UnityAds.isInitialized) return null
        return BannerView(activity, BANNER_PLACEMENT, UnityBannerSize(320, 50)).also { it.load() }
    }

    fun showInterstitialIfAllowed(activity: Activity): Boolean {
        if (!AdPolicy.shouldShowAds(activity)) return false
        val now = SystemClock.elapsedRealtime()
        if (now - lastInterstitialAt < 45_000L) return false
        if (!interstitialLoaded) {
            loadInterstitialAd()
            return false
        }

        lastInterstitialAt = now
        interstitialLoaded = false
        UnityAds.show(activity, INTERSTITIAL_PLACEMENT, UnityAdsShowOptions(), object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                placementId: String,
                error: UnityAds.UnityAdsShowError,
                message: String
            ) {
                interstitialLoaded = false
                loadInterstitialAd()
            }

            override fun onUnityAdsShowStart(placementId: String) = Unit
            override fun onUnityAdsShowClick(placementId: String) = Unit
            override fun onUnityAdsShowComplete(
                placementId: String,
                state: UnityAds.UnityAdsShowCompletionState
            ) {
                loadInterstitialAd()
            }
        })
        return true
    }

    fun showRewardedAd(
        activity: Activity,
        onRewarded: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!rewardedLoaded) {
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
                rewardedLoaded = false
                onError(message)
                loadRewardedAd()
            }
            override fun onUnityAdsShowStart(placementId: String) {
                rewardedLoaded = false
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
