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

class BehaviorListViewModel(application: Application) : AndroidViewModel(application) {

  fun getBehaviorList(): List<Behavior> {
    return Behavior.values().toList()
  }

  enum class Behavior(
    @DrawableRes val iconId: Int,
    @StringRes val textId: Int,
    val questionnaireFileName: String,
    val workFlow: WorkflowType = WorkflowType.BEHAVIOR
  ) {
    CALCULATED_EXPRESSION(
      R.drawable.ic_calculations_behavior,
      R.string.behavior_name_calculated_expression,
      "behavior_calculated_expression.json"
    ),
    CONTEXT_VARIABLES(
      R.drawable.ic_context,
      R.string.behavior_name_context_variables,
      "behavior_context_variables.json"
    ),
    SKIP_LOGIC(
      R.drawable.ic_skiplogic_behavior,
      R.string.behavior_name_skip_logic,
      "behavior_skip_logic.json"
    ),
    DYNAMIC_QUESTION_TEXT(
      R.drawable.ic_dynamic_text_behavior,
      R.string.behavior_name_dynamic_question_text,
      "behavior_dynamic_question_text.json"
    )
  }
}
