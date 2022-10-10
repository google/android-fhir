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
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk

    testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    // Runs only once
    testInstrumentationRunnerArguments["androidx.benchmark.dryRunMode.enable"] = "true"
    // Includes Startup time
    testInstrumentationRunnerArguments["androidx.benchmark.startupMode.enable"] = "true"
    multiDexEnabled = true
  }

  sourceSets {
    getByName("test").apply { resources.setSrcDirs(listOf("testdata")) }
    getByName("androidTest").apply { resources.setSrcDirs(listOf("testdata")) }
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

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.AndroidxTest.benchmarkJunit)
  androidTestImplementation(Dependencies.truth)
  androidTestImplementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)

  androidTestImplementation(Dependencies.Cql.evaluator)
  androidTestImplementation(Dependencies.Cql.evaluatorBuilder)
  androidTestImplementation(project(":engine"))
  androidTestImplementation(project(":workflow"))
}
