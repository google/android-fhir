plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.google.android.fhir.document"
  compileSdk = Sdk.COMPILE_SDK

  defaultConfig {
    minSdk = Sdk.MIN_SDK
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  compileOptions {
    // Flag to enable support for the new language APIs
    // See https = //developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  packaging { resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt")) }

  sourceSets { getByName("test").apply { resources.setSrcDirs(listOf("test-data")) } }

  kotlin { jvmToolchain(11) }
}

dependencies {
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.test.ext.junit)

  coreLibraryDesugaring(libs.desugar.jdk.libs)

  implementation(libs.android.fhir.engine)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core)
  implementation(libs.http.interceptor)
  implementation(libs.material)
  implementation(libs.nimbus)
  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.gson)
  implementation(libs.timber)
  implementation(libs.zxing)

  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.mock.web.server)
  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.robolectric)
  testImplementation(libs.truth)
}
