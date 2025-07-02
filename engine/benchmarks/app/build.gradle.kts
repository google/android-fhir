plugins {
  id(Plugins.BuildPlugins.application)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinCompose)
  id(Plugins.BuildPlugins.kotlinSerialization)
}

android {
  namespace = "com.google.android.fhir.engine.benchmarks.app"
  compileSdk = Sdk.COMPILE_SDK

  defaultConfig {
    applicationId = "com.google.android.fhir.engine.benchmarks.app"
    minSdk = Sdk.MIN_SDK
    targetSdk = Sdk.TARGET_SDK
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = Dependencies.androidJunitRunner

    val baseUrlKey = "FHIR_SERVER_BASE_URL"
    val fhirServerBaseUrl =
      gradleLocalProperties().getProperty(baseUrlKey) ?: properties[baseUrlKey]?.toString() ?: ""
    buildConfigField(type = "String", name = baseUrlKey, "\"$fhirServerBaseUrl\"")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }

    create("benchmark") {
      initWith(buildTypes.getByName("release"))
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += listOf("release")
      isDebuggable = false
    }
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions { jvmTarget = "11" }
  buildFeatures {
    compose = true
    buildConfig = true
  }

  packaging { resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt")) }

  kotlin { jvmToolchain(11) }
}

configureSyntheaTask()

dependencies {
  coreLibraryDesugaring(Dependencies.desugarJdkLibs)
  implementation(project(":engine"))

  implementation(libs.androidx.core)
  implementation(libs.androidx.lifecycle.viewmodel.savedstate)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.bundles.androidx.tracing)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.androidx.work.runtime)
  implementation(libs.androidx.datastore.preferences)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
