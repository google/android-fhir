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
  implementation("com.diffplug.spotless:spotless-plugin-gradle:5.12.5")

  implementation("com.android.tools.build:gradle:7.0.2")

  // Necessary. See: https://youtrack.jetbrains.com/issue/KT-31643#focus=Comments-27-4818475.0-0
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.5.31")

  implementation("app.cash.licensee:licensee-gradle-plugin:1.2.0")
}
