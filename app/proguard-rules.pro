# Keep Supabase models
-keep class io.github.jan-tennert.supabase.** { *; }
-keep class io.ktor.** { *; }
-keep class io.ktor.client.** { *; }
-keep class io.ktor.http.** { *; }
-keep class io.ktor.serialization.** { *; }
-keep class io.ktor.utils.io.** { *; }

# Keep serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }

# Keep data classes
-keep class com.example.ridepricematcher.domain.model.** { *; }
-keep class com.example.ridepricematcher.data.local.entity.** { *; }
-keep class com.example.ridepricematcher.BuildConfig { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Kotlin serialization generated serializers
-keepclassmembers class **$$serializer { *; }
-keepclassmembers class com.example.ridepricematcher.domain.model.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# Compose and Navigation
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }

# Unity Ads and OpenCV
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }
-keep class org.opencv.** { *; }
