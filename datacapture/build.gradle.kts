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
  compileSdkVersion(versions.Sdk.compileSdk)
  buildToolsVersion(versions.Plugins.buildTools)

  defaultConfig {
    minSdkVersion(versions.Sdk.minSdk)
    targetSdkVersion(versions.Sdk.targetSdk)
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
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
  androidTestImplementation(deps.TestDependencies.AndroidxTest.core)
  androidTestImplementation(deps.TestDependencies.AndroidxTest.extJunit)
  androidTestImplementation(deps.TestDependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(deps.TestDependencies.AndroidxTest.runner)
  androidTestImplementation(deps.TestDependencies.truth)
  androidTestImplementation(deps.TestDependencies.AndroidxTest.rules)

  api(deps.AppDependencies.Cql.hapiR4)

  coreLibraryDesugaring(deps.AppDependencies.Androidx.desugar)

  implementation(deps.AppDependencies.Androidx.appCompat)
  implementation(deps.AppDependencies.Kotlin.androidxCoreKtx)
  implementation(deps.AppDependencies.Androidx.fragment)
  implementation(deps.AppDependencies.Kotlin.kotlin)
  implementation(deps.AppDependencies.Kotlin.kotlinTesting)
  implementation(deps.AppDependencies.Lifecycle.viewModel)
  implementation(deps.AppDependencies.Androidx.materialDesign)

  testImplementation(deps.TestDependencies.AndroidxTest.core)
  testImplementation(deps.TestDependencies.AndroidxTest.junit)
  testImplementation(deps.TestDependencies.roboelectric)
  testImplementation(deps.TestDependencies.truth)
}
