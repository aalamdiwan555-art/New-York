package com.example.ridepricematcher.domain.parser

class TextNormalizer {

    fun normalize(text: String): String {
        return text
            .trim()
            .replace(Regex("""\s+"""), " ")
            .normalizeUnicode()
            .normalizePunctuation()
            .ocrNormalize()
    }

    private fun String.normalizeUnicode(): String {
        // Normalize to NFC form
        return java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFC)
    }

    private fun String.normalizePunctuation(): String {
        return this
            .replace("\u2018", "'") // left single quote
            .replace("\u2019", "'") // right single quote
            .replace("\u201C", "\"") // left double quote
            .replace("\u201D", "\"") // right double quote
            .replace("\u2013", "-") // en dash
            .replace("\u2014", "-") // em dash
            .replace("\u00A0", " ") // non-breaking space
            .replace("\u200B", "") // zero-width space
            .replace("\u200C", "") // zero-width non-joiner
            .replace("\u200D", "") // zero-width joiner
    }

    private fun String.ocrNormalize(): String {
        return this
            .replace(Regex("""0(?=\d{2,})"""), "O") // common OCR error: 0 vs O in context
            .replace(Regex("""\\|"""), "I") // pipe to I
    }

    fun normalizeForMatching(text: String, language: String): String {
        var normalized = normalize(text).lowercase()

        // Script-specific normalizations
        when (language) {
            "hi", "mr", "ne" -> normalized = normalizeDevanagari(normalized)
            "kn" -> normalized = normalizeKannada(normalized)
            "te" -> normalized = normalizeTelugu(normalized)
            "ta" -> normalized = normalizeTamil(normalized)
            "bn" -> normalized = normalizeBengali(normalized)
            "ml" -> normalized = normalizeMalayalam(normalized)
        }

        return normalized
    }

    private fun normalizeDevanagari(text: String): String {
        // Common Devanagari normalization
        return text
            .replace("\u093C", "") // nukta removal for base matching
            .replace("\u094D", "") // virama optional
    }

    private fun normalizeKannada(text: String): String = text
    private fun normalizeTelugu(text: String): String = text
    private fun normalizeTamil(text: String): String = text
    private fun normalizeBengali(text: String): String = text
    private fun normalizeMalayalam(text: String): String = text
}
