plugins {
  id(Plugins.BuildPlugins.application)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinKapt)
}

android {
  compileSdkVersion(Sdk.compileSdk)
  buildToolsVersion(Plugins.Versions.buildTools)

  defaultConfig {
    applicationId("com.google.android.fhir.cqlreference")
    minSdkVersion(Sdk.minSdk)
    targetSdkVersion(Sdk.targetSdk)
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner(Dependencies.androidJunitRunner)

    multiDexEnabled = true
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  packagingOptions {
    exclude("META-INF/ASL-2.0.txt")
    exclude("META-INF/LGPL-3.0.txt")
  }
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
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.Espresso.espressoCore)

  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Androidx.appCompat)
  implementation(Dependencies.Androidx.constraintLayout)
  implementation(Dependencies.Androidx.workRuntimeKtx)
  implementation(Dependencies.Cql.cqlEngine)
  implementation(Dependencies.Cql.cqlEngineFhir)
  implementation(Dependencies.Kotlin.androidxCoreKtx)
  implementation(Dependencies.Kotlin.kotlinTestJunit)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Lifecycle.viewModelKtx)
  implementation(Dependencies.material)

  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.junit)
}
