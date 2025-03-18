// app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "2.0.0"
    // Compose Compiler plugin
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"

    // Kotlin Kapt (if you use annotation processing)
    id("kotlin-kapt")

    // Google services plugin for Firebase (must be declared in root build.gradle.kts with apply false)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.chukaonlinegrocerystore"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chukaonlinegrocerystore"
        minSdk = 24
        targetSdk = 35
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

    // Using Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    // Enable Jetpack Compose
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // Compose BOM manages versions for Compose libraries
    implementation(platform(libs.androidx.compose.bom.v20230900))

    // AndroidX and Compose
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.lifecycle.runtime.ktx.v262)
    implementation(libs.androidx.activity.compose.v172)
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
//    implementation(libs.material3)
    implementation(libs.androidx.navigation.compose)

    // RecyclerView (if you need it in other parts of your app)
    implementation(libs.androidx.recyclerview)

    // Firebase BOM (manages versions for Firebase libraries)
    implementation(platform(libs.firebase.bom))

    // Firebase dependencies (example)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.material3.android)
    // Add more (e.g., firebase-storage-ktx) as needed

    // Test Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(platform(libs.androidx.compose.bom.v20250200))
    androidTestImplementation(libs.ui.test.junit4)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}