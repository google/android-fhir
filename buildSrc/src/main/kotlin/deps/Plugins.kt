package deps

object Plugins {
  const val androidLib = "com.android.library"
  const val kotlinAndroid = "kotlin-android"
  const val kotlinKapt = "kotlin-kapt"
  const val mavenPublish = "maven-publish"
  const val androidGradlePlugin = "com.android.tools.build:gradle:${versions.Plugins.androidGradlePlugin}"
  const val kotlin =
    "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.AppDependencies.Kotlin.kotlin}"
  const val spotless = "com.diffplug.spotless:spotless-plugin-gradle:${versions.Plugins.spotless}"
  const val navSafeArgs =
    "androidx.navigation:navigation-safe-args-gradle-plugin:${versions.AppDependencies.CoreDeps.navigation}"
}
