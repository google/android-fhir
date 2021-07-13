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

package com.google.android.fhir.datacapture

import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.junit.Test

class MoreQuestionnairesTest {

  @Test
  fun `targetStructureMap() should return null when Questionnaire lacks extension`() {
    Truth.assertThat(Questionnaire().targetStructureMap).isEqualTo(null)
  }

  @Test
  fun `targetStructureMap() should return StructureMap url`() {
    val structureMapUrl = "https://fhir.labs.smartregister.org/StructureMap/383"

    val questionnaire = Questionnaire()
    questionnaire.extension =
      listOf(
        Extension(
          "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap",
          CanonicalType(structureMapUrl)
        )
      )

    Truth.assertThat(questionnaire.targetStructureMap).isEqualTo(structureMapUrl)
  }
}
