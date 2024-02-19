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

package com.google.android.fhir.datacapture.test.views

import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.AttachmentViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
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
class AttachmentViewHolderFactoryEspressoTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.getScenario().onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = AttachmentViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun shouldDisplayTakePhotoAndUploadPhotoButton() {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("image/*"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.take_photo).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload_photo).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload_photo).text)
      .isEqualTo(parent.context.getString(R.string.upload_photo))
  }

  @Test
  fun shouldDisplayUploadAudioButton() {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("audio/*"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload_audio).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload_audio).text)
      .isEqualTo(parent.context.getString(R.string.upload_audio))
  }

  @Test
  fun shouldDisplayUploadVideoButton() {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("video/*"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload_video).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload_video).text)
      .isEqualTo(parent.context.getString(R.string.upload_video))
  }

  @Test
  fun shouldDisplayUploadDocumentButton() {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("application/pdf"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload_document).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload_document).text)
      .isEqualTo(parent.context.getString(R.string.upload_document))
  }

  @Test
  fun shouldDisplayTakePhotoAndUploadFileButton() {
    val questionnaireItemView =
      QuestionnaireViewItem(
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
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.take_photo).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload_file).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<Button>(R.id.upload_file).text)
      .isEqualTo(parent.context.getString(R.string.upload_file))
  }

  @Test
  fun shouldDisplayImagePreviewFromAnswer() {
    val questionnaireItemView =
      QuestionnaireViewItem(
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
                  title = "IMG_1"
                  data =
                    Base64.decode(
                      "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                      Base64.DEFAULT,
                    )
                  contentType = "image/*"
                }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.photo_preview).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.photo_title).text).isEqualTo("IMG_1")
  }

  @Test
  fun shouldDisplayAudioFilePreviewFromAnswer() {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("audio/*"))
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
                      Base64.DEFAULT,
                    )
                  contentType = "audio/*"
                }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.file_preview).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.file_title).text)
      .isEqualTo("Audio File")
  }

  @Test
  fun shouldDisplayVideoFilePreviewFromAnswer() {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/mimeType"
            setValue(CodeType("video/*"))
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
                      Base64.DEFAULT,
                    )
                  contentType = "video/*"
                }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.file_preview).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.file_title).text)
      .isEqualTo("Video File")
  }

  @Test
  fun shouldDisplayDocumentFilePreviewFromAnswer() {
    val questionnaireItemView =
      QuestionnaireViewItem(
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
                      Base64.DEFAULT,
                    )
                  contentType = "application/pdf"
                }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.file_preview).visibility)
      .isEqualTo(View.VISIBLE)

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.file_title).text)
      .isEqualTo("Document File")
  }

  @Test
  fun doNotShowPreviewIfAnswerDoesNotHaveAttachment() {
    val questionnaireItemView =
      QuestionnaireViewItem(
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { addAnswer(null) },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.photo_preview).visibility)
      .isEqualTo(View.GONE)

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.file_preview).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun doNotShowPreviewOfPreviousAnswerAttachmentForCurrentAnswerItem() {
    val questionnaireItem =
      QuestionnaireViewItem(
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Attachment().apply {
                  title = "IMG_1.jpeg"
                  data =
                    Base64.decode(
                      "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                      Base64.DEFAULT,
                    )
                  contentType = "image/jpeg"
                }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItem) }

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.photo_preview).visibility)
      .isEqualTo(View.VISIBLE)

    val questionnaireItemWithNullAnswer =
      QuestionnaireViewItem(
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { addAnswer(null) },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemWithNullAnswer) }

    assertThat(viewHolder.itemView.findViewById<ConstraintLayout>(R.id.photo_preview).visibility)
      .isEqualTo(View.GONE)
  }

  /** Method to run code snippet on UI/main thread */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.scenario.onActivity { activity -> action() }
  }

  /** Method to set content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.scenario.onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }
}
