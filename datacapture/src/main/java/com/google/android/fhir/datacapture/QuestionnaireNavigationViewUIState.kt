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

package com.google.android.fhir.datacapture

sealed class QuestionnaireNavigationViewUIState {
  object Hidden : QuestionnaireNavigationViewUIState()

  object Disabled : QuestionnaireNavigationViewUIState()

  data class Enabled(val onClickAction: () -> Unit) : QuestionnaireNavigationViewUIState()
}

data class QuestionnaireNavigationUIState(
  val navPrevious: QuestionnaireNavigationViewUIState = QuestionnaireNavigationViewUIState.Hidden,
  val navNext: QuestionnaireNavigationViewUIState = QuestionnaireNavigationViewUIState.Hidden,
  val navSubmit: QuestionnaireNavigationViewUIState = QuestionnaireNavigationViewUIState.Hidden,
  val navCancel: QuestionnaireNavigationViewUIState = QuestionnaireNavigationViewUIState.Hidden,
  val navReview: QuestionnaireNavigationViewUIState = QuestionnaireNavigationViewUIState.Hidden,
)
