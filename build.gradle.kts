// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  repositories {
    google()
    jcenter()
  }
  dependencies {
    classpath(deps.Plugins.androidGradlePlugin)
    classpath(deps.Plugins.kotlin)
    classpath(deps.Plugins.spotless)
    classpath(deps.Plugins.navSafeArgs)
  }
}

allprojects {
  repositories {
    google()
    jcenter()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
  }
}

subprojects {
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      ktlint().userData(mapOf("indent_size" to "2", "continuation_indent_size" to "2"))
      ktfmt().googleStyle()
      licenseHeaderFile("${project.rootProject.projectDir}/license-header.txt")
    }

    format("xml") {
      target("**/*.xml")
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
      apply(plugin = "maven-publish")
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
