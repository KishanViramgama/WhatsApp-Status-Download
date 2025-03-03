plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.app.status"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.status"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    val whatsApp: String by project.extra
    val whatsAppTesting: String by project.extra
    val whatsApp11: String by project.extra
    val whatsApp11Testing: String by project.extra

    val whatsAppBusiness: String by project.extra
    val whatsAppBusinessTesting: String by project.extra
    val whatsAppBusiness11: String by project.extra
    val whatsAppBusiness11Testing: String by project.extra

    flavorDimensions += "url"
    productFlavors {
        create("development") {
            dimension = "url"
            buildConfigField("String", "whatsApp", whatsAppTesting)
            buildConfigField("String", "whatsApp11", whatsApp11Testing)
            buildConfigField("String", "whatsAppBusiness", whatsAppBusinessTesting)
            buildConfigField("String", "whatsAppBusiness11", whatsAppBusiness11Testing)
        }
        create("production"){
            dimension = "url"
            buildConfigField("String", "whatsApp", whatsApp)
            buildConfigField("String", "whatsApp11", whatsApp11)
            buildConfigField("String", "whatsAppBusiness", whatsAppBusiness)
            buildConfigField("String", "whatsAppBusiness11", whatsAppBusiness11)

        }
    }

}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.tools.core)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.accompanist.flowlayout)

    //Splash screen
    implementation(libs.androidx.core.splashscreen)

    //Constraint layout
    implementation(libs.androidx.constraintlayout.compose)

    //Glide
    implementation(libs.compose)

    // For media playback using ExoPlayer
    implementation(libs.androidx.media3.exoplayer)

    //Navigating
    implementation(libs.androidx.navigation.compose)

    //Paging Compose
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    //Coroutines
    implementation(libs.kotlinx.coroutines.android)

    //LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //Data store
    implementation(libs.androidx.datastore.preferences)

    //hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.dagger.compiler)
    ksp(libs.hilt.compiler)

    //Gson
    implementation(libs.gson)

}