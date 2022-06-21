import java.net.URL

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinKapt)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
  // Use Dokka 1.6.10 until https://github.com/Kotlin/dokka/issues/2452 is resolved.
  id("org.jetbrains.dokka").version("1.6.10")
}

publishArtifact(Releases.Engine)

createJacocoTestReportTask()

android {
  compileSdk = Sdk.compileSdk
  defaultConfig {
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    testInstrumentationRunner = Dependencies.androidJunitRunner
    // need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir"
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
    resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt"))
  }

  // See https = //developer.android.com/studio/write/java8-support
  kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }

  configureJacocoTestOptions()
}

configurations {
  all {
    exclude(module = "json")
    exclude(module = "xpp3")
    exclude(group = "net.sf.saxon", module = "Saxon-HE")
    exclude(module = "hamcrest-all")
    exclude(module = "jakarta.activation-api")
    exclude(module = "javax.activation")
    exclude(module = "jakarta.xml.bind-api")
  }
}

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.core)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.AndroidxTest.workTestingRuntimeKtx)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.truth)

  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Androidx.datastorePref)
  implementation(Dependencies.Androidx.sqliteKtx)
  implementation(Dependencies.Androidx.workRuntimeKtx)
  implementation(Dependencies.HapiFhir.validation) {
    exclude(module = "commons-logging")
    exclude(module = "httpclient")
  }
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Lifecycle.liveDataKtx)
  implementation(Dependencies.Retrofit.coreRetrofit)
  implementation(Dependencies.Retrofit.gsonConverter)
  implementation(Dependencies.Room.ktx)
  implementation(Dependencies.Room.runtime)
  implementation(Dependencies.androidFhirCommon)
  implementation(Dependencies.guava)
  implementation(Dependencies.httpInterceptor)
  implementation(Dependencies.jsonToolsPatch)
  implementation(Dependencies.sqlcipher)
  implementation(Dependencies.timber)

  kapt(Dependencies.Room.compiler)

  testImplementation(Dependencies.AndroidxTest.archCore)
  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.AndroidxTest.workTestingRuntimeKtx)
  testImplementation(Dependencies.Kotlin.kotlinCoroutinesTest)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.mockitoInline)
  testImplementation(Dependencies.mockitoKotlin)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
}

// Generate SearchParameterRepositoryGenerated.kt.
tasks.getByName("build") { dependsOn(":codegen:runCodeGenerator") }

tasks.dokkaHtml.configure {
  outputDirectory.set(buildDir.resolve("dokka"))
  suppressInheritedMembers.set(true)
  dokkaSourceSets {
    named("main") {
      moduleName.set("engine")
      moduleVersion.set("0.1.0-beta01")
      noAndroidSdkLink.set(false)
      externalDocumentationLink {
        url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/"))
        packageListUrl.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/element-list"))
      }
    }
  }
}
