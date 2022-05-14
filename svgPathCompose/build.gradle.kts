plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
}

android {

    compileSdk = ApplicationConfiguration.compileSdk
    defaultConfig {
        minSdk = ApplicationConfiguration.minSdk
        targetSdk = ApplicationConfiguration.targetSdk
        testInstrumentationRunner = ApplicationConfiguration.androidTestInstrumentation
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ApplicationDependencyVersions.compose
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = ApplicationConfiguration.groupId
            artifactId = ApplicationConfiguration.artifactId
            version = ApplicationConfiguration.version

            //from(components["java"])
        }
    }
}

dependencies {
    implementation(ApplicationDependencies.dependenciesLibrary)
}