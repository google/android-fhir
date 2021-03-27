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

        testInstrumentationRunner(deps.Dependencies.TestLibraries.androidJunitRunner)
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
    coreLibraryDesugaring(deps.Dependencies.Libraries.desugarJdkLibs)

    implementation(deps.Dependencies.Libraries.Androidx.appCompat)
    implementation(deps.Dependencies.Libraries.Androidx.constraintLayout)
    implementation(deps.Dependencies.Libraries.Androidx.fragmentKtx)
    implementation(deps.Dependencies.Libraries.material)
    implementation(deps.Dependencies.Libraries.Kotlin.androidxCoreKtx)
    implementation(deps.Dependencies.Libraries.Kotlin.stdlib)
    implementation(deps.Dependencies.Libraries.Navigation.navFragmentKtx)
    implementation(deps.Dependencies.Libraries.Navigation.navUiKtx)

    implementation (project(path = ":datacapture"))

    testImplementation(deps.Dependencies.TestLibraries.AndroidxTest.junit)

    androidTestImplementation(deps.Dependencies.TestLibraries.AndroidxTest.extJunit)
    androidTestImplementation(deps.Dependencies.TestLibraries.Espresso.espressoCore)
}