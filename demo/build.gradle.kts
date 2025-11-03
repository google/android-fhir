plugins {
  id(Plugins.BuildPlugins.application)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.navSafeArgs)
}

configureRuler()

android {
  namespace = "com.google.android.fhir.demo"
  compileSdk = Sdk.COMPILE_SDK
  defaultConfig {
    applicationId = Releases.Demo.applicationId
    minSdk = Sdk.MIN_SDK
    targetSdk = Sdk.TARGET_SDK
    versionCode = Releases.Demo.versionCode
    versionName = Releases.Demo.versionName
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.test.ext.junit)

  coreLibraryDesugaring(libs.desugar.jdk.libs)

  implementation(libs.timber)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.fragment)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.navigation.fragment)
  implementation(libs.androidx.navigation.ui)
  implementation(libs.androidx.recyclerview)
  implementation(libs.androidx.work.runtime)
  implementation(libs.kotlin.stdlib)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.material)
  implementation(project(":datacapture")) {
    exclude(group = "com.google.android.fhir", module = "engine")
  }
  implementation(project(":engine"))

  testImplementation(libs.junit)
}
