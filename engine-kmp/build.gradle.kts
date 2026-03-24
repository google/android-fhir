plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("com.android.kotlin.multiplatform.library")
}

kotlin {
  jvmToolchain(21)

  androidLibrary {
    namespace = "com.google.android.fhir.engine"
    compileSdk = Sdk.COMPILE_SDK
    minSdk = Sdk.MIN_SDK
  }

  jvm("desktop")

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  sourceSets { commonMain { dependencies { implementation(libs.kotlinx.coroutines.core) } } }
}
