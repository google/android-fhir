plugins {
    id(deps.Plugins.application)
    id(deps.Plugins.kotlinAndroid)
    id(deps.Plugins.navSafeArgs)
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
        isMultiDexEnabled = true
    }

    buildFeatures {
        isViewBinding = true
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
    coreLibraryDesugaring(deps.AppDependencies.CoreDeps.desugar)

    implementation(deps.AppDependencies.CoreDeps.appCompat)
//    implementation(deps.AppDependencies.Kotlin.androidxCoreKtx)
    implementation(deps.AppDependencies.CoreDeps.fragment)
    implementation(deps.AppDependencies.Kotlin.kotlin)
    implementation(deps.AppDependencies.CoreDeps.materialDesign)
    implementation(deps.AppDependencies.CoreDeps.constraintLayout)
    implementation(deps.AppDependencies.Navigation.navFragment)
    implementation(deps.AppDependencies.Navigation.navUi)


//    coreLibraryDesugaring deps.desugar

//    implementation deps.appcompat
//    implementation deps.constraint_layout
//    implementation deps.core
//    implementation deps.fragment
//    implementation deps.kotlin.stdlib
//    implementation deps.material
//    implementation deps.navigation.fragment
//    implementation deps.navigation.ui

    implementation project("path" = ":datacapture")

    testImplementation(deps.TestDependencies.CoreTestDeps.junit)

    androidTestImplementation(deps.TestDependencies.CoreTestDeps.extJunit)
    androidTestImplementation(deps.TestDependencies.Espresso.espresso)
}