/*
 * Copyright 2022-2023 Google LLC
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

class LayoutListViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {

  fun getLayoutList(): List<Layout> {
    return Layout.values().toList()
  }

  enum class Layout(
    @DrawableRes val iconId: Int,
    @StringRes val textId: Int,
    val questionnaireFileName: String,
    val workflow: WorkflowType = WorkflowType.PAGINATED,
  ) {
    DEFAULT(
      R.drawable.ic_defaultlayout,
      R.string.layout_name_default_text,
      "layout_default.json",
      WorkflowType.DEFAULT,
    ),
    PAGINATED(
      R.drawable.ic_paginatedlayout,
      R.string.layout_name_paginated,
      "layout_paginated.json",
    ),
    REVIEW(R.drawable.ic_reviewlayout, R.string.layout_name_review, ""),
    READ_ONLY(R.drawable.ic_readonlylayout, R.string.layout_name_read_only, ""),
  }
}
