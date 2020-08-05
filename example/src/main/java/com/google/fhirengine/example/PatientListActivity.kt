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

package com.google.fhirengine.example

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.fhirengine.FhirEngine

/**
 * An activity representing a list of Patients.
 */
class PatientListActivity : AppCompatActivity() {
    var fhirEngine: FhirEngine? = null
    var patientListViewModel: PatientListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        // Launch the old Fhir and CQL resources loading screen.
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val resLoadIntent = Intent(baseContext, CqlLoadActivity::class.java)
            startActivity(resLoadIntent)
        }

        fhirEngine = FhirApplication.fhirEngine(this)
        // val jsonString = getJsonStrForPatientData()
        val jsonStringPatients = getJsonStrForPatientData()
        val jsonStringObservations = getJsonStrForObservationData()

        patientListViewModel = ViewModelProvider(this, PatientListViewModelFactory(
            this.application, fhirEngine!!
        ))
            .get(PatientListViewModel::class.java)
        val recyclerView: RecyclerView = findViewById(R.id.patient_list)

        // Click handler to help display the details about the patients from the list.
        val onPatientItemClicked: (PatientListViewModel.PatientItem) -> Unit = { patientItem ->
            val intent = Intent(this.applicationContext,
                PatientDetailActivity::class.java).apply {
                putExtra(PatientDetailFragment.ARG_ITEM_ID, patientItem.id)
            }
            this.startActivity(intent)
        }

        val adapter = PatientItemRecyclerViewAdapter(onPatientItemClicked)
        recyclerView.adapter = adapter

        patientListViewModel!!.getSearchedPatients()?.observe(this,
            Observer<List<PatientListViewModel.PatientItem>> {
                adapter.submitList(it)
            })

        patientListViewModel!!.getObservations().observe(this,
            Observer<List<PatientListViewModel.ObservationItem>> {
                //adapter.submitList(it)
            })
    }

    // To suppress the warning. Seems to be an issue with androidx library.
    // "MenuBuilder.setOptionalIconsVisible can only be called from within the same library group
    // prefix (referenced groupId=androidx.appcompat with prefix androidx from groupId=fhir-engine"
    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflator: MenuInflater = menuInflater
        inflator.inflate(R.menu.list_options_menu, menu)
        // To ensure that icons show up in the overflow options menu. Icons go missing without this.
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        // return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val view: View = findViewById(R.id.app_bar)

        // Handle item selection
        return when (item.itemId) {
            R.id.sync_resources -> {
                sync_resources(view)
                true
            }
            R.id.load_resource -> {
                load_resources()
                true
            }
            R.id.about -> {
                showAbout(view)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun load_resources() {
        val resLoadIntent = Intent(baseContext, CqlLoadActivity::class.java)
        startActivity(resLoadIntent)
    }

    private fun showAbout(view: View) {
        Snackbar.make(view, R.string.about_text, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun sync_resources(view: View) {
        Snackbar.make(view, "Getting Patients List", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
        patientListViewModel!!.searchPatients()

    }

    /**
     * Helper function to read patient asset file data as string.
     */
    private fun getJsonStrForPatientData(): String {
        val patientJsonFilename = "sample_patients_bundle.json"

        return assets.open(patientJsonFilename).bufferedReader().use {
            it.readText()
        }
    }

    /**
     * Helper function to read observation asset file data as string.
     */
    private fun getJsonStrForObservationData(): String {
        val observationJsonFilename = "sample_observations_bundle.json"

        return this.applicationContext.assets.open(observationJsonFilename).bufferedReader().use {
            it.readText()
        }
    }

}
