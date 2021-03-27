plugins {
  id(BuildPlugins.androidLib)
  id(BuildPlugins.kotlinAndroid)
  id(BuildPlugins.kotlinKapt)
}

android {
  compileSdkVersion(deps.Dependencies.Sdk.compileSdk)
  defaultConfig {
    minSdkVersion(deps.Dependencies.Sdk.minSdk)
    targetSdkVersion(deps.Dependencies.Sdk.targetSdk)
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner(deps.Dependencies.androidJunitRunner)
    // need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments(mapOf("package" to "com.google.android.fhir"))
    // Required when setting minSdkVersion to 20 or lower
    // See https://developer.android.com/studio/write/java8-support
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
  api(deps.Dependencies.Cql.cqlEngine)
  api(deps.Dependencies.hapiFhirStructuresR4) { exclude(module = "junit") }

  coreLibraryDesugaring(deps.Dependencies.desugarJdkLibs)

  implementation(deps.Dependencies.Androidx.Room.runtime)
  implementation(deps.Dependencies.Androidx.Room.ktx)
  implementation(deps.Dependencies.Androidx.workRuntimeKtx)
  implementation(deps.Dependencies.Kotlin.stdlib)
  implementation(deps.Dependencies.caffeine)
  implementation(deps.Dependencies.guava)
  implementation(deps.Dependencies.jsonToolsPatch)

  kapt(deps.Dependencies.Androidx.Room.compiler)

  testImplementation(deps.Dependencies.AndroidxTest.core)
  testImplementation(deps.Dependencies.AndroidxTest.junit)
  testImplementation(deps.Dependencies.roboelectric)
  testImplementation(deps.Dependencies.truth)

  androidTestImplementation(deps.Dependencies.AndroidxTest.core)
  androidTestImplementation(deps.Dependencies.AndroidxTest.junit)
  androidTestImplementation(deps.Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(deps.Dependencies.AndroidxTest.runner)
  androidTestImplementation(deps.Dependencies.truth)
}
