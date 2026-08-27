package com.example.ridepricematcher

import android.app.Application
import androidx.room.Room
import com.example.ridepricematcher.data.local.AppDatabase
import com.example.ridepricematcher.data.repository.*

class RidePriceMatcherApplication : Application() {

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

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "ride_price_matcher.db"
        ).build()

        authRepository = AuthRepository()
        entitlementRepository = EntitlementRepository(database.entitlementDao())
        languageRepository = LanguageRepository(database.languageDao(), database.phraseDao())
        userPreferenceRepository = UserPreferenceRepository(database.userPreferenceDao())
        adminRepository = AdminRepository()
    }

    companion object {
        lateinit var instance: RidePriceMatcherApplication
            private set
    }
}
