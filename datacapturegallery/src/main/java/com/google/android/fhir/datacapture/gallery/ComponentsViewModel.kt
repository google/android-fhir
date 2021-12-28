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

package com.google.android.fhir.datacapture.gallery

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle

class ComponentsViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {

  fun getComponentsList(): List<Components> {
    return Components.values().toList()
  }

  fun getLayoutList(): List<SdcLayouts> {
    return SdcLayouts.values().toList()
  }

  enum class Components(@DrawableRes val iconId: Int, @StringRes val textId: Int) {
    MULTIPLE_CHOICE(R.drawable.ic_layout_icon, R.string.multiple_choice),
    SINGLE_CHOICE(R.drawable.ic_components_icon, R.string.single_choice),
    OPEN_CHOICE(R.drawable.ic_layout_icon, R.string.open_choice),
    TEXT_FIELD(R.drawable.ic_components_icon, R.string.text_field),
    DATE_PICKER(R.drawable.ic_layout_icon, R.string.date_picker),
    TIME_PICKER(R.drawable.ic_components_icon, R.string.time_picker),
    MODAL(R.drawable.ic_layout_icon, R.string.modal),
    SLIDER(R.drawable.ic_components_icon, R.string.slider),
    DROPDOWN(R.drawable.ic_layout_icon, R.string.dropdown),
    IMAGE(R.drawable.ic_components_icon, R.string.image),
    DATE_OF_BIRTH(R.drawable.ic_layout_icon, R.string.date_of_Birth),
    DATE_RANGE_PICKER(R.drawable.ic_components_icon, R.string.date_range_picker),
    UNIT_OPTIONS(R.drawable.ic_components_icon, R.string.unit_options),
  }

  enum class SdcLayouts(@DrawableRes val iconId: Int, @StringRes val textId: Int) {
    DEFAULT(R.drawable.ic_layout_icon, R.string.default_text),
    PAGINATED(R.drawable.ic_components_icon, R.string.paginated),
    REVIEW(R.drawable.ic_layout_icon, R.string.review),
    READ_ONLY(R.drawable.ic_components_icon, R.string.read_only),
  }
}
