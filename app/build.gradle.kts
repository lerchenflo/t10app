plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.lerchenflo.t10elementekatalog"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lerchenflo.t10elementekatalog"
        minSdk = 24
        targetSdk = 35
        versionCode = 6
        versionName = "Beta 1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            versionNameSuffix = "1.5"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.common)
    implementation(libs.room.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //PDF Viewer
    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.3")

    //Google play auto update
    implementation("com.google.android.play:app-update:2.1.0")


    //Gson compile
    implementation("com.google.code.gson:gson:2.10.1")

}