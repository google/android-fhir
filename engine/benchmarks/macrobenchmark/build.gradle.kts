plugins {
  id(Plugins.BuildPlugins.androidTest)
  id(Plugins.BuildPlugins.kotlinAndroid)
}

android {
  namespace = "com.google.android.fhir.engine.macrobenchmark"
  compileSdk = Sdk.COMPILE_SDK

  defaultConfig {
    minSdk = Sdk.MIN_SDK
    targetSdk = Sdk.TARGET_SDK
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    // This benchmark buildType is used for benchmarking, and should function like your
    // release build (for example, with minification on). It"s signed with a debug key
    // for easy local/CI testing.
    create("benchmark") {
      isDebuggable = true
      signingConfig = getByName("debug").signingConfig
      matchingFallbacks += listOf("release")
    }
  }

  targetProjectPath = ":engine:benchmarks:app"
  @Suppress("UnstableApiUsage")
  experimentalProperties["android.experimental.self-instrumenting"] = true

  kotlin { jvmToolchain(11) }
}

afterEvaluate { configureFirebaseTestLabForMacroBenchmark() }

dependencies {
  implementation(libs.androidx.test.ext.junit)
  implementation(libs.androidx.test.espresso.core)
  implementation(libs.androidx.uiautomator)
  implementation(libs.androidx.benchmark.macro.junit4)
}

androidComponents { beforeVariants(selector().all()) { it.enable = it.buildType == "benchmark" } }
