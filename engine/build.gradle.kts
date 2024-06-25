import codegen.GenerateSearchParamsTask
import java.net.URL

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.kotlinKsp)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
  id(Plugins.BuildPlugins.dokka).version(Plugins.Versions.dokka)
}

publishArtifact(Releases.Engine)

createJacocoTestReportTask()

val generateSearchParamsTask =
  project.tasks.register("generateSearchParamsTask", GenerateSearchParamsTask::class) {
    srcOutputDir.set(project.layout.buildDirectory.dir("gen/main"))
    testOutputDir.set(project.layout.buildDirectory.dir("gen/test"))
  }

kotlin {
  sourceSets {
    val main by getting
    val test by getting
    main.kotlin.srcDirs(generateSearchParamsTask.map { it.srcOutputDir })
    test.kotlin.srcDirs(generateSearchParamsTask.map { it.testOutputDir })
  }
  jvmToolchain(11)
}

android {
  namespace = "com.google.android.fhir"
  compileSdk = Sdk.compileSdk
  defaultConfig {
    minSdk = Sdk.minSdk
    testInstrumentationRunner = Dependencies.androidJunitRunner
    // need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir"
    consumerProguardFile("proguard-rules.pro")

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
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    // Flag to enable support for the new language APIs
    // See https = //developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
  }

  packaging { resources.excludes.addAll(listOf("META-INF/ASL-2.0.txt", "META-INF/LGPL-3.0.txt")) }

  configureJacocoTestOptions()
}

afterEvaluate { configureFirebaseTestLabForLibraries() }

configurations {
  all {
    exclude(module = "json")
    exclude(module = "xpp3")
    exclude(group = "net.sf.saxon", module = "Saxon-HE")
    exclude(module = "hamcrest-all")
    exclude(module = "jakarta.activation-api")
    exclude(module = "javax.activation")
    exclude(module = "jakarta.xml.bind-api")
    exclude(module = "hapi-fhir-caching-caffeine")
    exclude(group = "com.github.ben-manes.caffeine", module = "caffeine")
    exclude(module = "jcl-over-slf4j")
  }
}

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.core)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.truth)
  androidTestImplementation(libs.androidx.room.testing)
  androidTestImplementation(libs.androidx.work.testing)

  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }

  // We have removed the dependency on Caffeine from HAPI due to conflicts with android
  // Guave Caching must be individually loaded instead.
  implementation(Dependencies.HapiFhir.guavaCaching)

  // Validation to load system types into FhirPath's Context
  // The loading happens via a ResourceStream in XML and thus
  // XML parsers are also necessary.
  implementation(Dependencies.HapiFhir.validationR4)
  implementation(Dependencies.woodstox)
  implementation(Dependencies.xerces)

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.HapiFhir.validation) {
    exclude(module = "commons-logging")
    exclude(module = "httpclient")
  }
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.Retrofit.coreRetrofit)
  implementation(Dependencies.androidFhirCommon)
  implementation(Dependencies.guava)
  implementation(Dependencies.httpInterceptor)
  implementation(Dependencies.jsonToolsPatch)
  implementation(Dependencies.sqlcipher)
  implementation(Dependencies.timber)
  implementation(Dependencies.truth)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.room.room)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.sqlite)
  implementation(libs.androidx.work.runtime)

  ksp(libs.androidx.room.compiler)

  testImplementation(Dependencies.AndroidxTest.archCore)
  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.Kotlin.kotlinCoroutinesTest)
  testImplementation(Dependencies.Kotlin.kotlinTestJunit)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.jsonAssert)
  testImplementation(Dependencies.mockitoInline)
  testImplementation(Dependencies.mockitoKotlin)
  testImplementation(Dependencies.mockWebServer)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
  testImplementation(libs.androidx.work.testing)

  constraints {
    Dependencies.hapiFhirConstraints().forEach { (libName, constraints) ->
      api(libName, constraints)
      implementation(libName, constraints)
    }
  }
}

tasks.dokkaHtml.configure {
  outputDirectory.set(
    file("../docs/use/api/${Releases.Engine.artifactId}/${Releases.Engine.version}"),
  )
  suppressInheritedMembers.set(true)
  dokkaSourceSets {
    named("main") {
      moduleName.set(Releases.Engine.name)
      moduleVersion.set(Releases.Engine.version)
      includes.from("Module.md")
      sourceLink {
        localDirectory.set(file("src/main/java"))
        remoteUrl.set(
          URL("https://github.com/google/android-fhir/tree/master/engine/src/main/java"),
        )
        remoteLineSuffix.set("#L")
      }
      externalDocumentationLink {
        url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/"))
        packageListUrl.set(
          URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/element-list"),
        )
      }
    }
  }
}
