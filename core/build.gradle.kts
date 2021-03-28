plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinKapt)
}

android {
  compileSdkVersion(Sdk.compileSdk)
  defaultConfig {
    minSdkVersion(Sdk.minSdk)
    targetSdkVersion(Sdk.targetSdk)
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner(Dependencies.androidJunitRunner)
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
  androidTestImplementation(Dependencies.AndroidxTest.core)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.truth)

  api(Dependencies.Cql.cqlEngine)
  api(Dependencies.hapiFhirStructuresR4) { exclude(module = "junit") }

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Room.runtime)
  implementation(Dependencies.Room.ktx)
  implementation(Dependencies.Androidx.workRuntimeKtx)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.caffeine)
  implementation(Dependencies.guava)
  implementation(Dependencies.jsonToolsPatch)

  kapt(Dependencies.Room.compiler)

  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.AndroidxTest.junit)
  testImplementation(Dependencies.roboelectric)
  testImplementation(Dependencies.truth)
}
