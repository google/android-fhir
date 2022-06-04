/*
 * Copyright 2022 Google LLC
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

package com.example.foodallergy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // 4.1 Create the QuestionnaireFragment
    // val fragment = QuestionnaireFragment()

    // 4.2 Load the questionnaire JSON from asset file
    // val questionnaireJsonString =
    //     application.assets.open("Questionnaire-food-allergy-questionnaire.json")
    //         .bufferedReader()
    //         .use { it.readText() }

    // 4.3 Set the questionnaire for the fragment
    // fragment.arguments = bundleOf(
    //     QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to questionnaireJsonString
    // )

    // 4.4 Add the questionnaire to the activity
    // supportFragmentManager.commit {
    //     add(R.id.fragment_container_view, fragment)
    // }
  }
}
