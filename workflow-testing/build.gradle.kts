import Dependencies.removeIncompatibleDependencies

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.google.android.fhir.workflow.testing"
  compileSdk = Sdk.COMPILE_SDK
  defaultConfig { minSdk = Sdk.MIN_SDK }
  kotlin { jvmToolchain(11) }
}

configurations { all { removeIncompatibleDependencies() } }

dependencies {
  compileOnly(libs.json.assert)
  compileOnly(libs.junit)
  compileOnly(libs.opencds.cqf.fhir.cr)
  compileOnly(libs.opencds.cqf.fhir.jackson)
  compileOnly(libs.opencds.cqf.fhir.utility)
  compileOnly(libs.truth)
  compileOnly(libs.woodstox)
  compileOnly(libs.xml.unit)
  compileOnly(project(":engine")) { exclude(module = "truth") }

  constraints {
    Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
      compileOnly(libName, constraints)
    }
  }
}
