import Dependencies.removeIncompatibleDependencies

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.dokka)
  `maven-publish`
  jacoco
}

publishArtifact(Releases.Common)

createJacocoTestReportTask()

android {
  namespace = "com.google.android.fhir.common"
  compileSdk = Sdk.COMPILE_SDK
  defaultConfig { minSdk = Sdk.MIN_SDK }
  configureJacocoTestOptions()
  kotlin { jvmToolchain(11) }
}

configurations { all { removeIncompatibleDependencies() } }

dependencies {
  api(libs.hapi.fhir.structures.r4)

  implementation(libs.fhir.ucum)

  testImplementation(libs.androidx.test.core)
  testImplementation(libs.junit)
  testImplementation(libs.kotlin.test.junit)
  testImplementation(libs.robolectric)
  testImplementation(libs.truth)

  constraints {
    Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
      api(libName, constraints)
    }
  }
}
