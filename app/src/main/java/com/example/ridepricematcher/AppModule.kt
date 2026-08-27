package com.example.ridepricematcher

import android.content.Context
import androidx.room.Room
import com.example.ridepricematcher.data.local.AppDatabase
import com.example.ridepricematcher.data.repository.AdminRepository
import com.example.ridepricematcher.data.repository.AuthRepository
import com.example.ridepricematcher.data.repository.EntitlementRepository
import com.example.ridepricematcher.data.repository.LanguageRepository
import com.example.ridepricematcher.data.repository.UserPreferenceRepository

/** Application-scoped dependencies backed by the application context only. */
object AppModule {
    lateinit var applicationContext: Context
        private set
    lateinit var database: AppDatabase
        private set
    lateinit var authRepository: AuthRepository
        private set
    lateinit var entitlementRepository: EntitlementRepository
        private set
    lateinit var languageRepository: LanguageRepository
        private set
    lateinit var userPreferenceRepository: UserPreferenceRepository
        private set
    lateinit var adminRepository: AdminRepository
        private set

    @Synchronized
    fun init(context: Context) {
        if (::database.isInitialized) return
        applicationContext = context.applicationContext
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "autopilot.db"
        ).build()
        authRepository = AuthRepository()
        entitlementRepository = EntitlementRepository(database.entitlementDao())
        languageRepository = LanguageRepository(database.languageDao(), database.phraseDao())
        userPreferenceRepository = UserPreferenceRepository(database.userPreferenceDao())
        adminRepository = AdminRepository()
    }
}
