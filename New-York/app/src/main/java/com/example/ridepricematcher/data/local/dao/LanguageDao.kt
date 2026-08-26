package com.example.ridepricematcher.data.local.dao

import androidx.room.*
import com.example.ridepricematcher.data.local.entity.CachedLanguageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageDao {
    @Query("SELECT * FROM languages WHERE enabled = 1")
    fun getEnabledLanguages(): Flow<List<CachedLanguageEntity>>

    @Query("SELECT * FROM languages")
    fun getAllLanguages(): Flow<List<CachedLanguageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(languages: List<CachedLanguageEntity>)

    @Query("DELETE FROM languages")
    suspend fun clearAll()
}
