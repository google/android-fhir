plugins {
  id(Plugins.BuildPlugins.application)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinKapt)
  id(Plugins.BuildPlugins.navSafeArgs)
}

configureRuler()

android {
  compileSdk = Sdk.compileSdk
  defaultConfig {
    applicationId = Releases.Demo.applicationId
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    versionCode = Releases.Demo.versionCode
    versionName = Releases.Demo.versionName
    testInstrumentationRunner = Dependencies.androidJunitRunner
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures { viewBinding = true }
  compileOptions {
    // Flag to enable support for the new language APIs
    // See https://developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true

    sourceCompatibility = Java.sourceCompatibility
    targetCompatibility = Java.targetCompatibility
  }
  kotlinOptions { jvmTarget = Java.kotlinJvmTarget.toString() }
  packagingOptions {
    resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt"))
  }
}

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.Espresso.espressoCore)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Androidx.activity)
  implementation(Dependencies.Androidx.appCompat)
  implementation(Dependencies.Androidx.constraintLayout)
  implementation(Dependencies.Androidx.datastorePref)
  implementation(Dependencies.Androidx.fragmentKtx)
  implementation(Dependencies.Androidx.recyclerView)
  implementation(Dependencies.Androidx.workRuntimeKtx)
  implementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)
  implementation(Dependencies.Kotlin.kotlinCoroutinesCore)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Lifecycle.liveDataKtx)
  implementation(Dependencies.Lifecycle.runtime)
  implementation(Dependencies.Lifecycle.viewModelKtx)
  implementation(Dependencies.Navigation.navFragmentKtx)
  implementation(Dependencies.Navigation.navUiKtx)
  implementation(Dependencies.material)
  implementation(Dependencies.timber)
  implementation(project(":datacapture")) {
    exclude(group = Dependencies.androidFhirGroup, module = Dependencies.androidFhirEngineModule)
  }
  implementation(project(":engine"))

  testImplementation(Dependencies.junit)
}
