/*
 * Copyright 2023 Google LLC
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
import org.gradle.kotlin.dsl.exclude

/*
 * Copyright 2022 Google LLC
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

object Dependencies {

  object Androidx {
    const val activity = "androidx.activity:activity:${Versions.Androidx.activity}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.Androidx.appCompat}"
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
    const val openCdsGroup = "org.opencds.cqf.cql"
    const val translatorGroup = "info.cqframework"

    const val engine = "$openCdsGroup:engine:${Versions.Cql.engine}"
    const val engineJackson = "$openCdsGroup:engine.jackson:${Versions.Cql.engine}"

    const val evaluator = "$openCdsGroup:evaluator:${Versions.Cql.evaluator}"
    const val evaluatorBuilder = "$openCdsGroup:evaluator.builder:${Versions.Cql.evaluator}"
    const val evaluatorDagger = "$openCdsGroup:evaluator.dagger:${Versions.Cql.evaluator}"
    const val evaluatorPlanDef = "$openCdsGroup:evaluator.plandefinition:${Versions.Cql.evaluator}"
    const val translatorCqlToElm = "$translatorGroup:cql-to-elm:${Versions.Cql.translator}"
    const val translatorElm = "$translatorGroup:elm:${Versions.Cql.translator}"
    const val translatorModel = "$translatorGroup:model:${Versions.Cql.translator}"

    const val translatorElmJackson = "$translatorGroup:elm-jackson:${Versions.Cql.translator}"
    const val translatorModelJackson = "$translatorGroup:model-jackson:${Versions.Cql.translator}"
  }

  object Glide {
    const val glide = "com.github.bumptech.glide:glide:${Versions.Glide.glide}"
  }

  object HapiFhir {
    const val fhirBase = "ca.uhn.hapi.fhir:hapi-fhir-base:${Versions.hapiFhir}"
    const val fhirClient = "ca.uhn.hapi.fhir:hapi-fhir-client:${Versions.hapiFhir}"
    const val structuresDstu2 = "ca.uhn.hapi.fhir:hapi-fhir-structures-dstu2:${Versions.hapiFhir}"
    const val structuresDstu3 = "ca.uhn.hapi.fhir:hapi-fhir-structures-dstu3:${Versions.hapiFhir}"
    const val structuresR4 = "ca.uhn.hapi.fhir:hapi-fhir-structures-r4:${Versions.hapiFhir}"
    const val structuresR4b = "ca.uhn.hapi.fhir:hapi-fhir-structures-r4b:${Versions.hapiFhir}"
    const val structuresR5 = "ca.uhn.hapi.fhir:hapi-fhir-structures-r5:${Versions.hapiFhir}"

    const val validation = "ca.uhn.hapi.fhir:hapi-fhir-validation:${Versions.hapiFhir}"
    const val validationDstu3 =
      "ca.uhn.hapi.fhir:hapi-fhir-validation-resources-dstu3:${Versions.hapiFhir}"
    const val validationR4 =
      "ca.uhn.hapi.fhir:hapi-fhir-validation-resources-r4:${Versions.hapiFhir}"
    const val validationR5 =
      "ca.uhn.hapi.fhir:hapi-fhir-validation-resources-r5:${Versions.hapiFhir}"

    const val fhirCoreDstu2 = "ca.uhn.hapi.fhir:org.hl7.fhir.dstu2:${Versions.hapiFhirCore}"
    const val fhirCoreDstu2016 =
      "ca.uhn.hapi.fhir:org.hl7.fhir.dstu2016may:${Versions.hapiFhirCore}"
    const val fhirCoreDstu3 = "ca.uhn.hapi.fhir:org.hl7.fhir.dstu3:${Versions.hapiFhirCore}"
    const val fhirCoreR4 = "ca.uhn.hapi.fhir:org.hl7.fhir.r4:${Versions.hapiFhirCore}"
    const val fhirCoreR4b = "ca.uhn.hapi.fhir:org.hl7.fhir.r4b:${Versions.hapiFhirCore}"
    const val fhirCoreR5 = "ca.uhn.hapi.fhir:org.hl7.fhir.r5:${Versions.hapiFhirCore}"
    const val fhirCoreUtils = "ca.uhn.hapi.fhir:org.hl7.fhir.utilities:${Versions.hapiFhirCore}"
    const val fhirCoreConvertors =
      "ca.uhn.hapi.fhir:org.hl7.fhir.convertors:${Versions.hapiFhirCore}"

    // Runtime dependency that is required to run FhirPath (also requires minSDK of 26).
    // Version 3.0 uses java.lang.System.Logger, which is not available on Android
    // Replace for Guava when this PR gets merged: https://github.com/hapifhir/hapi-fhir/pull/3977
    const val caffeine = "com.github.ben-manes.caffeine:caffeine:${Versions.caffeine}"
  }

  object Jackson {
    const val mainGroup = "com.fasterxml.jackson"
    const val coreGroup = "$mainGroup.core"
    const val dataformatGroup = "$mainGroup.dataformat"
    const val datatypeGroup = "$mainGroup.datatype"
    const val moduleGroup = "$mainGroup.module"

    const val annotations = "$coreGroup:jackson-annotations:${Versions.jackson}"
    const val bom = "$mainGroup:jackson-bom:${Versions.jackson}"
    const val core = "$coreGroup:jackson-core:${Versions.jackson}"
    const val databind = "$coreGroup:jackson-databind:${Versions.jackson}"
    const val dataformatXml = "$dataformatGroup:jackson-dataformat-xml:${Versions.jackson}"
    const val jaxbAnnotations = "$moduleGroup:jackson-module-jaxb-annotations:${Versions.jackson}"
    const val jsr310 = "$datatypeGroup:jackson-datatype-jsr310:${Versions.jackson}"
  }

  object Kotlin {
    const val kotlinCoroutinesAndroid =
      "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val kotlinCoroutinesCore =
      "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val kotlinTestJunit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.Kotlin.stdlib}"
    const val kotlinCoroutinesTest =
      "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.kotlinCoroutinesCore}"
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

  const val androidFhirGroup = "com.google.android.fhir"
  const val androidFhirEngineModule = "engine"
  const val androidFhirKnowledgeModule = "knowledge"
  const val androidFhirCommon = "$androidFhirGroup:common:${Versions.androidFhirCommon}"
  const val androidFhirEngine =
    "$androidFhirGroup:$androidFhirEngineModule:${Versions.androidFhirEngine}"
  const val androidFhirKnowledge = "$androidFhirGroup:knowledge:${Versions.androidFhirKnowledge}"

  const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.desugarJdkLibs}"
  const val fhirUcum = "org.fhir:ucum:${Versions.fhirUcum}"
  const val gson = "com.google.code.gson:gson:${Versions.gson}"
  const val guava = "com.google.guava:guava:${Versions.guava}"
  const val httpInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.http}"
  const val http = "com.squareup.okhttp3:okhttp:${Versions.http}"
  const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.http}"

  const val jsonToolsPatch = "com.github.java-json-tools:json-patch:${Versions.jsonToolsPatch}"
  const val material = "com.google.android.material:material:${Versions.material}"
  const val sqlcipher = "net.zetetic:android-database-sqlcipher:${Versions.sqlcipher}"
  const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
  const val woodstox = "com.fasterxml.woodstox:woodstox-core:${Versions.woodstox}"
  const val xerces = "xerces:xercesImpl:${Versions.xerces}"

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
      const val engine = "2.4.0"
      const val evaluator = "2.4.0"
      const val translator = "2.4.0"
    }

    object Glide {
      const val glide = "4.14.2"
    }

    object Kotlin {
      const val kotlinCoroutinesCore = "1.7.2"
      const val stdlib = "1.8.20"
    }

    const val androidFhirCommon = "0.1.0-alpha04"
    const val androidFhirEngine = "0.1.0-beta03"
    const val androidFhirKnowledge = "0.1.0-alpha01"
    const val desugarJdkLibs = "2.0.3"
    const val caffeine = "2.9.1"
    const val fhirUcum = "1.0.3"
    const val gson = "2.9.1"
    const val guava = "28.2-android"

    // Hapi FHIR and HL7 Core Components are interlinked.
    // Newer versions of HapiFhir don't work on Android due to the use of Caffeine 3+
    // Wait for this to release (6.3): https://github.com/hapifhir/hapi-fhir/pull/4196
    const val hapiFhir = "6.0.1"

    // Newer versions don't work on Android due to Apache Commons Codec:
    // Wait for this fix: https://github.com/hapifhir/org.hl7.fhir.core/issues/1046
    const val hapiFhirCore = "5.6.36"

    const val http = "4.11.0"
    const val jackson = "2.14.1"
    const val jsonToolsPatch = "1.13"
    const val jsonAssert = "1.5.1"
    const val material = "1.9.0"
    const val retrofit = "2.9.0"
    const val sqlcipher = "4.5.4"
    const val timber = "5.0.1"
    const val truth = "1.1.5"
    const val woodstox = "6.5.1"
    const val xerces = "2.12.2"
    const val xmlUnit = "2.9.1"

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
  }

  fun Configuration.removeIncompatibleDependencies() {
    exclude(module = "xpp3")
    exclude(module = "xpp3_min")
    exclude(module = "xmlpull")
    exclude(module = "javax.json")
    exclude(module = "jcl-over-slf4j")
    exclude(group = "org.apache.httpcomponents")
  }

  fun Configuration.forceHapiVersion() {
    // Removes newer versions of caffeine and manually imports 2.9
    // Removes newer versions of hapi and keeps on 6.0.1
    // (newer versions don't work on Android)
    resolutionStrategy {
      force(HapiFhir.caffeine)

      force(HapiFhir.fhirBase)
      force(HapiFhir.fhirClient)
      force(HapiFhir.fhirCoreConvertors)

      force(HapiFhir.fhirCoreDstu2)
      force(HapiFhir.fhirCoreDstu2016)
      force(HapiFhir.fhirCoreDstu3)
      force(HapiFhir.fhirCoreR4)
      force(HapiFhir.fhirCoreR4b)
      force(HapiFhir.fhirCoreR5)
      force(HapiFhir.fhirCoreUtils)

      force(HapiFhir.structuresDstu2)
      force(HapiFhir.structuresDstu3)
      force(HapiFhir.structuresR4)
      force(HapiFhir.structuresR5)

      force(HapiFhir.validation)
      force(HapiFhir.validationDstu3)
      force(HapiFhir.validationR4)
      force(HapiFhir.validationR5)
    }
  }
}
