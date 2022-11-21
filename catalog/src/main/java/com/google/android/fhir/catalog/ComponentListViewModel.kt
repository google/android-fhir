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
import com.google.android.fhir.catalog.ComponentsRecyclerViewAdapter.ViewType
import com.google.android.fhir.catalog.ComponentsRecyclerViewAdapter.ViewType.HEADER_TYPE
import com.google.android.fhir.catalog.ComponentsRecyclerViewAdapter.ViewType.ITEM_TYPE

class ComponentListViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {

  fun getComponentList(): List<Component> {
    return Component.values().toList()
  }

  enum class Component(
    @DrawableRes val iconId: Int = 0,
    @StringRes val textId: Int,
    val viewType: ViewType = ITEM_TYPE,
    /** Path to the questionnaire json file with no required fields. */
    val questionnaireFile: String = "",
    /**
     * Path to the questionnaire json file with some or all required fields. If the user doesn't
     * answer the required questions, an error may be displayed on the particular question.
     */
    val questionnaireFileWithValidation: String = "",
    val workflow: WorkflowType = WorkflowType.COMPONENT
  ) {
    WIDGETS(textId = R.string.widgets, viewType = HEADER_TYPE),
    BOOLEAN_CHOICE(
      R.drawable.ic_booleanchoice,
      R.string.component_name_boolean_choice,
      questionnaireFile = "component_boolean_choice.json",
      questionnaireFileWithValidation = "component_boolean_choice_with_validation.json"
    ),
    SINGLE_CHOICE(
      R.drawable.ic_singlechoice,
      R.string.component_name_single_choice,
      questionnaireFile = "component_single_choice.json",
      questionnaireFileWithValidation = "component_single_choice_with_validation.json"
    ),
    MULTIPLE_CHOICE(
      R.drawable.ic_multiplechoice,
      R.string.component_name_multiple_choice,
      questionnaireFile = "component_multi_select_choice.json",
      questionnaireFileWithValidation = "component_multi_select_choice_with_validation.json"
    ),
    DROPDOWN(
      R.drawable.ic_group_1278,
      R.string.component_name_dropdown,
      questionnaireFile = "component_dropdown.json",
      questionnaireFileWithValidation = "component_dropdown_with_validation.json"
    ),
    MODAL(
      R.drawable.ic_modal,
      R.string.component_name_modal,
      questionnaireFile = "component_modal.json",
      questionnaireFileWithValidation = "component_modal_with_validation.json"
    ),
    OPEN_CHOICE(
      R.drawable.ic_openchoice,
      R.string.component_name_open_choice,
      questionnaireFile = "component_open_choice.json",
      questionnaireFileWithValidation = "component_open_choice_with_validation.json"
    ),
    TEXT_FIELD(
      R.drawable.ic_textfield,
      R.string.component_name_text_field,
      questionnaireFile = "component_text_fields.json",
      questionnaireFileWithValidation = "component_text_fields_with_validation.json"
    ),
    DATE_PICKER(
      R.drawable.ic_datepicker,
      R.string.component_name_date_picker,
      questionnaireFile = "component_date_picker.json",
      questionnaireFileWithValidation = "component_date_picker_with_validation.json"
    ),
    DATE_TIME_PICKER(
      R.drawable.ic_timepicker,
      R.string.component_name_date_time_picker,
      questionnaireFile = "component_date_time_picker.json",
      questionnaireFileWithValidation = "component_date_time_picker_with_validation.json"
    ),

    // TODO https://github.com/google/android-fhir/issues/1260
    //    SLIDER(
    //      R.drawable.ic_slider,
    //      R.string.component_name_slider,
    //      "component_slider.json",
    //      "component_slider_with_validation.json"
    //    ),
    IMAGE(R.drawable.ic_image, R.string.component_name_image),
    AUTO_COMPLETE(
      R.drawable.ic_autocomplete,
      R.string.component_name_auto_complete,
      questionnaireFile = "component_auto_complete.json",
      questionnaireFileWithValidation = "component_auto_complete_with_validation.json"
    ),
    REPEATED_GROUP(
      R.drawable.ic_textfield,
      R.string.component_name_repeated_group,
      questionnaireFile = "component_repeated_group.json",
    ),
    MISC_COMPONENTS(textId = R.string.misc_components, viewType = HEADER_TYPE),
    HELP(
      R.drawable.ic_help,
      R.string.component_name_help,
      questionnaireFile = "component_help.json"
    )
  }
}
