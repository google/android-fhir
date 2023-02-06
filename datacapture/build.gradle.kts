import java.net.URL

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
  id(Plugins.BuildPlugins.dokka).version(Plugins.Versions.dokka)
}

publishArtifact(Releases.DataCapture)

createJacocoTestReportTask()

android {
  compileSdk = Sdk.compileSdk

  defaultConfig {
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    testInstrumentationRunner = Dependencies.androidJunitRunner
    // Need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir.datacapture"
  }

  buildFeatures { viewBinding = true }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }
  compileOptions {
    // Flag to enable support for the new language APIs
    // See https://developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true

    sourceCompatibility = Java.sourceCompatibility
    targetCompatibility = Java.targetCompatibility
  }

  packagingOptions {
    resources.excludes.addAll(
      listOf("META-INF/ASL2.0", "META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt")
    )
  }

  kotlinOptions { jvmTarget = Java.kotlinJvmTarget.toString() }
  configureJacocoTestOptions()

  sourceSets { getByName("androidTest").apply { resources.setSrcDirs(listOf("sampledata")) } }

  testOptions { animationsDisabled = true }
}

afterEvaluate { configureFirebaseTestLab() }

configurations { all { exclude(module = "xpp3") } }

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.core)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.AndroidxTest.rules)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.truth)
  androidTestImplementation(Dependencies.Espresso.espressoCore)
  androidTestImplementation(Dependencies.Espresso.espressoContrib) {
    // build fails with error "Duplicate class found" (org.checkerframework.checker.*)
    exclude(group = "org.checkerframework", module = "checker")
  }
  api(Dependencies.HapiFhir.structuresR4)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.androidFhirCommon)
  implementation(Dependencies.Androidx.appCompat)
  implementation(Dependencies.Androidx.constraintLayout)
  implementation(Dependencies.Androidx.coreKtx)
  implementation(Dependencies.Androidx.fragmentKtx)
  implementation(Dependencies.Glide.glide)
  implementation(Dependencies.HapiFhir.validation) {
    exclude(module = "commons-logging")
    exclude(module = "httpclient")
    exclude(group = "net.sf.saxon", module = "Saxon-HE")
  }
  implementation(Dependencies.Kotlin.kotlinCoroutinesCore)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Lifecycle.viewModelKtx)
  implementation(Dependencies.material)
  implementation(Dependencies.lifecycleExtensions)
  implementation(Dependencies.timber)

  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.AndroidxTest.fragmentTesting)
  testImplementation(Dependencies.Kotlin.kotlinCoroutinesTest)
  testImplementation(Dependencies.Kotlin.kotlinTestJunit)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.mockitoInline)
  testImplementation(Dependencies.mockitoKotlin)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
  testImplementation(project(":testing"))
}

tasks.dokkaHtml.configure {
  outputDirectory.set(
    file("../docs/${Releases.DataCapture.artifactId}/${Releases.DataCapture.version}")
  )
  suppressInheritedMembers.set(true)
  dokkaSourceSets {
    named("main") {
      moduleName.set(Releases.DataCapture.artifactId)
      moduleVersion.set(Releases.DataCapture.version)
      noAndroidSdkLink.set(false)
      sourceLink {
        localDirectory.set(file("src/main/java"))
        remoteUrl.set(
          URL("https://github.com/google/android-fhir/tree/master/datacapture/src/main/java")
        )
        remoteLineSuffix.set("#L")
      }
      externalDocumentationLink {
        url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/"))
        packageListUrl.set(
          URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/element-list")
        )
      }
    }
  }
}
