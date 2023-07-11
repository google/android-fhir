import codegen.GenerateSourcesTask
import java.net.URL

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinKapt)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
  id(Plugins.BuildPlugins.dokka).version(Plugins.Versions.dokka)
}

publishArtifact(Releases.Engine)

createJacocoTestReportTask()

val generateSourcesTask =
  project.tasks.register("generateSearchParamsTask", GenerateSourcesTask::class) {
    srcOutputDir.set(project.layout.buildDirectory.dir("gen/main"))
    testOutputDir.set(project.layout.buildDirectory.dir("gen/test"))
  }

kotlin {
  sourceSets {
    val main by getting
    val test by getting
    main.kotlin.srcDirs(generateSourcesTask.map { it.srcOutputDir })
    test.kotlin.srcDirs(generateSourcesTask.map { it.testOutputDir })
  }
}

android {
  compileSdk = Sdk.compileSdk
  defaultConfig {
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    testInstrumentationRunner = Dependencies.androidJunitRunner
    // need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir"

    javaCompileOptions {
      annotationProcessorOptions {
        compilerArgumentProviders(RoomSchemaArgProvider(File(projectDir, "schemas")))
      }
    }
  }

  sourceSets {
    getByName("androidTest").apply {
      resources.setSrcDirs(listOf("test-data"))
      assets.srcDirs("$projectDir/schemas")
    }

    getByName("test").apply { resources.setSrcDirs(listOf("test-data")) }
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
    sourceCompatibility = Java.sourceCompatibility
    targetCompatibility = Java.targetCompatibility
  }

  packagingOptions {
    resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt"))
  }

  kotlinOptions { jvmTarget = Java.kotlinJvmTarget.toString() }

  configureJacocoTestOptions()
}

afterEvaluate { configureFirebaseTestLab() }

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
  androidTestImplementation(Dependencies.Room.testing)
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
  implementation(Dependencies.Room.ktx)
  implementation(Dependencies.Room.runtime)
  implementation(Dependencies.androidFhirCommon)
  implementation(Dependencies.guava)
  implementation(Dependencies.httpInterceptor)
  implementation(Dependencies.jsonToolsPatch)
  implementation(Dependencies.sqlcipher)
  implementation(Dependencies.timber)
  implementation(Dependencies.truth)

  kapt(Dependencies.Room.compiler)

  testImplementation(Dependencies.AndroidxTest.archCore)
  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.AndroidxTest.workTestingRuntimeKtx)
  testImplementation(Dependencies.Kotlin.kotlinCoroutinesTest)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.mockitoInline)
  testImplementation(Dependencies.mockitoKotlin)
  testImplementation(Dependencies.mockWebServer)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
}

tasks.dokkaHtml.configure {
  outputDirectory.set(file("../docs/${Releases.Engine.artifactId}/${Releases.Engine.version}"))
  suppressInheritedMembers.set(true)
  dokkaSourceSets {
    named("main") {
      moduleName.set(Releases.Engine.artifactId)
      moduleVersion.set(Releases.Engine.version)
      noAndroidSdkLink.set(false)
      sourceLink {
        localDirectory.set(file("src/main/java"))
        remoteUrl.set(
          URL("https://github.com/google/android-fhir/tree/master/engine/src/main/java")
        )
        remoteLineSuffix.set("#L")
      }
      externalDocumentationLink {
        url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/"))
        packageListUrl.set(
          URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/element-list")
        )
      }
    }
  }
}
