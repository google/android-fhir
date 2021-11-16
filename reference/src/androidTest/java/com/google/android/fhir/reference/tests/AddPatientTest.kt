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
public class AddPatientTest: BaseTest() {

    private val addPatientPage: AddPatientPage = AddPatientPage()
    private val addPatientTestData: AddPatientTestData = AddPatientTestData()
    private val registeredPatientListPage : RegisteredPatientListPage = RegisteredPatientListPage()


    @Before
    fun shouldBeAbleToValidatePage(){
        addPatientPage.validate_page()
    }

    @Test
    fun shouldBeAbleToAddPatient(){

        val firstname = addPatientTestData.firstName()
        val familyname = addPatientTestData.familyName()
        addPatientPage.clickOnAddPatientButton() //Click On Add Patient button
        addPatientPage.validate_page()
        /*Enter Patient details*/
        addPatientPage.shouldBeAbleToEnterDetails(
            firstname,
            familyname,
            addPatientTestData.phoneNumber,
            addPatientTestData.gender,
            addPatientTestData.city,
            addPatientTestData.country,
        addPatientTestData.isActive)
        addPatientPage.shouldBeAbleToSubmitPatientDetails() // Click on submit button
        registeredPatientListPage.shouldBeAbleToSearchPatientByName(firstname)
        registeredPatientListPage.shouldBeAbleToClickAddedPatient(firstname)
        registeredPatientListPage.shouldBeAbleToVerifyPatientName(firstname,familyname)
    }

//    @Test
//    fun shouldBeAbleToSearchAddedPatient(){
//        shouldBeAbleToAddPatient()
//        registeredPatientListPage.shouldBeAbleToSearchPatientByName(addPatientTestData.firstName)
//
//
//    }


}