import Releases.useApache2License

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
}

afterEvaluate {
  publishing {
    publications {
      register("release", MavenPublication::class) {
        from(components["release"])
        groupId = Releases.groupId
        artifactId = Releases.Common.artifactId
        version = Releases.Common.version
        // Also publish source code for developers' convenience
        artifact(
          tasks.create<Jar>("androidSourcesJar") {
            archiveClassifier.set("sources")
            from(android.sourceSets.getByName("main").java.srcDirs)
          }
        )
        pom {
          name.set(Releases.Common.name)
          useApache2License()
        }
      }
    }
  }
}

createJacocoTestReportTask()

android {
  compileSdk = Sdk.compileSdk
  buildToolsVersion = Plugins.Versions.buildTools

  defaultConfig {
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
  configureJacocoTestOptions()
}

configurations { all { exclude(module = "xpp3") } }

dependencies {
  api(Dependencies.HapiFhir.structuresR4)

  implementation(Dependencies.fhirUcum)

  testImplementation(Dependencies.Kotlin.kotlinTestJunit)
  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
}
