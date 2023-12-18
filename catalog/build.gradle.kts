import Dependencies.forceGuava

plugins {
  id(Plugins.BuildPlugins.application)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.navSafeArgs)
}

configureRuler()

android {
  namespace = "com.google.android.fhir.catalog"
  compileSdk = Sdk.compileSdk
  defaultConfig {
    applicationId = Releases.Catalog.applicationId
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    versionCode = Releases.Catalog.versionCode
    versionName = Releases.Catalog.versionName
    testInstrumentationRunner = Dependencies.androidJunitRunner
  }

  buildFeatures { viewBinding = true }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }
  compileOptions {
    // Flag to enable support for the new language APIs
    // See https://developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
  }

  packaging {
    resources.excludes.addAll(
      listOf("META-INF/ASL2.0", "META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt"),
    )
  }
  kotlin { jvmToolchain(11) }
}

configurations { all { forceGuava() } }

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.Espresso.espressoCore)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Androidx.appCompat)
  implementation(Dependencies.Androidx.constraintLayout)
  implementation(Dependencies.Androidx.coreKtx)
  implementation(Dependencies.Androidx.fragmentKtx)
  implementation(Dependencies.material)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Navigation.navFragmentKtx)
  implementation(Dependencies.Navigation.navUiKtx)

  implementation(project(path = ":datacapture"))
  implementation(project(path = ":engine"))
  implementation(project(path = ":contrib:barcode"))

  testImplementation(Dependencies.junit)
}
