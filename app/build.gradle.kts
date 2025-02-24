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
        versionCode = 2
        versionName = "1.1 Beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            versionNameSuffix = "Beta 1"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.3")


}