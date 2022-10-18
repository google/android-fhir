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

package com.google.android.fhir.datacapture

import android.os.Build
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_STRING
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Questionnaire
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireFragmentTest {

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  @Test
  fun `fragment should have valid questionnaire response`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          }
        )
      }
    val questionnaireJson = parser.encodeResourceToString(questionnaire)
    val scenario =
      launchFragmentInContainer<QuestionnaireFragment>(
        bundleOf(EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJson)
      )
    scenario.moveToState(Lifecycle.State.RESUMED)
    scenario.withFragment {
      assertThat(this.getQuestionnaireResponse()).isNotNull()
      assertThat(this.getQuestionnaireResponse().item.any { it.linkId == "a-link-id" }).isTrue()
    }
  }

  @Test
  fun `calculateProgressPercentage should return zero for totalCount value zero`() {
    val questionnaire = Questionnaire().apply { id = "a-questionnaire" }
    val questionnaireJson = parser.encodeResourceToString(questionnaire)
    val scenario =
      launchFragmentInContainer<QuestionnaireFragment>(
        bundleOf(EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJson)
      )
    scenario.moveToState(Lifecycle.State.RESUMED)
    scenario.withFragment {
      assertThat(calculateProgressPercentage(count = 10, totalCount = 0)).isEqualTo(0)
    }
  }

  @Test
  fun `calculateProgressPercentage should return valid percentage result`() {
    val questionnaire = Questionnaire().apply { id = "a-questionnaire" }
    val questionnaireJson = parser.encodeResourceToString(questionnaire)
    val scenario =
      launchFragmentInContainer<QuestionnaireFragment>(
        bundleOf(EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJson)
      )
    scenario.moveToState(Lifecycle.State.RESUMED)
    scenario.withFragment {
      assertThat(calculateProgressPercentage(count = 10, totalCount = 50)).isEqualTo(20)
    }
  }
}
