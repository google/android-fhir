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
      Component.BOOLEAN_CHOICE -> "single_choice_questionnaire_boolean.json"
      else -> "" // TODO remove else when all components cases are added to the when.
    // https://github.com/google/android-fhir/issues/1076
    }
  }

  enum class Component(@DrawableRes val iconId: Int, @StringRes val textId: Int) {
    SINGLE_CHOICE(R.drawable.ic_singlechoice, R.string.component_name_single_choice),
    BOOLEAN_CHOICE(R.drawable.ic_booleanchoice, R.string.component_name_boolean_choice),
    MULTIPLE_CHOICE(R.drawable.ic_multiplechoice, R.string.component_name_multiple_choice),
    DROPDOWN(R.drawable.ic_group_1278, R.string.component_name_dropdown),
    MODAL(R.drawable.ic_modal, R.string.component_name_modal),
    OPEN_CHOICE(R.drawable.ic_openchoice, R.string.component_name_open_choice),
    TEXT_FIELD(R.drawable.ic_textfield, R.string.component_name_text_field),
    DATE_PICKER(R.drawable.ic_datepicker, R.string.component_name_date_picker),
    TIME_PICKER(R.drawable.ic_timepicker, R.string.component_name_time_picker),
    SLIDER(R.drawable.ic_slider, R.string.component_name_slider),
    IMAGE(R.drawable.ic_image, R.string.component_name_image),
  }
}
