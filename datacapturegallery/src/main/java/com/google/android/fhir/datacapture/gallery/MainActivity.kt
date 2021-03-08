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
                    "patient-registration.json"
                ),
                // Example taken from https://openhie.github.io/hiv-ig/Questionnaire-hiv-case-report-questionnaire.json.html
                QuestionnaireListItem(
                    "HIV Case Report",
                    "HIV Case Reporting and Monitoring IG",
                    "openhie-hiv-case-report.json"
                ),
                // Example taken from https://openhie.github.io/covid-ig/Questionnaire-WhoCrQuestionnaireCovid19Surveillance.json.html
                QuestionnaireListItem(
                    "COVID-19 Case Report",
                    "WHO Case Reporting for COVID-19 Surveillance IG",
                    "openhie-covid-case-report.json"
                ),
                // Example taken from https://github.com/cqframework/cds4cpm-mypain/blob/master/src/content/mypain-questionnaire.json
                QuestionnaireListItem(
                    "MyPAIN",
                    "CDS For Chronic Pain Management",
                    "cds4cpm-mypain.json"
                ),
                // IPRD-WITS Clinic HIV Risk assessment questionnaire authored by @joiskash
                QuestionnaireListItem(
                    "HIV-Risk Assessment",
                    "HIV-Risk Assessment",
                    "iprd-hiv-fhir-questionnaire.json"
                ),
                // Example taken from https://www.hl7.org/fhir/questionnaire-example-f201-lifelines.json.html & https://www.hl7.org/fhir/questionnaireResponse-example-f201-lifelines.json.html
                QuestionnaireListItem(
                    "Neonate record from New South Wales, Australia pre-filled",
                    "HL7 example \"bb\" with questionnaire response to pre-fill answers",
                    "hl7-questionnaire-example-bluebook.json",
                    "hl7-questionnaireresponse-example-bluebook.json"
                )
            )
        )
    }
}
