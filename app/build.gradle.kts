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
        versionCode = 7
        versionName = "V1"

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

    //Android Deps
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
    implementation(libs.mhiew.android.pdf.viewer)

    //Google play auto update
    implementation(libs.app.update)


    //Gson compile
    implementation(libs.gson)
    implementation(libs.gson.v289)

}