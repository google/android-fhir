plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
}

android {
  compileSdk = Sdk.compileSdk
  ndkVersion = Sdk.ndkVersion

  defaultConfig {
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    testInstrumentationRunner = Dependencies.androidJunitRunner
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }

  externalNativeBuild {
    ndkBuild {
      path("src/main/jni/Android.mk")
    }
  }
}

dependencies {
  implementation(Dependencies.Kotlin.stdlib)
  testImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
}
