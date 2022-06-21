import java.net.URL

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
  // Use Dokka 1.6.10 until https://github.com/Kotlin/dokka/issues/2452 is resolved.
  id("org.jetbrains.dokka").version("1.6.10")
}

publishArtifact(Releases.DataCapture)

createJacocoTestReportTask()

android {
  compileSdk = Sdk.compileSdk
  buildToolsVersion = Plugins.Versions.buildTools

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
    // Sets Java compatibility to Java 8
    // See https://developer.android.com/studio/write/java8-support
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    // See https://developer.android.com/studio/write/java8-support
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
  configureJacocoTestOptions()

  testOptions { animationsDisabled = true }
}

configurations { all { exclude(module = "xpp3") } }

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.core)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.AndroidxTest.rules)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.truth)

  api(Dependencies.HapiFhir.structuresR4)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.androidFhirCommon)
  implementation(Dependencies.Androidx.appCompat)
  implementation(Dependencies.Androidx.constraintLayout)
  implementation(Dependencies.Androidx.coreKtx)
  implementation(Dependencies.Androidx.fragmentKtx)
  implementation(Dependencies.HapiFhir.validation) {
    exclude(module = "commons-logging")
    exclude(module = "httpclient")
    exclude(group = "net.sf.saxon", module = "Saxon-HE")
  }
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Lifecycle.viewModelKtx)
  implementation(Dependencies.material)
  implementation(Dependencies.flexBox)
  implementation(Dependencies.barcodeScanning)
  implementation(Dependencies.lifecycleExtensions)
  implementation(Dependencies.objectDetection)
  implementation(Dependencies.objectDetectionCustom)
  implementation(Dependencies.timber)

  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.AndroidxTest.fragmentTesting)
  testImplementation(Dependencies.Kotlin.kotlinTestJunit)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.mockitoInline)
  testImplementation(Dependencies.mockitoKotlin)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
  androidTestImplementation(Dependencies.Espresso.espressoCore)
}

tasks.dokkaHtml.configure {
  outputDirectory.set(buildDir.resolve("dokka"))
  suppressInheritedMembers.set(true)
  dokkaSourceSets {
    named("main") {
      moduleName.set("datacapture")
      moduleVersion.set("0.1.0-beta03")
      noAndroidSdkLink.set(false)
      externalDocumentationLink {
        url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/"))
        /* HAPI FHIR does not include a package-list file with their documentation. As a workaround,
           you can manually create one locally and point to it to get external doc links to work. */
        // packageListUrl.set(URL("file:///local/path/to/hapi-fhir/package-list"))
        packageListUrl.set(URL("file:///usr/local/google/home/williamito/Downloads/hapi-package-list"))
      }
    }
  }
}
