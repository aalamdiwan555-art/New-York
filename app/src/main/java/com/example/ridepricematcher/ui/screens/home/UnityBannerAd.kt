package com.example.ridepricematcher.ui.screens.home

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.services.banners.BannerView
import com.example.ridepricematcher.ads.UnityAdsManager

@Composable
fun UnityBannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var initialized by remember { mutableStateOf(false) }
    var banner by remember { mutableStateOf<BannerView?>(null) }

    LaunchedEffect(Unit) {
        UnityAdsManager.initialize(context) { initialized = true }
    }

    DisposableEffect(initialized) {
        if (!initialized) return@DisposableEffect onDispose { }
        val view = UnityAdsManager.createBanner(context)
        banner = view
        onDispose {
            view?.destroy()
            if (banner === view) banner = null
        }
    }

    banner?.let { bannerView ->
        AndroidView(
            factory = { bannerView },
            modifier = modifier
                .wrapContentWidth()
                .height(50.dp)
        )
    }
}