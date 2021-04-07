// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  dependencies {
    classpath(Plugins.androidGradlePlugin)
    classpath(Plugins.kotlinGradlePlugin)
    classpath(Plugins.navSafeArgsGradlePlugin)
    classpath(Plugins.spotlessGradlePlugin)
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    gradlePluginPortal()
  }
  apply(plugin = Plugins.BuildPlugins.spotless)
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      ktlint().userData(mapOf("indent_size" to "2", "continuation_indent_size" to "2"))
      ktfmt().googleStyle()
      licenseHeaderFile(
        "${project.rootProject.projectDir}/license-header.txt",
        "package|import|class|object|sealed|open|interface|abstract "
        // It is necessary to tell spotless the top level of a file in order to apply config to it
        // See: https://github.com/diffplug/spotless/issues/135
        )
    }
    kotlinGradle {
      target("*.gradle.kts")
      ktlint().userData(mapOf("indent_size" to "2", "continuation_indent_size" to "2"))
      ktfmt().googleStyle()
    }
    format("xml") {
      target(".idea/jarRepositories.xml", "**/*.xml")
      prettier(mapOf("prettier" to "2.0.5", "@prettier/plugin-xml" to "0.13.0"))
        .config(mapOf("parser" to "xml", "tabWidth" to 4))
    }
  }
}

// Create a CI repository and also change versions to include the build number
afterEvaluate {
  val buildNumber = System.getenv("GITHUB_RUN_ID")
  if (buildNumber != null) {
    subprojects {
      apply(plugin = Plugins.BuildPlugins.mavenPublish)
      configure<PublishingExtension> {
        repositories {
          maven {
            name = "CI"
            url = uri("file://${rootProject.buildDir}/ci-repo")
          }
        }
        // update version to have suffix of build id
        project.version = "${project.version}-build_$buildNumber"
      }
    }
  }
}
