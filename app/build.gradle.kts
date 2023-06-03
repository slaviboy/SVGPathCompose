plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = ApplicationConfiguration.appNamsespace
    compileSdk = ApplicationConfiguration.compileSdk
    defaultConfig {
        applicationId = ApplicationConfiguration.appNamsespace
        minSdk = ApplicationConfiguration.minSdk
        targetSdk = ApplicationConfiguration.targetSdk
        testInstrumentationRunner = ApplicationConfiguration.androidTestInstrumentation
        versionCode = ApplicationConfiguration.versionCode
        versionName = ApplicationConfiguration.versionName
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ApplicationDependencyVersions.composeCompiler
    }
}

dependencies {
    implementation(ApplicationDependencies.dependenciesApp)
    implementation(project(mapOf("path" to ":svgPathCompose")))
}