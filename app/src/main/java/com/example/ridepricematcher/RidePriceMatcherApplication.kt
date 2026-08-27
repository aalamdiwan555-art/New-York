package com.example.ridepricematcher

import android.app.Application
import com.example.ridepricematcher.ads.UnityAdsManager

class RidePriceMatcherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule.init(this)
        UnityAdsManager.initialize(this)
    }
}
