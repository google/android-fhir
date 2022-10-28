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

package com.google.android.fhir.datacapture.views

import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemAttachmentPickerViewHolderFactoryEspressoTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.getScenario().onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = QuestionnaireItemAttachmentViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun shouldShowTakePhotoAndUploadPhotoButton() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("image/*"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.take_photo).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).text)
      .isEqualTo(parent.context.getString(R.string.upload_photo))
  }

  @Test
  fun shouldShowTakePhotoAndSelectFileButton() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("image/*"))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("application/pdf"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.take_photo).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).text)
      .isEqualTo(parent.context.getString(R.string.select_file))
  }

  @Test
  fun shouldShowSelectFileButtonOnly() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("application/pdf"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).text)
      .isEqualTo(parent.context.getString(R.string.select_file))
  }

  @Test
  fun shouldShowPreviewPhotoFromAnswer() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("image/*"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Attachment().apply {
                  data =
                    Base64.decode(
                      "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                      Base64.DEFAULT
                    )
                  contentType = "image/*"
                }
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.take_photo).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).text)
      .isEqualTo(parent.context.getString(R.string.upload_photo))

    assertThat(viewHolder.itemView.findViewById<ImageView>(R.id.photo_preview).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun shouldShowPreviewDocumentFileFromAnswer() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("application/pdf"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Attachment().apply {
                  title = "Document File"
                  data =
                    Base64.decode(
                      "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                      Base64.DEFAULT
                    )
                  contentType = "application/pdf"
                }
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).text)
      .isEqualTo(parent.context.getString(R.string.select_file))

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.file_preview).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<ImageView>(R.id.icon_file_preview).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.title_file_preview).text)
      .isEqualTo("Document File")
  }

  @Test
  fun shouldShowPreviewVideoFileFromAnswer() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("application/pdf"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Attachment().apply {
                  title = "Video File"
                  data =
                    Base64.decode(
                      "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                      Base64.DEFAULT
                    )
                  contentType = "application/pdf"
                }
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).text)
      .isEqualTo(parent.context.getString(R.string.select_file))

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.file_preview).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<ImageView>(R.id.icon_file_preview).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.title_file_preview).text)
      .isEqualTo("Video File")
  }

  @Test
  fun shouldShowPreviewAudioFileFromAnswer() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("application/pdf"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Attachment().apply {
                  title = "Audio File"
                  data =
                    Base64.decode(
                      "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                      Base64.DEFAULT
                    )
                  contentType = "application/pdf"
                }
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload).text)
      .isEqualTo(parent.context.getString(R.string.select_file))

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.file_preview).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<ImageView>(R.id.icon_file_preview).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.title_file_preview).text)
      .isEqualTo("Audio File")
  }

  /** Method to run code snippet on UI/main thread */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.getScenario().onActivity { activity -> action() }
  }

  /** Method to set content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.getScenario().onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }
}
