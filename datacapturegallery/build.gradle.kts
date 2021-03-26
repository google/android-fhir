plugins {
    id(BuildPlugins.application)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.navSafeArgs)
}

android {
    compileSdkVersion(versions.Sdk.compileSdk)
    buildToolsVersion(versions.Plugins.buildTools)

    defaultConfig {
        applicationId ("com.google.android.fhir.datacapture.gallery")
        minSdkVersion(versions.Sdk.minSdk)
        targetSdkVersion(versions.Sdk.targetSdk)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner(deps.TestDependencies.standardRunner)
        // Required when setting minSdkVersion to 20 or lower
        multiDexEnabled = true
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        // Flag to enable support for the new language APIs
        // See https://developer.android.com/studio/write/java8-support
        isCoreLibraryDesugaringEnabled = true
        // Sets Java compatibility to Java 8
        // See https://developer.android.com/studio/write/java8-support
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        exclude ("META-INF/ASL-2.0.txt")
        exclude ("META-INF/LGPL-3.0.txt")
    }
    kotlinOptions {
        // See https://developer.android.com/studio/write/java8-support
        jvmTarget = "1.8"
    }
}

dependencies {
    coreLibraryDesugaring(deps.AppDependencies.Androidx.desugar)

    implementation(deps.AppDependencies.Androidx.appCompat)
    implementation(deps.AppDependencies.Androidx.constraintLayout)
    implementation(deps.AppDependencies.Androidx.fragment)
    implementation(deps.AppDependencies.Androidx.materialDesign)
    implementation(deps.AppDependencies.Kotlin.androidxCoreKtx)
    implementation(deps.AppDependencies.Kotlin.kotlin)
    implementation(deps.AppDependencies.Navigation.navFragment)
    implementation(deps.AppDependencies.Navigation.navUi)

    implementation (project(path = ":datacapture"))

    testImplementation(deps.TestDependencies.AndroidxTest.junit)

    androidTestImplementation(deps.TestDependencies.AndroidxTest.extJunit)
    androidTestImplementation(deps.TestDependencies.Espresso.espresso)
}