import Dependencies.removeIncompatibleDependencies

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
}

android {
  namespace = "com.google.android.fhir.workflow.testing"
  compileSdk = Sdk.COMPILE_SDK
  defaultConfig { minSdk = Sdk.MIN_SDK }
  kotlin { jvmToolchain(11) }
}

configurations { all { removeIncompatibleDependencies() } }

dependencies {
  compileOnly(libs.opencds.cqf.fhir.cr)
  compileOnly(libs.opencds.cqf.fhir.jackson)
  compileOnly(libs.opencds.cqf.fhir.utility)
  compileOnly(project(":engine")) { exclude(module = "truth") }

  compileOnly(Dependencies.jsonAssert)
  compileOnly(Dependencies.woodstox)
  compileOnly(Dependencies.xmlUnit)
  compileOnly(libs.junit)
  compileOnly(libs.truth)

  constraints {
    Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
      compileOnly(libName, constraints)
    }
  }
}
