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
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun Project.configureLicensee() {
  apply(plugin = "app.cash.licensee")
  configure<app.cash.licensee.LicenseeExtension> {
    allow("Apache-2.0")
    allow("MIT")

    // Occasionally, dependencies may add their licenses via a direct URL instead of an SPDX id.
    nonStandardLicenseUrls.forEach { allowUrl(it) }

    ignoreDependencies("junit", "junit") {
      because("JUnit is used in tests only, so it is not distributed with our library")
    }
    ignoreDependencies("org.jacoco", "org.jacoco.agent") {
      because("JaCoCo is used in tests only, so it is not distributed with our library")
    }
    allowDependency("org.javassist", "javassist", "3.20.0-GA") {
      because("Multi-licensed under Apache. https://github.com/jboss-javassist/javassist")
    }

    // xpp3 (HAPI FHIR transitive dep)
    allowDependency("xpp3", "xpp3_xpath", "1.1.4c") {
      because("Custom license, essentially BSD-5. https://fedoraproject.org/wiki/Licensing/xpp")
    }
    // json-patch and its transitive deps
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

    // SQLCipher
    allowDependency("net.zetetic", "android-database-sqlcipher", "4.5.0") {
      because("Custom license, essentially BSD-3. https://www.zetetic.net/sqlcipher/license/")
    }

    // Jakarta XML Binding API
    allowDependency("jakarta.xml.bind", "jakarta.xml.bind-api", "2.3.3") {
      because("BSD 3-clause.")
    }

    // Jakarta Activation API 2.1 Specification
    allowDependency("jakarta.activation", "jakarta.activation-api", "1.2.2") {
      because(
        "Licensed under Eclipse Distribution License 1.0. http://www.eclipse.org/org/documents/edl-v10.php"
      )
    }

    // Javax Annotation API
    allowDependency("javax.annotation", "javax.annotation-api", "1.3.2") {
      because("Dual-licensed under CDDL 1.1 and GPL v2 with classpath exception.")
    }

    // Streaming API for XML (StAX)
    allowDependency("javax.xml.stream", "stax-api", "1.0-2") {
      because("Dual-licensed under CDDL 1.0 and GPL v3.")
    }

    // xml-commons
    allowDependency("xml-apis", "xml-apis", "1.4.01") {
      because("Licensed under Mozilla Public License Version 2.0. http://www.mozilla.org/MPL/2.0/")
    }

    // The XSLT and XQuery Processor
    allowDependency("net.sf.saxon", "Saxon-HE", "9.8.0-15") {
      because("BSD 3-clause. http://www.antlr.org/license.html")
    }

    // ANTLR 4
    allowDependency("org.antlr", "antlr-runtime", "3.5.3") {
      because("BSD 3-clause. http://www.antlr.org/license.html")
    }
    // ANTLR 4
    allowDependency("org.antlr", "antlr4-runtime", "4.10.1") {
      because("BSD 3-clause. http://www.antlr.org/license.html")
    }

    // Utilities
    // https://developers.google.com/android/reference/com/google/android/gms/common/package-summary
    allowDependency("com.google.android.gms", "play-services-base", "17.4.0") { because("") }

    // More utility classes
    // https://developers.google.com/android/reference/com/google/android/gms/common/package-summary
    allowDependency("com.google.android.gms", "play-services-basement", "17.4.0") { because("") }

    // https://developers.google.com/android/reference/com/google/android/gms/common/package-summary
    allowDependency("com.google.android.gms", "play-services-clearcut", "17.0.0") { because("") }

    // ML Kit barcode scanning https://developers.google.com/ml-kit/vision/barcode-scanning/android
    allowDependency("com.google.android.gms", "play-services-mlkit-barcode-scanning", "16.1.4") {
      because("")
    }

    // Play Services Phenotype
    allowDependency("com.google.android.gms", "play-services-phenotype", "17.0.0") { because("") }

    // Tasks API Android https://developers.google.com/android/guides/tasks
    allowDependency("com.google.android.gms", "play-services-tasks", "17.2.0") { because("") }

    // Barcode Scanning https://developers.google.com/ml-kit/vision/barcode-scanning
    allowDependency("com.google.mlkit", "barcode-scanning", "16.1.1") { because("") }

    // MLKit Common https://developers.google.com/ml-kit/vision/barcode-scanning
    allowDependency("com.google.mlkit", "common", "17.1.1") { because("") }

    // Object Detection https://developers.google.com/ml-kit/vision/object-detection
    allowDependency("com.google.mlkit", "object-detection", "16.2.3") { because("") }

    // Object Detection https://developers.google.com/ml-kit/vision/object-detection
    allowDependency("com.google.mlkit", "object-detection-common", "17.0.0") { because("") }

    // Object Detection https://developers.google.com/ml-kit/vision/object-detection
    allowDependency("com.google.mlkit", "object-detection-custom", "16.3.1") { because("") }

    // Vision Common
    // https://developers.google.com/android/reference/com/google/mlkit/vision/common/package-summary
    allowDependency("com.google.mlkit", "vision-common", "16.3.0") { because("") }

    // Vision Common
    // https://developers.google.com/android/reference/com/google/mlkit/vision/common/package-summary
    allowDependency("com.google.mlkit", "vision-internal-vkp", "18.0.0") { because("") }

    // Glide
    allowDependency("com.github.bumptech.glide", "glide", "4.14.2") {
      because("BSD, part MIT and Apache 2.0. https://github.com/bumptech/glide#license")
    }

    // Glide Annotations
    allowDependency("com.github.bumptech.glide", "annotations", "4.14.2") {
      because("BSD, part MIT and Apache 2.0. https://github.com/bumptech/glide#license")
    }

    // Glide Disk LRU Cache
    allowDependency("com.github.bumptech.glide", "disklrucache", "4.14.2") {
      because("BSD, part MIT and Apache 2.0. https://github.com/bumptech/glide#license")
    }

    // Glide GIF Decoder
    allowDependency("com.github.bumptech.glide", "gifdecoder", "4.14.2") {
      because("BSD, part MIT and Apache 2.0. https://github.com/bumptech/glide#license")
    }
  }
}

private val nonStandardLicenseUrls =
  listOf(
    // BSD-3
    "http://opensource.org/licenses/BSD-3-Clause",
    "http://www.opensource.org/licenses/bsd-license.php",
    "https://asm.ow2.io/license.html",
  )
