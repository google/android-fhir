/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.datacapture.contrib.views.barcode

import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BarCodeReaderViewHolderFactoryInstrumentedTest {

  private lateinit var context: ContextThemeWrapper
  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setUp() {
    context =
      ContextThemeWrapper(
        InstrumentationRegistry.getInstrumentation().targetContext,
        com.google.android.material.R.style.Theme_MaterialComponents,
      )
    parent = FrameLayout(context)
    viewHolder = BarCodeReaderViewHolderFactory.create(parent)
  }

  @Test
  fun shouldShowPrefixText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isTrue()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).text.toString())
      .isEqualTo("Prefix?")
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.tv_rescan).isVisible).isFalse()
  }

  @Test
  fun shouldHidePrefixText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isFalse()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.tv_rescan).isVisible).isFalse()
  }

  @Test
  fun shouldSetTextViewText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.tv_rescan).isVisible).isFalse()
  }

  @Test
  @UiThreadTest
  fun shouldSetInputText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = StringType("Barcode Result")
            },
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.textInputEditText).text.toString())
      .isEqualTo("Barcode Result")
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.tv_rescan).isVisible).isTrue()
  }
}
