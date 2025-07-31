import Dependencies.removeIncompatibleDependencies
import java.net.URL

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinCompose)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
  id(Plugins.BuildPlugins.dokka).version(Plugins.Versions.dokka)
}

publishArtifact(Releases.DataCapture)

createJacocoTestReportTask()

android {
  namespace = "com.google.android.fhir.datacapture"
  compileSdk = Sdk.COMPILE_SDK
  defaultConfig {
    minSdk = Sdk.MIN_SDK
    testInstrumentationRunner = Dependencies.androidJunitRunner
    // Need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir.datacapture"
    consumerProguardFile("proguard-rules.pro")
  }

  buildFeatures {
    viewBinding = true
    compose = true
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }
  compileOptions {
    // Flag to enable support for the new language APIs
    // See https://developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
  }

  packaging {
    resources.excludes.addAll(
      listOf("META-INF/ASL2.0", "META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt"),
    )
  }

  configureJacocoTestOptions()

  sourceSets {
    getByName("androidTest").apply { resources.setSrcDirs(listOf("sampledata")) }

    getByName("test").apply { resources.setSrcDirs(listOf("sampledata")) }
  }

  testOptions { animationsDisabled = true }
  kotlin { jvmToolchain(11) }
}

afterEvaluate { configureFirebaseTestLabForLibraries() }

configurations {
  all {
    exclude(module = "xpp3")
    exclude(group = "net.sf.saxon", module = "Saxon-HE")
    removeIncompatibleDependencies()
  }
}

dependencies {
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.test.espresso.contrib) {
    // build fails with error "Duplicate class found" (org.checkerframework.checker.*)
    exclude(group = "org.checkerframework", module = "checker")
  }
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.ext.junit.ktx)
  androidTestImplementation(libs.androidx.test.rules)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.kotlinx.coroutines.test)
  androidTestImplementation(libs.truth)

  api(Dependencies.HapiFhir.structuresR4)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.HapiFhir.guavaCaching)
  implementation(Dependencies.HapiFhir.validation) {
    exclude(module = "commons-logging")
    exclude(module = "httpclient")
  }
  implementation(Dependencies.timber)
  implementation(libs.android.fhir.common)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.core)
  implementation(libs.androidx.fragment)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.glide)
  implementation(libs.kotlin.stdlib)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.material)

  // Androidx Compose
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.accompanist.themeadapter.material3)

  testImplementation(Dependencies.mockitoInline)
  testImplementation(Dependencies.mockitoKotlin)
  testImplementation(Dependencies.robolectric)
  testImplementation(project(":knowledge")) {
    exclude(group = "com.google.android.fhir", module = "engine")
  }
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.fragment.testing)
  testImplementation(libs.junit)
  testImplementation(libs.kotlin.test.junit)
  testImplementation(libs.kotlinx.coroutines.test)
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
    file("../docs/use/api/${Releases.DataCapture.artifactId}/${Releases.DataCapture.version}"),
  )
  suppressInheritedMembers.set(true)
  dokkaSourceSets {
    named("main") {
      moduleName.set(Releases.DataCapture.name)
      moduleVersion.set(Releases.DataCapture.version)
      includes.from("Module.md")
      sourceLink {
        localDirectory.set(file("src/main/java"))
        remoteUrl.set(
          URL("https://github.com/google/android-fhir/tree/master/datacapture/src/main/java"),
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
