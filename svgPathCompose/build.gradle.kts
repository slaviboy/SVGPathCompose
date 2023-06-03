plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
}

android {
    namespace = ApplicationConfiguration.libraryNamespace
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

afterEvaluate {
    publishing {
        publications {

            // Creates a Maven publication called "release".
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = ApplicationConfiguration.groupId
                artifactId = ApplicationConfiguration.artifactId
                version =ApplicationConfiguration.version
            }

        }
    }
}

dependencies {
    implementation(ApplicationDependencies.dependenciesLibrary)
}