package com.google.android.fhir.reference.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.fhir.reference.pages.RegisteredPatientListPage


import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class RegisteredPatientListTest: BaseTest() {

    var registeredPatientListPage: RegisteredPatientListPage = RegisteredPatientListPage()

    @Test
    fun validate_page_name(){
        Thread.sleep(2000)
        registeredPatientListPage.validate_page()


    }


}
