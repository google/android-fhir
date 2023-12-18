import Dependencies.forceGuava
import Dependencies.forceHapiVersion
import Dependencies.forceJacksonVersion
import Dependencies.removeIncompatibleDependencies

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.benchmark)
}

android {
  namespace = "com.google.android.fhir.benchmark"
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
    forceGuava()
    forceHapiVersion()
    forceJacksonVersion()
  }
}

dependencies {
  androidTestImplementation(Dependencies.Androidx.workRuntimeKtx)
  androidTestImplementation(Dependencies.AndroidxTest.benchmarkJunit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.AndroidxTest.workTestingRuntimeKtx)
  androidTestImplementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)
  androidTestImplementation(Dependencies.Retrofit.coreRetrofit)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.mockWebServer)
  androidTestImplementation(Dependencies.truth)

  androidTestImplementation(project(":engine"))
  // for test json files only
  androidTestImplementation(project(":workflow-testing"))
}
