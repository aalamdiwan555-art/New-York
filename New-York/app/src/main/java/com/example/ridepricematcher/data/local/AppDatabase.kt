package com.example.ridepricematcher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ridepricematcher.data.local.dao.*
import com.example.ridepricematcher.data.local.entity.*

@Database(
    entities = [
        CachedLanguageEntity::class,
        CachedPhraseEntity::class,
        CachedUserPreferenceEntity::class,
        CachedEntitlementEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun languageDao(): LanguageDao
    abstract fun phraseDao(): PhraseDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun entitlementDao(): EntitlementDao
    abstract fun auditLogDao(): AuditLogDao
}
