import java.io.FileInputStream
import java.util.Properties

val secretsPropertiesFile = rootProject.file("secrets.properties")
val secrets = Properties()

if (secretsPropertiesFile.exists()) {
    secrets.load(FileInputStream(secretsPropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.cornellappdev.resell.android"
    compileSdk = 35

    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
    }

    defaultConfig {
        applicationId = "com.cornellappdev.resell.android"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
            keyAlias = "resell"
            keyPassword = secrets.getProperty("KEY_PASS")
            storeFile = file("/../resell-keystore.jks")
            storePassword = secrets.getProperty("KEY_PASS")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Declare the dependency for the Firestore library
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.foundation)
    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material)
    kapt(libs.hilt.android.compiler)
    implementation(libs.coil.compose)
    implementation(libs.androidx.activity.ktx)

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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
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
