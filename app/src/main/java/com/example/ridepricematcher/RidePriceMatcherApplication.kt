package com.example.ridepricematcher

import android.app.Application
import android.os.Build
import android.os.StrictMode
import com.example.ridepricematcher.ads.UnityAdsManager
import com.example.ridepricematcher.data.remote.SupabaseClientProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RidePriceMatcherApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        AppModule.init(this)
        UnityAdsManager.initialize(this)

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }

        applicationScope.launch {
            runCatching { SupabaseClientProvider.client }
        }
    }
}
