plugins {
  id(Plugins.BuildPlugins.mavenPublish)
  id("java-library")
  id("kotlin")
}

afterEvaluate {
  publishing {
    publications {
      register("release", MavenPublication::class) {
        from(components["java"])
        artifactId = "converter"
        groupId = "com.google.android.fhir"
        version = "0.1.0-alpha01"

        pom {
          name.set("Android FHIR Hapi Proto Converter Library")
          licenses {
            license {
              name.set("The Apache License, Version 2.0")
              url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
          }
        }
      }
    }
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  api(Dependencies.HapiFhir.structuresR4) { exclude(module = "junit") }
  api(Dependencies.FhirProto.fhirProtobufs)
  implementation(Dependencies.Kotlin.stdlib)
  runtimeOnly(Dependencies.kotlinPoet)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.truth)
}
