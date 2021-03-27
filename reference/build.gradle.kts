plugins {
    id(BuildPlugins.application)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinKapt)
}

android {
    compileSdkVersion(deps.Dependencies.Sdk.compileSdk)
    defaultConfig {
        applicationId("com.google.android.fhir.reference")
        minSdkVersion(deps.Dependencies.Sdk.minSdk)
        targetSdkVersion(deps.Dependencies.Sdk.targetSdk)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner(deps.Dependencies.TestLibraries.androidJunitRunner)
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
    coreLibraryDesugaring(deps.Dependencies.Libraries.desugarJdkLibs)

    implementation(deps.Dependencies.Libraries.Androidx.activity)
    implementation(deps.Dependencies.Libraries.Androidx.appCompat)
    implementation(deps.Dependencies.Libraries.Androidx.constraintLayout)
    implementation(deps.Dependencies.Libraries.Androidx.fragmentKtx)
    implementation(deps.Dependencies.Libraries.Androidx.recyclerView)
    implementation(deps.Dependencies.Libraries.Androidx.workRuntimeKtx)
    implementation(deps.Dependencies.Libraries.Kotlin.kotlinCoroutinesAndroid)
    implementation(deps.Dependencies.Libraries.Kotlin.kotlinCoroutinesCore)
    implementation(deps.Dependencies.Libraries.Kotlin.stdlib)
    implementation(deps.Dependencies.Libraries.Lifecycle.liveDataKtx)
    implementation(deps.Dependencies.Libraries.Lifecycle.runtime)
    implementation(deps.Dependencies.Libraries.Lifecycle.viewModelKtx)
    implementation(deps.Dependencies.Libraries.Navigation.navFragmentKtx)
    implementation(deps.Dependencies.Libraries.Navigation.navUiKtx)
    implementation(deps.Dependencies.Libraries.Retrofit.coreRetrofit)
    implementation(deps.Dependencies.Libraries.Retrofit.gsonConverter)
    implementation(deps.Dependencies.Libraries.Retrofit.retrofitMock)
    implementation(deps.Dependencies.Libraries.httpInterceptor)
    implementation(deps.Dependencies.Libraries.material)

    implementation(project(path = ":core"))

    testImplementation(deps.Dependencies.TestLibraries.AndroidxTest.junit)

    androidTestImplementation(deps.Dependencies.TestLibraries.AndroidxTest.extJunit)
    androidTestImplementation(deps.Dependencies.TestLibraries.Espresso.espressoCore)

}
