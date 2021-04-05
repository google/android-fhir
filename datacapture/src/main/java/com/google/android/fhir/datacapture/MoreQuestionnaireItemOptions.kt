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

package com.google.android.fhir.datacapture.views

import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * If a [Questionnaire.QuestionnaireItemComponent] has nested items and is not of type group, its
 * corresponding [QuestionnaireResponse.QuestionnaireResponseItemComponent] will have the nested
 * items within its [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent]
 *
 * The property below is used to identify such [Questionnaire.QuestionnaireItemComponent]
 */
val Questionnaire.QuestionnaireItemComponent.hasNestedItemsWithinAnswers: Boolean
  get() = item.isNotEmpty() && type != Questionnaire.QuestionnaireItemType.GROUP
