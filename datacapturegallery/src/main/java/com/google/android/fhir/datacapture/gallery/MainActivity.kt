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
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.QuestionnaireFragment
import org.hl7.fhir.r4.model.Questionnaire

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example taken from https://www.hl7.org/fhir/questionnaire-example-f201-lifelines.json.html
        val questionnaireJson = assets.open("hl7-fhir-examples-f201.json").bufferedReader()
            .use { it.readText() }
        val jsonParser = FhirContext.forR4().newJsonParser()
        val questionnaire = jsonParser.parseResource(Questionnaire::class.java, questionnaireJson)

        // Modifications to the questionnaire
        questionnaire.title = "My questionnaire"
        supportFragmentManager.setFragmentResultListener(
            QuestionnaireFragment.QUESTIONNAIRE_RESPONSE_REQUEST_KEY,
            this,
            object : FragmentResultListener {
                override fun onFragmentResult(requestKey: String, result: Bundle) {
                    val dialogFragment = QuestionnaireResponseDialogFragment()
                    dialogFragment.arguments = bundleOf(Pair("contents",result.getString(QuestionnaireFragment.QUESTIONNAIRE_RESPONSE_BUNDLE_KEY)))
                    dialogFragment.show(
                        supportFragmentManager,
                        QuestionnaireResponseDialogFragment.TAG
                    )
                }
            }
        )
        // Only add the fragment once, when the activity is first created.
        if (savedInstanceState == null) {
            val fragment = QuestionnaireFragment()
            fragment.arguments = bundleOf(Pair("questionnaire", questionnaire))
            supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .commit()
        }
    }
}
