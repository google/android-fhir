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
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.reference.databinding.ActivityMainBinding
import com.google.android.fhir.sync.State
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val MAX_RESOURCE_COUNT = 20

class MainActivity :
  AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NavigationDrawer {
  private lateinit var binding: ActivityMainBinding
  private lateinit var drawerToggle: ActionBarDrawerToggle
  private val TAG = javaClass.name
  private val viewModel: MainActivityViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    initActionBar()
    initNavigationDrawer()
    observeLastSyncTime()
    requestSyncPoll()
  }

  override fun onBackPressed() {
    if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
      binding.drawer.closeDrawer(GravityCompat.START)
      return
    }
    super.onBackPressed()
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_sync -> {
        requestSyncPoll()
        true
      }
    }
    binding.drawer.closeDrawer(GravityCompat.START)
    return false
  }

  override fun setDrawerEnabled(enabled: Boolean) {
    val lockMode =
      if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
    binding.drawer.setDrawerLockMode(lockMode)
    drawerToggle.isDrawerIndicatorEnabled = enabled
  }

  override fun openNavigationDrawer() {
    binding.drawer.openDrawer(GravityCompat.START)
    requestLastSyncTime()
  }

  private fun initActionBar() {
    val toolbar = binding.toolbar
    setSupportActionBar(toolbar)
    toolbar.title = title
  }

  private fun initNavigationDrawer() {
    binding.navigationView.setNavigationItemSelectedListener(this)
    drawerToggle = ActionBarDrawerToggle(this, binding.drawer, R.string.open, R.string.close)
    binding.drawer.addDrawerListener(drawerToggle)
    drawerToggle.syncState()
  }

  private fun updateSyncStatus(syncStatus: String) {
    binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.last_sync_tv).text =
      syncStatus
  }

  private fun showToast(message: String) {
    Log.i(TAG, message)
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  private fun requestSyncPoll() {
    val flow = viewModel.poll()
    observeState(flow)
  }

  private fun requestLastSyncTime() {
    viewModel.getLastSyncStatus()
  }

  private fun observeState(flow: Flow<State>) {
    lifecycleScope.launch {
      flow.collect {
        when (it) {
          is State.Started -> showToast("Sync: started")
          is State.InProgress -> showToast("Sync: in progress with ${it.resourceType?.name}")
          is State.Finished -> showToast("Sync: successeded at ${it.result.timestamp}")
          is State.Failed -> showToast("Sync: failed at ${it.result.timestamp}")
          else -> showToast("Sync: unknown state.")
        }
      }
    }
  }

  private fun observeLastSyncTime() {
    viewModel.lastSyncLiveData.observe(this, { updateSyncStatus(it) })
  }
}
