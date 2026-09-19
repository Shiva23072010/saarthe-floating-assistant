package com.saarthe.dot4

import android.content.Context
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class TranslationEngine(private val context: Context) {
    private fun language(code: String) = when (code) {
        "en" -> TranslateLanguage.ENGLISH
        "hi" -> TranslateLanguage.HINDI
        else -> TranslateLanguage.ENGLISH
    }

    fun translate(text: String, from: String, to: String, done: (String) -> Unit) {
        if (text.isBlank() || from == to) { done(text); return }
        val opts = TranslatorOptions.Builder()
            .setSourceLanguage(language(from))
            .setTargetLanguage(language(to))
            .build()
        val translator = Translation.getClient(opts)
        translator.downloadModelIfNeeded(DownloadConditions.Builder().build())
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { result -> done(result); translator.close() }
                    .addOnFailureListener { done(text); translator.close() }
            }
            .addOnFailureListener { done(text); translator.close() }
    }
}
