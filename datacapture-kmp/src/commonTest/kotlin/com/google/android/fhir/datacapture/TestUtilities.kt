/*
 * Copyright 2026 Google LLC
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

import android_fhir.datacapture_kmp.generated.resources.Res
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal val fhirR4Json = FhirR4Json()

@OptIn(ExperimentalTestApi::class)
internal suspend fun ComposeUiTest.setQuestionnaireContent(
  fileName: String,
  isReviewMode: Boolean = false,
  responseFileName: String? = null,
  onSubmit: suspend (QuestionnaireResponse) -> Unit = {},
): suspend () -> QuestionnaireResponse {
  val questionnaireJsonString = Res.readBytes(fileName).decodeToString()
  val questionnaireResponseJsonString = responseFileName?.let { Res.readBytes(it).decodeToString() }
  return setQuestionnaireContent(
    questionnaireJsonString,
    questionnaireResponseJsonString = questionnaireResponseJsonString,
    isReviewMode = isReviewMode,
    onSubmit = onSubmit,
  )
}

@OptIn(ExperimentalTestApi::class)
internal fun ComposeUiTest.setQuestionnaireContent(
  questionnaire: Questionnaire,
  isReviewMode: Boolean = false,
  showReviewPageFirst: Boolean = false,
  showNavigationLongScroll: Boolean = false,
  submitText: String? = null,
): suspend () -> QuestionnaireResponse {
  val questionnaireJsonString = fhirR4Json.encodeToString(questionnaire)

  return setQuestionnaireContent(
    questionnaireJsonString,
    isReviewMode = isReviewMode,
    showReviewPageFirst = showReviewPageFirst,
    showNavigationLongScroll = showNavigationLongScroll,
    submitText = submitText,
  )
}

@OptIn(ExperimentalTestApi::class)
internal fun ComposeUiTest.setQuestionnaireContent(
  questionnaireJsonString: String,
  questionnaireResponseJsonString: String? = null,
  isReviewMode: Boolean = false,
  showReviewPageFirst: Boolean = false,
  showNavigationLongScroll: Boolean = false,
  submitText: String? = null,
  onSubmit: suspend (QuestionnaireResponse) -> Unit = {},
): suspend () -> QuestionnaireResponse {
  val testLifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
  val viewModelStoreOwner =
    object : ViewModelStoreOwner {
      override val viewModelStore = ViewModelStore()
    }

  setContent {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }

    CompositionLocalProvider(
      LocalLifecycleOwner provides testLifecycleOwner,
      LocalViewModelStoreOwner provides remember { viewModelStoreOwner },
    ) {
      Questionnaire(
        questionnaireJson = questionnaireJsonString,
        showReviewPage = isReviewMode,
        showReviewPageFirst = showReviewPageFirst,
        showNavigationLongScroll = showNavigationLongScroll,
        submitButtonText = submitText,
        onSubmit = { coroutineScope.launch { onSubmit(it.invoke()) } },
        onCancel = {},
        questionnaireResponseJson = questionnaireResponseJsonString,
        showCancelButton = true,
      )
    }
  }

  return suspend {
    (viewModelStoreOwner.viewModelStore[QUESTIONNAIRE_VIEW_MODEL_PROVIDER_KEY]
        as QuestionnaireViewModel)
      .getQuestionnaireResponse()
  }
}
