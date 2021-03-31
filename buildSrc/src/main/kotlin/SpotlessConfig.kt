import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun Project.configureSpotless() {
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("**/generated-sources/**")
      ktlint().userData(mapOf("indent_size" to "2", "continuation_indent_size" to "2"))
      ktfmt().googleStyle()
//      licenseHeaderFile("${project.rootProject.projectDir}/license-header.txt", "package|class|object|sealed|open|interface|abstract ")
    }
    kotlinGradle {
      target("*.gradle.kts")
      ktlint().userData(mapOf("indent_size" to "2", "continuation_indent_size" to "2"))
      ktfmt().googleStyle()
    }
    format("xml") {
      target("**/*.xml")
      prettier(mapOf("prettier" to "2.0.5", "@prettier/plugin-xml" to "0.13.0"))
        .config(mapOf("parser" to "xml", "tabWidth" to 4))
    }
  }
}
