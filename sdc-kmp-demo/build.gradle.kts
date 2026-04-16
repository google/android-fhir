import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("com.android.application")
  id("org.jetbrains.kotlin.plugin.compose")
  id("org.jetbrains.compose.hot-reload")
  id("org.jetbrains.compose")
  id("org.jetbrains.kotlin.plugin.serialization")
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.example.sdckmpdemo"
  compileSdk = Sdk.COMPILE_SDK

  defaultConfig {
    applicationId = "com.example.sdckmpdemo"
    minSdk = Sdk.MIN_SDK
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
  }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
  buildTypes { getByName("release") { isMinifyEnabled = false } }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
}

kotlin {
  androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }

  jvm("desktop")

  val xcfName = "sdcKmpDemoKit"

  listOf(
      iosX64(),
      iosArm64(),
      iosSimulatorArm64(),
    )
    .forEach { iosTarget ->
      iosTarget.binaries.framework {
        baseName = xcfName
        isStatic = true
      }
    }

  sourceSets {
    androidMain.dependencies {
      implementation(compose.preview)
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.work.runtime)
    }
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)
      implementation(libs.androidx.lifecycle.viewmodel.compose)
      implementation(libs.androidx.lifecycle.runtime.compose)
      implementation(compose.material3)
      implementation(libs.material.icons.extended)
      implementation(libs.kotlin.fhir)
      implementation(libs.kotlinx.serialization.json)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.navigation.compose)
      implementation(project(":datacapture-kmp"))
      implementation(project(":engine-kmp"))
    }

    val desktopMain by getting { dependencies { implementation(compose.desktop.currentOs) } }
  }
}

compose.desktop {
  application {
    mainClass = "com.example.sdckmpdemo.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "com.example.sdckmpdemo"
      packageVersion = "1.0.0"
    }
  }
}
