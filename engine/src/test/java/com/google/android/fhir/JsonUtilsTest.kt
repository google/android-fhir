/*
 * Copyright 2023 Google LLC
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

import android.os.Build
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.db.impl.addUpdatedReferenceToResource
import com.google.android.fhir.db.impl.extractAllValuesWithKey
import com.google.android.fhir.db.impl.replaceJsonValue
import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Reference
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.skyscreamer.jsonassert.JSONAssert

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class JsonUtilsTest : TestCase() {

  @Test
  fun addUpdatedReferenceToResource_updatesReferenceInPatient() {
    val oldPractitionerReference = "Practitioner/123"
    val updatedPractitionerReference = "Practitioner/345"
    val patient =
      Patient().apply {
        id = "f001"
        addGeneralPractitioner(Reference(oldPractitionerReference))
      }
    val updatedPatientResource =
      addUpdatedReferenceToResource(
        iParser,
        patient,
        oldPractitionerReference,
        updatedPractitionerReference,
      )
        as Patient
    assertThat(updatedPatientResource.generalPractitioner.first().reference)
      .isEqualTo(updatedPractitionerReference)
  }

  @Test
  fun addUpdatedReferenceToResource_updatesMultipleReferenceInCarePlan() {
    val oldPatientReference = "Patient/123"
    val updatedPatientReference = "Patient/345"
    val carePlan =
      CarePlan().apply {
        id = "f001"
        subject = (Reference(oldPatientReference))
        activityFirstRep.detail.performer.add(Reference(oldPatientReference))
      }
    val updatedCarePlan =
      addUpdatedReferenceToResource(iParser, carePlan, oldPatientReference, updatedPatientReference)
        as CarePlan
    assertThat(updatedCarePlan.subject.reference).isEqualTo(updatedPatientReference)
    assertThat(updatedCarePlan.activityFirstRep.detail.performer.first().reference)
      .isEqualTo(updatedPatientReference)
  }

  @Test
  fun replaceJsonValue_jsonObject1() {
    val json =
      JSONObject(
        """
      {
        "key1": "valueToBeReplaced",
        "key2": {
          "key3": {
            "key4": [
              "valueToBeReplaced",
              "otherValueNotToBeReplaced"
            ]
          }
        }
      }
            """
          .trimIndent(),
      )
    val updatedJson = replaceJsonValue(json, "valueToBeReplaced", "newValue")
    val expectedJson =
      JSONObject(
        """
      {
        "key1": "newValue",
        "key2": {
          "key3": {
            "key4": [
              "newValue",
              "otherValueNotToBeReplaced"
            ]
          }
        }
      }
            """
          .trimIndent(),
      )
    JSONAssert.assertEquals(updatedJson, expectedJson, false)
  }

  @Test
  fun replaceJsonValue_jsonObject2() {
    val json =
      JSONObject(
        """
        {
          "key1": "valueToBeReplaced",
          "key2": {
            "key3": {
              "key4": [
                [
                  "otherValueNotToBeReplaced",
                  "valueToBeReplaced"
                ],
                [
                  "otherValueNotToBeReplaced"
                ]
              ]
            }
          }
        }
            """
          .trimIndent(),
      )
    val updatedJson = replaceJsonValue(json, "valueToBeReplaced", "newValue")
    val expectedJson =
      JSONObject(
        """
      {
        "key1": "newValue",
        "key2": {
          "key3": {
            "key4": [
              [
                "otherValueNotToBeReplaced",
                "newValue"
              ],
              [
                "otherValueNotToBeReplaced"
              ]
            ]
          }
        }
      }
            """
          .trimIndent(),
      )
    JSONAssert.assertEquals(updatedJson, expectedJson, false)
  }

  @Test
  fun replaceJsonValue_jsonObject3() {
    val json =
      JSONObject(
        """
        {
          "key1": "valueToBeReplaced",
          "key2": {
            "key3": {
              "key4": [
                [
                  {
                    "key5": "valueToBeReplaced"
                  }
                ],
                [
                  {
                    "key6": "otherValueNotToBeReplaced"
                  }
                ]
              ]
            }
          }
        }
            """
          .trimIndent(),
      )
    val updatedJson = replaceJsonValue(json, "valueToBeReplaced", "newValue")
    val expectedJson =
      JSONObject(
        """
      {
        "key1": "newValue",
        "key2": {
          "key3": {
            "key4": [
              [
                {
                  "key5": "newValue"
                }
              ],
              [
                {
                  "key6": "otherValueNotToBeReplaced"
                }
              ]
            ]
          }
        }
      }
            """
          .trimIndent(),
      )
    JSONAssert.assertEquals(updatedJson, expectedJson, false)
  }

  @Test
  fun extractAllValueWithKey_extractsValuesFromJson() {
    val testJson =
      """
      {
        "key1": "newValue",
        "reference": "testValue1",
        "key2": {
          "key3": {
            "key4": [
              [
                {
                  "reference": "testValue2"
                }
              ],
              [
                {
                  "key6": "otherValueNotToBeReplaced"
                }
              ]
            ]
          },
          "key5": {
            "reference": "testValue3"
          }
        }
      }
        """
        .trimIndent()
    val referenceValues = extractAllValuesWithKey("reference", JSONObject(testJson))
    assertThat(referenceValues.size).isEqualTo(3)
    assertThat(referenceValues).containsExactly("testValue1", "testValue2", "testValue3")
  }

  companion object {
    val iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  }
}
