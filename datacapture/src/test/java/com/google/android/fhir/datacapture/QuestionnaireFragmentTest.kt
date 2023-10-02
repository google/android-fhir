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

import android.os.Build
import android.view.View
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_ENABLE_REVIEW_PAGE
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_SHOW_REVIEW_PAGE_FIRST
import com.google.android.fhir.datacapture.testing.DataCaptureTestApplication
import com.google.android.fhir.datacapture.views.factories.DateTimePickerViewHolderFactory
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Questionnaire
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = DataCaptureTestApplication::class)
class QuestionnaireFragmentTest {

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  @Before
  fun setup() {
    check(
      ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
        is DataCaptureConfig.Provider,
    ) {
      "Few tests require a custom application class that implements DataCaptureConfig.Provider"
    }
    ReflectionHelpers.setStaticField(DataCapture::class.java, "configuration", null)
  }

  @Test
  fun `fragment should have valid questionnaire response`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val questionnaireJson = parser.encodeResourceToString(questionnaire)
    val scenario =
      launchFragmentInContainer<QuestionnaireFragment>(
        bundleOf(EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJson),
      )
    scenario.moveToState(Lifecycle.State.RESUMED)
    scenario.withFragment {
      assertThat(this.getQuestionnaireResponse()).isNotNull()
      assertThat(this.getQuestionnaireResponse().item.any { it.linkId == "a-link-id" }).isTrue()
    }
  }

  @Test
  fun `calculateProgressPercentage should return zero for totalCount value zero`() {
    val questionnaire = Questionnaire().apply { id = "a-questionnaire" }
    val questionnaireJson = parser.encodeResourceToString(questionnaire)
    val scenario =
      launchFragmentInContainer<QuestionnaireFragment>(
        bundleOf(EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJson),
      )
    scenario.moveToState(Lifecycle.State.RESUMED)
    scenario.withFragment {
      assertThat(calculateProgressPercentage(count = 10, totalCount = 0)).isEqualTo(0)
    }
  }

  @Test
  fun `calculateProgressPercentage should return valid percentage result`() {
    val questionnaire = Questionnaire().apply { id = "a-questionnaire" }
    val questionnaireJson = parser.encodeResourceToString(questionnaire)
    val scenario =
      launchFragmentInContainer<QuestionnaireFragment>(
        bundleOf(EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJson),
      )
    scenario.moveToState(Lifecycle.State.RESUMED)
    scenario.withFragment {
      assertThat(calculateProgressPercentage(count = 10, totalCount = 50)).isEqualTo(20)
    }
  }

  @Test
  fun `review page should show both edit and submit button`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val questionnaireJson = parser.encodeResourceToString(questionnaire)
    val scenario =
      launchFragmentInContainer<QuestionnaireFragment>(
        bundleOf(
          EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJson,
          EXTRA_ENABLE_REVIEW_PAGE to true,
          EXTRA_SHOW_REVIEW_PAGE_FIRST to true,
        ),
      )
    scenario.moveToState(Lifecycle.State.RESUMED)
    val view = scenario.withFragment { requireView() }

    assertThat(view.findViewById<Button>(R.id.review_mode_edit_button).visibility)
      .isEqualTo(View.VISIBLE)
    assertThat(view.findViewById<Button>(R.id.submit_questionnaire).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun `questionnaireItemViewHolderFactoryMatchersProvider should have custom QuestionnaireItemViewHolderFactoryMatchers `() {
    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .dataCaptureConfiguration =
      DataCaptureConfig(
        questionnaireItemViewHolderFactoryMatchersProviderFactory =
          QuestionnaireItemViewHolderFactoryMatchersProviderFactoryTestImpl,
      )
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val questionnaireJson = parser.encodeResourceToString(questionnaire)
    val scenario =
      launchFragmentInContainer<QuestionnaireFragment>(
        QuestionnaireFragment.builder()
          .setQuestionnaire(questionnaireJson)
          .setCustomQuestionnaireItemViewHolderFactoryMatchersProvider("Provider")
          .buildArgs(),
      )
    scenario.moveToState(Lifecycle.State.RESUMED)
    scenario.withFragment {
      assertThat(this.questionnaireItemViewHolderFactoryMatchersProvider.get().size).isEqualTo(1)
    }
  }

  @Test
  fun `questionnaireItemViewHolderFactoryMatchersProvider should have no custom QuestionnaireItemViewHolderFactoryMatchers if provider is not set`() {
    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .dataCaptureConfiguration =
      DataCaptureConfig(
        questionnaireItemViewHolderFactoryMatchersProviderFactory =
          QuestionnaireItemViewHolderFactoryMatchersProviderFactoryTestImpl,
      )

    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val questionnaireJson = parser.encodeResourceToString(questionnaire)
    val scenario =
      launchFragmentInContainer<QuestionnaireFragment>(
        QuestionnaireFragment.builder().setQuestionnaire(questionnaireJson).buildArgs(),
      )
    scenario.moveToState(Lifecycle.State.RESUMED)
    scenario.withFragment {
      assertThat(this.questionnaireItemViewHolderFactoryMatchersProvider.get()).isEmpty()
    }
  }

  @Test
  fun `questionnaireItemViewHolderFactoryMatchersProvider should have no custom QuestionnaireItemViewHolderFactoryMatchers if QuestionnaireItemViewHolderFactoryMatchersProviderFactory is not provided`() {
    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .dataCaptureConfiguration =
      DataCaptureConfig(questionnaireItemViewHolderFactoryMatchersProviderFactory = null)

    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val questionnaireJson = parser.encodeResourceToString(questionnaire)
    val scenario =
      launchFragmentInContainer<QuestionnaireFragment>(
        QuestionnaireFragment.builder()
          .setQuestionnaire(questionnaireJson)
          .setCustomQuestionnaireItemViewHolderFactoryMatchersProvider("Provider")
          .buildArgs(),
      )
    scenario.moveToState(Lifecycle.State.RESUMED)
    scenario.withFragment {
      assertThat(this.questionnaireItemViewHolderFactoryMatchersProvider.get()).isEmpty()
    }
  }

  object QuestionnaireItemViewHolderFactoryMatchersProviderFactoryTestImpl :
    QuestionnaireItemViewHolderFactoryMatchersProviderFactory {
    override fun get(
      provider: String,
    ): QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider {
      return QuestionnaireItemViewHolderFactoryMatchersProviderTestImpl
    }
  }

  object QuestionnaireItemViewHolderFactoryMatchersProviderTestImpl :
    QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider() {
    override fun get(): List<QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher> {
      return listOf(
        QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher(
          factory = DateTimePickerViewHolderFactory,
          matches = { true },
        ),
      )
    }
  }
}
