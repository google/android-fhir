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

    // Remove this after this issue has been fixed:
    // https://github.com/cqframework/clinical_quality_language/issues/799
    const val antlr4Runtime = "org.antlr:antlr4-runtime:${Versions.Cql.antlr}"

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
    const val structuresR4 = "ca.uhn.hapi.fhir:hapi-fhir-structures-r4:${Versions.hapiFhir}"
    const val validation = "ca.uhn.hapi.fhir:hapi-fhir-validation:${Versions.hapiFhir}"

    // Runtime dependency that is required to run FhirPath (also requires minSDK of 26).
    // Version 3.0 uses java.lang.System.Logger, which is not available on Android
    // Replace for Guava when this PR gets merged: https://github.com/hapifhir/hapi-fhir/pull/3977
    const val caffeine = "com.github.ben-manes.caffeine:caffeine:${Versions.caffeine}"
  }

  object Jackson {
    const val annotations = "com.fasterxml.jackson.core:jackson-annotations:${Versions.jackson}"
    const val core = "com.fasterxml.jackson.core:jackson-core:${Versions.jackson}"
    const val databind = "com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}"
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
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
  }

  object Room {
    const val compiler = "androidx.room:room-compiler:${Versions.Androidx.room}"
    const val ktx = "androidx.room:room-ktx:${Versions.Androidx.room}"
    const val runtime = "androidx.room:room-runtime:${Versions.Androidx.room}"
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
  const val androidFhirCommon = "$androidFhirGroup:common:${Versions.androidFhirCommon}"
  const val androidFhirEngineModule = "engine"
  const val androidFhirEngine =
    "$androidFhirGroup:$androidFhirEngineModule:${Versions.androidFhirEngine}"

  const val lifecycleExtensions =
    "androidx.lifecycle:lifecycle-extensions:${Versions.Androidx.lifecycle}"
  const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.desugarJdkLibs}"
  const val fhirUcum = "org.fhir:ucum:${Versions.fhirUcum}"
  const val guava = "com.google.guava:guava:${Versions.guava}"
  const val httpInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.http}"
  const val http = "com.squareup.okhttp3:okhttp:${Versions.http}"

  const val jsonToolsPatch = "com.github.java-json-tools:json-patch:${Versions.jsonToolsPatch}"
  const val kotlinPoet = "com.squareup:kotlinpoet:${Versions.kotlinPoet}"
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
  const val slf4j = "org.slf4j:slf4j-android:${Versions.slf4j}"
  const val truth = "com.google.truth:truth:${Versions.truth}"
  // Makes XML assertions where the order of elements, tabs/whitespaces are not important.
  const val xmlUnit = "org.xmlunit:xmlunit-core:${Versions.xmlUnit}"

  object Versions {
    object Androidx {
      const val activity = "1.2.1"
      const val appCompat = "1.1.0"
      const val constraintLayout = "2.1.1"
      const val coreKtx = "1.2.0"
      const val datastorePref = "1.0.0"
      const val fragmentKtx = "1.3.1"
      const val lifecycle = "2.2.0"
      const val navigation = "2.3.4"
      const val recyclerView = "1.1.0"
      const val room = "2.4.2"
      const val sqliteKtx = "2.1.0"
      const val workRuntimeKtx = "2.7.1"
    }

    object Cql {
      const val antlr = "4.10.1"
      const val engine = "2.1.0"
      const val evaluator = "2.1.0"
      const val translator = "2.2.0"
    }

    object Glide {
      const val glide = "4.14.2"
    }

    object Kotlin {
      const val kotlinCoroutinesCore = "1.6.2"
      const val stdlib = "1.6.10"
    }

    const val androidFhirCommon = "0.1.0-alpha03"
    const val androidFhirEngine = "0.1.0-beta02"
    const val desugarJdkLibs = "1.1.5"
    const val caffeine = "2.9.1"
    const val fhirUcum = "1.0.3"
    const val guava = "28.2-android"
    const val hapiFhir = "6.0.1"
    const val http = "4.9.1"
    const val jackson = "2.12.2"
    const val jsonToolsPatch = "1.13"
    const val jsonAssert = "1.5.1"
    const val kotlinPoet = "1.9.0"
    const val material = "1.6.0"
    const val retrofit = "2.7.2"
    const val slf4j = "1.7.36"
    const val sqlcipher = "4.5.0"
    const val timber = "5.0.1"
    const val truth = "1.0.1"
    const val woodstox = "6.2.7"
    const val xerces = "2.12.2"
    const val xmlUnit = "2.9.0"

    // Test dependencies

    object AndroidxTest {
      const val benchmarkJUnit = "1.1.0"
      const val core = "1.4.0"
      const val archCore = "2.1.0"
      const val extJunit = "1.1.3"
      const val rules = "1.4.0"
      const val runner = "1.4.0"
      const val fragmentVersion = "1.3.6"
    }

    const val espresso = "3.4.0"
    const val jacoco = "0.8.7"
    const val junit = "4.13.2"
    const val mockitoKotlin = "3.2.0"
    const val mockitoInline = "4.0.0"
    const val robolectric = "4.7.3"

    object Mlkit {
      const val barcodeScanning = "16.1.1"
      const val objectDetection = "16.2.3"
      const val objectDetectionCustom = "16.3.1"
    }
  }
}
