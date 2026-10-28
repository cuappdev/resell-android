import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val secretsPropertiesFile = rootProject.file("secrets.properties")
val secrets = Properties()

if (secretsPropertiesFile.exists()) {
    secrets.load(FileInputStream(secretsPropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.cornellappdev.resell.android"
    compileSdk = 37

    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/INDEX.LIST")
    }

    defaultConfig {
        applicationId = "com.cornellappdev.resell.android"
        minSdk = 28
        targetSdk = 35
        versionCode = 22
        versionName = "2.0 - Refactor [Beta]"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            "String",
            "FCM_URL", "\"${secrets.getProperty("FCM_URL")}\""
        )

        buildConfigField(
            "String",
            "NOTIFICATIONS_KEY", "\"${secrets.getProperty("FIREBASE_NOTIFICATIONS_KEY")}\""
        )
    }

    signingConfigs {
        create("release") {
            keyAlias = secrets.getProperty("KEY_ALIAS")
            keyPassword = secrets.getProperty("KEY_PASS")
            storeFile = file("/../resell-keystore.jks")
            storePassword = secrets.getProperty("KEY_STORE_PASS")
        }
        create("release-mock") {
            keyAlias = secrets.getProperty("KEY_MOCK_ALIAS")
            keyPassword = secrets.getProperty("KEY_MOCK_PASS")
            storeFile = file("/../resell-mock-keystore.jks")
            storePassword = secrets.getProperty("KEY_MOCK_STORE_PASS")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "GOOGLE_AUTH_CLIENT_ID", "\"${secrets.getProperty("GOOGLE_AUTH_CLIENT_ID")}\""
            )
            buildConfigField(
                "String",
                "BASE_API_URL", "\"${secrets.getProperty("API_URL_PROD")}\""
            )
        }

        create("releaseMock") {
            signingConfig = signingConfigs.getByName("release-mock")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "GOOGLE_AUTH_CLIENT_ID", "\"${secrets.getProperty("GOOGLE_AUTH_CLIENT_ID")}\""
            )
            buildConfigField(
                "String",
                "BASE_API_URL", "\"${secrets.getProperty("API_URL_PROD")}\""
            )
        }

        debug {
            buildConfigField(
                "String",
                "GOOGLE_AUTH_CLIENT_ID", "\"${secrets.getProperty("GOOGLE_AUTH_CLIENT_ID_LOCAL")}\""
            )
            buildConfigField(
                "String",
                "BASE_API_URL", "\"${secrets.getProperty("API_URL_DEV")}\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // Declare the dependency for the Firestore library
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.foundation)
    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material)
    kapt(libs.hilt.android.compiler)
    kapt(libs.kotlin.metadata.jvm)
    implementation(libs.coil.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.ui.tooling)

    implementation(libs.kotlinx.coroutines.core)

    // Google Play Services Auth
    implementation(libs.gms.play.services.auth)
    implementation(libs.google.auth.library.oauth2.http)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.test.manifest)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.coroutines.android)

    // OkHttp3
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
