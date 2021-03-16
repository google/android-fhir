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

package com.google.android.fhir.datacapture.gallery

/**
 * @param name the name of the questionnaire to be displayed
 * @param description the description of the questionnaire to be displayed
 * @param questionnairePath the questionnaire's JSON filename in the assets directory
 * @param questionnaireResponsePath the corresponding questionnaireResponse's JSON filename in the
 * assets directory (optional)
 */
data class QuestionnaireListItem(
  val name: String,
  val description: String,
  val questionnairePath: String,
  val questionnaireResponsePath: String?
) {

  constructor(
    name: String,
    description: String,
    questionnairePath: String
  ) : this(name, description, questionnairePath, null)
}
