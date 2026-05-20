plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.linkitsoft.serialcommunication"
    compileSdk {
        version = release(36)
    }

    externalNativeBuild {

        cmake {

            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    defaultConfig {
        applicationId = "com.linkitsoft.serialcommunication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {

            abiFilters += listOf(
                "armeabi-v7a",
                "arm64-v8a"
            )
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
}