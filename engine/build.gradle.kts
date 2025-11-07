import codegen.GenerateSearchParamsTask
import java.net.URL

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.ksp)
  `maven-publish`
  jacoco
  alias(libs.plugins.dokka)
}

publishArtifact(Releases.Engine)

createJacocoTestReportTask()

// Generate database schema in the schemas folder
ksp { arg("room.schemaLocation", "$projectDir/schemas") }

val generateSearchParamsTask =
  project.tasks.register("generateSearchParamsTask", GenerateSearchParamsTask::class) {
    srcOutputDir.set(project.layout.buildDirectory.dir("gen/main"))
    testOutputDir.set(project.layout.buildDirectory.dir("gen/test"))
  }

kotlin {
  sourceSets {
    val main by getting
    val test by getting
    main.kotlin.srcDirs(generateSearchParamsTask.map { it.srcOutputDir })
    test.kotlin.srcDirs(generateSearchParamsTask.map { it.testOutputDir })
  }
  jvmToolchain(11)
}

android {
  namespace = "com.google.android.fhir"
  compileSdk = Sdk.COMPILE_SDK
  defaultConfig {
    minSdk = Sdk.MIN_SDK
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    // need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir"
    consumerProguardFile("proguard-rules.pro")
  }

  sourceSets {
    getByName("androidTest").apply {
      resources.setSrcDirs(listOf("test-data"))
      assets.srcDirs("$projectDir/schemas")
    }

    getByName("test").apply { resources.setSrcDirs(listOf("test-data")) }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    // Flag to enable support for the new language APIs
    // See https = //developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
  }

  packaging { resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt")) }

  configureJacocoTestOptions()
}

afterEvaluate { configureFirebaseTestLabForLibraries() }

configurations {
  all {
    exclude(module = "json")
    exclude(module = "xpp3")
    exclude(group = "net.sf.saxon", module = "Saxon-HE")
    exclude(module = "hamcrest-all")
    exclude(module = "jakarta.activation-api")
    exclude(module = "javax.activation")
    exclude(module = "jakarta.xml.bind-api")
    exclude(module = "hapi-fhir-caching-caffeine")
    exclude(group = "com.github.ben-manes.caffeine", module = "caffeine")
    exclude(module = "jcl-over-slf4j")
  }
}

dependencies {
  androidTestImplementation(libs.androidx.room.testing)
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit.ktx)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.work.testing)
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.truth)

  api(libs.hapi.fhir.structures.r4) { exclude(module = "junit") }

  coreLibraryDesugaring(libs.desugar.jdk.libs)

  implementation(libs.android.fhir.common)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.room.room)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.sqlite)
  implementation(libs.androidx.work.runtime)
  implementation(libs.guava)
  implementation(libs.hapi.fhir.caching.guava)
  implementation(libs.hapi.fhir.validation) {
    exclude(module = "commons-logging")
    exclude(module = "httpclient")
  }
  implementation(libs.hapi.fhir.validation.r4)
  implementation(libs.http.interceptor)
  implementation(libs.json.tools.patch)
  implementation(libs.kotlin.stdlib)
  implementation(libs.retrofit)
  implementation(libs.sqlcipher)
  implementation(libs.timber)
  implementation(libs.truth)
  implementation(libs.woodstox)
  implementation(libs.xerces)

  ksp(libs.androidx.room.compiler)

  testImplementation(libs.androidx.arch.core.testing)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.work.testing)
  testImplementation(libs.json.assert)
  testImplementation(libs.junit)
  testImplementation(libs.kotlin.test.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.mock.web.server)
  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.robolectric)
  testImplementation(libs.truth)

  constraints {
    Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
      api(libName, constraints)
      implementation(libName, constraints)
    }
  }
}

tasks.dokkaHtml.configure {
  outputDirectory.set(
    file("../docs/use/api/${Releases.Engine.artifactId}/${Releases.Engine.version}"),
  )
  suppressInheritedMembers.set(true)
  dokkaSourceSets {
    named("main") {
      moduleName.set(Releases.Engine.name)
      moduleVersion.set(Releases.Engine.version)
      includes.from("Module.md")
      sourceLink {
        localDirectory.set(file("src/main/java"))
        remoteUrl.set(
          URL("https://github.com/google/android-fhir/tree/master/engine/src/main/java"),
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
