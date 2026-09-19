package com.saarthe.dot4

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.provider.Settings
import android.content.Intent
import android.os.Handler
import android.os.Looper

class SaartheAccessibilityService : AccessibilityService() {
    private lateinit var overlay: SaartheOverlay
    private lateinit var engine: TranslationEngine
    private val handler = Handler(Looper.getMainLooper())
    private var lastText = ""
    private var lastAt = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        overlay = SaartheOverlay(this)
        engine = TranslationEngine(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (!Settings.canDrawOverlays(this)) return

        val pkg = event.packageName?.toString() ?: return
        // Avoid processing Saarthe itself or system UI.
        if (pkg == packageName || pkg.startsWith("com.android.systemui")) return

        val root = rootInActiveWindow ?: return
        val texts = ArrayList<String>()
        collectText(root, texts)
        val candidate = texts.asReversed().firstOrNull { looksLikeMessage(it) } ?: return

        val now = System.currentTimeMillis()
        if (candidate == lastText && now - lastAt < 3000) return
        lastText = candidate
        lastAt = now

        val detected = detectLanguage(candidate)
        val target = if (detected == "hi") "en" else "hi"
        engine.translate(candidate, detected, target) { translated ->
            val suggestions = makeSuggestions(candidate, translated, target)
            handler.post { overlay.show(candidate, translated, suggestions, target) }
        }
    }

    private fun collectText(node: AccessibilityNodeInfo, out: MutableList<String>) {
        node.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let(out::add)
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                collectText(child, out)
                child.recycle()
            }
        }
    }

    private fun looksLikeMessage(s: String): Boolean =
        s.length in 2..500 && s.count { it.isLetterOrDigit() } >= 2

    private fun detectLanguage(s: String): String {
        val devanagari = s.count { it.code in 0x0900..0x097F }
        return if (devanagari > 0) "hi" else "en"
    }

    private fun makeSuggestions(message: String, translated: String, targetLanguage: String): List<String> {
        val s = message.lowercase(java.util.Locale.getDefault())

        return if (targetLanguage == "hi") {
            when {
                s.contains("how are you") || s.contains("how r u") ->
                    listOf("मैं ठीक हूँ, आप कैसे हैं?", "मैं भी ठीक हूँ। पूछने के लिए धन्यवाद!", "सब बढ़िया है।")
                s.contains("where are you") ->
                    listOf("मैं अभी घर पर हूँ।", "मैं रास्ते में हूँ।", "मैं थोड़ी देर में बताता हूँ।")
                s.contains("what") || s.contains("why") || s.contains("when") || s.contains("?") ->
                    listOf("हाँ, मैं समझ गया।", "मैं अभी चेक करके बताता हूँ।", "ठीक है, मैं आपको बताता हूँ।")
                s.contains("thank") || s.contains("thanks") ->
                    listOf("आपका स्वागत है!", "कोई बात नहीं।", "खुशी हुई मदद करके।")
                s.contains("sorry") ->
                    listOf("कोई बात नहीं।", "ठीक है, चिंता मत कीजिए।", "समझ गया।")
                s.contains("hello") || s.contains("hi ") || s == "hi" ->
                    listOf("नमस्ते! कैसे हैं?", "हैलो! क्या हाल है?", "नमस्ते, बताइए।")
                else ->
                    listOf("ठीक है, समझ गया।", "हाँ, बिल्कुल।", "मैं थोड़ी देर में जवाब देता हूँ।")
            }
        } else {
            when {
                s.contains("कैसे हैं") || s.contains("कैसी हैं") ->
                    listOf("I'm fine, how are you?", "I'm doing well. Thanks for asking!", "Everything is good.")
                s.contains("कहाँ") ->
                    listOf("I'm at home right now.", "I'm on my way.", "I'll let you know shortly.")
                s.contains("क्या") || s.contains("क्यों") || s.contains("कब") || s.contains("?") ->
                    listOf("Yes, I understand.", "I'll check and let you know.", "Okay, I'll tell you.")
                s.contains("धन्यवाद") || s.contains("शुक्रिया") ->
                    listOf("You're welcome!", "No problem.", "Happy to help.")
                s.contains("माफ") || s.contains("सॉरी") ->
                    listOf("No problem.", "It's okay, don't worry.", "I understand.")
                s.contains("नमस्ते") || s.contains("हैलो") ->
                    listOf("Hello! How are you?", "Hi! What's up?", "Hello, tell me.")
                else ->
                    listOf("Okay, I understand.", "Yes, absolutely.", "I'll reply shortly.")
            }
        }
    }

    override fun onInterrupt() {
        if (::overlay.isInitialized) overlay.hide()
    }
}
