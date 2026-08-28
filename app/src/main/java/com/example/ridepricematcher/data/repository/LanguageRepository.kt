package com.example.ridepricematcher.data.repository

import com.example.ridepricematcher.data.local.dao.LanguageDao
import com.example.ridepricematcher.data.local.dao.PhraseDao
import com.example.ridepricematcher.data.local.entity.CachedLanguageEntity
import com.example.ridepricematcher.data.local.entity.CachedPhraseEntity
import com.example.ridepricematcher.data.remote.SupabaseClientProvider
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.LanguageConfig
import com.example.ridepricematcher.AppModule
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LanguageRepository(
    private val languageDao: LanguageDao,
    private val phraseDao: PhraseDao
) {

    private val postgrest = SupabaseClientProvider.postgrest

    fun getEnabledLanguages(): Flow<List<LanguageConfig>> {
        return languageDao.getEnabledLanguages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getEnabledLanguagesWithPhrases(): List<LanguageConfig> = withContext(Dispatchers.IO) {
        try {
            val langs = languageDao.getEnabledLanguagesOnce()
            val phrases = phraseDao.getEnabledPhrasesOnce()
            
            if (langs.isEmpty()) {
                return@withContext getDefaultLanguages()
            }
            
            langs.map { lang ->
                val langPhrases = phrases.filter { it.languageId == lang.id && it.type == "acceptance" }.map { it.phrase }
                lang.toDomain().copy(acceptancePhrases = langPhrases)
            }
        } catch (e: Exception) {
            getDefaultLanguages()
        }
    }

    private fun getDefaultLanguages(): List<LanguageConfig> {
        return listOf(
            LanguageConfig("1", "en", "English", "English", listOf("Accept", "Confirm"), listOf("fare", "price"), listOf("km", "distance"), listOf("min", "duration"), emptyList()),
            LanguageConfig("2", "hi", "Hindi", "हिन्दी", listOf("स्वीकार करें", "Confirm"), listOf("किराया", "मूल्य"), listOf("कि.मी."), listOf("मिनट"), emptyList()),
            LanguageConfig("3", "kn", "Kannada", "ಕನ್ನಡ", listOf("ಸ್ವೀಕರಿಸಿ"), listOf("ದರ"), listOf("ಕಿ.ಮೀ"), listOf("ನಿಮಿಷ"), emptyList()),
            LanguageConfig("4", "te", "Telugu", "తెలుగు", listOf("అంగీకరించండి"), listOf("ధర"), listOf("कि.మీ"), listOf("నిమిషాలు"), emptyList()),
            LanguageConfig("5", "ta", "Tamil", "தமிழ்", listOf("ஏற்றுக்கொள்"), listOf("விலை"), listOf("கி.மீ"), listOf("நிமிடங்கள்"), emptyList()),
            LanguageConfig("6", "bn", "Bengali", "বাংলা", listOf("গ্রহণ করুন"), listOf("দাম"), listOf("কি.मी."), listOf("মিনিট"), emptyList()),
            LanguageConfig("7", "mr", "Marathi", "मराठी", listOf("स्वीकारा"), listOf("मूल्य"), listOf("कि.मी."), listOf("मिनिटे"), emptyList()),
            LanguageConfig("8", "ml", "Malayalam", "മലയാളം", listOf("സ്വീകരിക്കുക"), listOf("വില"), listOf("കി.மீ"), listOf("മിനിറ്റ്"), emptyList()),
        )
    }

    fun getAllLanguages(): Flow<List<LanguageConfig>> {
        return languageDao.getAllLanguages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun syncLanguages(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val languages = postgrest.from("languages")
                .select()
                .decodeList<LanguageDto>()

            val phrases = postgrest.from("phrases")
                .select()
                .decodeList<PhraseDto>()

            AppModule.database.withTransaction {
                languageDao.clearAll()
                languageDao.insertAll(languages.map { it.toEntity() })

                phraseDao.clearAll()
                phraseDao.insertAll(phrases.map { it.toEntity() })
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AppError.Network("Failed to sync languages", e.message ?: ""))
        }
    }

    suspend fun getLanguageWithPhrases(locale: String): LanguageConfig? = withContext(Dispatchers.IO) {
        val lang = postgrest.from("languages")
            .select { filter { eq("locale", locale) } }
            .decodeSingleOrNull<LanguageDto>()
            ?: return@withContext null

        val phrases = postgrest.from("phrases")
            .select { filter { eq("language_id", lang.id) } }
            .decodeList<PhraseDto>()

        lang.toDomain(phrases)
    }

    private fun CachedLanguageEntity.toDomain(): LanguageConfig {
        return LanguageConfig(
            id = id,
            locale = locale,
            displayName = displayName,
            displayNameNative = displayNameNative,
            acceptancePhrases = emptyList(), // Phrases loaded separately
            priceKeywords = priceKeywords,
            distanceKeywords = distanceKeywords,
            durationKeywords = durationKeywords,
            aliases = aliases,
            enabled = enabled
        )
    }

    @kotlinx.serialization.Serializable
    private data class LanguageDto(
        val id: String,
        val locale: String,
        val name: String,
        val name_native: String? = null,
        val aliases: List<String> = emptyList(),
        val enabled: Boolean = true,
        val price_keywords: List<String> = emptyList(),
        val distance_keywords: List<String> = emptyList(),
        val duration_keywords: List<String> = emptyList()
    ) {
        fun toEntity() = CachedLanguageEntity(
            id = id,
            locale = locale,
            displayName = name,
            displayNameNative = name_native ?: name,
            aliases = aliases,
            enabled = enabled,
            priceKeywords = price_keywords,
            distanceKeywords = distance_keywords,
            durationKeywords = duration_keywords
        )

        fun toDomain(phrases: List<PhraseDto>) = LanguageConfig(
            id = id,
            locale = locale,
            displayName = name,
            displayNameNative = name_native ?: name,
            acceptancePhrases = phrases.filter { it.type == "acceptance" }.map { it.phrase },
            priceKeywords = price_keywords,
            distanceKeywords = distance_keywords,
            durationKeywords = duration_keywords,
            aliases = aliases,
            enabled = enabled
        )
    }

    @kotlinx.serialization.Serializable
    private data class PhraseDto(
        val id: String,
        val language_id: String,
        val type: String,
        val phrase: String,
        val enabled: Boolean = true
    ) {
        fun toEntity() = CachedPhraseEntity(
            id = id,
            languageId = language_id,
            type = type,
            phrase = phrase,
            enabled = enabled
        )
    }
}
