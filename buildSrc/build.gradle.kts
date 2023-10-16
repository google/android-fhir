plugins {
  `kotlin-dsl`
}

repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
}

dependencies {
  implementation("com.diffplug.spotless:spotless-plugin-gradle:6.22.0")

  implementation("com.android.tools.build:gradle:8.1.2")

  implementation("app.cash.licensee:licensee-gradle-plugin:1.3.0")
  implementation("com.osacky.flank.gradle:fladle:0.17.4")

  implementation("com.spotify.ruler:ruler-gradle-plugin:1.4.0")

  implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:6.8.0")
  implementation("com.squareup:kotlinpoet:1.14.2")
}
