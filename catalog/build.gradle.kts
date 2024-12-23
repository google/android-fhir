plugins {
  id(Plugins.BuildPlugins.application)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.navSafeArgs)
}

configureRuler()

android {
  namespace = "com.google.android.fhir.catalog"
  compileSdk = Sdk.COMPILE_SDK
  defaultConfig {
    applicationId = Releases.Catalog.applicationId
    minSdk = Sdk.MIN_SDK
    targetSdk = Sdk.TARGET_SDK
    versionCode = Releases.Catalog.versionCode
    versionName = Releases.Catalog.versionName
    testInstrumentationRunner = Dependencies.androidJunitRunner
  }

  buildFeatures { viewBinding = true }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

dependencies {
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.test.ext.junit)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.core)
  implementation(libs.androidx.fragment)
  implementation(libs.androidx.navigation.fragment)
  implementation(libs.androidx.navigation.ui)
  implementation(libs.kotlin.stdlib)
  implementation(libs.material)

  implementation(project(path = ":datacapture"))
  implementation(project(path = ":engine"))
  implementation(project(path = ":contrib:barcode"))
  implementation(project(path = ":contrib:locationwidget"))
  implementation(project(path = ":contrib:timeselector"))

  testImplementation(libs.junit)
}
