package com.example.ridepricematcher.matcher

import com.example.ridepricematcher.domain.matcher.AcceptancePhraseMatcher
import com.example.ridepricematcher.domain.model.LanguageConfig
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AcceptancePhraseMatcherTest {

    private val matcher = AcceptancePhraseMatcher()

    private val languages = listOf(
        LanguageConfig("1", "en", "English", "English", listOf("Accept", "Confirm"), emptyList(), emptyList(), emptyList(), emptyList()),
        LanguageConfig("2", "hi", "Hindi", "हिन्दी", listOf("स्वीकार करें"), emptyList(), emptyList(), emptyList(), emptyList()),
        LanguageConfig("3", "kn", "Kannada", "ಕನ್ನಡ", listOf("ಸ್ವೀಕರಿಸಿ"), emptyList(), emptyList(), emptyList(), emptyList()),
        LanguageConfig("4", "te", "Telugu", "తెలుగు", listOf("అంగీకరించండి"), emptyList(), emptyList(), emptyList(), emptyList()),
        LanguageConfig("5", "ta", "Tamil", "தமிழ்", listOf("ஏற்றுக்கொள்"), emptyList(), emptyList(), emptyList(), emptyList()),
        LanguageConfig("6", "bn", "Bengali", "বাংলা", listOf("গ্রহণ করুন"), emptyList(), emptyList(), emptyList(), emptyList()),
        LanguageConfig("7", "mr", "Marathi", "मराठी", listOf("स्वीकारा"), emptyList(), emptyList(), emptyList(), emptyList()),
        LanguageConfig("8", "ml", "Malayalam", "മലയാളം", listOf("സ്വീകരിക്കുക"), emptyList(), emptyList(), emptyList(), emptyList()),
    )

    @Test
    fun `match English accept`() {
        val result = matcher.match("Please Accept the ride", languages)
        assertThat(result).isNotNull()
        assertThat(result!!.phrase).isEqualTo("Accept")
        assertThat(result.language).isEqualTo("en")
    }

    @Test
    fun `match Hindi phrase`() {
        val result = matcher.match("यात्रा स्वीकार करें", languages)
        assertThat(result).isNotNull()
        assertThat(result!!.phrase).isEqualTo("स्वीकार करें")
    }

    @Test
    fun `match Kannada phrase`() {
        val result = matcher.match("ಸವಾರಿ ಸ್ವೀಕರಿಸಿ", languages)
        assertThat(result).isNotNull()
        assertThat(result!!.phrase).isEqualTo("ಸ್ವೀಕರಿಸಿ")
    }

    @Test
    fun `match Telugu phrase`() {
        val result = matcher.match("అంగీకరించండి", languages)
        assertThat(result).isNotNull()
    }

    @Test
    fun `match Tamil phrase`() {
        val result = matcher.match("ஏற்றுக்கொள்", languages)
        assertThat(result).isNotNull()
    }

    @Test
    fun `match Bengali phrase`() {
        val result = matcher.match("গ্রহণ করুন", languages)
        assertThat(result).isNotNull()
    }

    @Test
    fun `match Marathi phrase`() {
        val result = matcher.match("स्वीकारा", languages)
        assertThat(result).isNotNull()
    }

    @Test
    fun `match Malayalam phrase`() {
        val result = matcher.match("സ്വീകരിക്കുക", languages)
        assertThat(result).isNotNull()
    }

    @Test
    fun `reject unrelated text`() {
        val result = matcher.match("Cancel the ride", languages)
        assertThat(result).isNull()
    }

    @Test
    fun `reject empty text`() {
        val result = matcher.match("", languages)
        assertThat(result).isNull()
    }

    @Test
    fun `fuzzy match with OCR noise`() {
        val result = matcher.match("Acc3pt", languages)
        assertThat(result).isNotNull()
        assertThat(result!!.confidence).isGreaterThan(0.5f)
    }
}
