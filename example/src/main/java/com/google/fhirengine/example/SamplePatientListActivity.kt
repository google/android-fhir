package com.google.fhirengine.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * An activity representing a list of Patients. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [SamplePatientDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class SamplePatientListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_samplepatient_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        // Launch the old Fhir and CQL resources loading screen.
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val resLoadIntent = Intent(baseContext, MainActivity::class.java)
            startActivity(resLoadIntent)
        }

        if (findViewById<NestedScrollView>(R.id.samplepatient_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        val patientListViewModel = PatientListViewModel(getJsonStrForPatientData())
        setupRecyclerView(findViewById(R.id.samplepatient_list), patientListViewModel)
    }

    /**
     * Helper function to read patient asset file data as string.
     */
    private fun getJsonStrForPatientData(): String {
        val patientJsonFilename = "sample_patients_bundle.json"

        return this.applicationContext.assets.open(patientJsonFilename).bufferedReader().use{
            it.readText()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, viewModel: PatientListViewModel) {
        recyclerView.adapter = SampleItemRecyclerViewAdapter(this, viewModel, twoPane)
    }

}