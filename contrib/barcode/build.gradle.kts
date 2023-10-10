plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
}

publishArtifact(Releases.Contrib.Barcode)

createJacocoTestReportTask()

android {
  namespace = "com.google.android.fhir.datacapture.contrib.views.barcode"
  compileSdk = Sdk.compileSdk
  defaultConfig {
    minSdk = Sdk.minSdk
    testInstrumentationRunner = Dependencies.androidJunitRunner
    // Need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir.datacapture"
  }

  buildFeatures { viewBinding = true }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }
  packaging {
    resources.excludes.addAll(
      listOf(
        "META-INF/INDEX.LIST",
        "META-INF/ASL2.0",
        "META-INF/ASL-2.0.txt",
        "META-INF/LGPL-3.0.txt",
      ),
    )
  }

  configureJacocoTestOptions()

  testOptions { animationsDisabled = true }
  kotlin { jvmToolchain(11) }
}

configurations { all { exclude(module = "xpp3") } }

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.core)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.AndroidxTest.fragmentTesting)
  androidTestImplementation(Dependencies.AndroidxTest.rules)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.mockitoInline)
  androidTestImplementation(Dependencies.truth)

  implementation(project(":datacapture"))
  implementation(Dependencies.Androidx.coreKtx)
  implementation(Dependencies.Androidx.fragmentKtx)
  implementation(Dependencies.Mlkit.barcodeScanning)
  implementation(Dependencies.Mlkit.objectDetection)
  implementation(Dependencies.Mlkit.objectDetectionCustom)
  implementation(Dependencies.material)
  implementation(Dependencies.timber)
  implementation(Dependencies.Androidx.appCompat)

  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.AndroidxTest.fragmentTesting)
  testImplementation(Dependencies.Kotlin.kotlinTestJunit)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.mockitoInline)
  testImplementation(Dependencies.mockitoKotlin)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
}
