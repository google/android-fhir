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

package com.google.android.fhir.datacapture.views

import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.Questionnaire

/**
 * Appends asterisk to the flyover text if question text
 * [Questionnaire.QuestionnaireItemComponent.localizedTextSpanned] is null or blank and
 * questionnaire item is must be answered.
 */
inline fun TextInputLayout.showAsteriskInFlyoverText(
  questionnaireItem: Questionnaire.QuestionnaireItemComponent
) {
  if (!questionnaireItem.required || !questionnaireItem.localizedTextSpanned.isNullOrBlank()) {
    return
  }
  showAsteriskInFlyoverText(questionnaireItem.localizedFlyoverSpanned.toString())
}

/** Appends asterisk to the argument [flyoverText]. */
inline fun TextInputLayout.showAsteriskInFlyoverText(flyoverText: String) {
  val asterisk = context.applicationContext.getString(R.string.space_asterisk)
  hint = flyoverText.plus(asterisk)
  helperText = context.applicationContext.getString(R.string.required)
}
