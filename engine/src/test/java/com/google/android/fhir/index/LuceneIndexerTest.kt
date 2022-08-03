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

package com.google.android.fhir.index

import android.os.Build
import org.hl7.fhir.r4.model.Patient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class LuceneIndexerTest {

  @Before
  fun setup() {
    LuceneIndexer.resourceIndexer(
      "/home/vend-maimoona/Documents/projects/android-fhir/engine/zzluceneindexer"
    )
  }

  @Test
  fun index_id() {
    val patient =
      Patient().apply {
        id = "3f511720-"
        addAddress().apply {
          this.city = "abc"
          this.country = "def"
        }
        active = true
      }

    LuceneIndexer.optimize()

    for (i in 1..9) LuceneIndexer.indexResource(patient.apply { id = patient.id + i })

    LuceneIndexer.close()

    patient.name
  }
}
