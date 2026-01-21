/*
 * Copyright 2023-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.factories

import android.util.Base64
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import kotlin.test.Test
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class AttachmentViewFactoryTest {

  @Test
  fun shouldDisplayTakePhotoAndUploadPhotoButton() = runComposeUiTest {
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
    onNodeWithTag(TAKE_PHOTO_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertTextEquals(context.getString(R.string.upload_photo))
  }

  @Test
  fun shouldDisplayUploadAudioButton() = runComposeUiTest {
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

    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    val context = viewHolder.itemView.context
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertTextEquals(context.getString(R.string.upload_audio))
  }

  @Test
  fun shouldDisplayUploadVideoButton() = runComposeUiTest {
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

    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    val context = viewHolder.itemView.context
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertTextEquals(context.getString(R.string.upload_video))
  }

  @Test
  fun shouldDisplayUploadDocumentButton() = runComposeUiTest {
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

    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    val context = viewHolder.itemView.context
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG)
      .assertTextEquals(context.getString(R.string.upload_document))
  }

  @Test
  fun shouldDisplayTakePhotoAndUploadFileButton() = runComposeUiTest {
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
    onNodeWithTag(TAKE_PHOTO_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertTextEquals(context.getString(R.string.upload_file))
  }

  @Test
  fun shouldDisplayImagePreviewFromAnswer() = runComposeUiTest {
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

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    onNodeWithText("IMG_1").assertIsDisplayed()
  }

  @Test
  fun shouldDisplayAudioFilePreviewFromAnswer() = runComposeUiTest {
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

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    onNodeWithText("Audio File").assertIsDisplayed()
  }

  @Test
  fun shouldDisplayVideoFilePreviewFromAnswer() = runComposeUiTest {
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

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    onNodeWithText("Video File").assertIsDisplayed()
  }

  @Test
  fun shouldDisplayDocumentFilePreviewFromAnswer() = runComposeUiTest {
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

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    onNodeWithText("Document File").assertIsDisplayed()
  }

  @Test
  fun doNotShowPreviewIfAnswerDoesNotHaveAttachment() = runComposeUiTest {
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

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsNotDisplayed().assertDoesNotExist()
  }

  @Test
  fun doNotShowPreviewOfPreviousAnswerAttachmentForCurrentAnswerItem() = runComposeUiTest {
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

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()

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

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsNotDisplayed().assertDoesNotExist()
  }

  @Test
  fun showPreviewReplacesPreviewOfPreviousAnswerAttachmentForCurrentAnswerItem() =
    runComposeUiTest {
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

      onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
      onNodeWithText("IMG_1.jpeg").assertIsDisplayed()

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

      onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
      onNodeWithText("IMG_1.jpeg").assertIsNotDisplayed().assertDoesNotExist()
      onNodeWithText("Yellow Doc").assertIsDisplayed()
    }

  @Test
  fun deleteRemovesPreviewOfAnswerAttachment() = runComposeUiTest {
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

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    onNodeWithText("IMG_1.jpeg").assertIsDisplayed()
    onNodeWithContentDescription(deleteText).assertIsDisplayed().performClick()

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsNotDisplayed()
    onNodeWithContentDescription(deleteText).assertIsNotDisplayed()
  }
}
