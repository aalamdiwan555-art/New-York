package com.example.ridepricematcher.data.remote

import com.example.ridepricematcher.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClientProvider {

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            defaultSerializer = KotlinXSerializer(jsonConfig)
            install(Auth) {
                // Session auto-refresh is enabled by default
            }
            install(Postgrest) {
                defaultSchema = "public"
            }
            install(Realtime)
            httpConfig {
                install(HttpTimeout) {
                    requestTimeoutMillis = 30_000
                    connectTimeoutMillis = 15_000
                    socketTimeoutMillis = 15_000
                }
                install(HttpRequestRetry) {
                    retryOnServerErrors(maxRetries = 3)
                    exponentialDelay()
                }
            }
        }
    }

    val auth get() = client.auth
    val postgrest get() = client.postgrest
}
