/*
 * Copyright 2023-2026 Google LLC
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.fhir.model.r4.QuestionnaireResponse

internal const val QUESTIONNAIRE_VIEW_MODEL_PROVIDER_KEY =
  "com.google.android.fhir.datacapture:QuestionnaireViewModel"

/**
 * Public composable function for displaying a FHIR Questionnaire in KMP applications.
 *
 * This provides a builder-like API similar to QuestionnaireFragment.Builder but for Compose.
 *
 * @param questionnaireJson JSON string of the FHIR Questionnaire to display
 * @param questionnaireResponseJson Optional JSON string of a pre-filled QuestionnaireResponse
 * @param showSubmitButton Whether to show the submit button (default: true)
 * @param showCancelButton Whether to show the cancel button (default: true)
 * @param showReviewPage Whether to show a review page before submission (default: false)
 * @param isReadOnly Whether the questionnaire is read-only (default: false)
 * @param showAsterisk Whether to show asterisk for required fields (default: true)
 * @param showRequiredText Whether to show "required" text (default: false)
 * @param showOptionalText Whether to show "optional" text (default: false)
 * @param submitButtonText Custom text for submit button (optional)
 * @param matchersProvider Custom matchers provider for custom question types (optional)
 * @param onSubmit Callback invoked when user submits the questionnaire with the response
 * @param onCancel Callback invoked when user cancels the questionnaire
 *
 * Example usage:
 * ```
 * Questionnaire(
 *   questionnaireJson = myQuestionnaireJson,
 *   showSubmitButton = true,
 *   showCancelButton = true,
 *   onSubmit = { response ->
 *     // Handle the questionnaire response
 *     println("Received response: $response")
 *   },
 *   onCancel = {
 *     // Handle cancellation
 *     navController.popBackStack()
 *   }
 * )
 * ```
 */
@Composable
fun Questionnaire(
  questionnaireJson: String,
  questionnaireResponseJson: String? = null,
  showSubmitButton: Boolean = true,
  showCancelButton: Boolean = true,
  showReviewPage: Boolean = false,
  showReviewPageFirst: Boolean = false,
  isReadOnly: Boolean = false,
  showAsterisk: Boolean = false,
  showRequiredText: Boolean = true,
  showOptionalText: Boolean = false,
  showNavigationLongScroll: Boolean = false,
  submitButtonText: String? = null,
  matchersProvider: QuestionnaireItemViewHolderFactoryMatchersProvider? = null,
  onSubmit: (suspend () -> QuestionnaireResponse) -> Unit,
  onCancel: () -> Unit,
) {
  val stateMap =
    remember(
      questionnaireJson,
      questionnaireResponseJson,
      showSubmitButton,
      showCancelButton,
      showReviewPage,
      showReviewPageFirst,
      isReadOnly,
      showAsterisk,
      showRequiredText,
      showOptionalText,
      showNavigationLongScroll,
      submitButtonText,
    ) {
      buildMap<String, Any> {
        put(EXTRA_QUESTIONNAIRE_JSON_STRING, questionnaireJson)
        questionnaireResponseJson?.let { put(EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING, it) }
        put(EXTRA_SHOW_SUBMIT_BUTTON, showSubmitButton)
        put(EXTRA_SHOW_CANCEL_BUTTON, showCancelButton)
        put(EXTRA_ENABLE_REVIEW_PAGE, showReviewPage)
        put(EXTRA_READ_ONLY, isReadOnly)
        put(EXTRA_SHOW_ASTERISK_TEXT, showAsterisk)
        put(EXTRA_SHOW_REQUIRED_TEXT, showRequiredText)
        put(EXTRA_SHOW_OPTIONAL_TEXT, showOptionalText)
        put(EXTRA_SHOW_REVIEW_PAGE_FIRST, showReviewPageFirst)
        put(EXTRA_SHOW_NAVIGATION_IN_DEFAULT_LONG_SCROLL, showNavigationLongScroll)
        submitButtonText?.let { put(EXTRA_SUBMIT_BUTTON_TEXT, it) }
      }
    }

  val viewModel: QuestionnaireViewModel =
    viewModel(key = QUESTIONNAIRE_VIEW_MODEL_PROVIDER_KEY) { QuestionnaireViewModel(stateMap) }

  LaunchedEffect(viewModel, onSubmit, onCancel) {
    viewModel.setOnSubmitButtonClickListener { onSubmit { viewModel.getQuestionnaireResponse() } }

    viewModel.setOnCancelButtonClickListener { onCancel() }
  }

  val effectiveMatchersProvider =
    matchersProvider ?: EmptyQuestionnaireItemViewHolderFactoryMatchersProvider

  QuestionnaireScreen(
    viewModel = viewModel,
    matchersProvider = effectiveMatchersProvider,
  )
}

/**
 * Default empty implementation of QuestionnaireItemViewHolderFactoryMatchersProvider that provides
 * no custom matchers.
 */
private object EmptyQuestionnaireItemViewHolderFactoryMatchersProvider :
  QuestionnaireItemViewHolderFactoryMatchersProvider() {
  override fun get() = emptyList<QuestionnaireItemViewHolderFactoryMatcher>()
}
