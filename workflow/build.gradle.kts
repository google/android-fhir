import Dependencies.removeIncompatibleDependencies
import java.net.URL

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
  id(Plugins.BuildPlugins.dokka).version(Plugins.Versions.dokka)
}

publishArtifact(Releases.Workflow)

createJacocoTestReportTask()

android {
  namespace = "com.google.android.fhir.workflow"
  compileSdk = Sdk.compileSdk
  defaultConfig {
    minSdk = Sdk.minSdk
    testInstrumentationRunner = Dependencies.androidJunitRunner
    // Need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir.workflow"
  }

  sourceSets {
    getByName("androidTest").apply { resources.setSrcDirs(listOf("sampledata")) }

    getByName("test").apply { resources.setSrcDirs(listOf("sampledata")) }
  }

  // Added this for fixing out of memory issue in running test cases
  tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() - 1).takeIf { it > 0 } ?: 1
    setForkEvery(100)
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }

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
        "META-INF/*.kotlin_module",
        "readme.html",
      ),
    )
  }
  configureJacocoTestOptions()
  kotlin { jvmToolchain(11) }
  compileOptions { isCoreLibraryDesugaringEnabled = true }
}

afterEvaluate { configureFirebaseTestLabForLibraries() }

configurations { all { removeIncompatibleDependencies() } }

dependencies {
  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  androidTestImplementation(Dependencies.AndroidxTest.core)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.AndroidxTest.workTestingRuntimeKtx)
  androidTestImplementation(Dependencies.jsonAssert)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.truth)
  androidTestImplementation(Dependencies.xmlUnit)
  androidTestImplementation(project(":workflow-testing"))

  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }
  api(Dependencies.HapiFhir.guavaCaching)

  implementation(Dependencies.Androidx.coreKtx)
  implementation(Dependencies.Cql.evaluator)
  implementation(Dependencies.Cql.evaluatorFhirJackson)
  implementation(Dependencies.HapiFhir.guavaCaching)
  implementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)
  implementation(Dependencies.Kotlin.kotlinCoroutinesCore)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.androidFhirEngine) { exclude(module = "truth") }
  implementation(Dependencies.androidFhirKnowledge)
  implementation(Dependencies.timber)
  implementation(Dependencies.xerces)

  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.jsonAssert)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
  testImplementation(Dependencies.xmlUnit)
  testImplementation(project(mapOf("path" to ":knowledge")))
  testImplementation(project(":workflow-testing"))

  constraints {
    Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
      api(libName, constraints)
      implementation(libName, constraints)
    }
  }
}

tasks.dokkaHtml.configure {
  outputDirectory.set(file("../docs/${Releases.Workflow.artifactId}/${Releases.Workflow.version}"))
  suppressInheritedMembers.set(true)
  dokkaSourceSets {
    named("main") {
      moduleName.set(Releases.Workflow.artifactId)
      moduleVersion.set(Releases.Workflow.version)
      noAndroidSdkLink.set(false)
      sourceLink {
        localDirectory.set(file("src/main/java"))
        remoteUrl.set(
          URL("https://github.com/google/android-fhir/tree/master/workflow/src/main/java"),
        )
        remoteLineSuffix.set("#L")
      }
      externalDocumentationLink {
        url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/"))
        packageListUrl.set(
          URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/element-list"),
        )
      }
      externalDocumentationLink {
        url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/"))
        packageListUrl.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/element-list"))
      }
    }
  }
}
