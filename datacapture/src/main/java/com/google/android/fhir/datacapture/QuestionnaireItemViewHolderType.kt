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

package com.google.android.fhir.datacapture

/**
 * Questionnaire item view holder types supported by default by the data capture library.
 *
 * This is used in [QuestionnaireItemAdapter] to determine how each [Questionnaire.Item] is
 * rendered.
 *
 * This list should provide sufficient coverage for values in
 * https://www.hl7.org/fhir/valueset-item-type.html and
 * http://hl7.org/fhir/R4/valueset-questionnaire-item-control.html.
 */
internal enum class QuestionnaireItemViewHolderType(val value: Int) {
  GROUP(0),
  DATE_PICKER(1),
  DATE_TIME_PICKER(2),
  EDIT_TEXT_SINGLE_LINE(3),
  EDIT_TEXT_MULTI_LINE(4),
  EDIT_TEXT_INTEGER(5),
  EDIT_TEXT_DECIMAL(6),
  RADIO_GROUP(7),
  DROP_DOWN(8),
  DISPLAY(9),
  QUANTITY(10),
  CHECK_BOX_GROUP(11),
  AUTO_COMPLETE(12),
  DIALOG_SELECT(13),
  BOOLEAN_TYPE_PICKER(14);

  companion object {
    private val VALUES = values()
    fun fromInt(value: Int) = VALUES[value]
  }
}
