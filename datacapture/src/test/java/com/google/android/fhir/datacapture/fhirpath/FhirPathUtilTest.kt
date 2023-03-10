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

package com.google.android.fhir.datacapture.fhirpath

import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.hl7.fhir.exceptions.PathEngineException
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirPathUtilTest {

  @Test
  fun `evaluateToDisplay should return concatenated string for expressions evaluation on given resource`() {
    val expressions = listOf("name.given", "name.family")
    val resource =
      Patient().apply {
        addName(
          HumanName().apply {
            this.family = "Doe"
            this.addGiven("John")
          }
        )
      }

    assertThat(evaluateToDisplay(expressions, resource)).isEqualTo("John Doe")
  }

  @Test
  fun `check() should pass for valid path in resource`() {

    val resource =
      Patient().apply {
        gender = Enumerations.AdministrativeGender.MALE
        addName(
          HumanName().apply {
            this.family = "Doe"
            this.addGiven("John")
          }
        )
      }

    val typeDetail =
      fhirPathEngine.check(
        resource,
        resource.resourceType.name,
        resource.resourceType.name,
        "Patient.gender"
      )

    assertThat(typeDetail.binding.valueSet)
      .isEqualTo("http://hl7.org/fhir/ValueSet/administrative-gender|4.0.1")
  }

  @Test
  fun `check() should fail for invalid path in resource`() {

    val resource =
      Patient().apply {
        gender = Enumerations.AdministrativeGender.MALE
        addName(
          HumanName().apply {
            this.family = "Doe"
            this.addGiven("John")
          }
        )
      }

    val exception =
      assertFailsWith<PathEngineException> {
        fhirPathEngine.check(
          resource,
          resource.resourceType.name,
          resource.resourceType.name,
          "gobbledygook"
        )
      }

    assertThat(exception.localizedMessage)
      .isEqualTo(
        "The name gobbledygook is not valid for any of the possible types: [http://hl7.org/fhir/StructureDefinition/Patient] (@char 1)"
      )
  }
}
