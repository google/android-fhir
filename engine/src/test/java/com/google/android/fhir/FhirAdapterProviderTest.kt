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

package com.google.android.fhir

import ca.uhn.fhir.context.FhirVersionEnum
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.exceptions.FHIRException
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirAdapterProviderTest {

  @Test
  fun `create FhirAdapterProvider using R5 should throw exception`() {
    val exception =
      assertThrows(FHIRException::class.java) {
        FhirAdapterProvider.from(FhirVersionEnum.R5).createAdapter()
      }

    assertThat(exception.localizedMessage)
      .isEqualTo("R5 version not yet supported in the FHIR Engine library")
  }
}
