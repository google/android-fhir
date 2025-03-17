/*
 * Copyright 2022-2025 Google LLC
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
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle

class LayoutListViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {

  fun getLayoutList(): List<Layout> {
    return Layout.values().toList()
  }

  enum class Layout(
    val config: LayoutConfig,
  ) {
    DEFAULT(
      layoutConfig {
        iconId = R.drawable.ic_defaultlayout
        textId = R.string.layout_name_default_text
        questionnaireFileName = "layout_default.json"
        enableReviewMode = false
      },
    ),
    PAGINATED(
      layoutConfig {
        iconId = R.drawable.ic_paginatedlayout
        textId = R.string.layout_name_paginated
        questionnaireFileName = "layout_paginated.json"
        enableReviewMode = false
      },
    ),
    REVIEW(
      layoutConfig {
        iconId = R.drawable.ic_reviewlayout
        textId = R.string.layout_name_review
        questionnaireFileName = "layout_review.json"
        enableReviewMode = true
      },
    ),
    READ_ONLY(
      layoutConfig {
        iconId = R.drawable.ic_readonlylayout
        textId = R.string.layout_name_read_only
        questionnaireFileName = ""
        enableReviewMode = false
      },
    ),
  }

  fun isDefaultLayout(context: Context, title: String) =
    context.getString(Layout.DEFAULT.config.textId) == title

  fun isPaginatedLayout(context: Context, title: String) =
    context.getString(Layout.PAGINATED.config.textId) == title
}
