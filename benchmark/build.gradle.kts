plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinKapt)
  id(Plugins.BuildPlugins.benchmark)
  id(Plugins.BuildPlugins.jetbrainsKotlinAndroid)
}

android {
  compileSdk = Sdk.compileSdk

  compileOptions {
    sourceCompatibility = Java.sourceCompatibility
    targetCompatibility = Java.targetCompatibility
  }

  kotlinOptions { jvmTarget = Java.kotlinJvmTarget.toString() }

  defaultConfig {
    minSdk = Sdk.minSdkWorkflow
    targetSdk = Sdk.targetSdk

    testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    // Runs only once
    testInstrumentationRunnerArguments["androidx.benchmark.dryRunMode.enable"] = "true"
    // Includes Startup time
    testInstrumentationRunnerArguments["androidx.benchmark.startupMode.enable"] = "true"
  }

  testBuildType = "release"
  buildTypes {
    debug {
      // Since isDebuggable can't be modified by gradle for library modules,
      // it must be done in a manifest - see src/androidTest/AndroidManifest.xml
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "benchmark-proguard-rules.pro"
      )
    }
  }
  packagingOptions {
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
      )
    )
  }
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
  androidTestImplementation(Dependencies.AndroidxTest.benchmarkJunit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.Cql.engineJackson)
  androidTestImplementation(Dependencies.Cql.evaluator)
  androidTestImplementation(Dependencies.Cql.evaluatorBuilder)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)
  androidTestImplementation(Dependencies.truth)

  androidTestImplementation(project(":engine"))
  androidTestImplementation(project(":workflow"))
  androidTestImplementation(project(":workflow-testing"))
}
