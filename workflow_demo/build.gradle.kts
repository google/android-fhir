plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.google.android.fhir.workflow.demo"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.google.android.fhir.workflow.demo"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    //    sourceCompatibility = JavaVersion.VERSION_1_8
    //    targetCompatibility = JavaVersion.VERSION_1_8
    // Flag to enable support for the new language APIs
    // See https://developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
  }
  //  kotlinOptions {
  //    jvmTarget = "1.8"
  //  }
  kotlin { jvmToolchain(11) }
  packaging {
    resources.excludes.addAll(
      listOf(
        "META-INF/ASL-2.0.txt",
        "META-INF/LGPL-3.0.txt",
        "META-INF/LICENSE.md",
        "META-INF/NOTICE.md",
        "META-INF/INDEX.LIST",
      ),
    )
  }
}

dependencies {
  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(libs.androidx.core)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.fragment)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.espresso.core)
  implementation(project(":engine"))

  implementation(project(":workflow")) {
    exclude(group = "com.google.android.fhir", module = "engine")
  }
  implementation(project(":workflow-testing")) {
    exclude(group = "com.google.android.fhir", module = "engine")
  }

  implementation(project(":knowledge")) {
    exclude(group = "com.google.android.fhir", module = "engine")
  }
  compileOnly(libs.opencds.cqf.fhir.cr)
  constraints {
    Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
      api(libName, constraints)
      implementation(libName, constraints)
    }
  }
}
