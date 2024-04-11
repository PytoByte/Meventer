plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    kotlin("plugin.serialization") version "1.4.21"

    kotlin("kapt")

    id("com.google.dagger.hilt.android")
}

android {
    namespace = "pachmp.meventer"
    compileSdk = 34

    defaultConfig {
        applicationId = "pachmp.meventer"
        minSdk = 24
        maxSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {
    // DI
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    // Alt navigation
    implementation("io.github.raamcosta.compose-destinations:core:1.10.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Alternative navigation
    implementation("io.github.raamcosta.compose-destinations:animations-core:1.10.1")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.10.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // ktor
    val ktor_version = "2.3.8"
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-auth:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")

    // Icons
    implementation ("androidx.compose.material:material-icons-extended:1.6.4")

    // EncryptedSharedPreferences
    implementation("androidx.security:security-crypto:1.0.0")

    // async images
    implementation("io.coil-kt:coil-compose:2.6.0")

    // ratingbar
    implementation("com.github.a914-gowtham:compose-ratingbar:1.2.5")

    // DateTime pickers
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.8.1-rc")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // default
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}
