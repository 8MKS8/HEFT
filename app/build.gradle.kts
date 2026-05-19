plugins {
    // Android application plugin
    alias(libs.plugins.android.application)

    // Kotlin Android plugin
    alias(libs.plugins.kotlin.android)

    // Kotlin Compose plugin
    alias(libs.plugins.kotlin.compose)

    // Google Services — connects Firebase
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.heft"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.heft"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    // WorkManager — for scheduling notifications
    implementation(libs.androidx.work.runtime)

    // AppCompat — needed for AlertDialog in Compose
    implementation("androidx.appcompat:appcompat:1.6.1")


    // Compose BOM — manages all Compose versions
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Firebase BOM — manages all Firebase versions
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Facebook SDK
    implementation(libs.facebook.sdk)

    // YouTube Player
    implementation(libs.youtube.player)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}