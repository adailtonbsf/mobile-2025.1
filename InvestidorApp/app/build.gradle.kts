plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android" )
    id("com.google.gms.google-services" )
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "me.daltonbsf.investidorapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "me.daltonbsf.investidorapp"
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
    // Firebase
    implementation(platform(libs.firebase.bom)) // Versão compatível com Kotlin 1.9.10
    implementation(libs.firebase.analytics )
    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom.v20230901))
    implementation(libs.ui )
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    implementation(libs.firebase.database.ktx)
    implementation (libs.firebase.messaging.ktx)
    debugImplementation(libs.ui.tooling)
    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}