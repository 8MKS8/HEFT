// Top-level build file
plugins {
    // Android application plugin
    alias(libs.plugins.android.application) apply false

    // Kotlin Android plugin
    alias(libs.plugins.kotlin.android) apply false

    // Kotlin Compose plugin
    alias(libs.plugins.kotlin.compose) apply false

    // Google Services plugin for Firebase
    alias(libs.plugins.google.services) apply false
}