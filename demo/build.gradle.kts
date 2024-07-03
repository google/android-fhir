plugins {
  id(Plugins.BuildPlugins.application)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.navSafeArgs)
}

configureRuler()

android {
  namespace = "com.google.android.fhir.demo"
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
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures {
    buildConfig = true
    viewBinding = true
  }
  compileOptions {
    // Flag to enable support for the new language APIs
    // See https://developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
  }

  packaging { resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt")) }
  kotlin { jvmToolchain(11) }
}

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.Espresso.espressoCore)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)
  implementation(Dependencies.Kotlin.kotlinCoroutinesCore)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.material)
  implementation(Dependencies.timber)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.fragment)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.lifecycle.runtime)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.navigation.fragment)
  implementation(libs.androidx.navigation.ui)
  implementation(libs.androidx.recyclerview)
  implementation(libs.androidx.work.runtime)
  implementation(project(":datacapture")) {
    exclude(group = Dependencies.androidFhirGroup, module = Dependencies.androidFhirEngineModule)
  }
  implementation(project(":engine"))

  testImplementation(Dependencies.junit)
}
