// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.60.1" apply false
    alias(libs.plugins.jetbrains.kotlin.serialization) apply false
    id("com.google.gms.google-services") version "4.5.0" apply false
}
