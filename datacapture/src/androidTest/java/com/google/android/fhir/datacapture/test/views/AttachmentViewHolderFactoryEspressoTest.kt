/*
 * Copyright 2023-2025 Google LLC
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
import android.widget.FrameLayout
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.ATTACHMENT_MEDIA_PREVIEW_TAG
import com.google.android.fhir.datacapture.views.factories.AttachmentViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.android.fhir.datacapture.views.factories.TAKE_PHOTO_BUTTON_TAG
import com.google.android.fhir.datacapture.views.factories.UPLOAD_FILE_BUTTON_TAG
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

  @get:Rule
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity ->
      viewHolder = AttachmentViewHolderFactory.create(FrameLayout(activity))
      activity.setContentView(viewHolder.itemView)
    }

    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
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

    viewHolder.bind(questionnaireItemView)

    val context = viewHolder.itemView.context
    composeTestRule.onNodeWithTag(TAKE_PHOTO_BUTTON_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    composeTestRule
      .onNodeWithTag(UPLOAD_FILE_BUTTON_TAG)
      .assertTextEquals(context.getString(R.string.upload_photo))
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

    viewHolder.bind(questionnaireItemView)

    composeTestRule.onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    val context = viewHolder.itemView.context
    composeTestRule
      .onNodeWithTag(UPLOAD_FILE_BUTTON_TAG)
      .assertTextEquals(context.getString(R.string.upload_audio))
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

    viewHolder.bind(questionnaireItemView)

    composeTestRule.onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    val context = viewHolder.itemView.context
    composeTestRule
      .onNodeWithTag(UPLOAD_FILE_BUTTON_TAG)
      .assertTextEquals(context.getString(R.string.upload_video))
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

    viewHolder.bind(questionnaireItemView)

    composeTestRule.onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    val context = viewHolder.itemView.context
    composeTestRule
      .onNodeWithTag(UPLOAD_FILE_BUTTON_TAG)
      .assertTextEquals(context.getString(R.string.upload_document))
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

    viewHolder.bind(questionnaireItemView)

    val context = viewHolder.itemView.context
    composeTestRule.onNodeWithTag(TAKE_PHOTO_BUTTON_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    composeTestRule
      .onNodeWithTag(UPLOAD_FILE_BUTTON_TAG)
      .assertTextEquals(context.getString(R.string.upload_file))
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

    viewHolder.bind(questionnaireItemView)

    composeTestRule.onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithText("IMG_1").assertIsDisplayed()
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

    viewHolder.bind(questionnaireItemView)

    composeTestRule.onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithText("Audio File").assertIsDisplayed()
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

    viewHolder.bind(questionnaireItemView)

    composeTestRule.onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithText("Video File").assertIsDisplayed()
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

    viewHolder.bind(questionnaireItemView)

    composeTestRule.onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithText("Document File").assertIsDisplayed()
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

    viewHolder.bind(questionnaireItemView)

    composeTestRule
      .onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG)
      .assertIsNotDisplayed()
      .assertDoesNotExist()
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

    viewHolder.bind(questionnaireItem)

    composeTestRule.onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()

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

    viewHolder.bind(questionnaireItemWithNullAnswer)

    composeTestRule
      .onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG)
      .assertIsNotDisplayed()
      .assertDoesNotExist()
  }

  @Test
  fun showPreviewReplacesPreviewOfPreviousAnswerAttachmentForCurrentAnswerItem() {
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

    viewHolder.bind(questionnaireItem)

    composeTestRule.onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithText("IMG_1.jpeg").assertIsDisplayed()

    val questionnaireItemWithDocumentAnswer =
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
                  title = "Yellow Doc"
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

    viewHolder.bind(questionnaireItemWithDocumentAnswer)

    composeTestRule.onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithText("IMG_1.jpeg").assertIsNotDisplayed().assertDoesNotExist()
    composeTestRule.onNodeWithText("Yellow Doc").assertIsDisplayed()
  }

  @Test
  fun deleteRemovesPreviewOfAnswerAttachment() {
    val questionnaireItem =
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

    viewHolder.bind(questionnaireItem)
    val deleteText = viewHolder.itemView.context.getString(R.string.delete)

    composeTestRule.onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithText("IMG_1.jpeg").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription(deleteText).assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsNotDisplayed()
    composeTestRule.onNodeWithContentDescription(deleteText).assertIsNotDisplayed()
  }
}
