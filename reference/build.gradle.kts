plugins {
    id(BuildPlugins.application)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {
    compileSdkVersion(versions.Sdk.compileSdk)
    defaultConfig {
        applicationId("com.google.android.fhir.reference")
        minSdkVersion(versions.Sdk.minSdk)
        targetSdkVersion(versions.Sdk.targetSdk)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner(deps.TestDependencies.standardRunner)
        // Required when setting minSdkVersion to 20 or lower
        // See https://developer.android.com/studio/write/java8-support
        multiDexEnabled = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    // See https://developer.android.com/studio/write/java8-support
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

configurations {
    all {
        exclude (module = "json")
        exclude (module = "xpp3")
    }
}

dependencies {
    androidTestImplementation(deps.TestDependencies.CoreTestDeps.extJunit)
    androidTestImplementation(deps.TestDependencies.Espresso.espresso)

    coreLibraryDesugaring(deps.AppDependencies.CoreDeps.desugar)

    implementation(deps.AppDependencies.CoreDeps.activity)
    implementation(deps.AppDependencies.CoreDeps.appCompat)
    implementation(deps.AppDependencies.CoreDeps.fragment)
    implementation(deps.AppDependencies.Kotlin.kotlin)
    implementation(deps.AppDependencies.Kotlin.androidCoroutines)
    implementation(deps.AppDependencies.Kotlin.coreKtCoroutines)
    implementation(deps.AppDependencies.CoreDeps.materialDesign)
    implementation(deps.AppDependencies.CoreDeps.constraintLayout)
    implementation(deps.AppDependencies.Navigation.navFragment)
    implementation(deps.AppDependencies.Navigation.navUi)
    implementation(deps.AppDependencies.Lifecycle.runtime)
    implementation(deps.AppDependencies.Lifecycle.liveDataKtx)
    implementation(deps.AppDependencies.Lifecycle.viewModel)
    implementation(deps.AppDependencies.Networking.httpInterceptor)
    implementation(deps.AppDependencies.Networking.coreRetrofit)
    implementation(deps.AppDependencies.Networking.gsonConverter)
    implementation(deps.AppDependencies.Networking.mockRetrofit)
    implementation(deps.AppDependencies.CoreDeps.recyclerView)
    implementation(deps.AppDependencies.CoreDeps.work)

    implementation(project(path = ":core"))

    testImplementation(deps.TestDependencies.CoreTestDeps.junit)
}
