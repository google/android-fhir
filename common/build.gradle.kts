import Dependencies.removeIncompatibleDependencies

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
}

publishArtifact(Releases.Common)

createJacocoTestReportTask()

android {
  namespace = "com.google.android.fhir.common"
  compileSdk = Sdk.compileSdk
  defaultConfig { minSdk = Sdk.minSdk }
  configureJacocoTestOptions()
  kotlin { jvmToolchain(11) }
}

configurations { all { removeIncompatibleDependencies() } }

dependencies {
  api(Dependencies.HapiFhir.structuresR4)

  implementation(Dependencies.fhirUcum)

  testImplementation(Dependencies.Kotlin.kotlinTestJunit)
  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)

  constraints {
    Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
      api(libName, constraints)
    }
  }
}
