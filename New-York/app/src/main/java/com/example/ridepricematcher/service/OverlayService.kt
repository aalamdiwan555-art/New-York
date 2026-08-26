package com.example.ridepricematcher.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.ridepricematcher.domain.model.MatchResult

/**
 * Overlay service that displays match notifications.
 * Runs as foreground service on API 26+ to comply with background restrictions.
 * Removable, lifecycle-safe, never creates duplicate windows.
 */
class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: android.view.View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createNotification())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW_MATCH -> {
                val price = intent.getStringExtra(EXTRA_PRICE) ?: "Unknown"
                val phrase = intent.getStringExtra(EXTRA_PHRASE) ?: ""
                showMatchOverlay(price, phrase)
            }
            ACTION_REMOVE -> removeOverlay()
        }
        return START_NOT_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "overlay_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        return Notification.Builder(this, channelId)
            .setContentTitle("Ride Price Matcher")
            .setContentText("Monitoring for price matches")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    private fun showMatchOverlay(price: String, phrase: String) {
        removeOverlay()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 100
        }

        val view = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(android.graphics.Color.parseColor("#CC1A1A2E"))

            addView(android.widget.TextView(this@OverlayService).apply {
                text = "Match Found!"
                setTextColor(android.graphics.Color.WHITE)
                textSize = 18f
            })
            addView(android.widget.TextView(this@OverlayService).apply {
                text = "Price: $price"
                setTextColor(android.graphics.Color.parseColor("#00D4AA"))
                textSize = 24f
            })
            addView(android.widget.TextView(this@OverlayService).apply {
                text = "Action: $phrase"
                setTextColor(android.graphics.Color.WHITE)
                textSize = 14f
            })
            addView(android.widget.Button(this@OverlayService).apply {
                text = "Dismiss"
                setOnClickListener { removeOverlay() }
            })
        }

        overlayView = view
        windowManager?.addView(view, params)
    }

    private fun removeOverlay() {
        overlayView?.let {
            try { windowManager?.removeView(it) } catch (_: Exception) {}
            overlayView = null
        }
    }

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }

    companion object {
        private const val ACTION_SHOW_MATCH = "SHOW_MATCH"
        private const val ACTION_REMOVE = "REMOVE"
        private const val EXTRA_PRICE = "price"
        private const val EXTRA_PHRASE = "phrase"
        private const val NOTIFICATION_ID = 1001

        fun showMatch(context: Context, result: MatchResult.Success) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_SHOW_MATCH
                putExtra(EXTRA_PRICE, "Rs.${result.price.amount}")
                putExtra(EXTRA_PHRASE, result.acceptancePhrase)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun remove(context: Context) {
            val intent = Intent(context, OverlayService::class.java).apply {
                action = ACTION_REMOVE
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
