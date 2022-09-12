plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
}

publishArtifact(Releases.Workflow)

createJacocoTestReportTask()

android {
  compileSdk = Sdk.compileSdk

  defaultConfig {
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    testInstrumentationRunner = Dependencies.androidJunitRunner
    // Need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir.workflow"
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

configurations {
  all {
    exclude(module = "xpp3")
    exclude(module = "jcl-over-slf4j")
    exclude(group = "org.apache.httpcomponents")
    // Remove this after this issue has been fixed:
    // https://github.com/cqframework/clinical_quality_language/issues/799
    exclude(module = "antlr4")
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
  androidTestImplementation(project(":testing"))
  androidTestImplementation(project(":workflow-testing"))

  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Androidx.coreKtx)

  // Remove this after this issue has been fixed:
  // https://github.com/cqframework/clinical_quality_language/issues/799
  implementation(Dependencies.Cql.antlr4Runtime)

  implementation(Dependencies.Cql.engine)
  implementation(Dependencies.Cql.engineJackson) // Necessary to import Executable XML/JSON CQL libs
  implementation(Dependencies.Cql.evaluator)
  implementation(Dependencies.Cql.evaluatorBuilder)
  implementation(Dependencies.Cql.evaluatorDagger)
  implementation(Dependencies.Cql.evaluatorPlanDef)
  implementation(Dependencies.Cql.translatorCqlToElm) // Overrides HAPI's old versions
  implementation(Dependencies.Cql.translatorElm) // Overrides HAPI's old versions
  implementation(Dependencies.Cql.translatorElmJackson) // Necessary to import XML/JSON CQL Libs
  implementation(Dependencies.Cql.translatorFhirR4) // Overrides HAPI's old versions
  implementation(Dependencies.Cql.translatorModel) // Overrides HAPI's old versions
  implementation(Dependencies.Cql.translatorModelJackson) // Necessary to import XML/JSON ModelInfos
  implementation(Dependencies.Cql.translatorQDM) // Overrides HAPI's old versions

  // Runtime dependency that is required to run FhirPath (also requires minSDK of 26).
  // Version 3.0 uses java.lang.System.Logger, which is not available on Android
  // Replace for Guava when this PR gets merged: https://github.com/hapifhir/hapi-fhir/pull/3977
  implementation(Dependencies.HapiFhir.caffeine)

  implementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)
  implementation(Dependencies.Kotlin.kotlinCoroutinesCore)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.xerces)
  implementation(project(":engine"))

  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.jsonAssert)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
  testImplementation(project(":testing"))
  testImplementation(project(":workflow-testing"))
}

configureDokka(Releases.Workflow.artifactId, Releases.Workflow.version)
