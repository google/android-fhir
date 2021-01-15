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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import com.google.android.fhir.datacapture.QuestionnaireFragment

class QuestionnaireActivity : AppCompatActivity() {
    private val viewModel: QuestionnaireViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)

        supportActionBar!!.apply {
            title = intent.getStringExtra(QUESTIONNAIRE_TITLE_KEY)
            setDisplayHomeAsUpEnabled(true)
        }

    val questionnaireJson = assets
      .open(intent.getStringExtra(QUESTIONNAIRE_FILE_PATH_KEY)!!)
      .bufferedReader()
      .use { it.readText() }

    // Only add the fragment once, when the activity is first created.
    if (savedInstanceState == null) {
      val fragment = QuestionnaireFragment()
      fragment.arguments = bundleOf(
          QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE to questionnaireJson
      )
      supportFragmentManager.setFragmentResultListener(
          QuestionnaireFragment.QUESTIONNAIRE_RESPONSE_REQUEST_KEY,
          this,
          { requestKey, result ->
              val dialogFragment = QuestionnaireResponseDialogFragment()
              dialogFragment.arguments = bundleOf(
                  QuestionnaireResponseDialogFragment.BUNDLE_KEY_CONTENTS to
                      result.getString(QuestionnaireFragment.QUESTIONNAIRE_RESPONSE_BUNDLE_KEY)
              )
              dialogFragment.show(
                  supportFragmentManager,
                  QuestionnaireResponseDialogFragment.TAG
              )
          }
      )
      supportFragmentManager.beginTransaction()
          .add(R.id.container, fragment)
          .commit()
    }
  }
    companion object {
        const val QUESTIONNAIRE_TITLE_KEY = "questionnaire-title-key"
        const val QUESTIONNAIRE_FILE_PATH_KEY = "questionnaire-file-path-key"
    }
}

