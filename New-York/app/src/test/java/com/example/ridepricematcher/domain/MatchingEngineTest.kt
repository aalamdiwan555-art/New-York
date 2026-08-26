package com.example.ridepricematcher.domain

import com.example.ridepricematcher.domain.matcher.MatchingEngine
import com.example.ridepricematcher.domain.model.*
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MatchingEngineTest {

    private val engine = MatchingEngine()

    private val languages = listOf(
        LanguageConfig("1", "en", "English", "English", listOf("Accept"), listOf("fare", "price"), emptyList(), emptyList(), emptyList()),
    )

    @Test
    fun `full match success`() {
        val rule = PriceRule(minimumFare = 100.0, maximumFare = 500.0, currency = "INR")
        val result = engine.process("Fare: ₹250 — Accept", rule, languages)
        assertThat(result).isInstanceOf(MatchResult.Success::class.java)
        val success = result as MatchResult.Success
        assertThat(success.price.amount).isEqualTo(250.0)
        assertThat(success.acceptancePhrase).isEqualTo("Accept")
    }

    @Test
    fun `no match when price too high`() {
        val rule = PriceRule(maximumFare = 100.0, currency = "INR")
        val result = engine.process("Fare: ₹250 — Accept", rule, languages)
        assertThat(result).isInstanceOf(MatchResult.NoMatch::class.java)
    }

    @Test
    fun `no match when no acceptance phrase`() {
        val rule = PriceRule(minimumFare = 100.0, currency = "INR")
        val result = engine.process("Fare: ₹250", rule, languages)
        assertThat(result).isInstanceOf(MatchResult.NoMatch::class.java)
    }

    @Test
    fun `no match when no price`() {
        val rule = PriceRule(minimumFare = 100.0, currency = "INR")
        val result = engine.process("Accept the ride", rule, languages)
        assertThat(result).isInstanceOf(MatchResult.NoMatch::class.java)
    }

    @Test
    fun `exact fare match`() {
        val rule = PriceRule(exactFare = 300.0, currency = "INR")
        val result = engine.process("₹300 — Accept", rule, languages)
        assertThat(result).isInstanceOf(MatchResult.Success::class.java)
    }

    @Test
    fun `exact fare mismatch`() {
        val rule = PriceRule(exactFare = 300.0, currency = "INR")
        val result = engine.process("₹250 — Accept", rule, languages)
        assertThat(result).isInstanceOf(MatchResult.NoMatch::class.java)
    }
}
