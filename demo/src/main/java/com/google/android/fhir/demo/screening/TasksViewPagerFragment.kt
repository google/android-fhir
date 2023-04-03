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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.fhir.demo.R
import com.google.android.fhir.demo.care.CareWorkflowExecutionStatus
import com.google.android.fhir.demo.care.CareWorkflowExecutionViewModel
import com.google.android.fhir.demo.databinding.FragmentTasksViewPagerBinding
import com.google.android.fhir.logicalId
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import timber.log.Timber

class TasksViewPagerFragment : Fragment() {
  private val taskViewPagerViewModel: TaskViewPagerViewModel by viewModels()
  private val careWorkflowExecutionViewModel by activityViewModels<CareWorkflowExecutionViewModel>()

  private val args: TasksViewPagerFragmentArgs by navArgs()
  private var _binding: FragmentTasksViewPagerBinding? = null
  private val binding
    get() = _binding!!
  private lateinit var careWorkflowExecutionStatusLayout: LinearLayout
  private lateinit var careWorkflowExecutionStatus: TextView
  private lateinit var careWorkflowExecutionImage: ImageView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    // Inflate the layout for this fragment
    _binding = FragmentTasksViewPagerBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    Timber.d("TasksViewPagerFragment called: onViewCreated")
    val fragmentList =
      listOf(0, 1).map {
        ListTasksFragment(this::navigateToQuestionnaireCallback).apply {
          arguments =
            bundleOf(
              ListTasksFragment.PATIENT_ID_KEY to args.patientId,
              ListTasksFragment.TASK_STATUS to taskViewPagerViewModel.getTaskStatus(it)
            )
        }
      }
    careWorkflowExecutionStatusLayout =
      binding.workflowExecutionStatusContainer.careWorkflowExecutionStatusLayout
    careWorkflowExecutionStatus =
      binding.workflowExecutionStatusContainer.careWorkflowExecutionStatus
    careWorkflowExecutionImage =
      binding.workflowExecutionStatusContainer.careWorkflowExecutionStatusImage
    binding.viewPager.adapter =
      object : FragmentStateAdapter(this) {
        override fun getItemCount() = fragmentList.size
        override fun createFragment(position: Int) = fragmentList[position]
      }
    binding.viewPager.offscreenPageLimit
    taskViewPagerViewModel.patientName.observe(viewLifecycleOwner) {
      (requireActivity() as AppCompatActivity).supportActionBar?.apply {
        title = it
        setDisplayHomeAsUpEnabled(true)
      }
    }
    val tasksTabHeadings = listOf("Tasks", "Completed Tasks")
    TabLayoutMediator(binding.tasksTabLayout, binding.viewPager) { tab, position ->
        tab.text = tasksTabHeadings[position]
      }
      .attach()
    taskViewPagerViewModel.livePendingTasksCount.observe(viewLifecycleOwner) {
      binding.tasksTabLayout.getTabAt(0)?.text = "Tasks ($it)"
    }
    taskViewPagerViewModel.liveCompletedTasksCount.observe(viewLifecycleOwner) {
      binding.tasksTabLayout.getTabAt(1)?.text = "Completed Tasks ($it)"
    }
    taskViewPagerViewModel.getTasksCount(args.patientId)
    taskViewPagerViewModel.getPatientName(args.patientId)

    collectWorkflowExecution()
  }

  private fun collectWorkflowExecution() {
    lifecycleScope.launch {
      careWorkflowExecutionViewModel.patientFlowForCareWorkflowExecution.collect {
        val status = it.careWorkflowExecutionStatus
        if (it.patient.logicalId == args.patientId) {
          updateWorkflowExecutionBar(status)
          taskViewPagerViewModel.getTasksCount(args.patientId)
          taskViewPagerViewModel.getPatientName(args.patientId)
        }
      }
    }
  }

  private fun updateWorkflowExecutionBar(status: CareWorkflowExecutionStatus) {
    when (status) {
      is CareWorkflowExecutionStatus.Started -> {
        careWorkflowExecutionStatusLayout.setBackgroundColor(
          resources.getColor(R.color.workflow_running)
        )
        careWorkflowExecutionStatus.text = resources.getString(R.string.updating_tasks)
        careWorkflowExecutionImage.setImageResource(R.drawable.ic_baseline_sync_24)
      }
      is CareWorkflowExecutionStatus.InProgress -> {}
      is CareWorkflowExecutionStatus.Finished -> {
        careWorkflowExecutionStatusLayout.setBackgroundColor(
          resources.getColor(R.color.workflow_finished)
        )
        careWorkflowExecutionStatus.text = resources.getString(R.string.tasks_updated)
        careWorkflowExecutionImage.setImageResource(R.drawable.ic_check)
      }
      is CareWorkflowExecutionStatus.Failed -> {}
    }
  }

  private fun navigateToQuestionnaireCallback(taskLogicalId: String, questionnaireString: String) {
    findNavController()
      .navigate(
        TasksViewPagerFragmentDirections.actionPatientDetailsToScreenEncounterFragment(
          args.patientId,
          taskLogicalId,
          questionnaireString
        )
      )
  }
}
