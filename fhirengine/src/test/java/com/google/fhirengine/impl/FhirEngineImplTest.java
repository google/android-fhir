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

package com.google.fhirengine.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import androidx.test.core.app.ApplicationProvider;
import com.google.fhirengine.FhirEngine;
import com.google.fhirengine.FhirServices;
import com.google.fhirengine.ResourceNotFoundException;
import com.google.fhirengine.resource.TestingUtils;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** Unit tests for {@link FhirEngineImpl}. */
@RunWith(RobolectricTestRunner.class)
public class FhirEngineImplTest {
  private static final String TEST_PATIENT_1_ID = "test_patient_1";
  private static final Patient TEST_PATIENT_1;

  static {
    TEST_PATIENT_1 = new Patient();
    TEST_PATIENT_1.setId(TEST_PATIENT_1_ID);
    TEST_PATIENT_1.setGender(Enumerations.AdministrativeGender.MALE);
  }

  private static final String TEST_PATIENT_2_ID = "test_patient_2";
  private static final Patient TEST_PATIENT_2;

  static {
    TEST_PATIENT_2 = new Patient();
    TEST_PATIENT_2.setId(TEST_PATIENT_2_ID);
    TEST_PATIENT_2.setGender(Enumerations.AdministrativeGender.MALE);
  }

  FhirServices services =
      FhirServices.builder(ApplicationProvider.getApplicationContext()).inMemory().build();

  FhirEngine fhirEngine = services.getFhirEngine();

  TestingUtils testingUtils = new TestingUtils(services.getParser());

  @Before
  public void setUp() {
    fhirEngine.save(TEST_PATIENT_1);
  }

  @Test
  public void save_shouldSaveResource() throws Exception {
    fhirEngine.save(TEST_PATIENT_2);
    testingUtils.assertResourceEquals(
        TEST_PATIENT_2, fhirEngine.load(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void saveAll_shouldSaveResource() throws Exception {
    List<Patient> patients = new ArrayList();
    patients.add(TEST_PATIENT_1);
    patients.add(TEST_PATIENT_2);
    fhirEngine.saveAll(patients);
    testingUtils.assertResourceEquals(
        TEST_PATIENT_1, fhirEngine.load(Patient.class, TEST_PATIENT_1_ID));
    testingUtils.assertResourceEquals(
        TEST_PATIENT_2, fhirEngine.load(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void update_nonexistentResource_shouldInsertResource() throws Exception {
    fhirEngine.update(TEST_PATIENT_2);
    testingUtils.assertResourceEquals(
        TEST_PATIENT_2, fhirEngine.load(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void update_shouldUpdateResource() throws Exception {
    Patient patient = new Patient();
    patient.setId(TEST_PATIENT_1_ID);
    patient.setGender(Enumerations.AdministrativeGender.FEMALE);
    fhirEngine.update(patient);
    testingUtils.assertResourceEquals(patient, fhirEngine.load(Patient.class, TEST_PATIENT_1_ID));
  }

  @Test
  public void load_nonexistentResource_shouldThrowResourceNotFondException() throws Exception {
    ResourceNotFoundException resourceNotFoundInDbException =
        assertThrows(
            ResourceNotFoundException.class,
            () -> fhirEngine.load(Patient.class, "nonexistent_patient"));
    assertEquals(
        "Resource not found with type "
            + ResourceType.Patient.name()
            + " and id nonexistent_patient!",
        resourceNotFoundInDbException.getMessage());
  }

  @Test
  public void load_shouldReturnResource() throws Exception {
    testingUtils.assertResourceEquals(
        TEST_PATIENT_1, fhirEngine.load(Patient.class, TEST_PATIENT_1_ID));
  }
}
