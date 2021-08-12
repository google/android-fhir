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

package com.google.android.fhir.datacapture.mapping

import android.content.Context
import com.google.android.fhir.datacapture.utilities.NpmPackageProvider
import org.hl7.fhir.utilities.npm.FilesystemPackageCacheManager
import org.hl7.fhir.utilities.npm.NpmPackage
import org.hl7.fhir.utilities.npm.ToolsVersion
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.util.ReflectionHelpers

/**
 * Shadow of [ResourceMapper] class implementing original NPM package management for running on
 * local JVM
 */
@Implements(NpmPackageProvider::class)
class ShadowNpmPackageProvider {

  @Implementation
  suspend fun loadNpmPackage(context: Context): NpmPackage {
    // Package name manually checked from
    // https://simplifier.net/packages/hl7.fhir.r4.core/4.0.1
    val npmPackage =
      FilesystemPackageCacheManager(true, ToolsVersion.TOOLS_VERSION)
        .loadPackage("hl7.fhir.r4.core", "4.0.1")

    ReflectionHelpers.setField(NpmPackageProvider, "npmPackage", npmPackage)
    return npmPackage
  }
}
