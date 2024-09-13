/*
 * Copyright 2022-2024 Google LLC
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

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
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
    runTest {
      val questionnaire =
        Questionnaire().apply {
          url = "questionnaire-1"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "name-id"
              this.required = true
              text = "First Name"
              type = Questionnaire.QuestionnaireItemType.STRING
            },
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
          factory = createDialogFragmentFactoryForTests(questionnaire, questionnaireResponse),
        )

      val result = scenario.withFragment { onCreateCustomView() }

      assertThat(result.findViewById<TextView>(R.id.dialog_title).text).isEqualTo("Errors found")
      assertThat(result.findViewById<TextView>(R.id.dialog_subtitle).text)
        .isEqualTo("Fix the following questions:")
      assertThat(result.findViewById<TextView>(R.id.body).text).isEqualTo("• First Name")
    }
  }

  /**
   * Test method to ensure that Submit anyway button is visible when its value is set as true in the
   * fragment arguments
   */
  @Test
  fun checkAlertDialog_submitAnywayButtonIsTrue_shouldShowSubmitAnywayButton() {
    runTest {
      val questionnaireValidationErrorMessageDialogArguments = Bundle()
      questionnaireValidationErrorMessageDialogArguments.putBoolean(
        QuestionnaireValidationErrorMessageDialogFragment.EXTRA_SHOW_SUBMIT_ANYWAY_BUTTON,
        true,
      )
      with(
        launchFragment<QuestionnaireValidationErrorMessageDialogFragment>(
          themeResId = R.style.Theme_Questionnaire,
          fragmentArgs = questionnaireValidationErrorMessageDialogArguments,
        ),
      ) {
        onFragment { fragment ->
          assertThat(fragment.dialog).isNotNull()
          assertThat(fragment.requireDialog().isShowing).isTrue()
          val alertDialog = fragment.dialog as? AlertDialog
          val context = InstrumentationRegistry.getInstrumentation().targetContext
          val positiveButtonText =
            context.getString(R.string.questionnaire_validation_error_fix_button_text)
          val negativeButtonText =
            context.getString(R.string.questionnaire_validation_error_submit_button_text)
          assertThat(alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.text)
            .isEqualTo(positiveButtonText)
          assertThat(alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.text)
            .isEqualTo(negativeButtonText)
        }
      }
    }
  }

  /**
   * Test method to ensure that Submit anyway button is visible when no fragment arguments are
   * passed
   */
  @Test
  fun checkAlertDialog_noArgsPassed_shouldShowSubmitAnywayButton() {
    runTest {
      with(
        launchFragment<QuestionnaireValidationErrorMessageDialogFragment>(
          themeResId = R.style.Theme_Questionnaire,
        ),
      ) {
        onFragment { fragment ->
          assertThat(fragment.dialog).isNotNull()
          assertThat(fragment.requireDialog().isShowing).isTrue()
          val alertDialog = fragment.dialog as? AlertDialog
          val context = InstrumentationRegistry.getInstrumentation().targetContext
          val positiveButtonText =
            context.getString(R.string.questionnaire_validation_error_fix_button_text)
          val negativeButtonText =
            context.getString(R.string.questionnaire_validation_error_submit_button_text)
          assertThat(alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.text)
            .isEqualTo(positiveButtonText)
          assertThat(alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.text)
            .isEqualTo(negativeButtonText)
        }
      }
    }
  }

  /**
   * Test method to ensure that Submit anyway button is not visible when it's value is set as false
   * in the fragment arguments
   */
  @Test
  fun checkAlertDialog_submitAnywayButtonIsFalse_shouldHideSubmitAnywayButton() {
    runTest {
      val validationErrorBundle = Bundle()
      validationErrorBundle.putBoolean(
        QuestionnaireValidationErrorMessageDialogFragment.EXTRA_SHOW_SUBMIT_ANYWAY_BUTTON,
        false,
      )
      with(
        launchFragment<QuestionnaireValidationErrorMessageDialogFragment>(
          themeResId = R.style.Theme_Questionnaire,
          fragmentArgs = validationErrorBundle,
        ),
      ) {
        onFragment { fragment ->
          assertThat(fragment.dialog).isNotNull()
          assertThat(fragment.requireDialog().isShowing).isTrue()
          val alertDialog = fragment.dialog as? AlertDialog
          val context = InstrumentationRegistry.getInstrumentation().targetContext
          val positiveButtonText =
            context.getString(R.string.questionnaire_validation_error_fix_button_text)
          assertThat(alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.text)
            .isEqualTo(positiveButtonText)
          assertEquals(alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.text, "")
        }
      }
    }
  }

  private suspend fun createTestValidationErrorViewModel(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
  ) =
    QuestionnaireValidationErrorViewModel().apply {
      setQuestionnaireAndValidation(
        questionnaire,
        QuestionnaireResponseValidator.validateQuestionnaireResponse(
          questionnaire,
          questionnaireResponse,
          RuntimeEnvironment.getApplication(),
        ),
      )
    }

  private suspend fun createDialogFragmentFactoryForTests(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
  ): FragmentFactory {
    val fragmentFactory: FragmentFactory = mock()
    val viewModel = createTestValidationErrorViewModel(questionnaire, questionnaireResponse)
    val factory =
      object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return viewModel as T
        }
      }

    `when`(fragmentFactory.instantiate(any(), any())).then {
      QuestionnaireValidationErrorMessageDialogFragment { factory }
    }

    return fragmentFactory
  }
}
