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

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireContainerFragmentTest {
  @Rule @JvmField var activityTestRule = ActivityTestRule(MainActivity::class.java)

  @Test
  fun testQuestionnaireFragment() {
    questionnaireContainerRobot {
      assert_questionnaire_recycler_view_displayed()

      click_Real_world_lifelines_questionnaire()
      assert_displayed_Real_world_lifelines_questionnaire()
      assert_submit_button_displayed()
      pressBack()

      click_Neonate_record_from_New_South_Wales_Australia()
      assert_submit_button_displayed()
      assert_displayed_Neonate_record_from_New_SouthWales_Australia_questionnaire()
      pressBack()

      click_Patient_registration()
      assert_displayed_patient_registration_questionnaire()
      assert_submit_button_displayed()
      pressBack()

      click_HIV_Case_Report()
      assert_displayed_HIV_Case_Report_questionnaire()
      assert_submit_button_displayed()
      pressBack()

      click_COVID19_Case_Report()
      assert_displayed_COVID19_Case_Report_questionnaire()
      assert_submit_button_displayed()
      pressBack()

      click_MyPAIN()
      assert_displayed_MyPAIN_questionnaire()
      assert_submit_button_displayed()
      pressBack()

      click_HIV_Risk_Assessment()
      assert_displayed_HIV_Risk_Assessment_questionnaire()
      assert_submit_button_displayed()
    }
  }
}
