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

package com.google.android.fhir.implementationguide.npm

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hl7.fhir.r4.model.ImplementationGuide
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NpmPackageManagerTest {

  @Test
  fun smoketest() {
    val cacheFolderPath = ApplicationProvider.getApplicationContext<Context>().cacheDir.absolutePath
    NpmPackageManager.fromResource(
      cacheFolderPath,
      ImplementationGuide(),
      "4.0.1",
      "https://packages.fhir.org",
      "https://packages.simplifier.net"
    )
  }
}
