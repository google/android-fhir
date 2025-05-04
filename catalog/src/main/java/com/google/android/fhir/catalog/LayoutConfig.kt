/*
 * Copyright 2025 Google LLC
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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.fhir.datacapture.QuestionnaireFragment

data class LayoutConfig(
  @DrawableRes val iconId: Int,
  @StringRes val textId: Int,
  val questionnaireFileName: String,
  val questionnaireLambdaKey: String,
) {

  companion object {
    val questionnaireLambdaMap: Map<String, QuestionnaireFragment.Builder.() -> Unit> =
      mapOf(
        "" to
          {
            showReviewPageFirst(false)
            showReviewPageBeforeSubmit(false)
          },
        "showreviewpagefirstandbeforesubmit" to
          {
            showReviewPageFirst(true)
            showReviewPageBeforeSubmit(true)
          },
      )
  }

  class Builder {
    @DrawableRes var iconId: Int = 0

    @StringRes var textId: Int = 0
    var questionnaireFileName: String = ""
    var questionnaireLambdaKey: String = ""

    fun build(): LayoutConfig {
      return LayoutConfig(iconId, textId, questionnaireFileName, questionnaireLambdaKey)
    }
  }
}

fun layoutConfig(block: LayoutConfig.Builder.() -> Unit): LayoutConfig {
  val builder = LayoutConfig.Builder()
  builder.block()
  return builder.build()
}
