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

/**
 * An activity representing a single SamplePatient detail screen. This activity is only used on
 * narrow width devices. On tablet-size devices, item details are presented side-by-side with a list
 * of items in a [SamplePatientListActivity].
 */
class SamplePatientDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_samplepatient_detail)
        setSupportActionBar(findViewById(R.id.detail_toolbar))

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
            val fragment = SamplePatientDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(SamplePatientDetailFragment.ARG_ITEM_ID,
                            intent.getStringExtra(SamplePatientDetailFragment.ARG_ITEM_ID))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.samplepatient_detail_container, fragment)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) { android.R.id.home -> {

                    // This ID represents the Home or Up button. In the case of this activity, the
                    // Up button is shown. For more details, see the Navigation pattern on Android
                    // Design:
                    //
                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                    navigateUpTo(Intent(this, SamplePatientListActivity::class.java))

                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
