/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.datacapture.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.QuestionnaireViewModel
import com.google.android.fhir.datacapture.gallery.databinding.FragmentQuestionnaireContainerBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.QuestionnaireResponse

class QuestionnaireContainerFragment : Fragment() {
  private val viewModel: QuestionnaireViewModel by activityViewModels()
  private var _binding: FragmentQuestionnaireContainerBinding? = null
  private val binding
    get() = _binding!!
  private val args: QuestionnaireContainerFragmentArgs by navArgs()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    super.onCreate(savedInstanceState)
    _binding = FragmentQuestionnaireContainerBinding.inflate(inflater, container, false)
    requireArguments().putString(QUESTIONNAIRE_FILE_PATH_KEY, args.questionnaireFilePathKey)
    requireArguments()
      .putString(QUESTIONNAIRE_RESPONSE_FILE_PATH_KEY, args.questionnaireResponseFilePathKey)

    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = args.questionnaireTitleKey
      setDisplayHomeAsUpEnabled(true)
    }
    // Only add the fragment once, when this fragment is first created.
    if (savedInstanceState == null) {
      lifecycleScope.launch {
        viewModel.setQ(getQuestionnaire()!!, getQuestionnaireResponse())
        childFragmentManager.commit {
          add(R.id.container, CustomQuestionnaireFragment(), QUESTIONNAIRE_FRAGMENT_TAG)
        }
      }
    }
    return binding.root
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.top_bar_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_submit -> {
        val questionnaireFragment =
          childFragmentManager.findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) as
            QuestionnaireFragment
        displayQuestionnaireResponse(questionnaireFragment.getQuestionnaireResponse())
        true
      }
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  // Display Questionnaire response as a dialog
  private fun displayQuestionnaireResponse(questionnaireResponse: QuestionnaireResponse) {
    val questionnaireResponseJson =
      FhirContext.forR4().newJsonParser().encodeResourceToString(questionnaireResponse)
    val dialogFragment = QuestionnaireResponseDialogFragment()
    dialogFragment.arguments =
      bundleOf(QuestionnaireResponseDialogFragment.BUNDLE_KEY_CONTENTS to questionnaireResponseJson)
    dialogFragment.show(childFragmentManager, QuestionnaireResponseDialogFragment.TAG)
  }

  companion object {
    const val QUESTIONNAIRE_FILE_PATH_KEY = "questionnaire-file-path-key"
    const val QUESTIONNAIRE_FRAGMENT_TAG = "questionnaire-fragment-tag"
    const val QUESTIONNAIRE_RESPONSE_FILE_PATH_KEY = "questionnaire-response-file-path-key"
  }

  override fun onDestroyView() {
    super.onDestroyView()
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(false)
      title = getString(R.string.app_name)
    }
    _binding = null
  }

  /////

  private var questionnaireJson: String? = null
  private suspend fun getQuestionnaire() =
    withContext(lifecycleScope.coroutineContext) {
      if (questionnaireJson == null) {
        questionnaireJson =
          readFileFromAssets(requireArguments().getString(QUESTIONNAIRE_FILE_PATH_KEY)!!)
      }
      questionnaireJson
    }

  private var questionnaireResponseJson: String? = null
  private suspend fun getQuestionnaireResponse() =
    withContext(lifecycleScope.coroutineContext) {
      if (questionnaireResponseJson == null) {
        requireArguments().getString(QUESTIONNAIRE_RESPONSE_FILE_PATH_KEY)?.let {
          questionnaireResponseJson = readFileFromAssets(it)
        }
      }
      questionnaireResponseJson
    }

  private fun readFileFromAssets(filename: String): String {
    return requireContext().applicationContext!!.assets.open(filename).bufferedReader()?.use {
      it.readText()
    }
  }
}
