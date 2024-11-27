/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.workflow.demo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.fhir.workflow.demo.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

  companion object {
    fun newInstance() = MainFragment()
  }

  private val viewModel: MainViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    return inflater.inflate(R.layout.fragment_main, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val pager: ViewPager2 = view.findViewById(R.id.phase_view_pager)
    val tabLayout = view.findViewById<TabLayout>(R.id.into_tab_layout)
    val progressView = view.findViewById<ProgressBar>(R.id.progress_circular)

    val adapter = ActivityFlowAdapter()
    pager.adapter = adapter
    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.adapterData.collect {
        adapter.submitList(it)

        tabLayout.postDelayed(
          { tabLayout.getTabAt(it.indexOfFirst { it.isActive })?.select() },
          1000,
        )
      }
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.progressFlow.collect {
        progressView.visibility = if (it) View.VISIBLE else View.GONE
      }
    }

    TabLayoutMediator(tabLayout, pager) { tab, position -> }.attach()

    view.findViewById<Button>(R.id.restart_flow).setOnClickListener { viewModel.restartFlow() }
    view.findViewById<Button>(R.id.install_dependencies).setOnClickListener {
      viewModel.installDependencies()
    }

    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.activity_flow_types, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    viewModel.selectFlow(item.itemId)
    return true
  }
}
