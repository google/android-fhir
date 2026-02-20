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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.fhir.model.r4.Attachment
import com.google.fhir.model.r4.Base64Binary
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AttachmentViewFactoryTest {

  @Composable
  fun QuestionnaireAttachmentView(questionnaireViewItem: QuestionnaireViewItem) {
    AttachmentViewFactory.Content(questionnaireViewItem)
  }

  @Test
  fun shouldDisplayTakePhotoAndUploadPhotoButton() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "image/*")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "attachment-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireItemView) }

    onNodeWithTag(TAKE_PHOTO_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertTextEquals("Upload Photo")
  }

  @Test
  fun shouldDisplayUploadAudioButton() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "audio/*")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "attachment-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireItemView) }

    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertTextEquals("Upload Audio")
  }

  @Test
  fun shouldDisplayUploadVideoButton() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "video/*")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "attachment-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireItemView) }

    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertTextEquals("Upload Video")
  }

  @Test
  fun shouldDisplayUploadDocumentButton() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "application/pdf")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "attachment-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireItemView) }

    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertTextEquals("Upload Document")
  }

  @Test
  fun shouldDisplayTakePhotoAndUploadFileButton() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "image/*")),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "application/pdf")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "attachment-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireItemView) }

    onNodeWithTag(TAKE_PHOTO_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(UPLOAD_FILE_BUTTON_TAG).assertTextEquals("Upload File")
  }

  @Test
  fun shouldDisplayImagePreviewFromAnswer() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "image/*")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "attachment-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Attachment(
                    value =
                      Attachment(
                        title = FhirR4String(value = "IMG_1"),
                        contentType = Code(value = "image/*"),
                        data =
                          Base64Binary(
                            value = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireItemView) }

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    onNodeWithText("IMG_1").assertIsDisplayed()
  }

  @Test
  fun shouldDisplayAudioFilePreviewFromAnswer() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "audio/*")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "attachment-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Attachment(
                    value =
                      Attachment(
                        title = FhirR4String(value = "Audio File"),
                        contentType = Code(value = "audio/*"),
                        data =
                          Base64Binary(
                            value = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireItemView) }

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    onNodeWithText("Audio File").assertIsDisplayed()
  }

  @Test
  fun shouldDisplayVideoFilePreviewFromAnswer() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "video/*")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "attachment-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Attachment(
                    value =
                      Attachment(
                        title = FhirR4String(value = "Video File"),
                        contentType = Code(value = "video/*"),
                        data =
                          Base64Binary(
                            value = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireItemView) }

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    onNodeWithText("Video File").assertIsDisplayed()
  }

  @Test
  fun shouldDisplayDocumentFilePreviewFromAnswer() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "application/pdf")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "attachment-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Attachment(
                    value =
                      Attachment(
                        title = FhirR4String(value = "Document File"),
                        contentType = Code(value = "application/pdf"),
                        data =
                          Base64Binary(
                            value = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireItemView) }

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    onNodeWithText("Document File").assertIsDisplayed()
  }

  @Test
  fun doNotShowPreviewIfAnswerDoesNotHaveAttachment() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "image/*")),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "application/pdf")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "attachment-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireItemView) }

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertDoesNotExist()
  }

  @Test
  fun doNotShowPreviewOfPreviousAnswerAttachmentForCurrentAnswerItem() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "attachment-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
            extension =
              listOf(
                Extension(
                  url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                  value = Extension.Value.Code(value = Code(value = "image/*")),
                ),
                Extension(
                  url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                  value = Extension.Value.Code(value = Code(value = "application/pdf")),
                ),
              ),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "attachment-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value =
                    QuestionnaireResponse.Item.Answer.Value.Attachment(
                      value =
                        Attachment(
                          title = FhirR4String(value = "IMG_1.jpeg"),
                          contentType = Code(value = "image/jpeg"),
                          data =
                            Base64Binary(
                              value = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                            ),
                        ),
                    ),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireAttachmentView(questionnaireViewItem) }

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()

    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "image/*")),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "application/pdf")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "attachment-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertDoesNotExist()
  }

  @Test
  fun showPreviewReplacesPreviewOfPreviousAnswerAttachmentForCurrentAnswerItem() =
    runComposeUiTest {
      var questionnaireViewItem by
        mutableStateOf(
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "attachment-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
              extension =
                listOf(
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                    value = Extension.Value.Code(value = Code(value = "image/*")),
                  ),
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                    value = Extension.Value.Code(value = Code(value = "application/pdf")),
                  ),
                ),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "attachment-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value =
                      QuestionnaireResponse.Item.Answer.Value.Attachment(
                        value =
                          Attachment(
                            title = FhirR4String(value = "IMG_1.jpeg"),
                            contentType = Code(value = "image/jpeg"),
                            data =
                              Base64Binary(
                                value = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                              ),
                          ),
                      ),
                  ),
                ),
            ),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        )

      setContent { QuestionnaireAttachmentView(questionnaireViewItem) }

      onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
      onNodeWithText("IMG_1.jpeg").assertIsDisplayed()

      questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "attachment-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
            extension =
              listOf(
                Extension(
                  url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                  value = Extension.Value.Code(value = Code(value = "image/*")),
                ),
                Extension(
                  url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                  value = Extension.Value.Code(value = Code(value = "application/pdf")),
                ),
              ),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "attachment-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value =
                    QuestionnaireResponse.Item.Answer.Value.Attachment(
                      value =
                        Attachment(
                          title = FhirR4String(value = "Yellow Doc"),
                          contentType = Code(value = "application/pdf"),
                          data =
                            Base64Binary(
                              value = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                            ),
                        ),
                    ),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
      onNodeWithText("IMG_1.jpeg").assertDoesNotExist()
      onNodeWithText("Yellow Doc").assertIsDisplayed()
    }

  @Test
  fun deleteRemovesPreviewOfAnswerAttachment() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "attachment-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Attachment),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/mimeType",
                value = Extension.Value.Code(value = Code(value = "image/*")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "attachment-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Attachment(
                    value =
                      Attachment(
                        title = FhirR4String(value = "IMG_1.jpeg"),
                        contentType = Code(value = "image/jpeg"),
                        data =
                          Base64Binary(
                            value = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAttachmentView(questionnaireViewItem) }

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertIsDisplayed()
    onNodeWithText("IMG_1.jpeg").assertIsDisplayed()
    onNodeWithContentDescription("Delete").assertIsDisplayed().performClick()

    onNodeWithTag(ATTACHMENT_MEDIA_PREVIEW_TAG).assertDoesNotExist()
    onNodeWithContentDescription("Delete").assertDoesNotExist()
  }
}
