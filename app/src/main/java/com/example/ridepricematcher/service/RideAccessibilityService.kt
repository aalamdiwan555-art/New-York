package com.example.ridepricematcher.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.ridepricematcher.domain.matcher.MatchingEngine
import com.example.ridepricematcher.domain.model.*
import kotlinx.coroutines.*

/**
 * AccessibilityService for legitimate visible-text observation.
 * Only processes text from explicitly allowed ride applications.
 * Never injects clicks or performs automatic actions.
 */
class RideAccessibilityService : AccessibilityService() {

    private val allowedPackages = setOf(
        "com.ubercab",
        "com.ola.android",
        "com.didi.global.passenger",
        "in.merucab",
        "com.ridewithvia.driver",
        "com.grabtaxi.passenger"
    )

    private val matchingEngine = MatchingEngine()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var lastProcessedText = ""
    private var lastProcessTime = 0L
    private val debounceMs = 500L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val packageName = event.packageName?.toString() ?: return
        if (packageName !in allowedPackages) return

        val eventTime = System.currentTimeMillis()
        if (eventTime - lastProcessTime < debounceMs) return

        val text = extractText(event) ?: return
        if (text == lastProcessedText) return
        lastProcessedText = text
        lastProcessTime = eventTime

        // Process in background
        scope.launch {
            processText(text, packageName)
        }
    }

    private fun extractText(event: AccessibilityEvent): String? {
        val textBuilder = StringBuilder()
        event.text?.forEach { textBuilder.append(it).append(" ") }

        val rootNode = rootInActiveWindow
        if (rootNode == null) {
            return textBuilder.toString().trim().ifEmpty { null }
        }
        try {
            traverseNode(rootNode, textBuilder)
        } finally {
            rootNode.recycle()
        }

        return textBuilder.toString().trim().ifEmpty { null }
    }

    private fun traverseNode(node: AccessibilityNodeInfo, builder: StringBuilder) {
        node.text?.let { builder.append(it).append(" ") }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                try {
                    traverseNode(child, builder)
                } finally {
                    child.recycle()
                }
            }
        }
    }

    private suspend fun processText(text: String, sourcePackage: String) {
        // Load user preferences and languages from local cache
        // In production: inject via service locator or DI
        val rule = PriceRule(minimumFare = 100.0, maximumFare = 500.0, currency = "INR")
        val languages = getDefaultLanguages()

        val result = matchingEngine.process(text, rule, languages)
        if (result is MatchResult.Success) {
            // Notify via overlay — never auto-click
            OverlayService.showMatch(applicationContext, result)
        }
    }

    private fun getDefaultLanguages(): List<LanguageConfig> {
        return listOf(
            LanguageConfig("1", "en", "English", "English", listOf("Accept", "Confirm"), listOf("fare", "price"), listOf("km", "distance"), listOf("min", "duration"), emptyList()),
            LanguageConfig("2", "hi", "Hindi", "हिन्दी", listOf("स्वीकार करें", "Confirm"), listOf("किराया", "मूल्य"), listOf("कि.मी."), listOf("मिनट"), emptyList()),
            LanguageConfig("3", "kn", "Kannada", "ಕನ್ನಡ", listOf("ಸ್ವೀಕರಿಸಿ"), listOf("ದರ"), listOf("ಕಿ.ಮೀ"), listOf("ನಿಮಿಷ"), emptyList()),
            LanguageConfig("4", "te", "Telugu", "తెలుగు", listOf("అంగీకరించండి"), listOf("ధర"), listOf("కి.మీ"), listOf("నిమిషాలు"), emptyList()),
            LanguageConfig("5", "ta", "Tamil", "தமிழ்", listOf("ஏற்றுக்கொள்"), listOf("விலை"), listOf("கி.மீ"), listOf("நிமிடங்கள்"), emptyList()),
            LanguageConfig("6", "bn", "Bengali", "বাংলা", listOf("গ্রহণ করুন"), listOf("দাম"), listOf("কি.মি."), listOf("মিনিট"), emptyList()),
            LanguageConfig("7", "mr", "Marathi", "मराठी", listOf("स्वीकारा"), listOf("मूल्य"), listOf("कि.मी."), listOf("मिनिटे"), emptyList()),
            LanguageConfig("8", "ml", "Malayalam", "മലയാളം", listOf("സ്വീകരിക്കുക"), listOf("വില"), listOf("കി.മീ"), listOf("മിനിറ്റ്"), emptyList()),
        )
    }

    override fun onInterrupt() {
        // Service interrupted, clean up
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
