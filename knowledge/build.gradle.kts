import Dependencies.removeIncompatibleDependencies
import java.net.URL

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinKsp)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
  id(Plugins.BuildPlugins.dokka).version(Plugins.Versions.dokka)
}

publishArtifact(Releases.Knowledge)

createJacocoTestReportTask()

android {
  namespace = "com.google.android.fhir.knowledge"
  compileSdk = Sdk.compileSdk
  defaultConfig {
    minSdk = Sdk.minSdk
    testInstrumentationRunner = Dependencies.androidJunitRunner
    // Need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir.knowledge"
  }

  sourceSets {
    getByName("androidTest").apply { resources.setSrcDirs(listOf("testdata")) }

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
  androidTestImplementation(Dependencies.AndroidxTest.core)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.Kotlin.kotlinCoroutinesTest)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.truth)

  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }
  api(Dependencies.HapiFhir.guavaCaching)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Kotlin.kotlinCoroutinesCore)
  implementation(Dependencies.timber)
  implementation(Dependencies.http)
  implementation(Dependencies.HapiFhir.fhirCoreConvertors)
  implementation(Dependencies.apacheCommonsCompress)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.room.room)
  implementation(libs.androidx.room.runtime)

  ksp(libs.androidx.room.compiler)

  testImplementation(Dependencies.AndroidxTest.archCore)
  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.Kotlin.kotlinCoroutinesTest)
  testImplementation(Dependencies.mockitoInline)
  testImplementation(Dependencies.mockitoKotlin)
  testImplementation(Dependencies.mockWebServer)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)

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
