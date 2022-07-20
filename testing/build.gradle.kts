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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
}

dependencies {
  api(project(":engine"))

  implementation(Dependencies.AndroidxTest.rules)
}
