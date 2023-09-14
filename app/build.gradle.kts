import com.asemlab.quakes.Configuration
import java.io.FileInputStream
import java.util.Properties


@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.plugin)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.google.maps)
    alias(libs.plugins.google.services)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    load(FileInputStream(keystorePropertiesFile))
}


android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFile") as String)
            storePassword = keystoreProperties.getProperty("storePassword") as String
            keyAlias = keystoreProperties.getProperty("keyAlias") as String
            keyPassword = keystoreProperties.getProperty("keyPassword") as String
        }
    }
    namespace = "com.asemlab.quakes"
    compileSdk = Configuration.compileSdk

    defaultConfig {

        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = Configuration.versionCode
        versionName = Configuration.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("String", "BANNER_ID", "\"ca-app-pub-8534811027114540/6119287493\"")
            buildConfigField("String", "INTERSTITIAL_ID", "\"ca-app-pub-8534811027114540/6810386782\"")

        }

        getByName("debug") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("String", "BANNER_ID", "\"ca-app-pub-3940256099942544/6300978111\"")
            buildConfigField("String", "INTERSTITIAL_ID", "\"ca-app-pub-3940256099942544/1033173712\"")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(":data"))

    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //Glide
    implementation(libs.glide)
    kapt(libs.glide.compiler)
    implementation(libs.coil)
    implementation(libs.coil.svg)

    // Google Maps
    implementation(libs.play.services.maps)
    implementation(libs.android.maps.utils)
    implementation(libs.maps.utils.ktx)

    // Splash screen API
    implementation(libs.androidx.core.splashscreen)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Pagination
    implementation(libs.androidx.paging.runtime)

    implementation(libs.utilcodex)

    // In-App update
    implementation(libs.play.services.update)

    // Google Ads
    implementation(libs.play.services.ads)
    implementation(libs.play.services.user.messaging)
}