import Dependencies.removeIncompatibleDependencies
import java.net.URL

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.ksp)
  `maven-publish`
  jacoco
  alias(libs.plugins.dokka)
}

publishArtifact(Releases.Knowledge)

createJacocoTestReportTask()

// Generate database schema in the schemas folder
ksp { arg("room.schemaLocation", "$projectDir/schemas") }

android {
  namespace = "com.google.android.fhir.knowledge"
  compileSdk = Sdk.COMPILE_SDK
  defaultConfig {
    minSdk = Sdk.MIN_SDK
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    // Need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir.knowledge"
  }

  sourceSets {
    getByName("androidTest").apply {
      resources.setSrcDirs(listOf("testdata"))
      assets.srcDirs("$projectDir/schemas")
    }

    getByName("test").apply { resources.setSrcDirs(listOf("testdata")) }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions { isCoreLibraryDesugaringEnabled = true }

  packaging {
    resources.excludes.addAll(
      listOf(
        "license.html",
        "META-INF/ASL2.0",
        "META-INF/ASL-2.0.txt",
        "META-INF/DEPENDENCIES",
        "META-INF/LGPL-3.0.txt",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/license.txt",
        "META-INF/license.html",
        "META-INF/LICENSE.md",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt",
        "META-INF/NOTICE.md",
        "META-INF/notice.txt",
        "META-INF/LGPL-3.0.txt",
        "META-INF/sun-jaxb.episode",
      ),
    )
  }
  configureJacocoTestOptions()
  kotlin { jvmToolchain(11) }
}

afterEvaluate { configureFirebaseTestLabForLibraries() }

configurations { all { removeIncompatibleDependencies() } }

dependencies {
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit.ktx)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.kotlinx.coroutines.test)
  androidTestImplementation(libs.truth)

  api(libs.hapi.fhir.structures.r4) { exclude(module = "junit") }
  api(libs.hapi.fhir.caching.guava)

  coreLibraryDesugaring(libs.desugar.jdk.libs)

  implementation(libs.kotlin.stdlib)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.timber)
  implementation(libs.http)
  implementation(libs.hapi.fhir.core.convertors)
  implementation(libs.apache.commons.compress)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.room.room)
  implementation(libs.androidx.room.runtime)

  ksp(libs.androidx.room.compiler)

  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.mock.web.server)
  testImplementation(libs.robolectric)
  testImplementation(libs.androidx.arch.core.testing)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.truth)

  constraints {
    Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
      api(libName, constraints)
    }
  }
}

tasks.dokkaHtml.configure {
  outputDirectory.set(
    file("../docs/use/api/${Releases.Knowledge.artifactId}/${Releases.Knowledge.version}"),
  )
  suppressInheritedMembers.set(true)
  dokkaSourceSets {
    named("main") {
      moduleName.set(Releases.Knowledge.name)
      moduleVersion.set(Releases.Knowledge.version)
      includes.from("Module.md")
      sourceLink {
        localDirectory.set(file("src/main/java"))
        remoteUrl.set(
          URL("https://github.com/google/android-fhir/tree/master/knowledge/src/main/java"),
        )
        remoteLineSuffix.set("#L")
      }
      externalDocumentationLink {
        url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/"))
        packageListUrl.set(
          URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/element-list"),
        )
      }
    }
  }
}
