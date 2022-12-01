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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commitNow
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.test.R
import kotlinx.coroutines.launch

class TestQuestionnaireFragment : Fragment() {

  private val viewModel: TestQuestionnaireViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.test_fragment_layout, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (savedInstanceState == null) {
      addQuestionnaireFragment()
    }
  }

  private fun addQuestionnaireFragment() {
    viewLifecycleOwner.lifecycleScope.launch {
      if (childFragmentManager.findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) == null) {
        childFragmentManager.commitNow {
          setReorderingAllowed(true)
          add<QuestionnaireFragment>(
            R.id.container,
            tag = QUESTIONNAIRE_FRAGMENT_TAG,
            args =
              bundleOf(
                QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to
                  viewModel.getQuestionnaireJson(),
                QuestionnaireFragment.EXTRA_ENABLE_REVIEW_PAGE to true
              )
          )
        }
      }
    }
  }

  companion object {
    const val QUESTIONNAIRE_FRAGMENT_TAG = "questionnaire-fragment-tag"
    const val QUESTIONNAIRE_FILE_PATH_KEY = "questionnaire-file-path-key"
    const val QUESTIONNAIRE_FILE_WITH_VALIDATION_PATH_KEY =
      "questionnaire-file-with-validation-path-key"
  }
}
