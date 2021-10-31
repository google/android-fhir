import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins { `kotlin-dsl` }

repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:5.12.5")
    implementation("com.android.tools.build:gradle:7.0.2")
}
