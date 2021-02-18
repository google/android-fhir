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
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.fhir.common.JsonFormat
import com.google.fhir.r4.core.QuestionnaireResponse

class QuestionnaireActivity : AppCompatActivity() {
    private val viewModel: QuestionnaireViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)

        supportActionBar!!.apply {
            title = intent.getStringExtra(QUESTIONNAIRE_TITLE_KEY)
            setDisplayHomeAsUpEnabled(true)
        }

        // Only add the fragment once, when the activity is first created.
        if (savedInstanceState == null) {
            val fragment = QuestionnaireFragment()
            fragment.arguments = bundleOf(
                QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE to viewModel.questionnaire,
                QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE to viewModel.questionnaireResponseJson // ktlint-disable max-line-length
            )

            supportFragmentManager.commit {
                add(R.id.container, fragment, QUESTIONNAIRE_FRAGMENT_TAG)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.action_submit -> {
                val questionnaireFragment = supportFragmentManager.findFragmentByTag(
                    QUESTIONNAIRE_FRAGMENT_TAG
                ) as QuestionnaireFragment
                displayQuestionnaireResponse(questionnaireFragment.getQuestionnaireResponse())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Display Quesitonnaire response as a dialog
    fun displayQuestionnaireResponse(questionnaireResponse: QuestionnaireResponse) {
        val questionnaireResponseJson = JsonFormat.getPrinter().print(questionnaireResponse)
        val dialogFragment = QuestionnaireResponseDialogFragment()
        dialogFragment.arguments = bundleOf(
            QuestionnaireResponseDialogFragment.BUNDLE_KEY_CONTENTS to questionnaireResponseJson
        )
        dialogFragment.show(
            supportFragmentManager,
            QuestionnaireResponseDialogFragment.TAG
        )
    }

    companion object {
        const val QUESTIONNAIRE_TITLE_KEY = "questionnaire-title-key"
        const val QUESTIONNAIRE_FILE_PATH_KEY = "questionnaire-file-path-key"
        const val QUESTIONNAIRE_RESPONSE_FILE_PATH_KEY = "questionnaire-response-file-path-key"
        const val QUESTIONNAIRE_FRAGMENT_TAG = "questionannire-fragment-tag"
    }
}
