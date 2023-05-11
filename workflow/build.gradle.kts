import Dependencies.forceHapiVersion
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
  compileSdk = Sdk.compileSdk

  defaultConfig {
    minSdk = Sdk.minSdkWorkflow
    targetSdk = Sdk.targetSdk
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
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }

  compileOptions {
    sourceCompatibility = Java.sourceCompatibility
    targetCompatibility = Java.targetCompatibility
  }

  packagingOptions {
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
      )
    )
  }

  kotlinOptions { jvmTarget = Java.kotlinJvmTarget.toString() }

  configureJacocoTestOptions()
}

afterEvaluate { configureFirebaseTestLab() }

configurations {
  all {
    removeIncompatibleDependencies()
    forceHapiVersion()
  }
}

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.core)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.AndroidxTest.workTestingRuntimeKtx)
  androidTestImplementation(Dependencies.jsonAssert)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.truth)
  androidTestImplementation(Dependencies.xmlUnit)
  androidTestImplementation(project(":testing"))
  androidTestImplementation(project(":workflow-testing"))

  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }

  implementation(Dependencies.Androidx.coreKtx)

  implementation(Dependencies.Cql.engine)
  implementation(Dependencies.Cql.engineJackson) // Necessary to import Executable XML/JSON CQL libs
  implementation(Dependencies.Cql.evaluator)
  implementation(Dependencies.Cql.evaluatorBuilder)
  implementation(Dependencies.Cql.evaluatorDagger)
  implementation(Dependencies.Cql.evaluatorPlanDef)
  implementation(Dependencies.Cql.translatorCqlToElm) // Overrides HAPI's old versions
  implementation(Dependencies.Cql.translatorElm) // Overrides HAPI's old versions
  implementation(Dependencies.Cql.translatorElmJackson) // Necessary to import XML/JSON CQL Libs
  implementation(Dependencies.Cql.translatorModel) // Overrides HAPI's old versions
  implementation(Dependencies.Cql.translatorModelJackson) // Necessary to import XML/JSON ModelInfos
  implementation(Dependencies.timber)

  // Forces the most recent version of jackson, ignoring what dependencies use.
  // Remove these lines when HAPI 6.4 becomes available.
  implementation(Dependencies.Jackson.annotations)
  implementation(Dependencies.Jackson.bom)
  implementation(Dependencies.Jackson.core)
  implementation(Dependencies.Jackson.databind)
  implementation(Dependencies.Jackson.dataformatXml)
  implementation(Dependencies.Jackson.jaxbAnnotations)
  implementation(Dependencies.Jackson.jsr310)

  // Runtime dependency that is required to run FhirPath (also requires minSDK of 26).
  // Version 3.0 uses java.lang.System.Logger, which is not available on Android
  // Replace for Guava when this PR gets merged: https://github.com/hapifhir/hapi-fhir/pull/3977
  implementation(Dependencies.HapiFhir.caffeine)

  implementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)
  implementation(Dependencies.Kotlin.kotlinCoroutinesCore)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.xerces)
  implementation(project(":engine"))
  implementation(project(":knowledge"))

  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.jsonAssert)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
  testImplementation(Dependencies.xmlUnit)
  testImplementation(project(":testing"))
  testImplementation(project(":workflow-testing"))
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
          URL("https://github.com/google/android-fhir/tree/master/workflow/src/main/java")
        )
        remoteLineSuffix.set("#L")
      }
      externalDocumentationLink {
        url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/"))
        packageListUrl.set(
          URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/element-list")
        )
      }
      externalDocumentationLink {
        url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/"))
        packageListUrl.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/element-list"))
      }
    }
  }
}
