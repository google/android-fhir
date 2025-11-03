plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.google.android.fhir.workflow.demo"
  compileSdk = Sdk.COMPILE_SDK

  defaultConfig {
    applicationId = Releases.WorkflowDemo.applicationId
    minSdk = Sdk.MIN_SDK
    targetSdk = Sdk.TARGET_SDK
    versionCode = Releases.WorkflowDemo.versionCode
    versionName = Releases.WorkflowDemo.versionName
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
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.test.ext.junit)

  compileOnly(libs.opencds.cqf.fhir.cr)
  coreLibraryDesugaring(libs.desugar.jdk.libs)

  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.core)
  implementation(libs.androidx.fragment)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.material)
  implementation(project(":engine"))
  implementation(project(":knowledge")) {
    exclude(group = "com.google.android.fhir", module = "engine")
  }
  implementation(project(":workflow")) {
    exclude(group = "com.google.android.fhir", module = "engine")
    exclude(group = "com.google.android.fhir", module = "knowledge")
  }
  implementation(project(":workflow-testing"))

  testImplementation(libs.junit)
  constraints {
    Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
      api(libName, constraints)
      implementation(libName, constraints)
    }
  }
}
