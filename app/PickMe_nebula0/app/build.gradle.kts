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
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore") {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-storage:20.2.1")

    // AndroidX dependencies
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Testing dependencies
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    // QR code and image loading
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0") {
        exclude("com.google.protobuf", "protobuf-lite")
    }

    // Multidex for large method count
    implementation("androidx.multidex:multidex:2.0.1")

    // Force protobuf-javalite
    implementation("com.google.protobuf:protobuf-javalite:3.22.3")
}


configurations.all {
    // Force protobuf-javalite to avoid conflicts with protobuf-lite
    resolutionStrategy {
        force("com.google.protobuf:protobuf-javalite:3.22.3")
        force("org.hamcrest:hamcrest-core:2.2")
        force("org.hamcrest:hamcrest-library:2.2")
    }
}
