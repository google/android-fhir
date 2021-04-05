plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinKapt)
}

android {
  compileSdkVersion(Sdk.compileSdk)
  defaultConfig {
    minSdkVersion(Sdk.minSdk)
    targetSdkVersion(Sdk.targetSdk)
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner(Dependencies.androidJunitRunner)
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    // Flag to enable support for the new language APIs
    // See https = //developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
    // Sets Java compatibility to Java 8
    // See https = //developer.android.com/studio/write/java8-support
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
}

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)

  api(Dependencies.hapiFhirStructuresR4)

  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Androidx.workRuntimeKtx)
  implementation(project(path = ":core"))

  testImplementation(Dependencies.junit)
}
