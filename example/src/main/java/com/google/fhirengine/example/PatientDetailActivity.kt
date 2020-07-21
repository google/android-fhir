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
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.fhirengine.example.data.SamplePatients

/**
 * An activity representing a single SamplePatient detail screen. This activity is only used on
 * narrow width devices. On tablet-size devices, item details are presented side-by-side with a list
 * of items in a [PatientListActivity].
 */
class PatientDetailActivity : AppCompatActivity() {
    var patients: List<SamplePatients.PatientItem>? = null
    var observations: List<SamplePatients.ObservationItem>? = null
    var patientsMap: Map<String, SamplePatients.PatientItem>? = null
    var observationsMap: Map<String, SamplePatients.ObservationItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)
        setSupportActionBar(findViewById(R.id.detail_toolbar))

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val jsonStringPatients = getJsonStrForPatientData()
        val jsonStringObservations = getJsonStrForObservationData()
        val fhirObservations = SamplePatients().getObservationItems(getJsonStrForObservationData())
        // val jsonString = getJsonStrForPatientData()
        val patientListViewModel = ViewModelProvider(this, PatientListViewModelFactory(
            jsonStringPatients, jsonStringObservations))
            .get(PatientListViewModel::class.java)

        patientsMap = patientListViewModel.getPatientsMap()
        observationsMap = patientListViewModel.getObservationsMap()

        patientListViewModel.getPatients().observe(this,
            Observer<List<SamplePatients.PatientItem>> {
                patients = it
                // adapter.submitList(it)
            })

        patientListViewModel.getObservations().observe(this,
            Observer<List<SamplePatients.ObservationItem>> {
                observations = it
                //adapter.submitList(it)
            })
        // patientListViewModel.getPatientsMap().observe(this,
        //     Observer<Map<String, SamplePatients.PatientItem>> {
        //         patientsMap = it
        //         // adapter.submitList(it)
        //     })
        //
        // patientListViewModel.getObservationsMap().observe(this,
        //     Observer<Map<String, SamplePatients.ObservationItem>> {
        //         observationsMap = it
        //         //adapter.submitList(it)
        //     })

        // savedInstanceState is non-null when there is fragment state saved from previous
        // configurations of this activity (e.g. when rotating the screen from portrait to
        // landscape). In this case, the fragment will automatically be re-added to its container so
        // we don"t need to manually add it. For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = PatientDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(PatientDetailFragment.ARG_ITEM_ID,
                            intent.getStringExtra(PatientDetailFragment.ARG_ITEM_ID))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.patient_detail_container, fragment)
                    .commit()
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

    /**
     * Helper function to read patient asset file data as string.
     */
    private fun getJsonStrForPatientData(): String {
        val patientJsonFilename = "new_sample_patients_bundle.json"

        return this.applicationContext.assets.open(patientJsonFilename).bufferedReader().use {
            it.readText()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) { android.R.id.home -> {

                    // This ID represents the Home or Up button. In the case of this activity, the
                    // Up button is shown. For more details, see the Navigation pattern on Android
                    // Design:
                    //
                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                    navigateUpTo(Intent(this, PatientListActivity::class.java))

                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
