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

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.fhir.reference.TAG
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
  private val firstname = addPatientTestData.firstName()
  private val familyName = addPatientTestData.familyName()

  @Before
  fun shouldBeAbleToValidatePage() {
    addPatientPage.validate_page()
  }

  @Test
  fun shouldBeAbleToAddPatient() {
    Log.d(TAG, "Click On Add Patient Button")
    addPatientPage.clickOnAddPatientButton() // Click On Add Patient button
    addPatientPage.validate_page()
    /*Enter Patient details*/
    addPatientPage.shouldBeAbleToEnterDetails(
      firstname,
      familyName,
      addPatientTestData.phoneNumber,
      addPatientTestData.gender,
      addPatientTestData.city,
      addPatientTestData.country,
      addPatientTestData.isActive
    )
    addPatientPage.shouldBeAbleToSubmitPatientDetails() // Click on submit button
    registeredPatientListPage.shouldBeAbleToSearchPatientByName(firstname)
    registeredPatientListPage.shouldBeAbleToClickAddedPatient(firstname)
    registeredPatientListPage.shouldBeAbleToVerifyPatientName(firstname, familyName)
  }

  @Test
  fun shouldNotBeAbleToAddPatientDetailsWithoutEmptyFirstName() {
    addPatientPage.clickOnAddPatientButton() // Click On Add Patient button
    addPatientPage.validate_page()
    /*Enter Patient details*/
    addPatientPage.shouldBeAbleToEnterDetails(
      "",
      familyName,
      addPatientTestData.phoneNumber,
      addPatientTestData.gender,
      addPatientTestData.city,
      addPatientTestData.country,
      addPatientTestData.isActive
    )
    addPatientPage.shouldBeAbleToSubmitPatientDetails()
    addPatientPage.verifyFirstNameErrorMessage()
  }

  @Test
  fun shouldNotBeAbleToAddPatientDetailsWithoutEmptyFamilyName() {
    addPatientPage.clickOnAddPatientButton() // Click On Add Patient button
    addPatientPage.validate_page()
    /*Enter Patient details*/
    addPatientPage.shouldBeAbleToEnterDetails(
      firstname,
      "",
      addPatientTestData.phoneNumber,
      addPatientTestData.gender,
      addPatientTestData.city,
      addPatientTestData.country,
      addPatientTestData.isActive
    )
    addPatientPage.shouldBeAbleToSubmitPatientDetails()
    addPatientPage.verifyFamilyNameErrorMessage()
  }

  @Test
  fun shouldNotBeAbleToAddPatientDetailsWithoutDateOfBirth() {

    addPatientPage.clickOnAddPatientButton() // Click On Add Patient button
    addPatientPage.validate_page()
    /*Enter Patient details*/
    addPatientPage.shouldBeAbleToEnterDetailsWithoutDOB(
      firstname,
      familyName,
      addPatientTestData.phoneNumber,
      addPatientTestData.gender,
      addPatientTestData.city,
      addPatientTestData.country,
      addPatientTestData.isActive
    )
    addPatientPage.shouldBeAbleToSubmitPatientDetails()
    addPatientPage.verifyDOBErrorMessage()
  }

  @Test
  fun shouldNotBeAbleToAddPatientDetailsWithoutPhoneNumber() {
    addPatientPage.clickOnAddPatientButton() // Click On Add Patient button
    addPatientPage.validate_page()
    /*Enter Patient details*/
    addPatientPage.shouldBeAbleToEnterDetails(
      firstname,
      familyName,
      "",
      addPatientTestData.gender,
      addPatientTestData.city,
      addPatientTestData.country,
      addPatientTestData.isActive
    )
    addPatientPage.shouldBeAbleToSubmitPatientDetails()
    addPatientPage.verifyPhoneNumberErrorMessage()
  }

  @Test
  fun shouldNotBeAbleToAddPatientDetailsWithBlankDetails() {
    addPatientPage.clickOnAddPatientButton() // Click On Add Patient button
    addPatientPage.validate_page()
    addPatientPage.shouldBeAbleToSubmitPatientDetails()
    addPatientPage.verifyFirstNameErrorMessage()
    addPatientPage.verifyFirstNameErrorMessage()
    addPatientPage.verifyDOBErrorMessage()
    addPatientPage.verifyPhoneNumberErrorMessage()
  }
}
