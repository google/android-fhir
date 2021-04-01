plugins {
  id(Plugins.BuildPlugins.application)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.navSafeArgs)
}

android {
  compileSdkVersion(Sdk.compileSdk)
  buildToolsVersion(Plugins.Versions.buildTools)

  defaultConfig {
    applicationId("com.google.android.fhir.datacapture.gallery")
    minSdkVersion(Sdk.minSdk)
    targetSdkVersion(Sdk.targetSdk)
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner(Dependencies.androidJunitRunner)
    // Required when setting minSdkVersion to 20 or lower
    multiDexEnabled = true
  }

  buildFeatures { viewBinding = true }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
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
    exclude("META-INF/ASL-2.0.txt")
    exclude("META-INF/LGPL-3.0.txt")
  }
  kotlinOptions {
    // See https://developer.android.com/studio/write/java8-support
    jvmTarget = "1.8"
  }
}

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.Espresso.espressoCore)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Androidx.appCompat)
  implementation(Dependencies.Androidx.constraintLayout)
  implementation(Dependencies.Androidx.fragmentKtx)
  implementation(Dependencies.material)
  implementation(Dependencies.Kotlin.androidxCoreKtx)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Navigation.navFragmentKtx)
  implementation(Dependencies.Navigation.navUiKtx)

  implementation(project(path = ":datacapture"))

  testImplementation(Dependencies.junit)
}
