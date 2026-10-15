plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.familycore.app"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.familycore.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 10
        versionName = "1.0.0"
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("debug").apply {
                storeFile = file(System.getProperty("user.home") + "/.android/debug.keystore")
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            val storePath = System.getenv("FAMILYCORE_UPLOAD_STORE_FILE")
            val storePasswordValue = System.getenv("FAMILYCORE_UPLOAD_STORE_PASSWORD")
            val keyAliasValue = System.getenv("FAMILYCORE_UPLOAD_KEY_ALIAS")
            val keyPasswordValue = System.getenv("FAMILYCORE_UPLOAD_KEY_PASSWORD")
            if (!storePath.isNullOrBlank() && !storePasswordValue.isNullOrBlank() &&
                !keyAliasValue.isNullOrBlank() && !keyPasswordValue.isNullOrBlank()) {
                signingConfig = signingConfigs.create("familyCoreUpload") {
                    storeFile = file(storePath)
                    storePassword = storePasswordValue
                    keyAlias = keyAliasValue
                    keyPassword = keyPasswordValue
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true; buildConfig = true }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    testImplementation("junit:junit:4.13.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
