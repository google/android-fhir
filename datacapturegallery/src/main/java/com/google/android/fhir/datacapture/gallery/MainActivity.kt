/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.datacapture.gallery

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(R.layout.activity_main) {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(findViewById(R.id.toolbar))
    setUpBottomNavigationView()
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
}
