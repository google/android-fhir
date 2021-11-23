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

package com.google.android.fhir.reference.pages

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.doubleClick
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withParentIndex
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.fhir.reference.R
import org.hamcrest.Matchers.allOf
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddPatientPage {
  /*Add patient page objects*/
  private val pageName = "Add Patient"
  private val phoneNumber: String = "Phone Number"
  private val dateOfBirth: String = "Date of Birth"
  private val okButton: String = "OK"
  private val previousMonth: String = "Previous month"
  private val dateOfBirthYear: String = "2019"
  private val selectYear: String = "2021"
  private val swipeYear: String = "2022"
  private val addPatientButton = R.id.add_patient
  private val patientEditText = R.id.textInputEditText
  private val recyclerview = R.id.recycler_view
  private val inputLayout = R.id.textInputLayout
  private val dateOfBirthIndex = 4
  private val inputError = R.id.textinput_error

  fun validate_page() {
    /*Validate Add patient page name*/
    onView(withText(pageName))
    closeSoftKeyboard()
    Thread.sleep(3000)
  }

  fun clickOnAddPatientButton() {
    onView(withId(addPatientButton)).perform(click())
  }

  fun shouldBeAbleToEnterDetails(
    FirstName: String,
    FamilyName: String,
    PhoneNumber: String,
    Gender: String,
    City: String,
    Country: String,
    active: String
  ) {
    enterData(FirstName, 2) /*Add first name*/
    enterData(FamilyName, 3) /*Add family name*/
    selectDateOfBirth() /*Add family name*/
    selectWithText(Gender) /*Select gender*/
    swipeScreenUp()
    /*Enter Phone number*/
    enterData(PhoneNumber, 4)
    enterData(City, 6) /*Enter City*/
    enterData(Country, 7) /*Enter Country*/
    selectWithText(active)
  }

  fun shouldBeAbleToEnterDetailsWithoutDOB(
    FirstName: String,
    FamilyName: String,
    PhoneNumber: String,
    Gender: String,
    City: String,
    Country: String,
    active: String
  ) {
    enterData(FirstName, 2) /*Add first name*/
    enterData(FamilyName, 3) /*Add family name*/
    selectWithText(Gender) /*Select gender*/
    swipeScreenUp()
    /*Enter Phone number*/
    enterData(PhoneNumber, 4)
    enterData(City, 6) /*Enter City*/
    enterData(Country, 7) /*Enter Country*/
    selectWithText(active)
  }

  fun shouldBeAbleToSubmitPatientDetails() {
    onView(withText("SUBMIT")).perform(click())
    Thread.sleep(5000)
  }

  private fun enterData(Text: String, index: Int) {
    onView(
        allOf(
          withId(patientEditText),
          withParent(
            allOf(
              withParentIndex(0),
              withParent(
                allOf(
                  withId(inputLayout),
                  withParent(allOf(withParentIndex(index), withParent(withId(recyclerview))))
                )
              )
            )
          )
        )
      )
      .perform(typeText(Text))
    closeSoftKeyboard()
  }

  private fun selectDateOfBirth() {
    onView(
        allOf(
          withId(patientEditText),
          withParent(
            allOf(
              withParentIndex(0),
              withParent(
                allOf(
                  withId(inputLayout),
                  withParent(
                    allOf(withParentIndex(dateOfBirthIndex), withParent(withId(recyclerview)))
                  )
                )
              )
            )
          )
        )
      )
      .perform(click())
    onView(withText(selectYear)).perform(click())
    onView(withText(swipeYear)).perform(swipeDown()).check(matches(isDisplayed()))
    onView(withText(dateOfBirthYear)).perform(doubleClick())
    onView(withContentDescription(previousMonth)).perform(click())
    onView(withText(okButton)).perform(click())
    closeSoftKeyboard()
  }

  private fun selectWithText(Text: String) {
    onView(withText(Text)).perform(click())
    closeSoftKeyboard()
  }

  private fun swipeScreenUp() {
    for (i in 0..100) {
      onView(withText("Male")).perform(swipeUp())
    }
  }

  private fun swipeScreenDown() {
    for (i in 0..100) {
      onView(withText(dateOfBirth)).perform(swipeDown())
    }
  }

  private fun verifyMessage(Index: Int) {
    onView(
        allOf(
          withId(inputError),
          withParent(
            allOf(
              withParentIndex(0),
              withParent(
                allOf(
                  withParentIndex(1),
                  withParent(
                    allOf(
                      withParentIndex(1),
                      withParent(allOf(withParentIndex(Index), withParent(withId(recyclerview))))
                    )
                  )
                )
              )
            )
          )
        )
      )
      .check(matches(isDisplayed()))
    Thread.sleep(5000)
  }

  fun verifyFirstNameErrorMessage() {
    swipeScreenDown()
    verifyMessage(2)
  }

  fun verifyFamilyNameErrorMessage() {
    swipeScreenDown()
    verifyMessage(3)
  }

  fun verifyDOBErrorMessage() {
    swipeScreenDown()
    verifyMessage(dateOfBirthIndex)
  }

  fun verifyPhoneNumberErrorMessage() {
    swipeScreenDown()
    verifyMessage(7)
  }
}
