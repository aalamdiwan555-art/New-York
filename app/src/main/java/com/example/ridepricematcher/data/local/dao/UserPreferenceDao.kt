package com.example.ridepricematcher.data.local.dao

import androidx.room.*
import com.example.ridepricematcher.data.local.entity.CachedUserPreferenceEntity

@Dao
interface UserPreferenceDao {
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    suspend fun getPreferences(userId: String): CachedUserPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preferences: CachedUserPreferenceEntity)

    @Query("DELETE FROM user_preferences WHERE userId = :userId")
    suspend fun delete(userId: String)
}
