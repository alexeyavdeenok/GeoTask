import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    id("com.google.dagger.hilt.android") version "2.48"
    id("androidx.navigation.safeargs.kotlin")
}


android {
    namespace = "com.geotask"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.geotask"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val apiKey = getApiKeyFromLocalProperties()

        buildConfigField("String", "OPENWEATHER_API_KEY", "\"$apiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.8.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("androidx.preference:preference:1.2.1")
}
private fun getApiKeyFromLocalProperties(): String {
    val properties = Properties()

    val localPropertiesFile = rootProject.file("local.properties")

    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use { input ->
            properties.load(input)
        }
        return properties.getProperty("OPENWEATHER_API_KEY")?.trim() ?: ""
    }

    // Если файла нет или ключа нет — возвращаем пустую строку (или можешь бросить ошибку)
    return ""
}