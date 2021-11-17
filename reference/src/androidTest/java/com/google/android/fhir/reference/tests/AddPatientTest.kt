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

package com.google.android.fhir.reference.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.fhir.reference.pages.AddPatientPage
import com.google.android.fhir.reference.pages.RegisteredPatientListPage
import com.google.android.fhir.reference.testData.AddPatientTestData
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddPatientTest : BaseTest() {

  private val addPatientPage: AddPatientPage = AddPatientPage()
  private val addPatientTestData: AddPatientTestData = AddPatientTestData()
  private val registeredPatientListPage: RegisteredPatientListPage = RegisteredPatientListPage()

  @Before
  fun shouldBeAbleToValidatePage() {
    addPatientPage.validate_page()
  }

  @Test
  fun shouldBeAbleToAddPatient() {

    val firstname = addPatientTestData.firstName()
    val familyname = addPatientTestData.familyName()
    addPatientPage.clickOnAddPatientButton() // Click On Add Patient button
    addPatientPage.validate_page()
    /*Enter Patient details*/
    addPatientPage.shouldBeAbleToEnterDetails(
      firstname,
      familyname,
      addPatientTestData.phoneNumber,
      addPatientTestData.gender,
      addPatientTestData.city,
      addPatientTestData.country,
      addPatientTestData.isActive
    )
    addPatientPage.shouldBeAbleToSubmitPatientDetails() // Click on submit button
    registeredPatientListPage.shouldBeAbleToSearchPatientByName(firstname)
    registeredPatientListPage.shouldBeAbleToClickAddedPatient(firstname)
    registeredPatientListPage.shouldBeAbleToVerifyPatientName(firstname, familyname)
  }
}
