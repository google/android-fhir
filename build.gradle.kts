// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  dependencies {
    classpath(Plugins.androidGradlePlugin)
    classpath(Plugins.benchmarkGradlePlugin)
    classpath(Plugins.flankGradlePlugin)
    classpath(Plugins.kotlinGradlePlugin)
    classpath(Plugins.navSafeArgsGradlePlugin)
    classpath(Plugins.rulerGradlePlugin)
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    gradlePluginPortal()
  }
  configureSpotless()
}

subprojects {
  // We have some empty folders like the :contrib root folder, which Gradle recognizes as projects.
  // Don't configure plugins for those folders.
  if (project.buildFile.exists()) {
    configureLicensee()
  }
  tasks.withType(Test::class.java).configureEach {
    maxParallelForks = 1
    if (project.providers.environmentVariable("GITHUB_ACTIONS").isPresent) {
      // limit memory usage to avoid running out of memory in the docker container.
      maxHeapSize = "512m"
    }
  }
}
