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
    allowDependency("net.sf.saxon", "Saxon-HE", "9.8.0-15") {
      // HAPI FHIR transitive dep
      because("Uses EPL-1.0, a reciprocal license. Remove?")
    }
    allowDependency("junit", "junit", "4.13.1") {
      // Note that we don't distribute this with FHIR SDK; it's only used in tests
      because("Uses EPL-1.0, a reciprocal license. Remove?")
    }
    allowDependency("org.jacoco", "org.jacoco.agent", "0.8.2") {
      // Note that we don't distribute this with FHIR SDK; it's only used in tests
      because("Uses EPL-1.0, a reciprocal license. Remove?")
    }
    allowDependency("org.javassist", "javassist", "3.20.0-GA") {
      // HAPI FHIR transitive dep
      because("Uses LGPL, MPL-1.1 (reciprocal license), and Apache. Do we need to fulfill all 3?")
    }
    allowDependency("xpp3", "xpp3_xpath", "1.1.4c") {
      // HAPI FHIR transitive dep
      // Working link to XPP license: https://fedoraproject.org/wiki/Licensing/xpp
      // "This license is derived from BSD, but it includes a unique warranty clause, and an
      // Apache-style attribution requirement. This license is Free, but GPL-Incompatible."
      because("XPP license is BSD-like. Can we use it?")
    }
    allowDependency("com.github.java-json-tools", "btf", "1.3") {
      // Brought into engine via json-patch
      because("LGPL licensed. Remove?")
    }
    allowDependency("com.github.java-json-tools", "jackson-coreutils", "2.0") {
      // Brought into engine via json-patch
      because("LGPL licensed. Remove?")
    }
    allowDependency("com.github.java-json-tools", "json-patch", "1.13") {
      // Brought into engine via json-patch
      because("LGPL licensed. Remove?")
    }
    allowDependency("com.github.java-json-tools", "msg-simple", "1.2") {
      // Brought into engine via json-patch
      because("LGPL licensed. Remove?")
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
