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

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.reference.FhirApplication.Companion.fhirEngine
import com.google.android.fhir.reference.api.HapiFhirService
import com.google.android.fhir.reference.data.FhirPeriodicSyncWorker
import com.google.android.fhir.reference.data.HapiFhirResourceDataSource
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.RepeatInterval
import com.google.android.fhir.sync.Sync
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.ResourceType

/** An activity representing a list of Patients. */
class PatientListActivity() : AppCompatActivity() {
  private lateinit var fhirEngine: FhirEngine
  private lateinit var patientListViewModel: PatientListViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d("PatientListActivity", "onCreate() called")
    setContentView(R.layout.activity_patient_list)

    Sync.periodicSync<FhirPeriodicSyncWorker>(
      this,
      PeriodicSyncConfiguration(
        syncConstraints = Constraints.Builder().build(),
        repeat = RepeatInterval(interval = 1, timeUnit = TimeUnit.MINUTES)
      )
    )

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)
    toolbar.title = title

    fhirEngine = fhirEngine(this)

    patientListViewModel =
      ViewModelProvider(this, PatientListViewModelFactory(this.application, fhirEngine))
        .get(PatientListViewModel::class.java)
    val recyclerView: RecyclerView = findViewById(R.id.patient_list)

    val adapter = PatientItemRecyclerViewAdapter(this::onPatientItemClicked)
    recyclerView.adapter = adapter

    patientListViewModel.liveSearchedPatients.observe(
      this,
      {
        Log.d("PatientListActivity", "Submitting ${it.count()} patient records")
        adapter.submitList(it)
      }
    )
  }

  // Click handler to help display the details about the patients from the list.
  private fun onPatientItemClicked(patientItem: PatientListViewModel.PatientItem) {
    val intent =
      Intent(this.applicationContext, PatientDetailActivity::class.java).apply {
        putExtra(PatientDetailFragment.ARG_ITEM_ID, patientItem.id)
      }
    this.startActivity(intent)
  }

  // To suppress the warning. Seems to be an issue with androidx library.
  // "MenuBuilder.setOptionalIconsVisible can only be called from within the same library group
  // prefix (referenced groupId=androidx.appcompat with prefix androidx from groupId=fhir-engine"
  @SuppressLint("RestrictedApi")
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.list_options_menu, menu)
    // To ensure that icons show up in the overflow options menu. Icons go missing without this.
    if (menu is MenuBuilder) {
      menu.setOptionalIconsVisible(true)
    }
    return true
  }
}
