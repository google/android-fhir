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
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.demo.care.WorkflowExecutionStatus
import com.google.android.fhir.demo.care.WorkflowExecutionViewModel
import com.google.android.fhir.demo.databinding.ActivityMainBinding
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

const val MAX_RESOURCE_COUNT = 20

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private lateinit var drawerToggle: ActionBarDrawerToggle
  private val viewModel: MainActivityViewModel by viewModels()
  private val workflowExecutionViewModel: WorkflowExecutionViewModel by viewModels()

  private lateinit var workflowBanner: LinearLayout
  private lateinit var workflowStatus: TextView
  private lateinit var workflowPercent: TextView
  private lateinit var workflowProgress: ProgressBar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    initActionBar()
    initNavigationDrawer()
    initWorkflowProgressBar()
    observeLastSyncTime()
    collectWorkflowExecutionState()
    viewModel.updateLastSyncTimestamp()
  }

  override fun onBackPressed() {
    if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
      binding.drawer.closeDrawer(GravityCompat.START)
      return
    }
    super.onBackPressed()
  }

  fun setDrawerEnabled(enabled: Boolean) {
    val lockMode =
      if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
    binding.drawer.setDrawerLockMode(lockMode)
    drawerToggle.isDrawerIndicatorEnabled = enabled
  }

  fun openNavigationDrawer() {
    binding.drawer.openDrawer(GravityCompat.START)
    viewModel.updateLastSyncTimestamp()
  }

  private fun initActionBar() {
    val toolbar = binding.toolbar
    setSupportActionBar(toolbar)
  }

  private fun initNavigationDrawer() {
    binding.navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected)
    drawerToggle = ActionBarDrawerToggle(this, binding.drawer, R.string.open, R.string.close)
    binding.drawer.addDrawerListener(drawerToggle)
    drawerToggle.syncState()
  }

  private fun onNavigationItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_sync -> {
        viewModel.triggerOneTimeSync()
        binding.drawer.closeDrawer(GravityCompat.START)
        return false
      }
    }
    return false
  }

  private fun initWorkflowProgressBar() {
    workflowBanner = binding.workflowExecutionStatusContainer.linearLayoutProgressStatus
    workflowStatus = binding.workflowExecutionStatusContainer.progressStatus
    workflowPercent = binding.workflowExecutionStatusContainer.progressPercent
    workflowProgress = binding.workflowExecutionStatusContainer.progressBar
  }

  private fun observeLastSyncTime() {
    viewModel.lastSyncTimestampLiveData.observe(this) {
      binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.last_sync_tv).text = it
    }
  }

  private fun collectWorkflowExecutionState() {
    lifecycleScope.launch {
      workflowExecutionViewModel.workflowExecutionState.collect {
        fadeInTopBanner(it)
        if (it is WorkflowExecutionStatus.Finished && it.completed == it.total) fadeOutTopBanner(it)
      }
    }
  }

  private fun fadeInTopBanner(state: WorkflowExecutionStatus) {
    if (workflowBanner.visibility != View.VISIBLE) {
      workflowStatus.text = resources.getString(R.string.executing_workflow)
      workflowProgress.progress = 0
      workflowProgress.visibility = View.VISIBLE
      workflowBanner.visibility = View.VISIBLE
      val animation = AnimationUtils.loadAnimation(workflowBanner.context, R.anim.fade_in)
      workflowBanner.startAnimation(animation)
    }
    if (state is WorkflowExecutionStatus.Finished) {
      val progress =
        state
          .let { it.completed.toDouble().div(it.total) }
          .let { if (it.isNaN()) 0.0 else it }
          .times(100)
          .roundToInt()
      "${state.completed} / ${state.total}".also { workflowPercent.text = it }
      workflowProgress.progress = progress
    }
  }

  private fun fadeOutTopBanner(state: WorkflowExecutionStatus) {
    if (state is WorkflowExecutionStatus.Finished) workflowPercent.text = ""
    workflowProgress.visibility = View.GONE

    if (workflowBanner.visibility == View.VISIBLE) {
      "${
      resources.getString(R.string.executing_workflow).uppercase()
      } ${state::class.java.simpleName.uppercase()}".also {
        workflowStatus.text = it
      }
      val animation = AnimationUtils.loadAnimation(workflowBanner.context, R.anim.fade_out)
      workflowBanner.startAnimation(animation)
      Handler(Looper.getMainLooper()).postDelayed({ workflowBanner.visibility = View.GONE }, 2000)
    }
  }
}
