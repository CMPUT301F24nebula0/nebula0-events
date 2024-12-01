import java.util.Properties

// Define the getLocalProperty function at the top
fun getLocalProperty(key: String): String {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    return if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
        properties.getProperty(key) ?: ""
    } else {
        ""
    }
}

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

        manifestPlaceholders["MAPS_API_KEY"] = getLocalProperty("MAPS_API_KEY")

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

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore") {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    implementation("com.google.firebase:firebase-messaging")

    implementation ("com.google.firebase:firebase-storage:20.2.1")
    implementation ("com.squareup.picasso:picasso:2.8")// For loading images
    implementation ("androidx.activity:activity-compose:1.9.3") // or the latest version
    implementation("com.google.android.gms:play-services-maps:18.1.0")


    // QR code support with ZXing
    implementation("com.journeyapps:zxing-android-embedded:4.1.0") {
        exclude("com.google.protobuf", "protobuf-lite")
    }

    // UI and Compose dependencies
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude("com.google.protobuf", "protobuf-lite")
    }
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")
    implementation("com.google.zxing:core:3.3.3")
    implementation("com.google.android.material:material:1.4.0")

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Force protobuf-javalite to avoid conflicts with protobuf-lite
    implementation("com.google.protobuf:protobuf-javalite:3.22.3")

    // Additional tools
    implementation(libs.rules)
    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
}

configurations.all {
    // Force protobuf-javalite to avoid conflicts with protobuf-lite
    resolutionStrategy {
        force("com.google.protobuf:protobuf-javalite:3.22.3")
        force("org.hamcrest:hamcrest-core:2.2")
        force("org.hamcrest:hamcrest-library:2.2")
    }
}