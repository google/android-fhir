/*
 * Copyright 2022-2023 Google LLC
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
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class BehaviorListFragment : Fragment(R.layout.behavior_list_fragment) {
  private val viewModel: BehaviorListViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpBehaviorsRecyclerView()
    (activity as? MainActivity)?.showOpenQuestionnaireMenu(true)
  }

  override fun onResume() {
    super.onResume()
    setUpActionBar()
    (requireActivity() as MainActivity).showBottomNavigationView(View.VISIBLE)
  }

  private fun setUpActionBar() {
    (activity as MainActivity).setActionBar(
      getString(R.string.toolbar_text),
      Gravity.CENTER_HORIZONTAL,
    )
    setHasOptionsMenu(true)
  }

  private fun setUpBehaviorsRecyclerView() {
    val behaviorRecyclerView =
      requireView().findViewById<RecyclerView>(R.id.sdcBehaviorRecyclerView)
    val adapter =
      BehaviorsRecyclerViewAdapter(::onItemClick).apply { submitList(viewModel.getBehaviorList()) }
    behaviorRecyclerView.adapter = adapter
    behaviorRecyclerView.layoutManager = GridLayoutManager(context, 2)
  }

  private fun onItemClick(behavior: BehaviorListViewModel.Behavior) {
    if (behavior.questionnaireFileName.isEmpty()) {
      return
    }
    launchQuestionnaireFragment(behavior)
  }

  private fun launchQuestionnaireFragment(behavior: BehaviorListViewModel.Behavior) {
    viewLifecycleOwner.lifecycleScope.launch {
      findNavController()
        .navigate(
          MainNavGraphDirections.actionGlobalGalleryQuestionnaireFragment(
            questionnaireTitleKey = context?.getString(behavior.textId) ?: "",
            questionnaireJsonStringKey =
              getQuestionnaireJsonStringFromAssets(
                context = requireContext(),
                backgroundContext = coroutineContext,
                fileName = behavior.questionnaireFileName,
              ),
            workflow = behavior.workFlow,
          ),
        )
    }
  }
}
