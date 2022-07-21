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

package com.google.android.fhir.catalog

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle

class ComponentListViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {

  fun getComponentList(): List<Component> {
    return Component.values().toList()
  }

  enum class Component(
    @DrawableRes val iconId: Int,
    @StringRes val textId: Int,
    /** Path to the questionnaire json file with no required fields. */
    val questionnaireFile: String,
    /**
     * Path to the questionnaire json file with some or all required fields. If the user doesn't
     * answer the required questions, an error may be displayed on the particular question.
     */
    val questionnaireFileWithValidation: String = "",
    val workflow: WorkflowType = WorkflowType.COMPONENT
  ) {
    SINGLE_CHOICE(
      R.drawable.ic_singlechoice,
      R.string.component_name_single_choice,
      "single_choice_questionnaire.json",
      "single_choice_with_validation_questionnaire.json"
    ),
    BOOLEAN_CHOICE(
      R.drawable.ic_booleanchoice,
      R.string.component_name_boolean_choice,
      "boolean_choice_questionnaire.json",
      "boolean_choice_with_validation_questionnaire.json"
    ),
    MULTIPLE_CHOICE(
      R.drawable.ic_multiplechoice,
      R.string.component_name_multiple_choice,
      "multi_select_choice_questionnaire.json",
      "multi_select_choice_with_validation_questionnaire.json"
    ),
    DROPDOWN(
      R.drawable.ic_group_1278,
      R.string.component_name_dropdown,
      "dropdown-questionnaire.json",
      "dropdown_with_validation_questionnaire.json"
    ),
    MODAL(
      R.drawable.ic_modal,
      R.string.component_name_modal,
      "modal-questionnaire.json",
      "modal_with_validation_questionnaire.json"
    ),
    OPEN_CHOICE(
      R.drawable.ic_openchoice,
      R.string.component_name_open_choice,
      "open-choice-questionnaire.json",
      "open_choice_with_validation_questionnaire.json"
    ),
    TEXT_FIELD(
      R.drawable.ic_textfield,
      R.string.component_name_text_field,
      "text_fields_questionnaire.json",
      "text_fields_with_validation_questionnaire.json"
    ),
    DATE_PICKER(
      R.drawable.ic_datepicker,
      R.string.component_name_date_picker,
      "date_picker_questionnaire.json",
      "date_picker_with_validation_questionnaire.json"
    ),
    DATE_TIME_PICKER(
      R.drawable.ic_timepicker,
      R.string.component_name_date_time_picker,
      "date_time_questionnaire.json",
      "date_time_with_validation_questionnaire.json"
    ),
    SLIDER(
      R.drawable.ic_slider,
      R.string.component_name_slider,
      "slider_questionnaire.json",
      "slider_with_validation_questionnaire.json"
    ),
    IMAGE(R.drawable.ic_image, R.string.component_name_image, "", ""),
    AUTO_COMPLETE(
      R.drawable.ic_textfield,
      R.string.component_name_auto_complete,
      "auto_complete_questionnaire.json",
      "auto_complete_with_validation_questionnaire.json"
    ),
    CALCULATED_EXPRESSION(
      R.drawable.ic_unitoptions,
      R.string.component_name_calculated_expression,
      "calculated_expression_questionnaire.json"
    ),
  }
}
