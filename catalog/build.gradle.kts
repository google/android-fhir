plugins {
  id(Plugins.BuildPlugins.application)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.navSafeArgs)
}

configureRuler()

android {
  compileSdk = Sdk.compileSdk

  defaultConfig {
    applicationId = Releases.Catalog.applicationId
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    versionCode = Releases.Catalog.versionCode
    versionName = Releases.Catalog.versionName

    testInstrumentationRunner = Dependencies.androidJunitRunner
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
    sourceCompatibility = Java.sourceCompatibility
    targetCompatibility = Java.targetCompatibility
  }
  kotlinOptions { jvmTarget = Java.kotlinJvmTarget.toString() }
}

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.Espresso.espressoCore)

  implementation(Dependencies.Androidx.appCompat)
  implementation(Dependencies.Androidx.constraintLayout)
  implementation(Dependencies.Androidx.coreKtx)
  implementation(Dependencies.Androidx.fragmentKtx)
  implementation(Dependencies.material)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Navigation.navFragmentKtx)
  implementation(Dependencies.Navigation.navUiKtx)

  implementation(project(path = ":datacapture"))
  implementation(project(path = ":contrib:barcode"))

  testImplementation(Dependencies.junit)
}
