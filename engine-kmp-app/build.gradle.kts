import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("com.android.application")
  id("org.jetbrains.kotlin.plugin.compose")
  id("org.jetbrains.compose")
}

android {
  namespace = "com.example.enginekmpapp"
  compileSdk = Sdk.COMPILE_SDK

  defaultConfig {
    applicationId = "com.example.enginekmpapp"
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
  jvmToolchain(21)

  androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }

  jvm("desktop")

  listOf(
      iosX64(),
      iosArm64(),
      iosSimulatorArm64(),
    )
    .forEach { iosTarget ->
      iosTarget.binaries.framework {
        baseName = "engineKmpAppKit"
        isStatic = true
      }
    }

  targets.configureEach {
    compilations.configureEach {
      compilerOptions.configure {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.addAll(
          "kotlin.time.ExperimentalTime",
          "kotlin.uuid.ExperimentalUuidApi",
        )
      }
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlin.fhir)
      implementation(project(":engine-kmp"))
    }
    androidMain.dependencies {
      implementation(compose.preview)
      implementation(libs.androidx.activity.compose)
    }
    val desktopMain by getting {
      dependencies {
        implementation(compose.desktop.currentOs)
      }
    }
    val iosMain by creating {
      dependsOn(commonMain.get())
    }
    val iosX64Main by getting { dependsOn(iosMain) }
    val iosArm64Main by getting { dependsOn(iosMain) }
    val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
  }
}

compose.desktop {
  application {
    mainClass = "com.example.enginekmpapp.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "com.example.enginekmpapp"
      packageVersion = "1.0.0"
    }
  }
}
