/*
 * Copyright 2022-2023 Google LLC
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

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_ENABLE_REVIEW_PAGE
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_URI
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_SHOW_REVIEW_PAGE_FIRST
import com.google.android.fhir.datacapture.testing.DataCaptureTestApplication
import com.google.common.truth.Truth.assertThat
import java.io.File
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

/**
 * Parameterized test for passing the questionnaire and the questionnaire response to the view
 * model.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = DataCaptureTestApplication::class)
class QuestionnaireViewModelParameterizedTest(
  private val questionnaireSource: QuestionnaireSource,
  private val questionnaireResponseSource: QuestionnaireResponseSource,
) {

  private lateinit var state: SavedStateHandle
  private val context = ApplicationProvider.getApplicationContext<Application>()

  @Before
  fun setUp() {
    state = SavedStateHandle()
    check(
      ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
        is DataCaptureConfig.Provider,
    ) {
      "Few tests require a custom application class that implements DataCaptureConfig.Provider"
    }
    ReflectionHelpers.setStaticField(DataCapture::class.java, "configuration", null)
  }

  @Test
  fun `should copy questionnaire if no response is provided`() {
    val questionnaire =
      Questionnaire().apply {
        url = "http://www.sample-org/FHIR/Resources/Questionnaire/a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Yes or no?"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        this.questionnaire = "http://www.sample-org/FHIR/Resources/Questionnaire/a-questionnaire"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Yes or no?"
          },
        )
      },
    )
  }

  @Test
  fun `should copy questionnaire response if provided`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              },
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  private fun createQuestionnaireViewModel(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse? = null,
    enableReviewPage: Boolean = false,
    showReviewPageFirst: Boolean = false,
  ): QuestionnaireViewModel {
    if (questionnaireSource == QuestionnaireSource.STRING) {
      state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))
    } else if (questionnaireSource == QuestionnaireSource.URI) {
      val questionnaireFile = File(context.cacheDir, "test_questionnaire")
      questionnaireFile.outputStream().bufferedWriter().use {
        printer.encodeResourceToWriter(questionnaire, it)
      }
      val questionnaireUri = Uri.fromFile(questionnaireFile)
      state.set(EXTRA_QUESTIONNAIRE_JSON_URI, questionnaireUri)
      shadowOf(context.contentResolver)
        .registerInputStream(questionnaireUri, questionnaireFile.inputStream())
    }

    questionnaireResponse?.let {
      if (questionnaireResponseSource == QuestionnaireResponseSource.STRING) {
        state.set(
          EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING,
          printer.encodeResourceToString(questionnaireResponse),
        )
      } else if (questionnaireResponseSource == QuestionnaireResponseSource.URI) {
        val questionnaireResponseFile = File(context.cacheDir, "test_questionnaire_response")
        questionnaireResponseFile.outputStream().bufferedWriter().use {
          printer.encodeResourceToWriter(questionnaireResponse, it)
        }
        val questionnaireResponseUri = Uri.fromFile(questionnaireResponseFile)
        state.set(EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI, questionnaireResponseUri)
        shadowOf(context.contentResolver)
          .registerInputStream(questionnaireResponseUri, questionnaireResponseFile.inputStream())
      }
    }
    enableReviewPage.let { state.set(EXTRA_ENABLE_REVIEW_PAGE, it) }
    showReviewPageFirst.let { state.set(EXTRA_SHOW_REVIEW_PAGE_FIRST, it) }
    return QuestionnaireViewModel(context, state)
  }

  private companion object {
    val printer: IParser = FhirContext.forR4().newJsonParser()

    fun <T : IBaseResource> assertResourceEquals(actual: T, expected: T) {
      assertThat(printer.encodeResourceToString(actual))
        .isEqualTo(printer.encodeResourceToString(expected))
    }

    @JvmStatic
    @Parameters
    fun parameters() =
      listOf(
        arrayOf(QuestionnaireSource.URI, QuestionnaireResponseSource.URI),
        arrayOf(QuestionnaireSource.URI, QuestionnaireResponseSource.STRING),
        arrayOf(QuestionnaireSource.STRING, QuestionnaireResponseSource.URI),
        arrayOf(QuestionnaireSource.STRING, QuestionnaireResponseSource.STRING),
      )
  }
}

/** The source of questionnaire. */
enum class QuestionnaireSource {
  STRING,
  URI,
}

/** The source of questionnaire-response. */
enum class QuestionnaireResponseSource {
  STRING,
  URI,
}
