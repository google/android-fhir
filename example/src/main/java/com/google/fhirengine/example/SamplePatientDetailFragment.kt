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

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.fhirengine.example.data.SampleObservations
import com.google.fhirengine.example.data.SamplePatients
import com.google.fhirengine.example.dummy.DummyContent

/**
 * A fragment representing a single SamplePatient detail screen.
 * This fragment is either contained in a [SamplePatientListActivity]
 * in two-pane mode (on tablets) or a [SamplePatientDetailActivity]
 * on handsets.
 */
class SamplePatientDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: DummyContent.DummyItem? = null
    private var observation: SampleObservations.ObservationItem? = null
    private var observation1: SampleObservations.ObservationItem? = null
    private var observation2: SampleObservations.ObservationItem? = null
    private var observation3: SampleObservations.ObservationItem? = null
    private var observation4: SampleObservations.ObservationItem? = null
    private var observation5: SampleObservations.ObservationItem? = null
    private var patient: SamplePatients.PatientItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                observation = SampleObservations.OBSERVATIONS_MAP[it.getString(ARG_ITEM_ID)]
                observation1 = SampleObservations.OBSERVATIONS_MAP["1"]
                observation2 = SampleObservations.OBSERVATIONS_MAP["2"]
                observation3 = SampleObservations.OBSERVATIONS_MAP["3"]
                observation4 = SampleObservations.OBSERVATIONS_MAP["4"]
                observation5 = SampleObservations.OBSERVATIONS_MAP["5"]
                patient = SamplePatients.PATIENTS_MAP[it.getString(ARG_ITEM_ID)]
                item = DummyContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
//                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = item
//                        ?.content
                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = patient
                        ?.name
            }
        }
    }

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.samplepatient_detail, container, false)

        // Show the dummy content as text in a TextView.
        item?.let {
            rootView.findViewById<TextView>(R.id.samplepatient_detail).text = it.details
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                rootView.findViewById<TextView>(R.id.samplepatient_detail).setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>", Html.FROM_HTML_MODE_COMPACT))
//                rootView.findViewById<TextView>(R.id.samplepatient_detail).setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>", Html.FROM_HTML_MODE_COMPACT))
//            }
//            else {
//                rootView.findViewById<TextView>(R.id.samplepatient_detail).setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>"))
//            }
        }
        patient?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                rootView.findViewById<TextView>(R.id.samplepatient_detail).setText(Html.fromHtml(it.html, Html.FROM_HTML_MODE_LEGACY))
            }
            else {
                rootView.findViewById<TextView>(R.id.samplepatient_detail).setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>"))
            }
        }
        observation1?.let {
            rootView.findViewById<TextView>(R.id.observation_detail1).text = "${it.code}: ${it.value}\nEffective: ${it.effective}"
        }
        observation2?.let {
            rootView.findViewById<TextView>(R.id.observation_detail2).text = "${it.code}: ${it.value}\nEffective: ${it.effective}"
        }
        observation3?.let {
            rootView.findViewById<TextView>(R.id.observation_detail3).text = "${it.code}: ${it.value}\nEffective: ${it.effective}"
        }
        observation4?.let {
            rootView.findViewById<TextView>(R.id.observation_detail4).text = "${it.code}: ${it.value}\nEffective: ${it.effective}"
        }
        observation5?.let {
            rootView.findViewById<TextView>(R.id.observation_detail5).text = "${it.code}: ${it.value}\nEffective: ${it.effective}"
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
