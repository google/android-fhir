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
    exclude(
      module = "hapi-fhir-structures-r4b",
    )
    resolutionStrategy {
      force(Dependencies.guava)
      force("ca.uhn.hapi.fhir:hapi-fhir-base:6.0.1")
      force("ca.uhn.hapi.fhir:hapi-fhir-client:6.0.1")
      force("ca.uhn.hapi.fhir:org.hl7.fhir.convertors:5.6.36")

      force("ca.uhn.hapi.fhir:hapi-fhir-structures-dstu2:6.0.1")
      force("ca.uhn.hapi.fhir:org.hl7.fhir.dstu2016may:5.6.36")
      force("ca.uhn.hapi.fhir:hapi-fhir-structures-dstu3:6.0.1")
      force("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:6.0.1")
      force("ca.uhn.hapi.fhir:hapi-fhir-structures-r5:6.0.1")
      force("ca.uhn.hapi.fhir:org.hl7.fhir.utilities:5.6.36")

      force("ca.uhn.hapi.fhir:org.hl7.fhir.dstu2:5.6.36")
      force("ca.uhn.hapi.fhir:org.hl7.fhir.dstu3:5.6.36")
      force("ca.uhn.hapi.fhir:org.hl7.fhir.r4:5.6.36")
      force("ca.uhn.hapi.fhir:org.hl7.fhir.r4b:5.6.36")
      force("ca.uhn.hapi.fhir:org.hl7.fhir.r5:5.6.36")

      force("ca.uhn.hapi.fhir:hapi-fhir-validation:6.0.1")
      force("ca.uhn.hapi.fhir:hapi-fhir-validation-resources-dstu3:6.0.1")
      force("ca.uhn.hapi.fhir:hapi-fhir-validation-resources-r4:6.0.1")
      force("ca.uhn.hapi.fhir:hapi-fhir-validation-resources-r5:6.0.1")
    }
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
