/*
 * Copyright 2020 Google LLC
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
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.Androidx.fragmentKtx}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.Androidx.recyclerView}"
    const val workRuntimeKtx = "androidx.work:work-runtime-ktx:${Versions.Androidx.workRuntimeKtx}"
  }

  object Cql {
    const val cqlEngine = "org.opencds.cqf:cql-engine:${Versions.Cql.cqlEngine}"
    const val cqlEngineFhir = "org.opencds.cqf:cql-engine-fhir:${Versions.Cql.cqlEngine}"
  }

  object HapiFhir {
    const val structuresR4 = "ca.uhn.hapi.fhir:hapi-fhir-structures-r4:${Versions.hapiFhir}"
    const val validation = "ca.uhn.hapi.fhir:hapi-fhir-validation:${Versions.hapiFhir}"
  }

  object Kotlin {
    const val androidxCoreKtx = "androidx.core:core-ktx:${Versions.Kotlin.androidxCoreKtx}"
    const val kotlinCoroutinesAndroid =
      "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val kotlinCoroutinesCore =
      "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.kotlinCoroutinesCore}"
    const val kotlinTestJunit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.Kotlin.stdlib}"
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
    const val retrofitMock = "com.squareup.retrofit2:retrofit-mock:${Versions.retrofit}"
  }

  object Room {
    const val compiler = "androidx.room:room-compiler:${Versions.Androidx.room}"
    const val ktx = "androidx.room:room-ktx:${Versions.Androidx.room}"
    const val runtime = "androidx.room:room-runtime:${Versions.Androidx.room}"
  }

  const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.desugarJdkLibs}"
  const val guava = "com.google.guava:guava:${Versions.guava}"
  const val httpInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.httpInterceptor}"
  const val jsonToolsPatch = "com.github.java-json-tools:json-patch:${Versions.jsonToolsPatch}"
  const val material = "com.google.android.material:material:${Versions.material}"

  // Dependencies for testing go here
  object AndroidxTest {
    const val core = "androidx.test:core:${Versions.AndroidxTest.core}"
    const val extJunit = "androidx.test.ext:junit:${Versions.AndroidxTest.extJunit}"
    const val extJunitKtx = "androidx.test.ext:junit-ktx:${Versions.AndroidxTest.extJunit}"
    const val rules = "androidx.test:rules:${Versions.AndroidxTest.rules}"
    const val runner = "androidx.test:runner:${Versions.AndroidxTest.runner}"
    const val workTestingRuntimeKtx =
      "androidx.work:work-testing:${Versions.Androidx.workRuntimeKtx}"
  }

  object Espresso {
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"
  }

  const val androidJunitRunner = "androidx.test.runner.AndroidJUnitRunner"
  const val apacheCommonsCompress =
    "org.apache.commons:commons-compress:${Versions.apacheCommonsCompress}"
  const val apacheCommonsIo = "commons-io:commons-io:${Versions.apacheCommonsIo}"
  const val junit = "junit:junit:${Versions.junit}"
  const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}"
  const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
  const val truth = "com.google.truth:truth:${Versions.truth}"
  const val flexBox = "com.google.android.flexbox:flexbox:${Versions.flexBox}"

  object Versions {
    object Androidx {
      const val activity = "1.2.1"
      const val appCompat = "1.1.0"
      const val constraintLayout = "1.1.3"
      const val fragmentKtx = "1.3.1"
      const val lifecycle = "2.2.0"
      const val navigation = "2.3.4"
      const val recyclerView = "1.1.0"
      const val room = "2.3.0"
      const val workRuntimeKtx = "2.5.0"
    }

    object Cql {
      const val cqlEngine = "1.3.14-SNAPSHOT"
    }

    object Kotlin {
      const val androidxCoreKtx = "1.2.0"
      const val kotlinCoroutinesCore = "1.4.2"
      const val stdlib = "1.4.31"
    }

    const val apacheCommonsCompress = "1.20"
    const val apacheCommonsIo = "2.10.0"
    const val desugarJdkLibs = "1.0.9"
    const val guava = "28.2-android"
    const val hapiFhir = "5.4.0"
    const val httpInterceptor = "4.0.0"
    const val jsonToolsPatch = "1.13"
    const val material = "1.3.0"
    const val retrofit = "2.7.2"
    const val truth = "1.0.1"
    const val flexBox = "3.0.0"

    object AndroidxTest {
      const val core = "1.2.0"
      const val extJunit = "1.1.2"
      const val rules = "1.1.0"
      const val runner = "1.1.0"
    }

    const val espresso = "3.3.0"
    const val junit = "4.13"
    const val mockitoKotlin = "3.2.0"
    const val robolectric = "4.5.1"
    const val coverage = "0.16.0"
  }
}
