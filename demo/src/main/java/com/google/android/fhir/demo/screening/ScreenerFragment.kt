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

package com.google.android.fhir.demo.screening

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.demo.R
import com.google.android.fhir.demo.care.CareWorkflowExecutionViewModel
import com.google.android.material.snackbar.Snackbar
import org.hl7.fhir.r4.model.Task

/** A fragment class to show screener questionnaire screen. */
class ScreenerFragment : Fragment(R.layout.screener_encounter_fragment) {

  private val viewModel: ScreenerViewModel by viewModels()
  private val careWorkflowExecutionViewModel: CareWorkflowExecutionViewModel by activityViewModels()
  private val args: ScreenerFragmentArgs by navArgs()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpActionBar()
    setHasOptionsMenu(true)
    onBackPressed()
    setViewModelQuestionnaire()
    observeResourcesSaveAction()
    if (savedInstanceState == null) {
      addQuestionnaireFragment()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.screen_encounter_fragment_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_add_patient_submit -> {
        onSubmitAction()
        true
      }
      android.R.id.home -> {
        showCancelScreenerQuestionnaireAlertDialog()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun setUpActionBar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
    }
  }

  private fun setViewModelQuestionnaire() {
    viewModel.questionnaireString = args.questionnaireString
  }

  private fun addQuestionnaireFragment() {
    childFragmentManager.commit {
      add(
        R.id.add_patient_container,
        QuestionnaireFragment.builder().setQuestionnaire(viewModel.questionnaireString).build(),
        QUESTIONNAIRE_FRAGMENT_TAG
      )
    }
  }

  private fun onSubmitAction() {
    val questionnaireFragment =
      childFragmentManager.findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) as QuestionnaireFragment
    viewModel.saveScreenerEncounter(
      questionnaireFragment.getQuestionnaireResponse(),
      args.patientId
    )
  }

  private fun showCancelScreenerQuestionnaireAlertDialog() {
    val alertDialog: AlertDialog? =
      activity?.let {
        val builder = AlertDialog.Builder(it)
        builder.apply {
          setMessage(getString(R.string.cancel_questionnaire_message))
          setPositiveButton(getString(android.R.string.yes)) { _, _ ->
            NavHostFragment.findNavController(this@ScreenerFragment).navigateUp()
          }
          setNegativeButton(getString(android.R.string.no)) { _, _ -> }
        }
        builder.create()
      }
    alertDialog?.show()
  }

  private fun onBackPressed() {
    activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
      showCancelScreenerQuestionnaireAlertDialog()
    }
  }

  private fun observeResourcesSaveAction() {
    viewModel.isResourcesSaved.observe(viewLifecycleOwner) {
      if (!it) {
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
          R.string.resources_saved,
          Snackbar.LENGTH_SHORT
        )
        .show()
      careWorkflowExecutionViewModel.updateTaskStatus(
        args.taskLogicalId,
        Task.TaskStatus.COMPLETED,
        true
      )
      NavHostFragment.findNavController(this).navigateUp()
    }
  }

  companion object {
    const val EXTRA_QUESTIONNAIRE_JSON_STRING = "questionnaire-json-string"
    const val QUESTIONNAIRE_FRAGMENT_TAG = "questionnaire-fragment-tag"
    const val CURRENT_TASK_ID = "CURRENT_TASK_ID"
    const val MODIFIED_TASK_ID = "MODIFIED_TASK_ID"
  }
}
