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

package com.google.android.fhir.reference

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.fhir.datacapture.QuestionnaireFragment

/** A fragment representing Edit Patient screen. This fragment is contained in a [MainActivity]. */
class EditPatientFragment : Fragment(R.layout.add_patient_fragment) {
  private val viewModel: EditPatientViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = requireContext().getString(R.string.edit_patient)
    }
    requireArguments()
      .putString(QUESTIONNAIRE_FILE_PATH_KEY, "new-patient-registration-paginated.json")

    viewModel.livePatientData.observe(viewLifecycleOwner) { addQuestionnaireFragment(it) }
    viewModel.isPatientSaved.observe(viewLifecycleOwner) {
      if (!it) {
        Toast.makeText(requireContext(), R.string.message_input_missing, Toast.LENGTH_SHORT).show()
        return@observe
      }
      Toast.makeText(requireContext(), R.string.message_patient_updated, Toast.LENGTH_SHORT).show()
      NavHostFragment.findNavController(this).navigateUp()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.add_patient_fragment_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      R.id.action_add_patient_submit -> {
        onSubmitAction()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun addQuestionnaireFragment(pair: Pair<String, String>) {
    val fragment = QuestionnaireFragment()
    fragment.arguments =
      bundleOf(
        QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE to pair.first,
        QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE to pair.second
      )
    childFragmentManager.commit {
      add(R.id.add_patient_container, fragment, QUESTIONNAIRE_FRAGMENT_TAG)
    }
  }

  private fun onSubmitAction() {
    val questionnaireFragment =
      childFragmentManager.findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) as QuestionnaireFragment
    viewModel.updatePatient(questionnaireFragment.getQuestionnaireResponse())
  }

  companion object {
    const val QUESTIONNAIRE_FILE_PATH_KEY = "edit-questionnaire-file-path-key"
    const val QUESTIONNAIRE_FRAGMENT_TAG = "edit-questionnaire-fragment-tag"
  }
}
