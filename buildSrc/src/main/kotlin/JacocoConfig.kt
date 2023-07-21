/*
 * Copyright 2023 Google LLC
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

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Create `jacocoTestReport` gradle task which combines the unit test coverage report and the
 * instrumentation test coverage report.
 *
 * To use this configuration in an Android library module:
 * 1. add `jacoco` plugin to the module's build script
 * 2. call `createJacocoTestReportTask()` in the top level script
 * 3. call `configureJacocoTestOptions()` in the `android` block
 *
 * To run the task locally: $./gradlew jacocoTestReport
 *
 * To run the task locally for a specific module: $./gradlew :<module>:jacocoTestReport
 *
 * The Jacoco test coverage report will be in the folder
 * `<module>/build/reports/jacoco/jacocoTestReport`.
 *
 * See blogpost https://proandroiddev.com/unified-code-coverage-for-android-revisited-44789c9b722f
 * and example
 * https://github.com/tngcanh07/survey-android/blob/070b5cd9b9037c20009f77d3dc3fe95d896cc20c/data/build.gradle.kts
 */
fun Project.createJacocoTestReportTask() {
  tasks.create(name = "jacocoTestReport", type = JacocoReport::class) {
    dependsOn(
      setOf(
        "testDebugUnitTest", // Generates unit test coverage report
      )
    )
    reports {
      xml.required.set(true)
      csv.required.set(true)
      html.required.set(true)
    }
    sourceDirectories.setFrom("$projectDir/src/main/java")
    classDirectories.setFrom(
      fileTree("$buildDir/tmp/kotlin-classes/debug")
        .exclude(
          "**/R.class",
          "**/R$*.class",
          "**/BuildConfig.*",
          "**/Manifest*.*",
          "**/*Test*.*",
          "android/**/*.*"
        )
    )

    executionData.setFrom(
      fileTree(buildDir) {
        include(
          listOf(
            // Unit test coverage report location
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
            // Instrumentation coverage report location
            "outputs/code_coverage/debugAndroidTest/connected/**/*.ec",
            // Instrumentation coverage report location from Firebase Test Lab
            "fladle/results/**/*.ec",
          )
        )
      }
    )
  }

  tasks.withType<Test> {
    configure<JacocoTaskExtension> {
      // Required for jacoco to work with Robolectric tests
      // See https://newbedev.com/jacoco-doesn-t-work-with-robolectric-tests
      isIncludeNoLocationClasses = true

      // Required for jacoco to work
      // See https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
      excludes = listOf("jdk.internal.*")
    }
  }
}

/** Configures jacoco test options in the `LibraryExtension`. */
fun LibraryExtension.configureJacocoTestOptions() {
  buildTypes { getByName("debug") { enableUnitTestCoverage = true } }
  testOptions {
    unitTests.isIncludeAndroidResources = true
    unitTests.isReturnDefaultValues = true
  }
  testCoverage { jacocoVersion = Dependencies.Versions.jacoco }
}
