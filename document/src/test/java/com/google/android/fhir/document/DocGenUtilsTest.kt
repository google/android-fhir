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

package com.google.android.fhir.document

import com.google.android.fhir.document.utils.DocumentGeneratorUtils
import org.hl7.fhir.r4.model.AllergyIntolerance
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Condition
import org.junit.Assert
import org.junit.Test

class DocGenUtilsTest {

  private val docGenUtils = DocumentGeneratorUtils()

  @Test
  fun activeAllergyReturnsCorrectTitle() {
    val allergy = AllergyIntolerance()
    allergy.clinicalStatus.coding.add(
      Coding().apply { code = "active" },
    )
    val title = docGenUtils.getResourceTitle(allergy)
    Assert.assertEquals(title, "Allergies and Intolerances")
  }

  @Test
  fun pastAllergyReturnsCorrectTitle() {
    val allergy = AllergyIntolerance()
    allergy.clinicalStatus.coding.add(
      Coding().apply { code = "remission" },
    )
    val title = docGenUtils.getResourceTitle(allergy)
    Assert.assertEquals(title, "History of Past Illness")
  }

  @Test
  fun activeProblemReturnsCorrectTitle() {
    val problem = Condition()
    problem.clinicalStatus.coding.add(
      Coding().apply { code = "active" },
    )
    val title = docGenUtils.getResourceTitle(problem)
    Assert.assertEquals(title, "Active Problems")
  }

  @Test
  fun pastProblemReturnsCorrectTitle() {
    val problem = Condition()
    problem.clinicalStatus.coding.add(
      Coding().apply { code = "remission" },
    )
    val title = docGenUtils.getResourceTitle(problem)
    Assert.assertEquals(title, "History of Past Illness")
  }
}
