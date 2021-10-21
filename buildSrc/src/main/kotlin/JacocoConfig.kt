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
 * See blogpost https://proandroiddev.com/unified-code-coverage-for-android-revisited-44789c9b722f
 * and example
 * https://github.com/tngcanh07/survey-android/blob/070b5cd9b9037c20009f77d3dc3fe95d896cc20c/data/build.gradle.kts
 */
fun Project.createJacocoTestReportTask() {
  tasks.create(name = "jacocoTestReport", type = JacocoReport::class) {
    dependsOn(
      setOf(
        "testDebugUnitTest", // Generates unit test coverage report
        "createDebugCoverageReport", // Generates instrumentation test coverage report
      )
    )
    reports {
      xml.isEnabled = true
      csv.isEnabled = false
      html.isEnabled = true
    }
    sourceDirectories.setFrom("$projectDir/src/main/java")
    classDirectories.setFrom(fileTree("$buildDir/tmp/kotlin-classes/debug"))
    executionData.setFrom(
      fileTree(buildDir) {
        include(
          listOf(
            // Unit test coverage report location
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
            // Instrumentation coverage report location
            "outputs/code_coverage/debugAndroidTest/connected/**/*.ec",
          )
        )
      }
    )
  }

  tasks.withType<Test> {
    configure<JacocoTaskExtension> {
      // The following property is required for jacoco to work with Robolectric tests
      // See https://newbedev.com/jacoco-doesn-t-work-with-robolectric-tests
      isIncludeNoLocationClasses = true
      exclude("jdk.internal.*")
    }
    finalizedBy(tasks.getByName("jacocoTestReport"))
  }
}

/** Configures jacoco test options in the `LibraryExtension`. */
fun LibraryExtension.configureJacocoTestOptions() {
  buildTypes { getByName("debug") { isTestCoverageEnabled = true } }
  testOptions {
    unitTests.isIncludeAndroidResources = true
    unitTests.isReturnDefaultValues = true
  }
  jacoco { version = "0.8.7" }
}
