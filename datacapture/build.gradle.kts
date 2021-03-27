plugins {
  id(BuildPlugins.androidLib)
  id(BuildPlugins.kotlinAndroid)
  id(BuildPlugins.mavenPublish)
}

val group = "com.google.android.fhir"
val version = "0.1.0-alpha01"

tasks {
  val sourcesJar by creating(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
  }
  artifacts { add("archives", sourcesJar) }
}

afterEvaluate {
  publishing {
    publications {
      register("release", MavenPublication::class) {
        from(components["release"])
        artifactId = "data-capture"
        // Also publish source code for developers" convenience
        pom {
          name.set("Android FHIR Structured Data Capture Library")
          licenses {
            license {
              name.set("The Apache License, Version 2.0")
              url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
          }
        }
      }
    }
  }
}

android {
  compileSdkVersion(deps.Dependencies.Sdk.compileSdk)
  buildToolsVersion(deps.Plugins.Versions.buildTools)

  defaultConfig {
    minSdkVersion(deps.Dependencies.Sdk.minSdk)
    targetSdkVersion(deps.Dependencies.Sdk.targetSdk)
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner(deps.Dependencies.androidJunitRunner)
    // Need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments(mapOf("package" to "com.google.android.fhir.datacapture"))
  }

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
}

configurations { all { exclude(module = "xpp3") } }

dependencies {

  api(deps.Dependencies.hapiFhirStructuresR4)

  coreLibraryDesugaring(deps.Dependencies.desugarJdkLibs)

  implementation(deps.Dependencies.Androidx.appCompat)
  implementation(deps.Dependencies.Androidx.fragmentKtx)
  implementation(deps.Dependencies.Kotlin.androidxCoreKtx)
  implementation(deps.Dependencies.Kotlin.kotlinTestJunit)
  implementation(deps.Dependencies.Kotlin.stdlib)
  implementation(deps.Dependencies.Lifecycle.viewModelKtx)
  implementation(deps.Dependencies.material)

  testImplementation(deps.Dependencies.AndroidxTest.core)
  testImplementation(deps.Dependencies.AndroidxTest.junit)
  testImplementation(deps.Dependencies.roboelectric)
  testImplementation(deps.Dependencies.truth)

  androidTestImplementation(deps.Dependencies.AndroidxTest.core)
  androidTestImplementation(deps.Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(deps.Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(deps.Dependencies.AndroidxTest.rules)
  androidTestImplementation(deps.Dependencies.AndroidxTest.runner)
  androidTestImplementation(deps.Dependencies.truth)
}
