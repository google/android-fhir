plugins {
  id(BuildPlugins.application)
  id(BuildPlugins.kotlinAndroid)
  id(BuildPlugins.kotlinKapt)
}

android {
  compileSdkVersion(deps.Dependencies.Sdk.compileSdk)
  buildToolsVersion(deps.Plugins.Versions.buildTools)

  defaultConfig {
    applicationId("com.google.android.fhir.cqlreference")
    minSdkVersion(deps.Dependencies.Sdk.minSdk)
    targetSdkVersion(deps.Dependencies.Sdk.targetSdk)
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner(deps.Dependencies.androidJunitRunner)

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
  coreLibraryDesugaring(deps.Dependencies.desugarJdkLibs)

  api(deps.Dependencies.hapiFhirStructuresR4) { exclude(module = "junit") }

  implementation(deps.Dependencies.Androidx.appCompat)
  implementation(deps.Dependencies.Androidx.constraintLayout)
  implementation(deps.Dependencies.Androidx.workRuntimeKtx)
  implementation(deps.Dependencies.Cql.cqlEngine)
  implementation(deps.Dependencies.Cql.cqlEngineFhir)
  implementation(deps.Dependencies.Kotlin.androidxCoreKtx)
  implementation(deps.Dependencies.Kotlin.stdlib)
  implementation(deps.Dependencies.Kotlin.kotlinTestJunit)
  implementation(deps.Dependencies.Lifecycle.viewModelKtx)
  implementation(deps.Dependencies.material)

  testImplementation(deps.Dependencies.AndroidxTest.core)
  testImplementation(deps.Dependencies.AndroidxTest.junit)

  androidTestImplementation(deps.Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(deps.Dependencies.Espresso.espressoCore)
}
