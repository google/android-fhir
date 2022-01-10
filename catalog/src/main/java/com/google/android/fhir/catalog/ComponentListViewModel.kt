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

  fun getQuestionnaire(component: Component): String {
    return when (component) {
      Component.MULTIPLE_CHOICE -> "multi_select_choice_questionnaire.json"
      Component.BOOLEAN_CHOICE -> "single_choice_questionnaire_boolean.json"
      else -> "" // TODO remove else when all components cases are added to the when.
    }
  }

  enum class Component(@DrawableRes val iconId: Int, @StringRes val textId: Int) {
    MULTIPLE_CHOICE(R.drawable.ic_multiplechoice, R.string.multiple_choice),
    SINGLE_CHOICE(R.drawable.ic_singlechoice, R.string.single_choice),
    OPEN_CHOICE(R.drawable.ic_openchoice, R.string.open_choice),
    BOOLEAN_CHOICE(R.drawable.ic_booleanchoice, R.string.boolean_choice),
    TEXT_FIELD(R.drawable.ic_textfield, R.string.text_field),
    DATE_PICKER(R.drawable.ic_datepicker, R.string.date_picker),
    TIME_PICKER(R.drawable.ic_timepicker, R.string.time_picker),
    MODAL(R.drawable.ic_modal, R.string.modal),
    SLIDER(R.drawable.ic_slider, R.string.slider),
    DROPDOWN(R.drawable.ic_group_1278, R.string.dropdown),
    IMAGE(R.drawable.ic_image, R.string.image),
    DATE_OF_BIRTH(R.drawable.ic_dateofbirth, R.string.date_of_Birth),
    DATE_RANGE_PICKER(R.drawable.ic_rangepicker, R.string.date_range_picker),
    UNIT_OPTIONS(R.drawable.ic_unitoptions, R.string.unit_options),
  }
}
