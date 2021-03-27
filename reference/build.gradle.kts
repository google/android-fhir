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
        testInstrumentationRunner(deps.Dependencies.androidJunitRunner)
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
    coreLibraryDesugaring(deps.Dependencies.desugarJdkLibs)

    implementation(deps.Dependencies.Androidx.activity)
    implementation(deps.Dependencies.Androidx.appCompat)
    implementation(deps.Dependencies.Androidx.constraintLayout)
    implementation(deps.Dependencies.Androidx.fragmentKtx)
    implementation(deps.Dependencies.material)
    implementation(deps.Dependencies.Androidx.recyclerView)
    implementation(deps.Dependencies.Androidx.workRuntimeKtx)
    implementation(deps.Dependencies.Kotlin.stdlib)
    implementation(deps.Dependencies.Kotlin.kotlinCoroutinesAndroid)
    implementation(deps.Dependencies.Kotlin.kotlinCoroutinesCore)
    implementation(deps.Dependencies.Lifecycle.runtime)
    implementation(deps.Dependencies.Lifecycle.liveDataKtx)
    implementation(deps.Dependencies.Lifecycle.viewModelKtx)
    implementation(deps.Dependencies.Navigation.navFragmentKtx)
    implementation(deps.Dependencies.Navigation.navUiKtx)
    implementation(deps.Dependencies.Retrofit.coreRetrofit)
    implementation(deps.Dependencies.Retrofit.gsonConverter)
    implementation(deps.Dependencies.retrofitMock)
    implementation(deps.Dependencies.httpInterceptor)

    implementation(project(path = ":core"))

    testImplementation(deps.Dependencies.AndroidxTest.junit)

    androidTestImplementation(deps.Dependencies.AndroidxTest.extJunit)
    androidTestImplementation(deps.Dependencies.Espresso.espressoCore)

}
