@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("com.android.kotlin.multiplatform.library")
  id("org.jetbrains.kotlin.plugin.compose")
  id("org.jetbrains.compose.hot-reload")
  id("org.jetbrains.compose")
  alias(libs.plugins.ksp)
}

kotlin {
  jvmToolchain(21)

  androidLibrary {
    namespace = "com.google.android.fhir.datacapture"
    compileSdk = 36
    minSdk = 24
    withJava()
    withHostTestBuilder {}
    withDeviceTestBuilder { sourceSetTreeName = "test" }
      .configure { instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }

    experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true

    compilations.configureEach {
      compilerOptions.configure {
        jvmTarget.set(
          org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11,
        )
      }
    }
    packaging {
      resources.excludes.addAll(
        listOf("META-INF/ASL2.0", "META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt"),
      )
    }
  }

  // For iOS targets, this is also where you should
  // configure native binary output. For more information, see:
  // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

  // A step-by-step guide on how to include this library in an XCode
  // project can be found here:
  // https://developer.android.com/kotlin/multiplatform/migrate
  val xcfName = "sharedKit"

  iosX64 { binaries.framework { baseName = xcfName } }

  iosArm64 { binaries.framework { baseName = xcfName } }

  iosSimulatorArm64 { binaries.framework { baseName = xcfName } }

  wasmJs {
    browser()
    binaries.library()
  }

  jvm("desktop")

  js {
    browser()
    binaries.library()
  }

  // Source set declarations.
  // Declaring a target automatically creates a source set with the same name. By default, the
  // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
  // common to share sources between related targets.
  // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
  sourceSets {
    all {
      languageSettings {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions { freeCompilerArgs.add("-Xexpect-actual-classes") }
      }
    }

    commonMain {
      dependencies {
        implementation(libs.material.icons.extended)
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.ui)
        implementation(compose.components.resources)
        implementation(compose.components.uiToolingPreview)
        implementation(libs.navigation.compose)
        implementation(libs.androidx.lifecycle.viewmodel.compose)
        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.kermit)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlin.fhir)
        implementation(libs.kotlinx.io.core)
        implementation(libs.kotlinx.serialization.json)
      }
    }

    commonTest { dependencies { implementation(libs.kotlin.test) } }

    androidMain {
      resources.srcDir("res")
      dependencies {

        // Add Android-specific dependencies here. Note that this source set depends on
        // commonMain by default and will correctly pull the Android artifacts of any KMP
        // dependencies declared in commonMain.
        //        api(libs.hapi.fhir.structures.r4)
        implementation(libs.accompanist.themeadapter.material3)
        //        implementation(libs.android.fhir.common)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.core)
        implementation(libs.androidx.fragment)
        implementation(libs.material)
        //        implementation(libs.hapi.fhir.caching.guava)
        /*   implementation(libs.hapi.fhir.validation) {
          exclude(module = "commons-logging")
          exclude(module = "httpclient")
        }*/
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.timber)
        implementation(libs.glide)
        /*
        constraints {
          Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
            api(libName, constraints)
            implementation(libName, constraints)
          }
        }*/
      }
    }

    getByName("androidDeviceTest") {
      dependencies {
        implementation(libs.androidx.test.runner)
        implementation(libs.androidx.test.core)
        implementation(libs.androidx.test.ext.junit)
        implementation(libs.androidx.compose.ui.test.junit4)
        implementation(libs.androidx.test.core)
        /* implementation(libs.androidx.test.espresso.contrib) {
          // build fails with error "Duplicate class found" (org.checkerframework.checker.*)
          exclude(group = "org.checkerframework", module = "checker")
        }*/
        implementation(libs.androidx.test.espresso.core)
        implementation(libs.androidx.test.ext.junit)
        implementation(libs.androidx.test.ext.junit.ktx)
        implementation(libs.androidx.test.rules)
        implementation(libs.androidx.test.runner)
        implementation(libs.junit)
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.truth)
      }
    }

    getByName("androidHostTest") {
      dependencies {
        implementation(libs.androidx.fragment.testing)
        implementation(libs.androidx.test.core)
        implementation(libs.junit)
        implementation(libs.kotlin.test.junit)
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.mockito.inline)
        implementation(libs.mockito.kotlin)
        implementation(libs.robolectric)
        implementation(libs.truth)
        /* implementation(project(":knowledge")) {
          exclude(group = "com.google.android.fhir", module = "engine")
        }*/
      }
    }

    iosMain { dependencies {} }
  }
}
