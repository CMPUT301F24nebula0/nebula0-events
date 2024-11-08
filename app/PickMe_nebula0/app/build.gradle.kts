plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin
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
    implementation(libs.espresso.intents)
    testImplementation(libs.ext.junit)
    testImplementation(libs.espresso.core)
    testImplementation(libs.espresso.core)
    // Import the Firebase BoM
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    testImplementation ("org.robolectric:robolectric:4.7.3") // Use the latest version available

    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.tasks)
    testImplementation(libs.junit)
    testImplementation(libs.runner)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")
    implementation("com.google.zxing:core:3.3.3")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.core:core-ktx:1.10.0")

}