package com.google.android.fhir.reference.pages


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.fhir.reference.R
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class RegisteredPatientListPage {
    /*Registered patient list page objects*/
    private val pageName = "Registered Patients"
    private val searchTextBox = R.id.search_src_text
    private val addedPatient = R.id.name
    private val patientName = R.id.title

    fun validate_page(){
        /*Validate Page name*/
        Thread.sleep(3000)
        onView(withText(pageName))
    }

    fun shouldBeAbleToSearchPatientByName(firstName: String){
        onView(withId(searchTextBox)).perform(typeText(firstName))
        Thread.sleep(5000)
    }

    fun shouldBeAbleToClickAddedPatient(firstName: String){
        onView(withId(addedPatient)).perform(click())
    }

    fun shouldBeAbleToVerifyPatientName(firstName: String, familyName: String){
        Thread.sleep(5000)
        onView(withText("$firstName $familyName")).check(matches(withId(patientName)))


    }

}
