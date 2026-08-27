package com.example.ridepricematcher.parser

import com.example.ridepricematcher.domain.parser.TextNormalizer
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TextNormalizerTest {

    private val normalizer = TextNormalizer()

    @Test
    fun `trim whitespace`() {
        val result = normalizer.normalize("  hello  ")
        assertThat(result).isEqualTo("hello")
    }

    @Test
    fun `normalize repeated spaces`() {
        val result = normalizer.normalize("hello    world")
        assertThat(result).isEqualTo("hello world")
    }

    @Test
    fun `normalize unicode`() {
        val result = normalizer.normalize("caf\u00E9")
        assertThat(result).isEqualTo("café")
    }

    @Test
    fun `normalize punctuation`() {
        val result = normalizer.normalize("hello\u2019world")
        assertThat(result).isEqualTo("hello'world")
    }

    @Test
    fun `remove zero width spaces`() {
        val result = normalizer.normalize("hello\u200Bworld")
        assertThat(result).isEqualTo("helloworld")
    }
}
