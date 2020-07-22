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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.uhn.fhir.context.FhirContext
import com.google.fhirengine.ui.QuestionnaireFragment
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

class QuestionnaireActivity : AppCompatActivity() {

    var resultTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)
        resultTextView = findViewById(R.id.response)

        // Example taken from https://www.hl7.org/fhir/questionnaire-example-f201-lifelines.json.html
        val jsonParser = FhirContext.forR4().newJsonParser()
        val questionnaire: Questionnaire = jsonParser.parseResource(Questionnaire::class.java, """
            {
              "resourceType": "Questionnaire",
              "id": "f201",
              "text": {
                "status": "generated",
                "div": "<div xmlns=\"http://www.w3.org/1999/xhtml\">\n      <pre>Lifelines Questionnaire 1 part 1\n  1. Do you have allergies?\n  2. General Questions:\n    2.a) What is your gender?\n    2.b) What is your date of birth?\n    2.c) What is your country of birth?\n    2.d) What is your marital status?\n    3. Intoxications:\n      3.a) Do you smoke?\n      3.b) Do you drink alcohol?</pre>\n    </div>"
              },
              "url": "http://hl7.org/fhir/Questionnaire/f201",
              "status": "active",
              "subjectType": [
                "Patient"
              ],
              "date": "2010",
              "code": [
                {
                  "system": "http://example.org/system/code/lifelines/nl",
                  "code": "VL 1-1, 18-65_1.2.2",
                  "display": "Lifelines Questionnaire 1 part 1"
                }
              ],
              "item": [
                {
                  "linkId": "1",
                  "text": "Do you have allergies?",
                  "type": "boolean"
                },
                {
                  "linkId": "2",
                  "text": "General questions",
                  "type": "group",
                  "item": [
                    {
                      "linkId": "2.1",
                      "text": "What is your gender?",
                      "type": "string"
                    },
                    {
                      "linkId": "2.2",
                      "text": "What is your date of birth?",
                      "type": "date"
                    },
                    {
                      "linkId": "2.3",
                      "text": "What is your country of birth?",
                      "type": "string"
                    },
                    {
                      "linkId": "2.4",
                      "text": "What is your marital status?",
                      "type": "string"
                    }
                  ]
                },
                {
                  "linkId": "3",
                  "text": "Intoxications",
                  "type": "group",
                  "item": [
                    {
                      "linkId": "3.1",
                      "text": "Do you smoke?",
                      "type": "boolean"
                    },
                    {
                      "linkId": "3.2",
                      "text": "Do you drink alchohol?",
                      "type": "boolean"
                    }
                  ]
                }
              ]
            }
        """.trimIndent())

        // modifications to the questionnaire
        questionnaire.title = "My questionnaire"

        val fragment = QuestionnaireFragment(questionnaire)
        supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .commit()
        fragment.setOnQuestionnaireSubmittedListener(object :
                QuestionnaireFragment.OnQuestionnaireSubmittedListener {
            override fun onSubmitted(questionnaireResponse: QuestionnaireResponse) {
                val parser = FhirContext.forR4().newJsonParser()
                resultTextView?.text = parser.encodeResourceToString(questionnaireResponse)
            }
        }
        )
    }
}
