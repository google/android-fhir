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
    compileSdk = Sdk.COMPILE_SDK
    minSdk = Sdk.MIN_SDK
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
        implementation(libs.fhir.path)
        implementation(libs.navigation.compose)
        implementation(libs.androidx.lifecycle.viewmodel.compose)
        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.filekit.dialogs.compose)
        implementation(libs.kermit)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlin.fhir)
        implementation(libs.kotlinx.io.core)
        implementation(libs.kotlinx.serialization.json)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.androidx.lifecycle.runtime.testing)
        implementation(libs.kotlin.test)
        implementation(libs.kotest.assertions.core)

        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        implementation(compose.uiTest)
      }
    }

    androidMain { resources.srcDir("res") }

    getByName("androidDeviceTest") {
      dependencies {
        implementation(libs.androidx.compose.ui.test.junit4)
        implementation(libs.androidx.compose.ui.test.manifest)
        implementation(libs.androidx.test.core)
        implementation(libs.androidx.test.ext.junit)
        implementation(libs.androidx.test.ext.junit.ktx)
        implementation(libs.androidx.test.runner)
        implementation(libs.androidx.test.rules)
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
        implementation(libs.truth)
      }
    }

    val desktopMain by getting {
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(libs.kotlinx.coroutines.swing)
      }
    }
  }
}
