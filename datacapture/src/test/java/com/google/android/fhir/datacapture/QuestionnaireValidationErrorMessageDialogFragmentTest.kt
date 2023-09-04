/*
 * Copyright 2022-2023 Google LLC
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

import android.widget.TextView
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireValidationErrorMessageDialogFragmentTest {

  @Test
  fun `createCustomView with an invalid questionnaire response validation result`() {
    val questionnaire =
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "name-id"
            this.required = true
            text = "First Name"
            type = Questionnaire.QuestionnaireItemType.STRING
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        this.questionnaire = "questionnaire-1"
        addItem(QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("name-id")))
      }
    val scenario =
      launchFragmentInContainer<QuestionnaireValidationErrorMessageDialogFragment>(
        initialState = Lifecycle.State.CREATED,
        factory = createDialogFragmentFactoryForTests(questionnaire, questionnaireResponse)
      )

    val result = scenario.withFragment { onCreateCustomView() }

    assertThat(result.findViewById<TextView>(R.id.dialog_title).text).isEqualTo("Errors found")
    assertThat(result.findViewById<TextView>(R.id.dialog_subtitle).text)
      .isEqualTo("Fix the following questions:")
    assertThat(result.findViewById<TextView>(R.id.body).text).isEqualTo("• First Name")
  }

  private fun createTestValidationErrorViewModel(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse
  ) =
    QuestionnaireValidationErrorViewModel().apply {
      setQuestionnaireAndValidation(
        questionnaire,
        QuestionnaireResponseValidator.validateQuestionnaireResponse(
          questionnaire,
          questionnaireResponse,
          RuntimeEnvironment.getApplication()
        )
      )
    }

  private fun createDialogFragmentFactoryForTests(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse
  ): FragmentFactory {
    val fragmentFactory: FragmentFactory = mock()
    val factory =
      object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return createTestValidationErrorViewModel(questionnaire, questionnaireResponse) as T
        }
      }

    `when`(fragmentFactory.instantiate(any(), any())).then {
      QuestionnaireValidationErrorMessageDialogFragment { factory }
    }

    return fragmentFactory
  }
}
