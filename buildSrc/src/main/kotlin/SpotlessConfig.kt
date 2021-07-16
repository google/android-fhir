/*
 * Copyright 2020 Google LLC
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
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun Project.configureSpotless() {
  val ktlintVersion = "0.41.0"
  val ktlintOptions = mapOf("indent_size" to "2", "continuation_indent_size" to "2")
  apply(plugin = Plugins.BuildPlugins.spotless)
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("**/build/")
      ktlint(ktlintVersion).userData(ktlintOptions)
      ktfmt().googleStyle()
      licenseHeaderFile(
        "${project.rootProject.projectDir}/license-header.txt",
        "package|import|class|object|sealed|open|interface|abstract "
        // It is necessary to tell spotless the top level of a file in order to apply config to it
        // See: https://github.com/diffplug/spotless/issues/135
        )
    }
    kotlinGradle {
      target("*.gradle.kts")
      ktlint(ktlintVersion).userData(ktlintOptions)
      ktfmt().googleStyle()
    }
    format("xml") {
      target("**/*.xml")
      targetExclude("**/build/", ".idea/")
      prettier(mapOf("prettier" to "2.0.5", "@prettier/plugin-xml" to "0.13.0"))
        .config(mapOf("parser" to "xml", "tabWidth" to 4))
    }
  }
}
