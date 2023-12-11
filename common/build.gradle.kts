plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
}

publishArtifact(Releases.Common)

createJacocoTestReportTask()

android {
  namespace = "com.google.android.fhir.common"
  compileSdk = Sdk.compileSdk
  defaultConfig { minSdk = Sdk.minSdk }
  configureJacocoTestOptions()
  kotlin { jvmToolchain(11) }
}

configurations {
  all {
    exclude(module = "xpp3")
    exclude(module = "hapi-fhir-caching-caffeine")
    exclude(group = "com.github.ben-manes.caffeine", module = "caffeine")

    resolutionStrategy { force("com.google.guava:guava:32.1.3-android") }
  }
}

dependencies {
  // REVERT to DEPENDENCIES LATER
  api("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:6.8.0")
  api("ca.uhn.hapi.fhir:hapi-fhir-caching-guava:6.10.0")

  implementation(Dependencies.fhirUcum)

  testImplementation(Dependencies.Kotlin.kotlinTestJunit)
  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
}
