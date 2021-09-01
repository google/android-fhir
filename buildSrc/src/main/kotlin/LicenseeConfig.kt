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

fun Project.configureLicensee() {
  apply(plugin = "app.cash.licensee")
  configure<app.cash.licensee.LicenseeExtension> {
    allow("Apache-2.0")

    // Occasionally, dependencies may add their licenses via a direct URL instead of an SPDX id.
    nonStandardLicenseUrls.forEach { allowUrl(it) }

    // TODO(kmost): We need to figure out the story around these dependencies
//    allowDependency("net.sf.saxon", "Saxon-HE", "9.8.0-15") {
//      // HAPI FHIR transitive dep
//      because("Uses EPL-1.0, a reciprocal license. Remove?")
//    }
    ignoreDependencies("junit", "junit") {
      because("JUnit is used in tests only, so it is not distributed with our library")
    }
    ignoreDependencies("org.jacoco", "org.jacoco.agent") {
      because("JaCoCo is used in tests only, so it is not distributed with our library")
    }
    allowDependency("org.javassist", "javassist", "3.20.0-GA") {
      because("Multi-licensed under Apache. https://github.com/jboss-javassist/javassist")
    }

    // HAPI FHIR transitive dep
    allowDependency("xpp3", "xpp3_xpath", "1.1.4c") {
      because("Custom license, essentially BSD-5. https://fedoraproject.org/wiki/Licensing/xpp")
    }

    // json-patch and related transitive deps
    allowDependency("com.github.java-json-tools", "btf", "1.3") {
      because("Dual-licensed under Apache. https://github.com/java-json-tools/btf")
    }
    allowDependency("com.github.java-json-tools", "jackson-coreutils", "2.0") {
      because("Dual-licensed under Apache. https://github.com/java-json-tools/jackson-coreutils")
    }
    allowDependency("com.github.java-json-tools", "json-patch", "1.13") {
      because("Dual-licensed under Apache. https://github.com/java-json-tools/json-patch")
    }
    allowDependency("com.github.java-json-tools", "msg-simple", "1.2") {
      because("Dual-licensed under Apache. https://github.com/java-json-tools/msg-simple")
    }
  }
}

private val nonStandardLicenseUrls =
  listOf(
    // MIT
    "http://opensource.org/licenses/MIT",
    "http://www.opensource.org/licenses/mit-license.php",

    // BSD
    "http://opensource.org/licenses/BSD-3-Clause",
    "http://www.opensource.org/licenses/bsd-license.php",

    // Apache
    "http://www.apache.org/licenses/",
  )
