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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.fhir.demo.databinding.FragmentTasksViewPagerBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.hl7.fhir.r4.model.Task
import timber.log.Timber

class TasksViewPagerFragment : Fragment() {
  private val args: TasksViewPagerFragmentArgs by navArgs()
  private var _binding: FragmentTasksViewPagerBinding? = null
  private val binding
    get() = _binding!!

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
              ListTasksFragment.TASK_STATUS to getTaskStatus(it)
            )
        }
      }
    binding.viewPager.adapter =
      object : FragmentStateAdapter(this) {
        override fun getItemCount() = fragmentList.size
        override fun createFragment(position: Int) = fragmentList[position]
      }
    binding.viewPager.offscreenPageLimit
    TabLayoutMediator(binding.tasksTabLayout, binding.viewPager) { tab, position ->
        tab.text = getTaskStatus(position).uppercase()
      }
      .attach()
  }

  /**
   * Currently only [Task.TaskStatus.COMPLETED] & [Task.TaskStatus.READY] are shown. This logic
   * could be extended.
   */
  private fun getTaskStatus(position: Int): String {
    return when (position) {
      1 -> Task.TaskStatus.COMPLETED
      else -> Task.TaskStatus.READY
    }.toCode()
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
