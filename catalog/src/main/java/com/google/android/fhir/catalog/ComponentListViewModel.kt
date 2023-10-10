/*
 * Copyright 2023 Google LLC
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

  sealed class ViewItem {
    data class HeaderItem(val header: Header) : ViewItem()
    data class ComponentItem(val component: Component) : ViewItem()
  }

  enum class Header(@StringRes val textId: Int) {
    WIDGETS(R.string.widgets),
    MISC_COMPONENTS(R.string.misc_components)
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
    val questionnaireFileWithValidation: String? = null,
    val workflow: WorkflowType = WorkflowType.COMPONENT
  ) {
    BOOLEAN_CHOICE(
      R.drawable.ic_booleanchoice,
      R.string.component_name_boolean_choice,
      "component_boolean_choice.json",
      "component_boolean_choice_with_validation.json"
    ),
    SINGLE_CHOICE(
      R.drawable.ic_singlechoice,
      R.string.component_name_single_choice,
      "component_single_choice.json",
      "component_single_choice_with_validation.json"
    ),
    MULTIPLE_CHOICE(
      R.drawable.ic_multiplechoice,
      R.string.component_name_multiple_choice,
      "component_multi_select_choice.json",
      "component_multi_select_choice_with_validation.json"
    ),
    DROPDOWN(
      R.drawable.ic_group_1278,
      R.string.component_name_dropdown,
      "component_dropdown.json",
      "component_dropdown_with_validation.json"
    ),
    MODAL(
      R.drawable.ic_modal,
      R.string.component_name_modal,
      "component_modal.json",
      "component_modal_with_validation.json"
    ),
    OPEN_CHOICE(
      R.drawable.ic_openchoice,
      R.string.component_name_open_choice,
      "component_open_choice.json",
      "component_open_choice_with_validation.json"
    ),
    TEXT_FIELD(
      R.drawable.ic_textfield,
      R.string.component_name_text_field,
      "component_text_fields.json",
      "component_text_fields_with_validation.json"
    ),
    AUTO_COMPLETE(
      R.drawable.ic_autocomplete,
      R.string.component_name_auto_complete,
      "component_auto_complete.json",
      "component_auto_complete_with_validation.json"
    ),
    DATE_PICKER(
      R.drawable.ic_datepicker,
      R.string.component_name_date_picker,
      "component_date_picker.json",
      "component_date_picker_with_validation.json"
    ),
    DATE_TIME_PICKER(
      R.drawable.ic_timepicker,
      R.string.component_name_date_time_picker,
      "component_date_time_picker.json",
      "component_date_time_picker_with_validation.json"
    ),
    SLIDER(
      R.drawable.ic_slider,
      R.string.component_name_slider,
      "component_slider.json",
      "component_slider_with_validation.json"
    ),
    QUANTITY(
      R.drawable.ic_unitoptions,
      R.string.component_name_quantity,
      "component_quantity.json",
      "component_quantity_with_validation.json"
    ),
    ATTACHMENT(
      R.drawable.ic_attachment,
      R.string.component_name_attachment,
      "component_attachment.json",
      "component_attachment_with_validation.json"
    ),
    REPEATED_GROUP(
      R.drawable.ic_repeatgroups,
      R.string.component_name_repeated_group,
      "component_repeated_group.json",
    ),
    HELP(R.drawable.ic_help, R.string.component_name_help, "component_help.json"),
    ITEM_MEDIA(
      R.drawable.ic_item_media,
      R.string.component_name_item_media,
      "component_item_media.json"
    ),
    ITEM_ANSWER_MEDIA(
      R.drawable.ic_item_answer_media,
      R.string.component_name_item_answer_media,
      ""
    )
  }

  val viewItemList =
    listOf(
      ViewItem.HeaderItem(Header.WIDGETS),
      ViewItem.ComponentItem(Component.BOOLEAN_CHOICE),
      ViewItem.ComponentItem(Component.SINGLE_CHOICE),
      ViewItem.ComponentItem(Component.MULTIPLE_CHOICE),
      ViewItem.ComponentItem(Component.DROPDOWN),
      ViewItem.ComponentItem(Component.MODAL),
      ViewItem.ComponentItem(Component.OPEN_CHOICE),
      ViewItem.ComponentItem(Component.TEXT_FIELD),
      ViewItem.ComponentItem(Component.AUTO_COMPLETE),
      ViewItem.ComponentItem(Component.DATE_PICKER),
      ViewItem.ComponentItem(Component.DATE_TIME_PICKER),
      ViewItem.ComponentItem(Component.SLIDER),
      ViewItem.ComponentItem(Component.QUANTITY),
      ViewItem.ComponentItem(Component.ATTACHMENT),
      ViewItem.ComponentItem(Component.REPEATED_GROUP),
      ViewItem.HeaderItem(Header.MISC_COMPONENTS),
      ViewItem.ComponentItem(Component.HELP),
      ViewItem.ComponentItem(Component.ITEM_MEDIA),
      ViewItem.ComponentItem(Component.ITEM_ANSWER_MEDIA),
    )
}
