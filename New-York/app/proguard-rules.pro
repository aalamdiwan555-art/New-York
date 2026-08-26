# Keep Supabase models
-keep class io.github.jan-tennert.supabase.** { *; }
-keep class io.ktor.** { *; }

# Keep serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }

# Keep data classes
-keep class com.example.ridepricematcher.data.model.** { *; }
-keep class com.example.ridepricematcher.domain.model.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
