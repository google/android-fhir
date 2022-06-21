import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
  `kotlin-dsl`
}

repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
}

dependencies {
  implementation("com.diffplug.spotless:spotless-plugin-gradle:6.7.2")

  implementation("com.android.tools.build:gradle:7.1.1")

  implementation("app.cash.licensee:licensee-gradle-plugin:1.3.0")

  implementation("com.spotify.ruler:ruler-gradle-plugin:1.2.1")
}
