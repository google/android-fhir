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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.demo.care.CareWorkflowExecutionStatus
import com.google.android.fhir.demo.care.CareWorkflowExecutionViewModel
import com.google.android.fhir.demo.databinding.FragmentListTasksBinding
import com.google.android.fhir.logicalId
import kotlinx.coroutines.launch
import timber.log.Timber

class ListTasksFragment(private val navigateToQuestionnaireCallback: (String, String) -> Unit) :
  Fragment() {
  private var _binding: FragmentListTasksBinding? = null
  private val binding
    get() = _binding!!
  private val viewModel by viewModels<ListScreeningsViewModel>()
  private val careWorkflowExecutionViewModel by activityViewModels<CareWorkflowExecutionViewModel>()
  private lateinit var args: Bundle

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    args = requireArguments()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentListTasksBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Timber.d("ListTasksFragment called: onViewCreated")
    val recyclerView = binding.rvTask
    val adapter = TaskItemRecyclerViewAdapter(::onTaskItemClicked)
    recyclerView.adapter = adapter
    getTasksForPatient()
    collectWorkflowExecution()

    viewModel.liveSearchedTasks.observe(viewLifecycleOwner) {
      Timber.d("Submitting ${it.count()} task records")
      adapter.submitList(it)
    }
  }

  private fun getTasksForPatient() {
    viewModel.getTasksForPatient(args.getString(PATIENT_ID_KEY)!!, args.getString(TASK_STATUS)!!)
  }

  private fun collectWorkflowExecution() {
    lifecycleScope.launch {
      careWorkflowExecutionViewModel.patientFlowForCareWorkflowExecution.collect {
        val status = it.careWorkflowExecutionStatus
        if (status is CareWorkflowExecutionStatus.Finished) {
          if (it.patient.logicalId == args.getString(PATIENT_ID_KEY)!!) {
            getTasksForPatient()
          }
        }
      }
    }
  }

  private fun onTaskItemClicked(taskItem: ListScreeningsViewModel.TaskItem) {
    navigateToQuestionnaireCallback(
      taskItem.resourceId,
      viewModel.fetchQuestionnaireString(taskItem)
    )
  }

  companion object {
    const val PATIENT_ID_KEY = "patient_id"
    const val TASK_STATUS = "tab_position"
  }
}
