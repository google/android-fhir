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
        QuestionnaireListItem("HL7 example",
          "Real-world lifelines questionnaire",
          "hl7-fhir-examples-f201.json"),
          // https://www.hl7.org/fhir/questionnaire-example-bluebook.html
          QuestionnaireListItem("Sample questionnaire to test integer type",
        "Sample questionnaire to test integer type",
        "sample-questionnaire-integer-type.json")
      )
    )
  }
}
