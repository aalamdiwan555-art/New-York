package com.example.ridepricematcher.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Restores monitoring state after device reboot.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // In production: check shared preferences for previous monitoring state
            // and restart services if user had monitoring enabled
        }
    }
}
