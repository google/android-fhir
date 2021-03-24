plugins {
  id(BuildPlugins.androidLib)
  id(BuildPlugins.kotlinAndroid)
  id(BuildPlugins.kotlinKapt)
}

val packageName = "com.google.android.fhir"
val pkg = "package"

android {
  compileSdkVersion(versions.Sdk.compileSdk)
  defaultConfig {
    minSdkVersion(versions.Sdk.minSdk)
    targetSdkVersion(versions.Sdk.targetSdk)
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner(deps.TestDependencies.standardRunner)
    // need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments(mapOf(pkg to packageName))
    // Required when setting minSdkVersion to 20 or lower
    // See https = //developer.android.com/studio/write/java8-support
    multiDexEnabled = true
  }

  sourceSets {
    getByName("androidTest").apply {
      java.srcDirs("src/test-common/java")
      resources.setSrcDirs(listOf("sampledata"))
    }

    getByName("test").apply {
      java.srcDirs("src/test-common/java")
      resources.setSrcDirs(listOf("sampledata"))
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    // Flag to enable support for the new language APIs
    // See https = //developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
    // Sets Java compatibility to Java 8
    // See https = //developer.android.com/studio/write/java8-support
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  packagingOptions {
    exclude("META-INF/ASL-2.0.txt")
    exclude("META-INF/LGPL-3.0.txt")
  }
  // See https = //developer.android.com/studio/write/java8-support

  kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
}

configurations {
  all {
    exclude(module = "json")
    exclude(module = "xpp3")
    exclude(module = "hamcrest-all")
    exclude(module = "jaxb-impl")
    exclude(module = "jaxb-core")
    exclude(module = "jakarta.activation-api")
    exclude(module = "javax.activation")
    exclude(module = "jakarta.xml.bind-api")
    // TODO =  the following line can be removed from the next CQL engine release.
    exclude(module = "hapi-fhir-jpaserver-base")
  }
}

dependencies {
  androidTestImplementation(deps.TestDependencies.CoreTestDeps.core)
  androidTestImplementation(deps.TestDependencies.CoreTestDeps.junit)
  androidTestImplementation(deps.TestDependencies.CoreTestDeps.extJunitKtx)
  androidTestImplementation(deps.TestDependencies.CoreTestDeps.runner)
  androidTestImplementation(deps.TestDependencies.truth)

  api(deps.AppDependencies.CoreDeps.Cql.cqlEngineCore)
  api(deps.AppDependencies.CoreDeps.Cql.hapiR4) { exclude(module = "junit") }

  coreLibraryDesugaring(deps.AppDependencies.CoreDeps.desugar)

  implementation(deps.AppDependencies.Kotlin.kotlin)

  implementation(deps.AppDependencies.CoreDeps.Room.runtime)
  implementation(deps.AppDependencies.CoreDeps.Room.ktx)
  implementation(deps.AppDependencies.CoreDeps.work)
  implementation(deps.AppDependencies.Externals.jsonTools)
  implementation(deps.AppDependencies.Externals.guava)
  implementation(deps.AppDependencies.Externals.caffeine)

  kapt(deps.AppDependencies.CoreDeps.Room.compiler)

  testImplementation(deps.TestDependencies.CoreTestDeps.core)
  testImplementation(deps.TestDependencies.CoreTestDeps.junit)
  testImplementation(deps.TestDependencies.truth)
  testImplementation(deps.TestDependencies.roboelectric)
}
