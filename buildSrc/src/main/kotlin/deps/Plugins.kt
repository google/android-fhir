package deps

object Plugins {
  const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"
  const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Versions.Kotlin.stdlib}"
  const val spotlessGradlePlugin = "com.diffplug.spotless:spotless-plugin-gradle:${Versions.spotlessGradlePlugin}"
  const val navSafeArgsGradlePlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${Dependencies.Versions.Androidx.navigation}"
  
  object Versions {
    const val androidGradlePlugin = "4.2.0-beta02"
    const val spotlessGradlePlugin = "5.11.0"
    const val buildTools = "30.0.2"
  }

}
