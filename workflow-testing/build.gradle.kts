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
  compileOptions {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }
}

configurations {
  all {
    removeIncompatibleDependencies()
    forceHapiVersion()
    forceJacksonVersion()
  }
}

dependencies {
  compileOnly(Dependencies.Cql.engine)
  compileOnly(Dependencies.Cql.evaluator)
  compileOnly(Dependencies.Cql.evaluatorBuilder)
  compileOnly(Dependencies.Cql.evaluatorDagger)
  compileOnly(Dependencies.Cql.evaluatorPlanDef)
  compileOnly(Dependencies.Cql.translatorCqlToElm)
  compileOnly(Dependencies.Cql.translatorElm)
  compileOnly(Dependencies.Cql.translatorModel)
  compileOnly(Dependencies.androidFhirEngine) { exclude(module = "truth") }

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
