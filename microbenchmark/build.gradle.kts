plugins {
  id("com.android.library")
  id("androidx.benchmark")
  id("org.jetbrains.kotlin.android")
}

android {
  compileSdk = 32

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  packagingOptions {
    resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt"))
  }

  kotlinOptions { jvmTarget = "1.8" }

  defaultConfig {
    minSdk = 21
    targetSdk = 32

    testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"

    testInstrumentationRunnerArguments["androidx.benchmark.profiling.mode"] = "StackSampling"
    testInstrumentationRunnerArguments["androidx.benchmark.output.enable"] = "true"
    if (System.getenv("CI") == "true") {
      testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR,UNLOCKED"
    }
  }

  testBuildType = "release"
  buildTypes {
    debug {
      // Since isDebuggable can"t be modified by gradle for library modules,
      // it must be done in a manifest - see src/androidTest/AndroidManifest.xml
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "benchmark-proguard-rules.pro"
      )
    }
    release { isDefault = true }
  }
}

dependencies {
  androidTestImplementation("androidx.test:runner:1.4.0")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.benchmark:benchmark-junit4:1.1.0-rc02")
  implementation(project(":engine"))
  // Add your dependencies here. Note that you cannot benchmark code
  // in an app module this way - you will need to move any code you
  // want to benchmark to a library module:
  // https://developer.android.com/studio/projects/android-library#Convert
}
