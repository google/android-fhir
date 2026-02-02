import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("com.android.application")
  id("org.jetbrains.kotlin.plugin.compose")
  id("org.jetbrains.compose")
  id("org.jetbrains.kotlin.plugin.serialization")
  alias(libs.plugins.androidx.navigation.safeargs)
}

// configureRuler()

android {
  namespace = "com.google.android.fhir.catalog"
  compileSdk = Sdk.COMPILE_SDK

  defaultConfig {
    applicationId = Releases.Catalog.applicationId
    minSdk = Sdk.MIN_SDK
    targetSdk = Sdk.TARGET_SDK
    versionCode = Releases.Catalog.versionCode
    versionName = Releases.Catalog.versionName
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  packaging {
    resources.excludes.addAll(
      listOf("META-INF/ASL2.0", "META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt"),
    )
  }
}

kotlin {
  androidTarget {
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11) }
  }

  jvm("desktop")

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      val rootProjectDir = rootProject.projectDir.path
      commonWebpackConfig {
        devServer =
          (devServer
              ?: org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer())
            .copy(
              static = (devServer?.static ?: mutableListOf()).apply { add(rootProjectDir) },
            )
      }
    }
    binaries.executable()
  }

  listOf(
      iosX64(),
      iosArm64(),
      iosSimulatorArm64(),
    )
    .forEach { iosTarget ->
      iosTarget.binaries.framework {
        baseName = "CatalogKit"
        isStatic = true
      }
    }

  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.appcompat)
      implementation(libs.androidx.constraintlayout)
      implementation(libs.androidx.core)
      implementation(libs.androidx.fragment)
      implementation(libs.material)
      // TODO restore after these libraries are migrated to Kotlin Multiplatform
      //      implementation(project(":engine"))
      //      implementation(project(":contrib:barcode"))
      //      implementation(project(":contrib:locationwidget"))
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
      implementation(libs.material.icons.extended)
      implementation(libs.kotlin.fhir)
      implementation(libs.kotlinx.serialization.json)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.navigation.compose)
      implementation(project(":datacapture-kmp"))
    }

    val desktopMain by getting { dependencies { implementation(compose.desktop.currentOs) } }
  }
}
