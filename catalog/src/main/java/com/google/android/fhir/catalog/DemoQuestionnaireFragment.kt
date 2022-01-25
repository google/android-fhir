/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.catalog

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.android.fhir.datacapture.QuestionnaireFragment
import kotlinx.coroutines.launch

class DemoQuestionnaireFragment : Fragment(R.layout.fragment_demo_questionnaire) {
  private val viewModel: DemoQuestionnaireViewModel by viewModels()
  private val args: DemoQuestionnaireFragmentArgs by navArgs()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    updateArguments()
    if (savedInstanceState == null) {
      addQuestionnaireFragment()
    }
  }

  override fun onResume() {
    super.onResume()
    (requireActivity() as MainActivity).showBottomNavigationView(View.GONE)
    setUpActionBar()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun setUpActionBar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
    }
    (requireActivity() as MainActivity).setActionBar(args.questionnaireTitleKey, Gravity.TOP)
    setHasOptionsMenu(true)
  }

  private fun updateArguments() {
    requireArguments()
      .putString(
        QuestionnaireContainerFragment.QUESTIONNAIRE_FILE_PATH_KEY,
        args.questionnaireFilePathKey
      )
  }

  private fun addQuestionnaireFragment() {
    val fragment = QuestionnaireFragment()
    viewLifecycleOwner.lifecycleScope.launch {
      fragment.arguments =
        bundleOf(
          QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to viewModel.getQuestionnaireJson()
        )
      childFragmentManager.commit { add(R.id.container, fragment, QUESTIONNAIRE_FRAGMENT_TAG) }
    }
  }

  companion object {
    const val QUESTIONNAIRE_FRAGMENT_TAG = "questionnaire-fragment-tag"
  }
}
