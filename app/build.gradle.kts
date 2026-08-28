import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { input ->
        localProperties.load(input)
    }
}

val supabaseAnonKey =
    System.getenv("SUPABASE_ANON_KEY")
        ?.takeIf { it.isNotBlank() }
        ?: localProperties.getProperty("SUPABASE_ANON_KEY")
        ?: project.findProperty("SUPABASE_ANON_KEY")?.toString()
        ?: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJod3BibnpiZXZ1Zm9sb2pqaW1oIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc4NzA1ODQ5MCwiZXhwIjoyMTAyNjM0NDkwfQ.qwJIMgdrbi8bb7jfqcedQENrAG-X2tu-RTLNgQj0kdA"

val escapedSupabaseAnonKey = supabaseAnonKey
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("$", "\\$")

val supabaseUrl =
    System.getenv("SUPABASE_URL")
        ?.takeIf { it.isNotBlank() }
        ?: localProperties.getProperty("SUPABASE_URL")
        ?: project.findProperty("SUPABASE_URL")?.toString()
        ?: "https://rhwpbnzbevufolojjimh.supabase.co"

val adminEmail =
    System.getenv("ADMIN_EMAIL")
        ?.takeIf { it.isNotBlank() }
        ?: localProperties.getProperty("ADMIN_EMAIL")
        ?: project.findProperty("ADMIN_EMAIL")?.toString()
        ?: ""

val escapedAdminEmail = adminEmail
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("$", "\\$")

android {
    namespace = "com.example.ridepricematcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ridepricematcher"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"$supabaseUrl\""
        )

        buildConfigField(
            "String",
            "SUPABASE_ANON_KEY",
            "\"$escapedSupabaseAnonKey\""
        )

        buildConfigField(
            "String",
            "UNITY_GAME_ID",
            "\"6178983\""
        )

        buildConfigField(
            "String",
            "ADMIN_EMAIL",
            "\"$escapedAdminEmail\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isDebuggable = true
        }
    }

    splits {
        abi {
            isEnable = true
            reset()

            include(
                "arm64-v8a",
                "armeabi-v7a",
                "x86_64"
            )

            isUniversalApk = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true

            all {
                it.jvmArgs("-XX:+IgnoreUnrecognizedVMOptions")
            }
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Supabase
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.0.0")
    implementation("io.github.jan-tennert.supabase:auth-kt:3.0.0")
    implementation("io.github.jan-tennert.supabase:realtime-kt:3.0.0")

    // Ktor
    implementation("io.ktor:ktor-client-android:3.0.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // OpenCV
    implementation("com.quickbirdstudios:opencv:4.5.3.0")

    // Unity Ads
    implementation("com.unity3d.ads:unity-ads:4.12.0")

    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.11")
    testImplementation("com.google.truth:truth:1.4.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    // Android Testing
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug Tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
