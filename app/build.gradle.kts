plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.subin.leafy"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.subin.leafy"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

kotlin{
    jvmToolchain(21)
}
configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":features:auth"))
    implementation(project(":features:search"))
    implementation(project(":features:note"))
    implementation(project(":features:home"))
    implementation(project(":features:community"))
    implementation(project(":features:timer"))
    implementation(project(":features:mypage"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.core.splashscreen)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.libraries)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.compose.material3)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase.libraries)
    implementation(libs.firebase.auth)

    implementation(libs.bundles.room.libraries)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)
    implementation(libs.play.services.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}