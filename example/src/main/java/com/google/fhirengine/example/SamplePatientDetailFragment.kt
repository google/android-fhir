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
import com.google.fhirengine.example.data.SamplePatients

/**
 * A fragment representing a single SamplePatient detail screen.
 * This fragment is either contained in a [SamplePatientDetailActivity].
 */
class SamplePatientDetailFragment : Fragment() {
    var parentActivity: SamplePatientDetailActivity? = null
    var patients: List<SamplePatients.PatientItem>? = null
    var observations: List<SamplePatients.ObservationItem>? = null
    var patientsMap: Map<String, SamplePatients.PatientItem>? = null
    var observationsMap: Map<String, SamplePatients.ObservationItem>? = null
    /**
     * The dummy content this fragment is presenting.
     */
    //private var item: DummyContent.DummyItem? = null
    private var observation: SamplePatients.ObservationItem? = null
    private var observation1: SamplePatients.ObservationItem? = null
    private var observation2: SamplePatients.ObservationItem? = null
    private var observation3: SamplePatients.ObservationItem? = null
    private var observation4: SamplePatients.ObservationItem? = null
    private var observation5: SamplePatients.ObservationItem? = null
    private var observation6: SamplePatients.ObservationItem? = null
    private var observation7: SamplePatients.ObservationItem? = null
    private var observation8: SamplePatients.ObservationItem? = null
    private var observation9: SamplePatients.ObservationItem? = null
    private var observation10: SamplePatients.ObservationItem? = null
    private var patient: SamplePatients.PatientItem? = null
    // private var item: DummyDetailsContent.DummyItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                parentActivity = activity as SamplePatientDetailActivity?
                patients = parentActivity?.patients
                observations = parentActivity?.observations
                patientsMap = parentActivity?.patientsMap
                observationsMap = parentActivity?.observationsMap

                // observation = SamplePatients.OBSERVATIONS_MAP[it.getString(ARG_ITEM_ID)]
                // observation1 = SamplePatients.OBSERVATIONS_MAP["1"]
                // observation2 = SamplePatients.OBSERVATIONS_MAP["2"]
                // observation3 = SamplePatients.OBSERVATIONS_MAP["3"]
                // observation4 = SamplePatients.OBSERVATIONS_MAP["4"]
                // observation5 = SamplePatients.OBSERVATIONS_MAP["5"]
                // patient = SamplePatients.PATIENTS_MAP[it.getString(ARG_ITEM_ID)]

                observation = observationsMap?.get(ARG_ITEM_ID)
                // observation1 = observationsMap?.get("1")
                // observation2 = observationsMap?.get("2")
                // observation3 = observationsMap?.get("3")
                // observation4 = observationsMap?.get("4")
                // observation5 = observationsMap?.get("5")
                // observation6 = observationsMap?.get("6")
                // observation7 = observationsMap?.get("7")
                // observation8 = observationsMap?.get("8")
                // observation9 = observationsMap?.get("9")
                // observation10 = observationsMap?.get("10")
                observation1 = observationsMap?.get("11")
                observation2 = observationsMap?.get("12")
                observation3 = observationsMap?.get("13")
                observation4 = observationsMap?.get("14")
                observation5 = observationsMap?.get("15")
                observation6 = observationsMap?.get("16")
                observation7 = observationsMap?.get("17")
                observation8 = observationsMap?.get("18")
                observation9 = observationsMap?.get("19")
                observation10 = observationsMap?.get("20")
                patient = patientsMap?.get(it.getString(ARG_ITEM_ID))
                //item = DummyContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
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
//        item?.let {
//            rootView.findViewById<TextView>(R.id.samplepatient_detail).text = it.details
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                rootView.findViewById<TextView>(R.id.samplepatient_detail).setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>", Html.FROM_HTML_MODE_COMPACT))
//                rootView.findViewById<TextView>(R.id.samplepatient_detail).setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>", Html.FROM_HTML_MODE_COMPACT))
//            }
//            else {
//                rootView.findViewById<TextView>(R.id.samplepatient_detail).setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>"))
//            }
//        }
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
        observation6?.let {
            rootView.findViewById<TextView>(R.id.observation_detail6).text = "${it.code}: ${it.value}\nEffective: ${it.effective}"
        }
        observation7?.let {
            rootView.findViewById<TextView>(R.id.observation_detail7).text = "${it.code}: ${it.value}\nEffective: ${it.effective}"
        }
        observation8?.let {
            rootView.findViewById<TextView>(R.id.observation_detail8).text = "${it.code}: ${it.value}\nEffective: ${it.effective}"
        }
        observation9?.let {
            rootView.findViewById<TextView>(R.id.observation_detail9).text = "${it.code}: ${it.value}\nEffective: ${it.effective}"
        }
        observation10?.let {
            rootView.findViewById<TextView>(R.id.observation_detail10).text = "${it.code}: ${it.value}\nEffective: ${it.effective}"
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
