/*
 * Copyright 2021 Google LLC
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

object Constant {

  //From views/DatePickerDialogFragment.kt
  const val TAG_DATE_PICKER = "date-picker-fragment"
  const val RESULT_DATE_PICKER_REQUEST_KEY = "date-picker-request-key"
  const val RESULT_DATE_PICKER_BUNDLE_KEY_YEAR = "date-picker-bundle-key-year"
  const val RESULT_DATE_PICKER_BUNDLE_KEY_MONTH = "date-picker-bundle-key-month"
  const val RESULT_DATE_PICKER_BUNDLE_KEY_DAY_OF_MONTH = "date-picker-bundle-day-of-month"

  //From QuestionnaireFragment.kt
  const val BUNDLE_KEY_QUESTIONNAIRE = "questionnaire"
  const val BUNDLE_KEY_QUESTIONNAIRE_RESPONSE = "questionnaire-response"

  //From mapping/ResourceMapper.kt
  /**
   * See
   * [Extension: item extraction context](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
   * .
   */
  const val ITEM_CONTEXT_EXTENSION_URL =
    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext"

  //From MoreQuestionnaireItemExtensions.kt
  const val ITEM_CONTROL_DROP_DOWN = "drop-down"
  const val ITEM_CONTROL_RADIO_BUTTON = "radio-button"
  const val EXTENSION_ITEM_CONTROL_URL =
    "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl"
  const val EXTENSION_ITEM_CONTROL_SYSTEM = "http://hl7.org/fhir/questionnaire-item-control"

  //From views/TimePickerDialogFragment.kt
  const val TAG_TIME_PICKER = "time-picker-fragment"
  const val RESULT_TIME_PICKER_REQUEST_KEY = "time-picker-request-key"
  const val RESULT_TIME_PICKER_BUNDLE_KEY_HOUR = "time-picker-bundle-key-hour"
  const val RESULT_TIME_PICKER_BUNDLE_KEY_MINUTE = "time-picker-bundle-key-minute"



}