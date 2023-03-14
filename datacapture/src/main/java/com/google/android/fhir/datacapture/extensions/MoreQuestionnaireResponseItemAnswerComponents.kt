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

package com.google.android.fhir.datacapture.extensions

import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Type

internal fun List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>
  .hasDifferentAnswerSet(answers: List<Type>) =
  this.size != answers.size ||
    this.map { it.value }.zip(answers).any { (v1, v2) -> v1.equalsDeep(v2).not() }
