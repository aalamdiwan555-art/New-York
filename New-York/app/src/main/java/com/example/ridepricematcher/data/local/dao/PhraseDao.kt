package com.example.ridepricematcher.data.local.dao

import androidx.room.*
import com.example.ridepricematcher.data.local.entity.CachedPhraseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseDao {
    @Query("SELECT * FROM phrases WHERE languageId = :languageId AND enabled = 1")
    fun getPhrasesForLanguage(languageId: String): Flow<List<CachedPhraseEntity>>

    @Query("SELECT * FROM phrases WHERE type = :type AND enabled = 1")
    fun getPhrasesByType(type: String): Flow<List<CachedPhraseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phrases: List<CachedPhraseEntity>)

    @Query("DELETE FROM phrases")
    suspend fun clearAll()
}
