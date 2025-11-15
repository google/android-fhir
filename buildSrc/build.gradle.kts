import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependency
import org.gradle.api.provider.Provider

plugins {
  `kotlin-dsl`
}

repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
}

dependencies {
  implementation(libs.spotless.plugin.gradle)
  implementation(libs.gradle)
  implementation(libs.licensee.gradle.plugin)
  implementation(libs.fladle)
  implementation(libs.ruler.gradle.plugin)
  implementation(libs.hapi.fhir.structures.r4.v6100)
  implementation(libs.kotlinpoet)
  implementation(plugin(libs.plugins.android.kotlin.multiplatform.library))
  implementation(plugin(libs.plugins.compose.compiler))
  implementation(plugin(libs.plugins.compose.hotreload))
  implementation(plugin(libs.plugins.compose.multiplatform))
  implementation(plugin(libs.plugins.kotlin.multiplatform))
}

fun DependencyHandler.plugin(plugin: Provider<PluginDependency>) =
  plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
