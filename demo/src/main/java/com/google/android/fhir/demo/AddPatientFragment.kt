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

package com.google.android.fhir.demo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.demo.care.CareWorkflowExecutionViewModel
import com.google.android.material.snackbar.Snackbar
import org.hl7.fhir.r4.model.QuestionnaireResponse

/** A fragment class to show patient registration screen. */
class AddPatientFragment : Fragment(R.layout.add_patient_fragment) {

  private val viewModel: AddPatientViewModel by viewModels()
  private val careWorkflowExecutionViewModel: CareWorkflowExecutionViewModel by activityViewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpActionBar()
    setHasOptionsMenu(true)
    updateArguments()
    if (savedInstanceState == null) {
      addQuestionnaireFragment()
    }
    observeSavedPatient()
    (activity as MainActivity).setDrawerEnabled(false)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.add_patient_fragment_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_add_patient_submit -> {
        onSubmitAction()
        true
      }
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun setUpActionBar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = requireContext().getString(R.string.add_patient)
      setDisplayHomeAsUpEnabled(true)
    }
  }

  private fun updateArguments() {
    requireArguments()
      .putString(QUESTIONNAIRE_FILE_PATH_KEY, "new-patient-registration-paginated.json")
  }

  private fun addQuestionnaireFragment() {
    childFragmentManager.commit {
      add(
        R.id.add_patient_container,
        QuestionnaireFragment.builder().setQuestionnaire(viewModel.questionnaire).build(),
        QUESTIONNAIRE_FRAGMENT_TAG
      )
    }
  }

  private fun onSubmitAction() {
    val questionnaireFragment =
      childFragmentManager.findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) as QuestionnaireFragment
    savePatient(questionnaireFragment.getQuestionnaireResponse())
  }

  private fun savePatient(questionnaireResponse: QuestionnaireResponse) {
    viewModel.savePatient(questionnaireResponse)
  }

  private fun observeSavedPatient() {
    viewModel.savedPatient.observe(viewLifecycleOwner) {
      if (it == null) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.inputs_missing,
            Snackbar.LENGTH_SHORT
          )
          .show()
        return@observe
      }
      Snackbar.make(
          requireActivity().findViewById(android.R.id.content),
          "Patient is saved. Updating tasks.",
          Snackbar.LENGTH_SHORT
        )
        .show()
      // workflow execution in mainActivityViewModel is necessary
      careWorkflowExecutionViewModel.executeCareWorkflowForPatient(it)
      NavHostFragment.findNavController(this)
        .previousBackStackEntry
        ?.savedStateHandle?.set(NEW_PATIENT_RESULT_KEY, it.name[0].nameAsSingleString)
      NavHostFragment.findNavController(this).navigateUp()
    }
  }

  companion object {
    const val QUESTIONNAIRE_FILE_PATH_KEY = "questionnaire-file-path-key"
    const val QUESTIONNAIRE_FRAGMENT_TAG = "questionnaire-fragment-tag"
    const val NEW_PATIENT_RESULT_KEY = "newPatientName"
  }
}
