plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
}

android {
  compileSdk = Sdk.compileSdk
  buildToolsVersion = Plugins.Versions.buildTools

  defaultConfig {
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  api(Dependencies.HapiFhir.structuresR4)

  implementation(Dependencies.fhirUcum)

  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
}
