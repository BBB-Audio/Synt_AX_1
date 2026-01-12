import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "bbb.audio.syntAX1"

    compileSdk = 36

    val localProperties = Properties()
    localProperties.load(rootProject.file("local.properties").inputStream())

    defaultConfig {
        applicationId = "bbb.audio.syntAX1"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "FREESOUND_API_KEY", "\"${localProperties.getProperty("freesound.api.key", "")}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "ENABLE_LOGS", "false")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("boolean", "ENABLE_LOGS", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = false
        dataBinding = false
    }


}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.media3.exoplayer)

    // UI Preview
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Serialization
    implementation(libs.kotlinx.serialization)

    // Accompanist Pager
    implementation(libs.accompanist.pager)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Networking
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Dependency Injection (Koin)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // External Libraries

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Gson (for JSON ↔ BLOB conversation)
    implementation(libs.gson)

    // Pure Data for Android
    implementation(libs.libpd.core)

    // Lottie Animations
    implementation(libs.lottie.compose)
}
