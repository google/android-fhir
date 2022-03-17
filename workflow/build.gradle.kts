import Releases.useApache2License

plugins {
  id(Plugins.BuildPlugins.androidLib)
  id(Plugins.BuildPlugins.kotlinAndroid)
  id(Plugins.BuildPlugins.mavenPublish)
  jacoco
}

afterEvaluate {
  publishing {
    publications {
      register("release", MavenPublication::class) {
        from(components["release"])
        groupId = Releases.groupId
        artifactId = Releases.Workflow.artifactId
        version = Releases.Workflow.version
        // Also publish source code for developers' convenience
        artifact(
          tasks.create<Jar>("androidSourcesJar") {
            archiveClassifier.set("sources")
            from(android.sourceSets.getByName("main").java.srcDirs)
          }
        )
        pom {
          name.set(Releases.Workflow.name)
          useApache2License()
        }
      }
    }
  }
}

createJacocoTestReportTask()

android {
  compileSdk = Sdk.compileSdk
  buildToolsVersion = Plugins.Versions.buildTools

  defaultConfig {
    minSdk = Sdk.minSdk
    targetSdk = Sdk.targetSdk
    testInstrumentationRunner = Dependencies.androidJunitRunner
    // Need to specify this to prevent junit runner from going deep into our dependencies
    testInstrumentationRunnerArguments["package"] = "com.google.android.fhir.workflow"
    // Required when setting minSdkVersion to 20 or lower
    // See https://developer.android.com/studio/write/java8-support
    multiDexEnabled = true
  }

  sourceSets { getByName("test").apply { resources.setSrcDirs(listOf("testdata")) } }

  tasks.withType<Test>().configureEach {
    testLogging.showStandardStreams = true
    minHeapSize = "512m"
    maxHeapSize = "4096m"
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }

  compileOptions {
    // Flag to enable support for the new language APIs
    // See https://developer.android.com/studio/write/java8-support
    isCoreLibraryDesugaringEnabled = true
    // Sets Java compatibility to Java 8
    // See https://developer.android.com/studio/write/java8-support
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  packagingOptions {
    resources.excludes.addAll(
      listOf(
        "license.html",
        "META-INF/ASL2.0",
        "META-INF/ASL-2.0.txt",
        "META-INF/DEPENDENCIES",
        "META-INF/LGPL-3.0.txt",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/license.txt",
        "META-INF/license.html",
        "META-INF/LICENSE.md",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt",
        "META-INF/NOTICE.md",
        "META-INF/notice.txt",
        "META-INF/LGPL-3.0.txt",
        "META-INF/sun-jaxb.episode",
        "META-INF/*.kotlin_module",
        "readme.html",
      )
    )
  }

  // See https://developer.android.com/studio/write/java8-support
  kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }

  configureJacocoTestOptions()
}

configurations {
  all {
    exclude(module = "json")
    exclude(module = "xpp3")
    exclude(module = "hamcrest-all")
    exclude(module = "javax.activation")
    exclude(group = "org.apache.httpcomponents")
    exclude(module = "activation", group = "javax.activation")
    exclude(module = "javaee-api", group = "javax")
    exclude(module = "hamcrest-all")
    exclude(module = "javax.activation")
    exclude(group = "xml-apis")
    exclude(group = "org.eclipse.persistence")
    exclude(group = "com.google.code.javaparser")
    exclude(group = "jakarta.activation")
  }
}

dependencies {
  androidTestImplementation(Dependencies.AndroidxTest.core)
  androidTestImplementation(Dependencies.AndroidxTest.extJunit)
  androidTestImplementation(Dependencies.AndroidxTest.extJunitKtx)
  androidTestImplementation(Dependencies.AndroidxTest.runner)
  androidTestImplementation(Dependencies.AndroidxTest.workTestingRuntimeKtx)
  androidTestImplementation(Dependencies.junit)
  androidTestImplementation(Dependencies.truth)

  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }

  coreLibraryDesugaring(Dependencies.desugarJdkLibs)

  implementation(Dependencies.Androidx.coreKtx)
  implementation(Dependencies.Cql.evaluator)
  implementation(Dependencies.Cql.evaluatorBuilder)
  implementation(Dependencies.Cql.evaluatorDagger)
  implementation(Dependencies.Jackson.annotations)
  implementation(Dependencies.Jackson.core)
  implementation(Dependencies.Jackson.databind)
  implementation(Dependencies.JavaJsonTools.jacksonCoreUtils)
  implementation(Dependencies.JavaJsonTools.msgSimple)
  implementation(Dependencies.Kotlin.kotlinCoroutinesAndroid)
  implementation(Dependencies.Kotlin.kotlinCoroutinesCore)
  implementation(Dependencies.Kotlin.stdlib)
  implementation(Dependencies.stax)
  implementation(Dependencies.woodstox)
  implementation(Dependencies.xerces)
  implementation(project(":engine"))

  testImplementation(Dependencies.AndroidxTest.core)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.robolectric)
  testImplementation(Dependencies.truth)
}
