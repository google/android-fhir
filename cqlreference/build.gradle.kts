plugins {
  id(BuildPlugins.application)
  id(BuildPlugins.kotlinAndroid)
  id(BuildPlugins.kotlinKapt)
}

android {
  compileSdkVersion(versions.Sdk.compileSdk)
  buildToolsVersion(versions.Plugins.buildTools)

  defaultConfig {
    applicationId("com.google.android.fhir.cqlreference")
    minSdkVersion(versions.Sdk.minSdk)
    targetSdkVersion(versions.Sdk.targetSdk)
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner(deps.TestDependencies.standardRunner)

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
  coreLibraryDesugaring(deps.AppDependencies.Androidx.desugar)
  api(deps.AppDependencies.Cql.hapiR4) { exclude(module = "junit") }

  implementation(deps.AppDependencies.Cql.cqlEngineCore)
  implementation(deps.AppDependencies.Cql.cqlEngineFhir)
  implementation(deps.AppDependencies.Androidx.work)
  implementation(deps.AppDependencies.Kotlin.kotlin)
  implementation(deps.AppDependencies.Kotlin.androidxCoreKtx)
  implementation(deps.AppDependencies.Kotlin.kotlinTesting)
  implementation(deps.AppDependencies.Androidx.appCompat)
  implementation(deps.AppDependencies.Androidx.constraintLayout)
  implementation(deps.AppDependencies.Lifecycle.viewModelKtx)
  implementation(deps.AppDependencies.Androidx.materialDesign)

  testImplementation(deps.TestDependencies.AndroidxTest.junit)
  testImplementation(deps.TestDependencies.AndroidxTest.core)

  androidTestImplementation(deps.TestDependencies.AndroidxTest.extJunit)
  androidTestImplementation(deps.TestDependencies.Espresso.espresso)
}
