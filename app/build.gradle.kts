plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.linkitsoft.serialcommunication"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.linkitsoft.serialcommunication"
        minSdk = 24
        targetSdk = 36
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
    // 传统4KB内存页面版本
    implementation("com.licheedev:android-serialport:2.1.4")
// 适配16KB页面版本，https://developer.android.google.cn/guide/practices/page-sizes?hl=zh-cn
    implementation("com.licheedev:android-serialport:2.1.5")
}