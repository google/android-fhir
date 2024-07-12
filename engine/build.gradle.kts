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
  compileSdk = Sdk.COMPILE_SDK
  defaultConfig {
    minSdk = Sdk.MIN_SDK
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
  androidTestImplementation(libs.androidx.room.testing)
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit.ktx)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.work.testing)
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.truth)

  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  // We have removed the dependency on Caffeine from HAPI due to conflicts with android
  // Guava Caching must be individually loaded instead.
  implementation(Dependencies.HapiFhir.guavaCaching)
  // Validation to load system types into FhirPath's Context
  // The loading happens via a ResourceStream in XML and thus
  // XML parsers are also necessary.
  implementation(Dependencies.HapiFhir.validationR4)
  implementation(Dependencies.HapiFhir.validation) {
    exclude(module = "commons-logging")
    exclude(module = "httpclient")
  }
  implementation(Dependencies.Retrofit.coreRetrofit)
  implementation(Dependencies.androidFhirCommon)
  implementation(Dependencies.guava)
  implementation(Dependencies.httpInterceptor)
  implementation(Dependencies.jsonToolsPatch)
  implementation(Dependencies.sqlcipher)
  implementation(Dependencies.timber)
  implementation(Dependencies.woodstox)
  implementation(Dependencies.xerces)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.room.room)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.sqlite)
  implementation(libs.androidx.work.runtime)
  implementation(libs.kotlin.stdlib)
  implementation(libs.truth)

  ksp(libs.androidx.room.compiler)

  testImplementation(Dependencies.jsonAssert)
  testImplementation(Dependencies.mockitoInline)
  testImplementation(Dependencies.mockitoKotlin)
  testImplementation(Dependencies.mockWebServer)
  testImplementation(Dependencies.robolectric)
  testImplementation(libs.androidx.arch.core.testing)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.work.testing)
  testImplementation(libs.junit)
  testImplementation(libs.kotlin.test.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.truth)

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
