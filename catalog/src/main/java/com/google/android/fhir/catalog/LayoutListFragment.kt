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

/** Fragment for the layout list. */
class LayoutListFragment : Fragment(R.layout.layout_list_fragment) {
  private val viewModel: LayoutListViewModel by viewModels()

  override fun onResume() {
    super.onResume()
    setUpActionBar()
    (requireActivity() as MainActivity).showBottomNavigationView(View.VISIBLE)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpLayoutsRecyclerView()
    (activity as? MainActivity)?.showOpenQuestionnaireMenu(true)
  }

  private fun setUpLayoutsRecyclerView() {
    val adapter =
      LayoutsRecyclerViewAdapter(::onItemClick).apply { submitList(viewModel.getLayoutList()) }
    val recyclerView = requireView().findViewById<RecyclerView>(R.id.sdcLayoutsRecyclerView)
    recyclerView.adapter = adapter
    recyclerView.layoutManager = GridLayoutManager(context, 2)
  }

  private fun setUpActionBar() {
    (requireActivity() as MainActivity).setNavigationUp(false)
    (activity as MainActivity).setActionBar(
      getString(R.string.toolbar_text),
      Gravity.CENTER_HORIZONTAL,
    )
    setHasOptionsMenu(true)
  }

  private fun onItemClick(layout: LayoutListViewModel.Layout) {
    // TODO Remove check when all layout questionnaire json are updated.
    // https://github.com/google/android-fhir/issues/1079
    if (layout.questionnaireFileName.isEmpty()) {
      return
    }
    launchQuestionnaireFragment(layout)
  }

  private fun launchQuestionnaireFragment(layout: LayoutListViewModel.Layout) {
    viewLifecycleOwner.lifecycleScope.launch {
      findNavController()
        .navigate(
          MainNavGraphDirections.actionGlobalGalleryQuestionnaireFragment(
            questionnaireTitleKey = context?.getString(layout.textId) ?: "",
            questionnaireJsonStringKey =
              getQuestionnaireJsonStringFromAssets(
                context = requireContext(),
                backgroundContext = coroutineContext,
                fileName = layout.questionnaireFileName,
              ),
            workflow = layout.workflow,
          ),
        )
    }
  }
}
