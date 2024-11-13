plugins {
    alias(libs.plugins.android.application)
    // Google services Gradle plugin for Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.pickme_nebula0"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pickme_nebula0"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Firebase BoM for version management
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    // Firebase Firestore, excluding protobuf-lite
    implementation("com.google.firebase:firebase-firestore") {
        exclude( "com.google.protobuf",  "protobuf-lite")
    }

    // ZXing for QR code support, excluding protobuf-lite
    implementation("com.journeyapps:zxing-android-embedded:4.1.0") {
        exclude( "com.google.protobuf",  "protobuf-lite")
    }

    // Espresso contrib for UI testing, excluding protobuf-lite
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude( "com.google.protobuf",  "protobuf-lite")
    }

    // Force protobuf-javalite to be the sole protobuf implementation
    implementation("com.google.protobuf:protobuf-javalite:3.22.3")

    // UI and other core libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.tasks)
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.core:core-ktx:1.10.0")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

configurations.all {
    // Force protobuf-javalite to avoid conflicts with protobuf-lite
    resolutionStrategy {
        force("com.google.protobuf:protobuf-javalite:3.22.3")
    }
}
