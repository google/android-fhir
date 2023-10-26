import androidx.build.gradle.gcpbuildcache.GcpBuildCache
import androidx.build.gradle.gcpbuildcache.GcpBuildCacheServiceFactory

plugins {
  id("com.gradle.enterprise") version ("3.10")
  id("androidx.build.gradle.gcpbuildcache") version "1.0.0-beta05"
  id("org.gradle.toolchains.foojay-resolver-convention") version ("0.5.0")
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    capture { isTaskInputFiles = true }
  }
}

val kokoroRun = providers.environmentVariable("KOKORO_BUILD_ID").isPresent

if (kokoroRun == true) {
  buildCache {
    registerBuildCacheService(GcpBuildCache::class, GcpBuildCacheServiceFactory::class)
    remote(GcpBuildCache::class) {
      projectId = "android-fhir-build"
      bucketName = "android-fhir-build-cache"
      isPush = true
    }
  }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // TODO(vorburger): Once this actually works, move it into gradle/libs.versions.toml instead, by
            // using https://docs.gradle.org/current/userguide/platforms.html#sec:sharing-catalogs
            version("jackson", "2.15.2")
            library("jackson-core", "com.fasterxml.jackson.core", "jackson-core").versionRef("jackson")
            library("jackson-annotations", "com.fasterxml.jackson.core", "jackson-annotations").versionRef("jackson")
            library("jackson-databind", "com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            library("jackson-xml", "com.fasterxml.jackson.dataformat", "jackson-dataformat-xml").versionRef("jackson")
            library("jackson-jaxb-annotations", "com.fasterxml.jackson.module", "jackson-module-jaxb-annotations").versionRef("jackson")
            library("jackson-jsr310", "com.fasterxml.jackson.datatype", "jackson-datatype-jsr310").versionRef("jackson")
            library("jackson-kotlin", "com.fasterxml.jackson.module", "jackson-module-kotlin").versionRef("jackson")
            bundle("jackson", listOf("jackson-core", "jackson-annotations", "jackson-databind", "jackson-xml", "jackson-jaxb-annotations", "jackson-jsr310", "jackson-kotlin"))
        }
    }
}

buildscript {
  dependencies {
    // NECESSARY force of the Jackson to run generateSearchParams in the new version of HAPI (6.8)
    classpath(libs.bundles.jackson) // defined in gradle/libs.versions.toml
  }
}

include(":catalog")

include(":common")

include(":contrib:barcode")

include(":datacapture")

include(":demo")

include(":engine")

include(":knowledge")

include(":workflow")

include(":workflow-testing")

include(":workflow:benchmark")

include(":engine:benchmark")
