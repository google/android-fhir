/*
 * Copyright 2023-2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.kotlin.dsl.exclude

object Dependencies {

  object Androidx {
    const val activity = "androidx.activity:activity:${Versions.Androidx.activity}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.Androidx.appCompat}"
    const val camera = "androidx.camera:camera-camera2:${Versions.Androidx.camera}"
    const val cameraLifecycle =
      "androidx.camera:camera-lifecycle:${Versions.Androidx.cameraLifecycle}"
    const val constraintLayout =
      "androidx.constraintlayout:constraintlayout:${Versions.Androidx.constraintLayout}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.Androidx.coreKtx}"
    const val datastorePref =
      "androidx.datastore:datastore-preferences:${Versions.Androidx.datastorePref}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.Androidx.fragmentKtx}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.Androidx.recyclerView}"
    const val sqliteKtx = "androidx.sqlite:sqlite-ktx:${Versions.Androidx.sqliteKtx}"
    const val workRuntimeKtx = "androidx.work:work-runtime-ktx:${Versions.Androidx.workRuntimeKtx}"
  }

  object Cql {
    const val evaluator = "org.opencds.cqf.fhir:cqf-fhir-cr:${Versions.Cql.clinicalReasoning}"
    const val evaluatorFhirJackson =
      "org.opencds.cqf.fhir:cqf-fhir-jackson:${Versions.Cql.clinicalReasoning}"
    const val evaluatorFhirUtilities =
      "org.opencds.cqf.fhir:cqf-fhir-utility:${Versions.Cql.clinicalReasoning}"
  }

  object HapiFhir {
    const val fhirBaseModule = "ca.uhn.hapi.fhir:hapi-fhir-base"
    const val fhirClientModule = "ca.uhn.hapi.fhir:hapi-fhir-client"
    const val structuresDstu2Module = "ca.uhn.hapi.fhir:hapi-fhir-structures-dstu2"
    const val structuresDstu3Module = "ca.uhn.hapi.fhir:hapi-fhir-structures-dstu3"
    const val structuresR4Module = "ca.uhn.hapi.fhir:hapi-fhir-structures-r4"
    const val structuresR4bModule = "ca.uhn.hapi.fhir:hapi-fhir-structures-r4b"
    const val structuresR5Module = "ca.uhn.hapi.fhir:hapi-fhir-structures-r5"

    const val validationModule = "ca.uhn.hapi.fhir:hapi-fhir-validation"
    const val validationDstu3Module = "ca.uhn.hapi.fhir:hapi-fhir-validation-resources-dstu3"
    const val validationR4Module = "ca.uhn.hapi.fhir:hapi-fhir-validation-resources-r4"
    const val validationR5Module = "ca.uhn.hapi.fhir:hapi-fhir-validation-resources-r5"

    const val fhirCoreDstu2Module = "ca.uhn.hapi.fhir:org.hl7.fhir.dstu2"
    const val fhirCoreDstu2016Module = "ca.uhn.hapi.fhir:org.hl7.fhir.dstu2016may"
    const val fhirCoreDstu3Module = "ca.uhn.hapi.fhir:org.hl7.fhir.dstu3"
    const val fhirCoreR4Module = "ca.uhn.hapi.fhir:org.hl7.fhir.r4"
    const val fhirCoreR4bModule = "ca.uhn.hapi.fhir:org.hl7.fhir.r4b"
    const val fhirCoreR5Module = "ca.uhn.hapi.fhir:org.hl7.fhir.r5"
    const val fhirCoreUtilsModule = "ca.uhn.hapi.fhir:org.hl7.fhir.utilities"
    const val fhirCoreConvertorsModule = "ca.uhn.hapi.fhir:org.hl7.fhir.convertors"

    const val guavaCachingModule = "ca.uhn.hapi.fhir:hapi-fhir-caching-guava"

    const val fhirBase = "$fhirBaseModule:${Versions.hapiFhir}"
    const val fhirClient = "$fhirClientModule:${Versions.hapiFhir}"
    const val structuresDstu2 = "$structuresDstu2Module:${Versions.hapiFhir}"
    const val structuresDstu3 = "$structuresDstu3Module:${Versions.hapiFhir}"
    const val structuresR4 = "$structuresR4Module:${Versions.hapiFhir}"
    const val structuresR4b = "$structuresR4bModule:${Versions.hapiFhir}"
    const val structuresR5 = "$structuresR5Module:${Versions.hapiFhir}"

    const val validation = "$validationModule:${Versions.hapiFhir}"
    const val validationDstu3 = "$validationDstu3Module:${Versions.hapiFhir}"
    const val validationR4 = "$validationR4Module:${Versions.hapiFhir}"
    const val validationR5 = "$validationR5Module:${Versions.hapiFhir}"

    const val fhirCoreDstu2 = "$fhirCoreDstu2Module:${Versions.hapiFhirCore}"
    const val fhirCoreDstu2016 = "$fhirCoreDstu2016Module:${Versions.hapiFhirCore}"
    const val fhirCoreDstu3 = "$fhirCoreDstu3Module:${Versions.hapiFhirCore}"
    const val fhirCoreR4 = "$fhirCoreR4Module:${Versions.hapiFhirCore}"
    const val fhirCoreR4b = "$fhirCoreR4bModule:${Versions.hapiFhirCore}"
    const val fhirCoreR5 = "$fhirCoreR5Module:${Versions.hapiFhirCore}"
    const val fhirCoreUtils = "$fhirCoreUtilsModule:${Versions.hapiFhirCore}"
    const val fhirCoreConvertors = "$fhirCoreConvertorsModule:${Versions.hapiFhirCore}"

    const val guavaCaching = "$guavaCachingModule:${Versions.hapiFhir}"
  }

  object Jackson {
    private const val mainGroup = "com.fasterxml.jackson"
    private const val coreGroup = "$mainGroup.core"
    private const val dataformatGroup = "$mainGroup.dataformat"
    private const val datatypeGroup = "$mainGroup.datatype"
    private const val moduleGroup = "$mainGroup.module"

    const val annotationsBase = "$coreGroup:jackson-annotations:${Versions.jackson}"
    const val bomBase = "$mainGroup:jackson-bom:${Versions.jackson}"
    const val coreBase = "$coreGroup:jackson-core:${Versions.jacksonCore}"
    const val databindBase = "$coreGroup:jackson-databind:${Versions.jackson}"
    const val dataformatXmlBase = "$dataformatGroup:jackson-dataformat-xml:${Versions.jackson}"
    const val jaxbAnnotationsBase =
      "$moduleGroup:jackson-module-jaxb-annotations:${Versions.jackson}"
    const val jsr310Base = "$datatypeGroup:jackson-datatype-jsr310:${Versions.jackson}"
  }

  object Kotlin {
    const val kotlinCoroutinesAndroid =
      "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val kotlinCoroutinesCore =
      "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val kotlinTestJunit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.Kotlin.stdlib}"
    const val kotlinCoroutinesTest =
      "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val kotlinCoroutinesPlay =
      "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Kotlin.stdlib}"
  }

  object Lifecycle {
    const val liveDataKtx =
      "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.Androidx.lifecycle}"
    const val runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.Androidx.lifecycle}"
    const val viewModelKtx =
      "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Androidx.lifecycle}"
  }

  object Navigation {
    const val navFragmentKtx =
      "androidx.navigation:navigation-fragment-ktx:${Versions.Androidx.navigation}"
    const val navUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.Androidx.navigation}"
  }

  object Retrofit {
    const val coreRetrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
  }

  object Room {
    const val compiler = "androidx.room:room-compiler:${Versions.Androidx.room}"
    const val ktx = "androidx.room:room-ktx:${Versions.Androidx.room}"
    const val runtime = "androidx.room:room-runtime:${Versions.Androidx.room}"
    const val testing = "androidx.room:room-testing:${Versions.Androidx.room}"
  }

  object Mlkit {
    const val barcodeScanning =
      "com.google.mlkit:barcode-scanning:${Versions.Mlkit.barcodeScanning}"
    const val objectDetection =
      "com.google.mlkit:object-detection:${Versions.Mlkit.objectDetection}"
    const val objectDetectionCustom =
      "com.google.mlkit:object-detection-custom:${Versions.Mlkit.objectDetectionCustom}"
  }

  const val playServicesLocation =
    "com.google.android.gms:play-services-location:${Versions.playServicesLocation}"

  const val androidFhirGroup = "com.google.android.fhir"
  const val androidFhirEngineModule = "engine"
  const val androidFhirKnowledgeModule = "knowledge"
  const val androidFhirCommon = "$androidFhirGroup:common:${Versions.androidFhirCommon}"
  const val androidFhirEngine =
    "$androidFhirGroup:$androidFhirEngineModule:${Versions.androidFhirEngine}"
  const val androidFhirKnowledge = "$androidFhirGroup:knowledge:${Versions.androidFhirKnowledge}"

  const val apacheCommonsCompress =
    "org.apache.commons:commons-compress:${Versions.apacheCommonsCompress}"

  const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.desugarJdkLibs}"
  const val fhirUcum = "org.fhir:ucum:${Versions.fhirUcum}"
  const val gson = "com.google.code.gson:gson:${Versions.gson}"

  const val guavaModule = "com.google.guava:guava"
  const val guava = "$guavaModule:${Versions.guava}"

  const val httpInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.http}"
  const val http = "com.squareup.okhttp3:okhttp:${Versions.http}"
  const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.http}"

  const val jsonToolsPatch = "com.github.java-json-tools:json-patch:${Versions.jsonToolsPatch}"
  const val material = "com.google.android.material:material:${Versions.material}"
  const val sqlcipher = "net.zetetic:android-database-sqlcipher:${Versions.sqlcipher}"
  const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
  const val woodstox = "com.fasterxml.woodstox:woodstox-core:${Versions.woodstox}"
  const val xerces = "xerces:xercesImpl:${Versions.xerces}"

  const val zxing = "com.google.zxing:core:${Versions.zxing}"
  const val nimbus = "com.nimbusds:nimbus-jose-jwt:${Versions.nimbus}"

  // Dependencies for testing go here
  object AndroidxTest {
    const val archCore = "androidx.arch.core:core-testing:${Versions.AndroidxTest.archCore}"
    const val benchmarkJunit =
      "androidx.benchmark:benchmark-junit4:${Versions.AndroidxTest.benchmarkJUnit}"
    const val core = "androidx.test:core:${Versions.AndroidxTest.core}"
    const val extJunit = "androidx.test.ext:junit:${Versions.AndroidxTest.extJunit}"
    const val extJunitKtx = "androidx.test.ext:junit-ktx:${Versions.AndroidxTest.extJunit}"
    const val fragmentTesting =
      "androidx.fragment:fragment-testing:${Versions.AndroidxTest.fragmentVersion}"
    const val rules = "androidx.test:rules:${Versions.AndroidxTest.rules}"
    const val runner = "androidx.test:runner:${Versions.AndroidxTest.runner}"
    const val workTestingRuntimeKtx =
      "androidx.work:work-testing:${Versions.Androidx.workRuntimeKtx}"
  }

  object Espresso {
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
  }

  const val androidBenchmarkRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
  const val androidJunitRunner = "androidx.test.runner.AndroidJUnitRunner"

  // Makes Json assertions where the order of elements, tabs/whitespaces are not important.
  const val jsonAssert = "org.skyscreamer:jsonassert:${Versions.jsonAssert}"
  const val junit = "junit:junit:${Versions.junit}"
  const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}"
  const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoInline}"
  const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
  const val truth = "com.google.truth:truth:${Versions.truth}"

  // Makes XML assertions where the order of elements, tabs/whitespaces are not important.
  const val xmlUnit = "org.xmlunit:xmlunit-core:${Versions.xmlUnit}"

  object Versions {
    object Androidx {
      const val activity = "1.7.2"
      const val appCompat = "1.6.1"
      const val camera = "1.3.1"
      const val cameraLifecycle = "1.3.1"
      const val constraintLayout = "2.1.4"
      const val coreKtx = "1.10.1"
      const val datastorePref = "1.0.0"
      const val fragmentKtx = "1.6.0"
      const val lifecycle = "2.6.1"
      const val navigation = "2.6.0"
      const val recyclerView = "1.3.0"
      const val room = "2.5.2"
      const val sqliteKtx = "2.3.1"
      const val workRuntimeKtx = "2.8.1"
    }

    object Cql {
      const val clinicalReasoning = "3.0.0-PRE9-SNAPSHOT"
    }

    object Kotlin {
      const val kotlinCoroutinesCore = "1.7.2"
      const val stdlib = "1.8.20"
    }

    const val androidFhirCommon = "0.1.0-alpha05"
    const val androidFhirEngine = "0.1.0-beta05"
    const val androidFhirKnowledge = "0.1.0-alpha03"
    const val apacheCommonsCompress = "1.21"
    const val desugarJdkLibs = "2.0.3"
    const val caffeine = "2.9.1"
    const val fhirUcum = "1.0.3"
    const val gson = "2.9.1"
    const val guava = "32.1.3-android"

    const val hapiFhir = "6.8.0"
    const val hapiFhirCore = "6.0.22"

    const val http = "4.11.0"

    // Maximum Jackson libraries (excluding core) version that supports Android API Level 24:
    // https://github.com/FasterXML/jackson-databind/issues/3658
    const val jackson = "2.13.5"

    // Maximum Jackson Core library version that supports Android API Level 24:
    const val jacksonCore = "2.15.2"

    const val jsonToolsPatch = "1.13"
    const val jsonAssert = "1.5.1"
    const val material = "1.9.0"
    const val retrofit = "2.9.0"
    const val gsonConverter = "2.1.0"
    const val sqlcipher = "4.5.4"
    const val timber = "5.0.1"
    const val truth = "1.1.5"
    const val woodstox = "6.5.1"
    const val xerces = "2.12.2"
    const val xmlUnit = "2.9.1"

    const val zxing = "3.4.1"
    const val nimbus = "9.31"
    const val vision = "20.1.3"

    // Test dependencies
    object AndroidxTest {
      const val benchmarkJUnit = "1.1.1"
      const val core = "1.5.0"
      const val archCore = "2.2.0"
      const val extJunit = "1.1.5"
      const val rules = "1.5.0"
      const val runner = "1.5.0"
      const val fragmentVersion = "1.6.0"
    }

    const val espresso = "3.5.1"
    const val jacoco = "0.8.10"
    const val junit = "4.13.2"
    const val mockitoKotlin = "3.2.0"
    const val mockitoInline = "4.0.0"
    const val robolectric = "4.10.3"

    object Mlkit {
      const val barcodeScanning = "16.1.1"
      const val objectDetection = "16.2.3"
      const val objectDetectionCustom = "16.3.1"
    }

    const val playServicesLocation = "21.0.1"
  }

  fun Configuration.removeIncompatibleDependencies() {
    exclude(module = "xpp3")
    exclude(module = "xpp3_min")
    exclude(module = "xmlpull")
    exclude(module = "javax.json")
    exclude(module = "jcl-over-slf4j")
    exclude(group = "org.apache.httpcomponents")
    exclude(group = "org.antlr", module = "antlr4")
    exclude(group = "org.eclipse.persistence", module = "org.eclipse.persistence.moxy")
    exclude(module = "hapi-fhir-caching-caffeine")
    exclude(group = "com.github.ben-manes.caffeine", module = "caffeine")
  }

  fun hapiFhirConstraints(): Map<String, DependencyConstraint.() -> Unit> {
    return mutableMapOf<String, DependencyConstraint.() -> Unit>(
      guavaModule to { version { strictly(Versions.guava) } },
      HapiFhir.fhirBaseModule to { version { strictly(Versions.hapiFhir) } },
      HapiFhir.fhirClientModule to { version { strictly(Versions.hapiFhir) } },
      HapiFhir.fhirCoreConvertorsModule to { version { strictly(Versions.hapiFhirCore) } },
      HapiFhir.fhirCoreDstu2Module to { version { strictly(Versions.hapiFhirCore) } },
      HapiFhir.fhirCoreDstu2016Module to { version { strictly(Versions.hapiFhirCore) } },
      HapiFhir.fhirCoreDstu3Module to { version { strictly(Versions.hapiFhirCore) } },
      HapiFhir.fhirCoreR4Module to { version { strictly(Versions.hapiFhirCore) } },
      HapiFhir.fhirCoreR4bModule to { version { strictly(Versions.hapiFhirCore) } },
      HapiFhir.fhirCoreR5Module to { version { strictly(Versions.hapiFhirCore) } },
      HapiFhir.fhirCoreUtilsModule to { version { strictly(Versions.hapiFhirCore) } },
      HapiFhir.structuresDstu2Module to { version { strictly(Versions.hapiFhir) } },
      HapiFhir.structuresDstu3Module to { version { strictly(Versions.hapiFhir) } },
      HapiFhir.structuresR4Module to { version { strictly(Versions.hapiFhir) } },
      HapiFhir.structuresR5Module to { version { strictly(Versions.hapiFhir) } },
      HapiFhir.validationModule to { version { strictly(Versions.hapiFhir) } },
      HapiFhir.validationDstu3Module to { version { strictly(Versions.hapiFhir) } },
      HapiFhir.validationR4Module to { version { strictly(Versions.hapiFhir) } },
      HapiFhir.validationR5Module to { version { strictly(Versions.hapiFhir) } },
      Jackson.annotationsBase to { version { strictly(Versions.jackson) } },
      Jackson.bomBase to { version { strictly(Versions.jackson) } },
      Jackson.coreBase to { version { strictly(Versions.jacksonCore) } },
      Jackson.databindBase to { version { strictly(Versions.jackson) } },
      Jackson.jaxbAnnotationsBase to { version { strictly(Versions.jackson) } },
      Jackson.jsr310Base to { version { strictly(Versions.jackson) } },
      Jackson.dataformatXmlBase to { version { strictly(Versions.jackson) } },
    )
  }
}
