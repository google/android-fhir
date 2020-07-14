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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.fhirengine.example.data.SamplePatients

/**
 * An activity representing a list of Patients.
 */
class SamplePatientListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        // Launch the old Fhir and CQL resources loading screen.
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val resLoadIntent = Intent(baseContext, MainActivity::class.java)
            startActivity(resLoadIntent)
        }

        val jsonString = getJsonStrForPatientData()
        val patientListViewModel = ViewModelProvider(this, PatientListViewModelFactory(jsonString))
            .get(PatientListViewModel::class.java)
        val recyclerView: RecyclerView = findViewById(R.id.samplepatient_list)

        // Click handler to help display the details about the patients from the list.
        val onPatientItemClicked: (SamplePatients.PatientItem) -> Unit = { patientItem ->
            val intent = Intent(this.applicationContext,
                SamplePatientDetailActivity::class.java).apply {
                putExtra(SamplePatientDetailFragment.ARG_ITEM_ID, patientItem.id)
            }
            this.startActivity(intent)
        }

        val adapter = SampleItemRecyclerViewAdapter(onPatientItemClicked)
        recyclerView.adapter = adapter

        patientListViewModel.getPatients().observe(this,
            Observer<List<SamplePatients.PatientItem>> {
            adapter.submitList(it)
        })
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
}
