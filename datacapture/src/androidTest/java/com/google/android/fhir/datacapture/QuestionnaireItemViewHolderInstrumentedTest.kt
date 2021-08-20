/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.datacapture

import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.views.QuestionnaireItemCheckBoxGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemViewHolderInstrumentedTest {

  @Test
  fun hiddenExtension_true_shouldHideQuestionnaireItemView() {
    val viewHolder =
      QuestionnaireItemCheckBoxGroupViewHolderFactory.create(
        FrameLayout(InstrumentationRegistry.getInstrumentation().context)
      )

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          addExtension().apply {
            url = EXTENSION_HIDDEN_URL
            setValue(BooleanType(true))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.isGone).isTrue()
  }
}
