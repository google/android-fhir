import Dependencies.forceGuava

plugins {
  id(Plugins.BuildPlugins.application)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinKapt)
  id(Plugins.BuildPlugins.navSafeArgs)
}

configureRuler()

android {
  namespace = "com.google.android.fhir.configurablecare"
  compileSdk = Sdk.compileSdk
  defaultConfig {
    applicationId = Releases.ConfigurableCare.applicationId
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    versionCode = Releases.ConfigurableCare.versionCode
    versionName = Releases.ConfigurableCare.versionName
    testInstrumentationRunner = Dependencies.androidJunitRunner
  }
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures {
    buildConfig = true
    viewBinding = true
  }
  compileOptions {
    // Flag to enable support for the new language APIs
    // See https://developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
  }

  packaging {
    resources.excludes.addAll(
      listOf(
        "META-INF/ASL-2.0.txt",
        "META-INF/LGPL-3.0.txt",
        "META-INF/LICENSE.md",
        "META-INF/NOTICE.md",
        "META-INF/sun-jaxb.episode",
        "META-INF/DEPENDENCIES"
      )
    )
  }

  // packaging { resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt")) }
  // packaging {
  //   resources.excludes.addAll(listOf(
  //   "META-INF/DEPENDENCIES",
  //   "META-INF/NOTICE",
  //   "META-INF/NOTICE.md",
  //   "META-INF/LICENSE",
  //   "META-INF/LICENSE.md",
  //   "META-INF/LICENSE.txt",
  //   "META-INF/NOTICE.txt"))
  // }
  kotlin { jvmToolchain(11) }
}

// sourceSets {
//   // getByName("androidTest").apply { resources.setSrcDirs(listOf("sampledata")) }
//
//   getByName("com.google.android.fhir.configurablecare").apply { resources.setSrcDirs(listOf("sampledata")) }
// }

configurations { all { forceGuava() } }

dependencies {
  // androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  // androidTestImplementation(Dependencies.Espresso.espressoCore)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Androidx.activity)
  implementation(Dependencies.Androidx.appCompat)
  implementation(Dependencies.Androidx.constraintLayout)
  implementation(Dependencies.Androidx.datastorePref)
  implementation(Dependencies.Androidx.fragmentKtx)
  implementation(Dependencies.Androidx.recyclerView)
  implementation(Dependencies.Androidx.workRuntimeKtx)
  implementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)
  implementation(Dependencies.Kotlin.kotlinCoroutinesCore)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Lifecycle.liveDataKtx)
  implementation(Dependencies.Lifecycle.runtime)
  implementation(Dependencies.Lifecycle.viewModelKtx)
  implementation(Dependencies.Navigation.navFragmentKtx)
  implementation(Dependencies.Navigation.navUiKtx)
  implementation(Dependencies.material)
  implementation(Dependencies.timber)
  implementation(Dependencies.gson)
  implementation(project(":engine"))
  implementation(project(":datacapture")) {
    // exclude(group = Dependencies.androidFhirGroup, module = Dependencies.androidFhirEngineModule)
  }
  implementation(project(":knowledge")) {
    // exclude(group = Dependencies.androidFhirGroup, module = Dependencies.androidFhirEngineModule)
    exclude(group = "org.apache.commons", module = "compress:${Dependencies.Versions.apacheCommonsCompress}")
    exclude(group = "org.apache.commons.logging.Log", module = "commons-logging-1.2")
  }
  implementation(project(":workflow")) {
    // exclude(group = Dependencies.androidFhirGroup, module = Dependencies.androidFhirEngineModule)
    // exclude(group = Dependencies.androidFhirGroup, module = Dependencies.androidFhirKnowledgeModule)
  }
  // testImplementation(Dependencies.junit)
}
