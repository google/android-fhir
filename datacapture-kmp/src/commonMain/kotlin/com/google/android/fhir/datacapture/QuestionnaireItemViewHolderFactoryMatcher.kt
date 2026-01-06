/*
 * Copyright 2025 Google LLC
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

import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewFactory
import com.google.fhir.model.r4.Questionnaire

/**
 * Data class that holds a matcher function ([matches]) which evaluates whether a given [factory]
 * should be used to display a given [Questionnaire.Item].
 *
 * See the
 * [developer guide](https://github.com/google/android-fhir/wiki/SDCL:-Customize-how-a-Questionnaire-is-displayed#custom-questionnaire-components)
 * for more information.
 */
data class QuestionnaireItemViewHolderFactoryMatcher(
  /** The custom [QuestionnaireItemViewFactory] to use. */
  val factory: QuestionnaireItemViewFactory,
  /**
   * A predicate function which, given a [Questionnaire.Item], returns true if the factory should
   * apply to that item.
   */
  val matches: (Questionnaire.Item) -> Boolean,
)
