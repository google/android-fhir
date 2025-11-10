plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.androidx.benchmark)
}

android {
  namespace = "com.google.android.fhir.engine.microbenchmark"
  compileSdk = Sdk.COMPILE_SDK
  defaultConfig {
    minSdk = Sdk.MIN_SDK
    testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
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

  compileOptions {
    // Flag to enable support for the new language APIs
    // See https = //developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
  }

  kotlin { jvmToolchain(11) }
}

afterEvaluate { configureFirebaseTestLabForMicroBenchmark() }

dependencies {
  androidTestImplementation(libs.retrofit)
  androidTestImplementation(libs.mock.web.server)
  androidTestImplementation(libs.androidx.benchmark.junit4)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.work.runtime)
  androidTestImplementation(libs.androidx.work.testing)
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.kotlinx.coroutines.android)
  androidTestImplementation(libs.truth)

  androidTestImplementation(project(":engine"))
  // for test json files only
  androidTestImplementation(project(":workflow-testing"))

  coreLibraryDesugaring(libs.desugar.jdk.libs)
}
