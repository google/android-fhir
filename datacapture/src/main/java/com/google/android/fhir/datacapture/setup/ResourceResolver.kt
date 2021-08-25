/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.datacapture.setup

import org.hl7.fhir.r4.model.Coding

/**
 * This helps library to get [List]<[Coding]> for a [org.hl7.fhir.r4.model.ValueSet] mentioned in
 * [org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent.answerValueSet]. The clients
 * using SDC library need to resolve the [List]<[Coding]> for it.
 */
interface AnswerValueSetResolver {
  suspend fun resolve(uri: String): List<Coding>
}
