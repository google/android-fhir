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

package com.google.android.fhir

import android.os.Build
import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.OperationOutcome
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

  @Test
  fun operationOutcomeIsSuccess_noIssue_shouldReturnFalse() {
    val outcome = OperationOutcome()
    assertThat(outcome.isUploadSuccess()).isFalse()
  }

  @Test
  fun operationOutcomeIsSuccess_errorIssue_shouldReturnFalse() {
    assertThat(TEST_OPERATION_OUTCOME_ERROR.isUploadSuccess()).isFalse()
  }

  @Test
  fun operationOutcomeIsSuccess_infoIssue_shouldReturnTrue() {
    assertThat(TEST_OPERATION_OUTCOME_INFO.isUploadSuccess()).isTrue()
  }

  @Test
  fun operationOutcomeIsSuccess_patient_shouldReturnFalse() {
    assertThat(Patient().isUploadSuccess()).isFalse()
  }

  companion object {
    val TEST_OPERATION_OUTCOME_ERROR = OperationOutcome()
    val TEST_OPERATION_OUTCOME_INFO = OperationOutcome()
    init {
      TEST_OPERATION_OUTCOME_ERROR.addIssue(
        OperationOutcome.OperationOutcomeIssueComponent()
          .setSeverity(OperationOutcome.IssueSeverity.ERROR)
      )
      TEST_OPERATION_OUTCOME_INFO.addIssue(
        OperationOutcome.OperationOutcomeIssueComponent()
          .setSeverity(OperationOutcome.IssueSeverity.INFORMATION)
      )
    }
  }
}
