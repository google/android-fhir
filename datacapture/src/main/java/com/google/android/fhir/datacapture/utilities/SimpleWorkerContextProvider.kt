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

package com.google.android.fhir.datacapture.utilities

import android.content.Context
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.utilities.npm.NpmPackage

/**
 * Loads the SimpleWorkerContext into memory from the NpmPackage. The operation takes a considerable
 * time (20 seconds to 1 minutes) and should be preferably done at application start.
 *
 * This operation will be done automatically when required during StructureMap-based extraction.
 */
object SimpleWorkerContextProvider {

  private lateinit var simpleWorkerContext: SimpleWorkerContext

  /**
   * Creates SimpleWorkerContext from [NpmPackage] stored in app storage. In case the [NpmPackage]
   * does not exist, the package will be extracted into app storage.
   *
   * The whole process can take 1-3 minutes on a clean installation, otherwise, it should take 20
   * seconds to 1 minute .
   */
  suspend fun loadSimpleWorkerContext(context: Context): SimpleWorkerContext {
    if (!this::simpleWorkerContext.isInitialized) {
      simpleWorkerContext =
        SimpleWorkerContext.fromPackage(NpmPackageProvider.loadNpmPackage(context))
      simpleWorkerContext.isCanRunWithoutTerminology = true
    }

    return simpleWorkerContext
  }
}
