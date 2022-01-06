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

package com.google.android.fhir.datacapture.views

import android.util.Base64
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemGroupViewHolderFactoryInstrumentedTest {
  private lateinit var context: ContextThemeWrapper
  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setUp() {
    context =
      ContextThemeWrapper(
        InstrumentationRegistry.getInstrumentation().targetContext,
        R.style.Theme_MaterialComponents
      )
    parent = FrameLayout(context)
    viewHolder = QuestionnaireItemGroupViewHolderFactory.create(parent)
  }

  @Test
  fun shouldShowPrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix_text_view).isVisible).isTrue()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix_text_view).text)
      .isEqualTo("Prefix?")
  }

  @Test
  fun shouldHidePrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix_text_view).isVisible)
      .isFalse()
  }

  @Test
  fun shouldSetTextViewText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Group header" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.group_header).text)
      .isEqualTo("Group header")
  }

  @Test
  fun shouldSetTextViewVisible() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Group header" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.group_header).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun shouldSetTextViewGone() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.group_header).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  @UiThreadTest
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.group_header).error)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  @UiThreadTest
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          required = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "display" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Coding().apply { display = "display" }
            }
          )
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.group_header).error).isNull()
  }

  @Test
  fun shouldShowImage_whenItemImageExtensionIsSet() = runBlocking {
    val attachment =
      Attachment().apply {
        data =
          Base64.decode("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7", Base64.DEFAULT)
        contentType = "image/png"
      }
    val questionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Kindly collect the reading as shown below in the figure"
        extension =
          listOf(
            Extension("http://hl7.org/fhir/uv/sdc/StructureDefinition/cpg-itemImage", attachment)
          )
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        questionnaireItemComponent,
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    delay(1000)

    assertThat(viewHolder.itemView.findViewById<ImageView>(R.id.itemImage).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun shouldHideImageView_whenItemImageExtensionIsNotSet() = runBlocking {
    val questionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Kindly collect the reading as shown below in the figure"
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        questionnaireItemComponent,
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    delay(1000)

    assertThat(viewHolder.itemView.findViewById<ImageView>(R.id.itemImage).visibility)
      .isEqualTo(View.GONE)
  }
}
