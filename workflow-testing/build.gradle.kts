import Dependencies.forceGuava
import Dependencies.forceHapiVersion
import Dependencies.forceJacksonVersion
import Dependencies.removeIncompatibleDependencies

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
}

android {
  namespace = "com.google.android.fhir.workflow.testing"
  compileSdk = Sdk.compileSdk
  defaultConfig { minSdk = Sdk.minSdk }
  kotlin { jvmToolchain(11) }
}

configurations {
  all {
    removeIncompatibleDependencies()
    forceGuava()
    forceHapiVersion()
    forceJacksonVersion()
  }
}

dependencies {
  compileOnly(Dependencies.Cql.evaluator)
  compileOnly(Dependencies.Cql.evaluatorFhirJackson)
  compileOnly(Dependencies.Cql.evaluatorFhirUtilities)
  compileOnly(project(":engine")) { exclude(module = "truth") }

  // Forces the most recent version of jackson, ignoring what dependencies use.
  // Remove these lines when HAPI 6.4 becomes available.
  compileOnly(Dependencies.Jackson.annotations)
  compileOnly(Dependencies.Jackson.bom)
  compileOnly(Dependencies.Jackson.core)
  compileOnly(Dependencies.Jackson.databind)
  compileOnly(Dependencies.Jackson.dataformatXml)
  compileOnly(Dependencies.Jackson.jaxbAnnotations)
  compileOnly(Dependencies.Jackson.jsr310)

  compileOnly(Dependencies.junit)
  compileOnly(Dependencies.jsonAssert)
  compileOnly(Dependencies.woodstox)
  compileOnly(Dependencies.xmlUnit)
  compileOnly(Dependencies.truth)
}
