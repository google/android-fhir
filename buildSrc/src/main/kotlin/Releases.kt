/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

interface LibraryArtifact {
  /** Maven coordinate artifact id. */
  val artifactId: String

  /** Maven coordinate version. */
  val version: String

  /** Descriptive name for library. */
  val name: String
}

object Releases {
  const val groupId = "com.google.android.fhir"

  // Libraries

  object Common : LibraryArtifact {
    override val artifactId = "common"
    override val version = "0.1.0-alpha03"
    override val name = "Android FHIR Common Library"
  }

  object Engine : LibraryArtifact {
    override val artifactId = "engine"
    override val version = "0.1.0-beta03"
    override val name = "Android FHIR Engine Library"
  }

  object DataCapture : LibraryArtifact {
    override val artifactId = "data-capture"
    override val version = "1.0.0"
    override val name = "Android FHIR Structured Data Capture Library"
  }

  object Workflow : LibraryArtifact {
    override val artifactId = "workflow"
    override val version = "0.1.0-alpha02"
    override val name = "Android FHIR Workflow Library"
  }

  object Contrib {
    object Barcode : LibraryArtifact {
      override val artifactId = "contrib-barcode"
      override val version = "0.1.0-beta3"
      override val name = "Android FHIR Structured Data Capture - Barcode Extensions (contrib)"
    }
  }

  object Knowledge : LibraryArtifact {
    override val artifactId = "knowledge"
    override val version = "0.1.0-alpha01"
    override val name = "Android FHIR Knowledge Manager Library"
  }

  // Demo apps

  object Demo {
    const val applicationId = "com.google.android.fhir.demo"
    const val versionCode = 1
    const val versionName = "1.0"
  }

  object Catalog {
    const val applicationId = "com.google.android.fhir.catalog"
    const val versionCode = 1
    const val versionName = "1.0"
  }
}

fun Project.publishArtifact(artifact: LibraryArtifact) {
  afterEvaluate {
    configure<PublishingExtension> {
      publications {
        register("release", MavenPublication::class) {
          from(components["release"])
          groupId = Releases.groupId
          artifactId = artifact.artifactId
          version = artifact.version
          // Also publish source code for developers' convenience
          artifact(
            tasks.create<Jar>("androidSourcesJar") {
              archiveClassifier.set("sources")

              val android =
                project.extensions.getByType<com.android.build.gradle.LibraryExtension>()
              from(android.sourceSets.getByName("main").java.srcDirs)
            }
          )
          pom {
            name.set(artifact.name)
            licenses {
              license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
              }
            }
          }
          repositories {
            maven {
              name = "CI"
              url = uri("file://${rootProject.buildDir}/ci-repo")
              version =
                if (project.providers.environmentVariable("GITHUB_ACTIONS").isPresent) {
                  "${artifact.version}-build_${System.getenv("GITHUB_RUN_ID")}"
                } else {
                  artifact.version
                }
            }
          }
        }
      }
    }
  }
}
