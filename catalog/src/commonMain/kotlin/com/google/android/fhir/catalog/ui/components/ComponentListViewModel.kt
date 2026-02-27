/*
 * Copyright 2023-2026 Google LLC
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

package com.google.android.fhir.catalog.ui.components

import android_fhir.catalog.generated.resources.Res
import android_fhir.catalog.generated.resources.component_name_attachment
import android_fhir.catalog.generated.resources.component_name_auto_complete
import android_fhir.catalog.generated.resources.component_name_boolean_choice
import android_fhir.catalog.generated.resources.component_name_date_picker
import android_fhir.catalog.generated.resources.component_name_date_time_picker
import android_fhir.catalog.generated.resources.component_name_dropdown
import android_fhir.catalog.generated.resources.component_name_help
import android_fhir.catalog.generated.resources.component_name_initial_value
import android_fhir.catalog.generated.resources.component_name_item_answer_media
import android_fhir.catalog.generated.resources.component_name_item_media
import android_fhir.catalog.generated.resources.component_name_location_widget
import android_fhir.catalog.generated.resources.component_name_modal
import android_fhir.catalog.generated.resources.component_name_multiple_choice
import android_fhir.catalog.generated.resources.component_name_open_choice
import android_fhir.catalog.generated.resources.component_name_per_question_custom_style
import android_fhir.catalog.generated.resources.component_name_quantity
import android_fhir.catalog.generated.resources.component_name_repeated_group
import android_fhir.catalog.generated.resources.component_name_single_choice
import android_fhir.catalog.generated.resources.component_name_slider
import android_fhir.catalog.generated.resources.component_name_text_field
import android_fhir.catalog.generated.resources.component_name_time_picker
import android_fhir.catalog.generated.resources.ic_attachment
import android_fhir.catalog.generated.resources.ic_autocomplete
import android_fhir.catalog.generated.resources.ic_booleanchoice
import android_fhir.catalog.generated.resources.ic_datepicker
import android_fhir.catalog.generated.resources.ic_group_1278
import android_fhir.catalog.generated.resources.ic_help
import android_fhir.catalog.generated.resources.ic_initial_value_component
import android_fhir.catalog.generated.resources.ic_item_answer_media
import android_fhir.catalog.generated.resources.ic_item_media
import android_fhir.catalog.generated.resources.ic_location_on
import android_fhir.catalog.generated.resources.ic_modal
import android_fhir.catalog.generated.resources.ic_multiplechoice
import android_fhir.catalog.generated.resources.ic_openchoice
import android_fhir.catalog.generated.resources.ic_repeatgroups
import android_fhir.catalog.generated.resources.ic_singlechoice
import android_fhir.catalog.generated.resources.ic_slider
import android_fhir.catalog.generated.resources.ic_textfield
import android_fhir.catalog.generated.resources.ic_timepicker
import android_fhir.catalog.generated.resources.ic_unitoptions
import android_fhir.catalog.generated.resources.misc_components
import android_fhir.catalog.generated.resources.text_format_48dp
import android_fhir.catalog.generated.resources.widgets
import androidx.lifecycle.ViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

class ComponentListViewModel : ViewModel() {

  sealed class ViewItem {
    data class HeaderItem(val header: Header) : ViewItem()

    data class ComponentItem(val component: Component) : ViewItem()
  }

  enum class Header(val title: StringResource) {
    WIDGETS(Res.string.widgets),
    MISC_COMPONENTS(Res.string.misc_components),
  }

  enum class Component(
    val questionnaireFile: String,
    val icon: DrawableResource,
    val text: StringResource,
    val questionnaireFileWithValidation: String? = null,
  ) {
    BOOLEAN_CHOICE(
      "component_boolean_choice.json",
      Res.drawable.ic_booleanchoice,
      Res.string.component_name_boolean_choice,
      "component_boolean_choice_with_validation.json",
    ),
    SINGLE_CHOICE(
      "component_single_choice.json",
      Res.drawable.ic_singlechoice,
      Res.string.component_name_single_choice,
      "component_single_choice_with_validation.json",
    ),
    MULTIPLE_CHOICE(
      "component_multi_select_choice.json",
      Res.drawable.ic_multiplechoice,
      Res.string.component_name_multiple_choice,
      "component_multi_select_choice_with_validation.json",
    ),
    DROPDOWN(
      "component_dropdown.json",
      Res.drawable.ic_group_1278,
      Res.string.component_name_dropdown,
      "component_dropdown_with_validation.json",
    ),
    MODAL(
      "component_modal.json",
      Res.drawable.ic_modal,
      Res.string.component_name_modal,
      "component_modal_with_validation.json",
    ),
    OPEN_CHOICE(
      "component_open_choice.json",
      Res.drawable.ic_openchoice,
      Res.string.component_name_open_choice,
      "component_open_choice_with_validation.json",
    ),
    TEXT_FIELD(
      "component_text_fields.json",
      Res.drawable.ic_textfield,
      Res.string.component_name_text_field,
      "component_text_fields_with_validation.json",
    ),
    AUTO_COMPLETE(
      "component_auto_complete.json",
      Res.drawable.ic_autocomplete,
      Res.string.component_name_auto_complete,
      "component_auto_complete_with_validation.json",
    ),
    DATE_PICKER(
      "component_date_picker.json",
      Res.drawable.ic_datepicker,
      Res.string.component_name_date_picker,
      "component_date_picker_with_validation.json",
    ),
    TIME_PICKER(
      "component_time_picker.json",
      Res.drawable.ic_timepicker,
      Res.string.component_name_time_picker,
      "component_time_picker_with_validation.json",
    ),
    DATE_TIME_PICKER(
      "component_date_time_picker.json",
      Res.drawable.ic_timepicker,
      Res.string.component_name_date_time_picker,
      "component_date_time_picker_with_validation.json",
    ),
    SLIDER(
      "component_slider.json",
      Res.drawable.ic_slider,
      Res.string.component_name_slider,
      "component_slider_with_validation.json",
    ),
    QUANTITY(
      "component_quantity.json",
      Res.drawable.ic_unitoptions,
      Res.string.component_name_quantity,
      "component_quantity_with_validation.json",
    ),
    ATTACHMENT(
      "component_attachment.json",
      Res.drawable.ic_attachment,
      Res.string.component_name_attachment,
      "component_attachment_with_validation.json",
    ),
    REPEATED_GROUP(
      "component_repeated_group.json",
      Res.drawable.ic_repeatgroups,
      Res.string.component_name_repeated_group,
    ),
    HELP(
      "component_help.json",
      Res.drawable.ic_help,
      Res.string.component_name_help,
    ),
    ITEM_MEDIA(
      "component_item_media.json",
      Res.drawable.ic_item_media,
      Res.string.component_name_item_media,
    ),
    ITEM_ANSWER_MEDIA(
      "",
      Res.drawable.ic_item_answer_media,
      Res.string.component_name_item_answer_media,
    ),
    INITIAL_VALUE(
      "component_initial_value.json",
      Res.drawable.ic_initial_value_component,
      Res.string.component_name_initial_value,
    ),
    LOCATION_WIDGET(
      "component_location_widget.json",
      Res.drawable.ic_location_on,
      Res.string.component_name_location_widget,
    ),
    QUESTION_ITEM_CUSTOM_STYLE(
      "component_per_question_custom_style.json",
      Res.drawable.text_format_48dp,
      Res.string.component_name_per_question_custom_style,
    ),
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
      ViewItem.ComponentItem(Component.TIME_PICKER),
      ViewItem.ComponentItem(Component.DATE_TIME_PICKER),
      ViewItem.ComponentItem(Component.SLIDER),
      ViewItem.ComponentItem(Component.QUANTITY),
      ViewItem.ComponentItem(Component.ATTACHMENT),
      ViewItem.ComponentItem(Component.REPEATED_GROUP),
      ViewItem.HeaderItem(Header.MISC_COMPONENTS),
      ViewItem.ComponentItem(Component.HELP),
      ViewItem.ComponentItem(Component.ITEM_MEDIA),
      ViewItem.ComponentItem(Component.ITEM_ANSWER_MEDIA),
      ViewItem.ComponentItem(Component.INITIAL_VALUE),
      ViewItem.ComponentItem(Component.LOCATION_WIDGET),
      ViewItem.ComponentItem(Component.QUESTION_ITEM_CUSTOM_STYLE),
    )
}
