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

package com.google.android.fhir.datacapture.gallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = QuestionnaireListAdapter(
            listOf(
                // Example taken from https://www.hl7.org/fhir/questionnaire-example-f201-lifelines.json.html
                QuestionnaireListItem(
                    "Real-world lifelines questionnaire",
                    "HL7 example \"f201\"",
                    "hl7-questionnaire-example-f201-lifelines.json"
                ),
                // Example taken from https://www.hl7.org/fhir/questionnaire-example-bluebook.json.html
                QuestionnaireListItem(
                    "Neonate record from New South Wales, Australia",
                    "HL7 example \"bb\"",
                    "hl7-questionnaire-example-bluebook.json"
                ),
                // Example authored by fredhersch@google.com.
                QuestionnaireListItem(
                    "Patient registration",
                    "Example authored by Fred Hersch",
                    "patient-registration.json"),
                // Example taken from https://openhie.github.io/hiv-ig/Questionnaire-hiv-case-report-questionnaire.json.html
                QuestionnaireListItem(
                    "HIV Case Report",
                    "OpenHIE HIV Case Reporting and Monitoring IG",
                    "openhie-hiv-case-report.json"
                )
            )
        )
    }
}
