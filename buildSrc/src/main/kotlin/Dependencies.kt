/*
 * Copyright 2021 Google LLC
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
    const val evaluator = "org.opencds.cqf.cql:evaluator:${Versions.Cql.evaluator}"
    const val evaluatorBuilder = "org.opencds.cqf.cql:evaluator.builder:${Versions.Cql.evaluator}"
    const val evaluatorDagger = "org.opencds.cqf.cql:evaluator.dagger:${Versions.Cql.evaluator}"
    const val evaluatorPlanDef =
      "org.opencds.cqf.cql:evaluator.plandefinition:${Versions.Cql.evaluator}"
  }

  object HapiFhir {
    const val structuresR4 = "ca.uhn.hapi.fhir:hapi-fhir-structures-r4:${Versions.hapiFhir}"
    const val validation = "ca.uhn.hapi.fhir:hapi-fhir-validation:${Versions.hapiFhir}"
  }

  object Jackson {
    const val annotations = "com.fasterxml.jackson.core:jackson-annotations:${Versions.jackson}"
    const val core = "com.fasterxml.jackson.core:jackson-core:${Versions.jackson}"
    const val databind = "com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}"
  }

  object JavaJsonTools {
    const val jacksonCoreUtils =
      "com.github.java-json-tools:jackson-coreutils:${Versions.JavaJsonTools.jacksonCoreUtils}"
    const val msgSimple =
      "com.github.java-json-tools:msg-simple:${Versions.JavaJsonTools.msgSimple}"
  }

  object Kotlin {
    const val kotlinCoroutinesAndroid =
      "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val kotlinCoroutinesCore =
      "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val kotlinTestJunit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.Kotlin.stdlib}"
    const val kotlinCoroutinesTest =
      "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.Kotlin.stdlib}"
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

  const val androidFhirCommon = "com.google.android.fhir:common:${Versions.androidFhirCommon}"
  const val lifecycleExtensions =
    "androidx.lifecycle:lifecycle-extensions:${Versions.Androidx.lifecycle}"
  const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.desugarJdkLibs}"
  const val fhirUcum = "org.fhir:ucum:${Versions.fhirUcum}"
  const val guava = "com.google.guava:guava:${Versions.guava}"
  const val httpInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.http}"
  const val http = "com.squareup.okhttp3:okhttp:${Versions.http}"
  const val stax = "javax.xml.stream:stax-api:${Versions.stax}"
  const val jsonToolsPatch = "com.github.java-json-tools:json-patch:${Versions.jsonToolsPatch}"
  const val kotlinPoet = "com.squareup:kotlinpoet:${Versions.kotlinPoet}"
  const val material = "com.google.android.material:material:${Versions.material}"
  const val sqlcipher = "net.zetetic:android-database-sqlcipher:${Versions.sqlcipher}"
  const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
  const val woodstox = "org.codehaus.woodstox:woodstox-core-asl:${Versions.woodstox}"
  const val xerces = "xerces:xercesImpl:${Versions.xerces}"

  // Dependencies for testing go here
  object AndroidxTest {
    const val archCore = "androidx.arch.core:core-testing:${Versions.AndroidxTest.archCore}"
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
  }

  const val androidJunitRunner = "androidx.test.runner.AndroidJUnitRunner"
  const val flexBox = "com.google.android.flexbox:flexbox:${Versions.flexBox}"
  const val junit = "junit:junit:${Versions.junit}"
  const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}"
  const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoInline}"
  const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
  const val truth = "com.google.truth:truth:${Versions.truth}"

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
      const val evaluator = "1.4.2"
    }

    object JavaJsonTools {
      const val jacksonCoreUtils = "2.0"
      const val msgSimple = "1.2"
    }

    object Kotlin {
      const val kotlinCoroutinesCore = "1.4.2"
      const val stdlib = "1.6.10"
    }

    const val androidFhirCommon = "0.1.0-alpha03"
    const val desugarJdkLibs = "1.1.5"
    const val fhirUcum = "1.0.3"
    const val flexBox = "3.0.0"
    const val guava = "28.2-android"
    const val hapiFhir = "5.4.0"
    const val http = "4.9.1"
    // TODO: The next release of HAPI FHIR will hopefully have
    // https://github.com/hapifhir/hapi-fhir/pull/3043 merged in. If it does, when we update, we
    // should remove any excludes directives for "net.sf.saxon" across our build.gradle files.
    const val jackson = "2.12.2"
    const val jsonToolsPatch = "1.13"
    const val kotlinPoet = "1.9.0"
    const val material = "1.6.0"
    const val retrofit = "2.7.2"
    const val stax = "1.0-2"
    const val sqlcipher = "4.5.0"
    const val timber = "5.0.1"
    const val truth = "1.0.1"
    const val woodstox = "4.4.1"
    const val xerces = "2.11.0"

    // Test dependencies

    object AndroidxTest {
      const val core = "1.4.0"
      const val archCore = "2.1.0"
      const val extJunit = "1.1.3"
      const val rules = "1.4.0"
      const val runner = "1.4.0"
      const val fragmentVersion = "1.3.6"
    }

    const val espresso = "3.4.0"
    const val jacoco = "0.8.7"
    const val junit = "4.12"
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
