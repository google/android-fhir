package com.google.android.fhir

import android.os.Build
import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Patient
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class UtilTest : TestCase() {

    @Test
    fun logicalId_patient_missing_id_shouldReturnEmptyString() {
        val patient = Patient()
        assertThat(patient.logicalId).isEmpty()
    }

    @Test
    fun logicalId_patient_null_id_shouldReturnEmptyString() {
        val patient = Patient()
        patient.idElement = null
        assertThat(patient.logicalId).isEmpty()
    }

    @Test
    fun logicalId_patient_blank_id_shouldReturnEmptyString() {
        val patient = Patient()
        patient.idElement = IdType()
        assertThat(patient.logicalId).isEmpty()
    }

    @Test
    fun logicalId_patient_stringId_shouldReturnId() {
        val patient = Patient()
        patient.id = "test_patient"
        assertThat(patient.logicalId).isEqualTo("test_patient")
    }

    @Test
    fun logicalId_patient_fullyQualifiedId_shouldReturnUnqualifiedId() {
        val patient = Patient()
        patient.idElement = IdType("http://hapi.fhir.org/baseR4/Patient/Nemo")
        assertThat(patient.logicalId).isEqualTo("Nemo")
    }
}