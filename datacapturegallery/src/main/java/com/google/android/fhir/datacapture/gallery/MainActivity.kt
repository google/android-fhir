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
import androidx.fragment.app.FragmentResultListener
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example taken from https://www.hl7.org/fhir/questionnaire-example-f201-lifelines.json.html
        val jsonResource = assets.open("hl7-fhir-examples-f201.json").bufferedReader()
            .use { it.readText() }
        val jsonParser = FhirContext.forR4().newJsonParser()
        val questionnaire = jsonParser.parseResource(Questionnaire::class.java, jsonResource)

        // Modifications to the questionnaire
        questionnaire.title = "My questionnaire"

        val questionnaireWithDuplicateItems = duplicateItemsInQuestionnaire(questionnaire,100)
        questionnaireWithDuplicateItems.title = "Questionnaire with duplicate"
        val fragment = QuestionnaireFragment(questionnaireWithDuplicateItems)
        supportFragmentManager.setFragmentResultListener(
            QuestionnaireFragment.QUESTIONNAIRE_RESPONSE_REQUEST_KEY,
            this,
            object : FragmentResultListener {
                override fun onFragmentResult(requestKey: String, result: Bundle) {
                    val dialogFragment = QuestionnaireResponseDialogFragment(
                        result.getString(QuestionnaireFragment.QUESTIONNAIRE_RESPONSE_BUNDLE_KEY)!!
                    )
                    dialogFragment.show(
                        supportFragmentManager,
                        QuestionnaireResponseDialogFragment.TAG
                    )
                }
            }
        )
        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment)
            .commit()
    }

    private fun duplicateItemsInQuestionnaire (
            questionnaire: Questionnaire,numberOfDuplicates: Int
    ): Questionnaire{
        var questionnaireWithDuplicates : Questionnaire = Questionnaire();
        for ( i in 0..numberOfDuplicates){
            val x  = i % 3
            val tmp:Questionnaire.QuestionnaireItemComponent = modifyQuestionnaireItem(questionnaire.item.get(x).copy(),i)
            questionnaireWithDuplicates.addItem(tmp)
        }

        return questionnaireWithDuplicates
    }
    private fun modifyQuestionnaireItem(questionnaireItem: Questionnaire.QuestionnaireItemComponent, index: Int):Questionnaire.QuestionnaireItemComponent{

        if(questionnaireItem.hasItem()){
            questionnaireItem.setText(questionnaireItem.text+"_"+index.toString())
            questionnaireItem.setLinkId(index.toString()+"_dup");
            for(internal_index in questionnaireItem.item.indices){
                modifyQuestionnaireItem(questionnaireItem.item[internal_index], (index.toString()+internal_index.toString()).toInt())
            }

        }else{
            questionnaireItem.setText(questionnaireItem.text+"_"+index.toString())
            questionnaireItem.setLinkId(index.toString()+"_dup");
        }
        return questionnaireItem

    }

}
