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

package com.google.android.fhir.datacapture

import android.widget.TextView
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireValidationErrorMessageDialogFragmentTest {

  //
  //
  //  @Test fun `createCustomView` () {
  //    val fragmentFactory : FragmentFactory = mock()
  //    val viewModel =  QuestionnaireValidationErrorViewModel()
  //    val factory = object : ViewModelProvider.Factory {
  //      override fun <T : ViewModel?> create(modelClass: Class<T>): T {
  //        return viewModel as  T
  //      }
  //    }
  //
  //    `when`(fragmentFactory.instantiate(any(), any())).then {
  // QuestionnaireValidationErrorMessageDialogFragment{
  //      factory
  //    }}
  //
  //   val scenario =
  // launchFragmentInContainer<QuestionnaireValidationErrorMessageDialogFragment>(initialState =
  // Lifecycle.State.CREATED)
  //    val result =  scenario.withFragment {
  //      createCustomView()
  //    }
  //    assertThat(result.findViewById<TextView>(R.id.dialog_title).text).isEqualTo("Complete all
  // required fields")
  //    assertThat(result.findViewById<TextView>(R.id.dialog_subtitle).text).isEqualTo("Fields that
  // need to be completed:")
  //
  // assertThat(result.findViewById<RecyclerView>(R.id.recycler_view).adapter!!.itemCount).isEqualTo(0)
  //  }

  @Test
  fun `createCustomView with invalid`() {
    val questionnaire =
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "name-id"
            this.required = true
            text = "First Name"
            type = Questionnaire.QuestionnaireItemType.STRING
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        this.questionnaire = "questionnaire-1"
        addItem(QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("name-id")))
      }

    val fragmentFactory: FragmentFactory = mock()
    val viewModel = QuestionnaireValidationErrorViewModel()
    viewModel.setQuestionnaireAndValidation(
      questionnaire,
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        questionnaireResponse,
        RuntimeEnvironment.getApplication()
      )
    )
    val factory =
      object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          return viewModel as T
        }
      }

    `when`(fragmentFactory.instantiate(any(), any())).then {
      QuestionnaireValidationErrorMessageDialogFragment { factory }
    }

    val scenario =
      launchFragmentInContainer<QuestionnaireValidationErrorMessageDialogFragment>(
        initialState = Lifecycle.State.CREATED,
        factory = fragmentFactory
      )
    val result = scenario.withFragment { createCustomView() }
    assertThat(result.findViewById<TextView>(R.id.dialog_title).text)
      .isEqualTo("Complete all required fields")
    assertThat(result.findViewById<TextView>(R.id.dialog_subtitle).text)
      .isEqualTo("Fields that need to be completed:")
    assertThat(result.findViewById<RecyclerView>(R.id.recycler_view).adapter!!.itemCount)
      .isEqualTo(1)
  }

  @Test
  fun `createCustomView with no invalid`() {
    val questionnaire =
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "name-id"
            text = "First Name"
            type = Questionnaire.QuestionnaireItemType.STRING
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        this.questionnaire = "questionnaire-1"
        addItem(QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("name-id")))
      }

    val fragmentFactory: FragmentFactory = mock()
    val viewModel = QuestionnaireValidationErrorViewModel()
    viewModel.setQuestionnaireAndValidation(
      questionnaire,
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        questionnaireResponse,
        RuntimeEnvironment.getApplication()
      )
    )
    val factory =
      object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          return viewModel as T
        }
      }

    `when`(fragmentFactory.instantiate(any(), any())).then {
      QuestionnaireValidationErrorMessageDialogFragment { factory }
    }

    val scenario =
      launchFragmentInContainer<QuestionnaireValidationErrorMessageDialogFragment>(
        initialState = Lifecycle.State.CREATED,
        factory = fragmentFactory
      )
    val result = scenario.withFragment { createCustomView() }
    assertThat(result.findViewById<TextView>(R.id.dialog_title).text)
      .isEqualTo("Complete all required fields")
    assertThat(result.findViewById<TextView>(R.id.dialog_subtitle).text)
      .isEqualTo("Fields that need to be completed:")
    assertThat(result.findViewById<RecyclerView>(R.id.recycler_view).adapter!!.itemCount)
      .isEqualTo(0)
  }
}
