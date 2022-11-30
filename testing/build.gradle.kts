plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
}

android {
  compileSdk = Sdk.compileSdk

  defaultConfig {
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
  }
  compileOptions {
    sourceCompatibility = Java.sourceCompatibility
    targetCompatibility = Java.targetCompatibility
  }

  packagingOptions {
    resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt"))
  }

  kotlinOptions { jvmTarget = Java.kotlinJvmTarget.toString() }
}

dependencies {
  api(project(":engine"))
  api(project(":r4"))
  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }

  implementation(Dependencies.AndroidxTest.rules)
}
