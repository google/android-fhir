/*
 * Copyright 2022-2026 Google LLC
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

package com.google.android.fhir.catalog.ui.layouts

import android_fhir.catalog.generated.resources.Res
import android_fhir.catalog.generated.resources.ic_defaultlayout
import android_fhir.catalog.generated.resources.ic_paginatedlayout
import android_fhir.catalog.generated.resources.ic_readonlylayout
import android_fhir.catalog.generated.resources.ic_reviewlayout
import android_fhir.catalog.generated.resources.layout_name_default_text
import android_fhir.catalog.generated.resources.layout_name_paginated
import android_fhir.catalog.generated.resources.layout_name_read_only
import android_fhir.catalog.generated.resources.layout_name_review
import androidx.lifecycle.ViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

class LayoutListViewModel : ViewModel() {

  fun getLayoutList(): List<Layout> {
    return Layout.entries
  }

  enum class Layout(
    val questionnaireFileName: String,
    val icon: DrawableResource,
    val text: StringResource,
    val showReviewPage: Boolean = false,
    val showReviewPageFirst: Boolean = false,
    val isReadOnly: Boolean = false,
  ) {
    DEFAULT(
      "layout_default.json",
      Res.drawable.ic_defaultlayout,
      Res.string.layout_name_default_text,
    ),
    PAGINATED(
      "layout_paginated.json",
      Res.drawable.ic_paginatedlayout,
      Res.string.layout_name_paginated,
    ),
    REVIEW(
      "layout_review.json",
      Res.drawable.ic_reviewlayout,
      Res.string.layout_name_review,
      showReviewPage = true,
      showReviewPageFirst = true,
    ),
    READ_ONLY(
      "layout_default.json", // Reuse default for demo
      Res.drawable.ic_readonlylayout,
      Res.string.layout_name_read_only,
      isReadOnly = true,
    ),
  }
}
