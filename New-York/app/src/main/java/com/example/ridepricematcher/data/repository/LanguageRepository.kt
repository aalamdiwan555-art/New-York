package com.example.ridepricematcher.data.repository

import com.example.ridepricematcher.data.local.dao.LanguageDao
import com.example.ridepricematcher.data.local.dao.PhraseDao
import com.example.ridepricematcher.data.local.entity.CachedLanguageEntity
import com.example.ridepricematcher.data.local.entity.CachedPhraseEntity
import com.example.ridepricematcher.data.remote.SupabaseClientProvider
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.LanguageConfig
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

            languageDao.clearAll()
            languageDao.insertAll(languages.map { it.toEntity() })

            phraseDao.clearAll()
            phraseDao.insertAll(phrases.map { it.toEntity() })

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
