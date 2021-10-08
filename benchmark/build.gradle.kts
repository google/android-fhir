plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.benchmark)
  id(Plugins.BuildPlugins.kotlinAndroid)
}

android {
  compileSdk = Sdk.compileSdk

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions { jvmTarget = "1.8" }

  defaultConfig {
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
  }

  testBuildType = "release"
  buildTypes {
    debug {
      // Since debuggable can"t be modified by gradle for library modules,
      // it must be done in a manifest - see src/androidTest/AndroidManifest.xml
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "benchmark-proguard-rules.pro"
      )
    }
    release { isDefault = true }
  }
  packagingOptions {
    resources.excludes.add("META-INF/ASL-2.0.txt")
    resources.excludes.add("META-INF/LGPL-3.0.txt")
    resources.excludes.add("META-INF/AL2.0")
    resources.excludes.add("META-INF/LGPL2.1")
  }
  defaultConfig {
    if (System.getenv("GITHUB_RUN_ID") != null)
      testInstrumentationRunnerArguments(
        mapOf(
          "androidx.benchmark.suppressErrors" to "EMULATOR",
          "androidx.benchmark.output.enable" to "true"
        )
      )
  }
}

dependencies {
  androidTestImplementation(Dependencies.Kotlin.kotlinCoroutinesTest)
  androidTestImplementation(project(mapOf("path" to ":engine")))
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.AndroidxTest.benchmark)
  coreLibraryDesugaring(Dependencies.desugarJdkLibs)
}
