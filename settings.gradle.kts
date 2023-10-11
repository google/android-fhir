import androidx.build.gradle.gcpbuildcache.GcpBuildCache
import androidx.build.gradle.gcpbuildcache.GcpBuildCacheServiceFactory

plugins {
  id("com.gradle.enterprise") version ("3.10")
  id("androidx.build.gradle.gcpbuildcache") version "1.0.0-beta01"
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

// NECESSARY force of the Jackson to run generateSearchParams in the new version of HAPI (6.8)
buildscript {
  dependencies {
    classpath("com.fasterxml.jackson.core:jackson-core:2.15.2")
    classpath("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    classpath("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    classpath("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    classpath("com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.15.2")
    classpath("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    classpath("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
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
