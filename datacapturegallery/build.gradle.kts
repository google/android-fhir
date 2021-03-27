plugins {
    id(BuildPlugins.application)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.navSafeArgs)
}

android {
    compileSdkVersion(deps.Dependencies.Sdk.compileSdk)
    buildToolsVersion(deps.Plugins.Versions.buildTools)

    defaultConfig {
        applicationId ("com.google.android.fhir.datacapture.gallery")
        minSdkVersion(deps.Dependencies.Sdk.minSdk)
        targetSdkVersion(deps.Dependencies.Sdk.targetSdk)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner(deps.Dependencies.androidJunitRunner)
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
    coreLibraryDesugaring(deps.Dependencies.desugarJdkLibs)

    implementation(deps.Dependencies.Androidx.appCompat)
    implementation(deps.Dependencies.Androidx.constraintLayout)
    implementation(deps.Dependencies.Androidx.fragmentKtx)
    implementation(deps.Dependencies.material)
    implementation(deps.Dependencies.Kotlin.androidxCoreKtx)
    implementation(deps.Dependencies.Kotlin.stdlib)
    implementation(deps.Dependencies.Navigation.navFragmentKtx)
    implementation(deps.Dependencies.Navigation.navUiKtx)

    implementation (project(path = ":datacapture"))

    testImplementation(deps.Dependencies.AndroidxTest.junit)

    androidTestImplementation(deps.Dependencies.AndroidxTest.extJunit)
    androidTestImplementation(deps.Dependencies.Espresso.espressoCore)
}