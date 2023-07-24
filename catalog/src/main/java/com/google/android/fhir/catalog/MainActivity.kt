/*
 * Copyright 2023 Google LLC
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

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(R.layout.activity_main) {
  private var showOpenQuestionnaireMenu = true
  val getContent =
    registerForActivityResult(ActivityResultContracts.GetContent()) {
      it?.let { launchQuestionnaireFragmentWithUri(it) }
    }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(findViewById(R.id.toolbar))
    setUpBottomNavigationView()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.open_questionnaire_menu, menu)
    return true
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    menu.findItem(R.id.select_questionnaire_menu).isVisible = showOpenQuestionnaireMenu
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.select_questionnaire_menu -> {
        getContent.launch("application/json")
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  fun showOptionsMenu(showMenu: Boolean) {
    showOpenQuestionnaireMenu = showMenu
    invalidateOptionsMenu()
  }

  fun showBottomNavigationView(value: Int) {
    findViewById<BottomNavigationView>(R.id.bottom_navigation_view).visibility = value
  }

  fun setNavigationUp(value: Boolean) {
    supportActionBar?.apply { setDisplayHomeAsUpEnabled(value) }
  }

  fun setActionBar(title: String, gravity: Int) {
    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)
    val titleTextView = toolbar.findViewById<TextView>(R.id.toolbarTitle)
    titleTextView.text = title
    titleTextView.gravity = gravity
  }

  private fun setUpBottomNavigationView() {
    val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
    NavigationUI.setupWithNavController(bottomNavigationView, navController)
  }

  private fun launchQuestionnaireFragmentWithUri(uri: Uri) {
    val navHostFragment =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    val currentFragment = navHostFragment.childFragmentManager.fragments.lastOrNull()
    (currentFragment as? MainView)?.launchQuestionnaireFragment(uri)
  }
}
