plugins {
  `kotlin-dsl`
}

repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
}

dependencies {
  implementation("com.diffplug.spotless:spotless-plugin-gradle:6.6.0")

  implementation("com.android.tools.build:gradle:7.1.1")

  implementation("app.cash.licensee:licensee-gradle-plugin:1.3.0")
  implementation("com.osacky.flank.gradle:fladle:0.17.4")

  implementation("com.spotify.ruler:ruler-gradle-plugin:1.2.1")

  implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:6.0.1")
  implementation("com.squareup:kotlinpoet:1.9.0")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
}
