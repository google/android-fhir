package com.google.android.fhir.reference.pages


import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.action.ViewActions.doubleClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.fhir.reference.R
import org.hamcrest.Matchers.*
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withParentIndex
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.withId


@RunWith(AndroidJUnit4::class)
@LargeTest
class AddPatientPage {
    /*Add patient page objects*/
    private val pageName = "Add Patient"
    private val phoneNumber: String ="Phone Number"
    private val okButton: String = "OK"
    private val previousMonth: String= "Previous month"
    private val dateOfBirthYear: String= "2019"
    private val selectYear: String = "2021"
    private val swipeYear: String = "2022"
    private val addPatientButton = R.id.add_patient
    private val patientEditText = R.id.textInputEditText
    private val recyclerview = R.id.recycler_view
    private val inputLayout= R.id.textInputLayout
    private val dateOfBirthIndex= 4


    fun validate_page(){
        /*Validate Add patient page name*/
        onView(withText(pageName))
        Thread.sleep(3000)
    }

    fun clickOnAddPatientButton(){
        onView(withId(addPatientButton))
            .perform(click())

    }

    fun shouldBeAbleToEnterDetails(
        FirstName: String,
        FamilyName: String,
        PhoneNumber: String,
        Gender: String,
        City: String,
        Country: String,
        active: String
    ){
        enterData(FirstName,2) /*Add first name*/
        enterData(FamilyName,3) /*Add family name*/
        selectDateOfBirth() /*Add family name*/
        selectWithText(Gender)/*Select gender*/
        enterData(PhoneNumber,7) /*Enter Phone number*/
        swipeScreenUp()
        Espresso.closeSoftKeyboard()
        enterData(City,7) /*Enter City*/
        Espresso.closeSoftKeyboard()
        enterData(Country,8)  /*Enter Country*/
        Espresso.closeSoftKeyboard()
        selectWithText(active) /*Select Is Active checkbox*/
        }

    fun shouldBeAbleToSubmitPatientDetails(){
        onView(withText("SUBMIT")).perform(click())
        Thread.sleep(5000)
    }

    private fun enterData(Text: String, index : Int){
        onView(allOf(withId(patientEditText), withParent(allOf(withParentIndex(0), withParent(
            allOf(withId(inputLayout), withParent(allOf(withParentIndex(index), withParent(
                withId(recyclerview)))))))))).perform(typeText(Text))
    }

    private fun selectDateOfBirth(){
        onView(allOf(withId(patientEditText), withParent(allOf(withParentIndex(0), withParent(
            allOf(withId(inputLayout), withParent(allOf(withParentIndex(dateOfBirthIndex), withParent(
                withId(recyclerview)))))))))).perform(click())
        onView(withText(selectYear)).perform(click())
        onView(withText(swipeYear)).perform(swipeDown())
            .check(matches(isDisplayed()))
        onView(withText(dateOfBirthYear)).perform(doubleClick())
        onView(withContentDescription(previousMonth)).perform(click())
        onView(withText(okButton)).perform(click())
    }

    private fun selectWithText(Text: String){
        onView(withText(Text)).perform(click())
    }

    private fun swipeScreenUp(){
        onView(withText(phoneNumber)).perform(swipeUp())
    }


}

