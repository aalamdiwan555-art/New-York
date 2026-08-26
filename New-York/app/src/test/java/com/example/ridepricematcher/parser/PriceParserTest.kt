package com.example.ridepricematcher.parser

import com.example.ridepricematcher.domain.parser.PriceParser
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PriceParserTest {

    private val parser = PriceParser()

    @Test
    fun `parse rupee symbol`() {
        val result = parser.parse("Your fare is ₹250")
        assertThat(result).isNotNull()
        assertThat(result!!.amount).isEqualTo(250.0)
        assertThat(result.currency).isEqualTo("INR")
        assertThat(result.confidence).isGreaterThan(0.5f)
    }

    @Test
    fun `parse Rs with dot`() {
        val result = parser.parse("Rs. 1,250.50")
        assertThat(result).isNotNull()
        assertThat(result!!.amount).isEqualTo(1250.50)
    }

    @Test
    fun `parse INR prefix`() {
        val result = parser.parse("INR 500")
        assertThat(result).isNotNull()
        assertThat(result!!.amount).isEqualTo(500.0)
    }

    @Test
    fun `parse Hindi rupee`() {
        val result = parser.parse("250 रुपये")
        assertThat(result).isNotNull()
        assertThat(result!!.amount).isEqualTo(250.0)
    }

    @Test
    fun `parse with comma`() {
        val result = parser.parse("₹1,999")
        assertThat(result).isNotNull()
        assertThat(result!!.amount).isEqualTo(1999.0)
    }

    @Test
    fun `reject non fare number`() {
        val result = parser.parse("OTP is 123456")
        assertThat(result).isNull()
    }

    @Test
    fun `parse multiple prices`() {
        val results = parser.parseMultiple("Fare ₹200, tip ₹50")
        assertThat(results).isNotEmpty()
        assertThat(results[0].amount).isEqualTo(200.0)
    }

    @Test
    fun `parse decimal price`() {
        val result = parser.parse("₹150.75")
        assertThat(result).isNotNull()
        assertThat(result!!.amount).isEqualTo(150.75)
    }

    @Test
    fun `handle empty text`() {
        val result = parser.parse("")
        assertThat(result).isNull()
    }

    @Test
    fun `handle null safety`() {
        val result = parser.parse("   ")
        assertThat(result).isNull()
    }

    @Test
    fun `confidence for reasonable fare`() {
        val result = parser.parse("Price: ₹350")
        assertThat(result).isNotNull()
        assertThat(result!!.confidence).isGreaterThan(0.6f)
    }

    @Test
    fun `low confidence for very large number`() {
        val result = parser.parse("₹999999")
        assertThat(result).isNotNull()
        assertThat(result!!.confidence).isLessThan(0.8f)
    }
}
