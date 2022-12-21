plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
}

android {
  compileSdk = Sdk.compileSdk

  defaultConfig {
    minSdk = Sdk.minSdkWorkflow
    targetSdk = Sdk.targetSdk
  }
  compileOptions {
    sourceCompatibility = Java.sourceCompatibility
    targetCompatibility = Java.targetCompatibility
  }

  kotlinOptions { jvmTarget = Java.kotlinJvmTarget.toString() }
}

configurations {
  all {
    exclude(module = "xpp3")
    exclude(module = "xpp3_min")
    exclude(module = "xmlpull")
    exclude(module = "javax.json")
    exclude(module = "jcl-over-slf4j")
    exclude(group = "org.apache.httpcomponents")

    // Removes newer versions of caffeine and manually imports 2.9
    // Removes newer versions of hapi and keeps on 6.0.1
    // (newer versions don't work on Android)
    resolutionStrategy {
      force(Dependencies.HapiFhir.caffeine)
      force(Dependencies.HapiFhir.fhirBase)
      force(Dependencies.HapiFhir.fhirClient)
      force(Dependencies.HapiFhir.structuresDstu2)
      force(Dependencies.HapiFhir.structuresDstu3)
      force(Dependencies.HapiFhir.structuresR4)
      force(Dependencies.HapiFhir.structuresR5)
      force(Dependencies.HapiFhir.validation)
      force(Dependencies.HapiFhir.validationDstu3)
      force(Dependencies.HapiFhir.validationR4)
      force(Dependencies.HapiFhir.validationR5)

      force(Dependencies.HapiFhir.fhirCoreDstu2)
      force(Dependencies.HapiFhir.fhirCoreDstu2016)
      force(Dependencies.HapiFhir.fhirCoreDstu3)
      force(Dependencies.HapiFhir.fhirCoreR4)
      force(Dependencies.HapiFhir.fhirCoreR4b)
      force(Dependencies.HapiFhir.fhirCoreR5)
      force(Dependencies.HapiFhir.fhirCoreUtils)
      force(Dependencies.HapiFhir.fhirCoreConvertors)
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
