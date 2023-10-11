import Dependencies.removeIncompatibleDependencies

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.benchmark)
}

android {
  namespace = "com.google.android.fhir.workflow.benchmark"
  compileSdk = Sdk.compileSdk
  defaultConfig {
    minSdk = Sdk.minSdk
    testInstrumentationRunner = Dependencies.androidBenchmarkRunner
  }

  testBuildType = "release"
  buildTypes { release {} }
  packaging {
    resources.excludes.addAll(
      listOf(
        "license.html",
        "META-INF/ASL2.0",
        "META-INF/ASL-2.0.txt",
        "META-INF/DEPENDENCIES",
        "META-INF/LGPL-3.0.txt",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/license.txt",
        "META-INF/license.html",
        "META-INF/LICENSE.md",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt",
        "META-INF/NOTICE.md",
        "META-INF/notice.txt",
        "META-INF/LGPL-3.0.txt",
        "META-INF/sun-jaxb.episode",
        "META-INF/*.kotlin_module",
        "readme.html",
      ),
    )
  }
  kotlin { jvmToolchain(11) }
}

afterEvaluate { configureFirebaseTestLabForMicroBenchmark() }

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
  androidTestImplementation(Dependencies.AndroidxTest.benchmarkJunit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.Cql.engineJackson)
  androidTestImplementation(Dependencies.Cql.evaluator)
  androidTestImplementation(Dependencies.Cql.evaluatorBuilder)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)
  androidTestImplementation(Dependencies.truth)
  androidTestImplementation(Dependencies.Androidx.workRuntimeKtx)
  androidTestImplementation(Dependencies.AndroidxTest.workTestingRuntimeKtx)
  androidTestImplementation(Dependencies.androidFhirEngine) { exclude(module = "truth") }
  androidTestImplementation(project(":knowledge")) {
    exclude(group = Dependencies.androidFhirGroup, module = Dependencies.androidFhirEngineModule)
  }
  androidTestImplementation(project(":workflow")) {
    exclude(group = Dependencies.androidFhirGroup, module = Dependencies.androidFhirEngineModule)
    exclude(group = Dependencies.androidFhirGroup, module = Dependencies.androidFhirKnowledgeModule)
  }
  androidTestImplementation(project(":workflow-testing"))
}
