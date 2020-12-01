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

package com.google.fhirengine.db.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.fhirengine.FhirServices;
import com.google.fhirengine.db.Database;
import com.google.fhirengine.db.ResourceNotFoundInDbException;
import com.google.fhirengine.db.impl.dao.LocalChangeDao.InvalidLocalChangeException;
import com.google.fhirengine.resource.TestingUtils;
import com.google.fhirengine.sync.FhirDataSource;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Unit tests for {@link DatabaseImpl}. */
@RunWith(AndroidJUnit4.class)
public class DatabaseImplTest {
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

  private static final String TEST_PATIENT_3_ID = "test_patient_3";
  private static final Patient TEST_PATIENT_3;

  static {
    TEST_PATIENT_3 = new Patient();
    TEST_PATIENT_3.setId(TEST_PATIENT_3_ID);
    TEST_PATIENT_3.setGender(Enumerations.AdministrativeGender.OTHER);
  }

  private FhirDataSource dataSource = (path, $completion) -> null;

  private FhirServices services =
      FhirServices.builder(dataSource, ApplicationProvider.getApplicationContext())
          .inMemory()
          .build();
  TestingUtils testingUtils = new TestingUtils(services.getParser());
  Database database = services.getDatabase();

  @Before
  public void setUp() throws Exception {
    database.insert(TEST_PATIENT_1);
  }

  @Test
  public void insert_existentResource_shouldThrowInvalidLocalChangeException() throws Exception {
    InvalidLocalChangeException invalidLocalChangeException =
        assertThrows(InvalidLocalChangeException.class, () -> database.insert(TEST_PATIENT_1));
    assertEquals("Can not INSERT on top of INSERT", invalidLocalChangeException.getMessage());
  }

  @Test
  public void insert_nonExistentResource_shouldInsertResource() throws Exception {
    database.insert(TEST_PATIENT_2);
    testingUtils.assertResourceEquals(
        TEST_PATIENT_2, database.select(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void insertAll_shouldInsertResources() throws Exception {
    List<Patient> patients = new ArrayList<>();
    patients.add(TEST_PATIENT_2);
    patients.add(TEST_PATIENT_3);
    database.insertAll(patients);
    testingUtils.assertResourceEquals(
        TEST_PATIENT_2, database.select(Patient.class, TEST_PATIENT_2_ID));
    testingUtils.assertResourceEquals(
        TEST_PATIENT_3, database.select(Patient.class, TEST_PATIENT_3_ID));
  }

  @Test
  public void update_existentResource_shouldUpdateResource() throws Exception {
    Patient patient = new Patient();
    patient.setId(TEST_PATIENT_1_ID);
    patient.setGender(Enumerations.AdministrativeGender.FEMALE);
    database.update(patient);
    testingUtils.assertResourceEquals(patient, database.select(Patient.class, TEST_PATIENT_1_ID));
  }

  @Test
  public void update_nonExistentResource_shouldThrowResourceNotFoundException() throws Exception {
    ResourceNotFoundInDbException resourceNotFoundInDbException =
        assertThrows(ResourceNotFoundInDbException.class, () -> database.update(TEST_PATIENT_2));
    assertEquals(
        "Resource not found with type "
            + ResourceType.Patient.name()
            + " and id "
            + TEST_PATIENT_2_ID
            + "!",
        resourceNotFoundInDbException.getMessage());
  }

  @Test
  public void select_existentResource_shouldReturnResource() throws Exception {
    testingUtils.assertResourceEquals(
        TEST_PATIENT_1, database.select(Patient.class, TEST_PATIENT_1_ID));
  }

  @Test
  public void select_nonExistentResource_shouldThrowResourceNotFoundException() throws Exception {
    ResourceNotFoundInDbException resourceNotFoundInDbException =
        assertThrows(
            ResourceNotFoundInDbException.class,
            () -> database.select(Patient.class, "nonexistent_patient"));
    assertEquals(
        "Resource not found with type "
            + ResourceType.Patient.name()
            + " and id nonexistent_patient!",
        resourceNotFoundInDbException.getMessage());
  }

  @Test
  public void select_invalidResourceType_shouldThrowIllegalArgumentException() throws Exception {
    IllegalArgumentException illegalArgumentException =
        assertThrows(
            IllegalArgumentException.class, () -> database.select(Resource.class, "resource_id"));
    assertEquals(
        "Cannot resolve resource type for " + Resource.class.getName(),
        illegalArgumentException.getMessage());
  }

  @Test
  public void delete_existentResource_shouldDeleteResource() throws Exception {
    testingUtils.assertResourceEquals(
        TEST_PATIENT_1, database.select(Patient.class, TEST_PATIENT_1_ID));
    database.delete(Patient.class, TEST_PATIENT_1_ID);
    ResourceNotFoundInDbException resourceNotFoundInDbException =
        assertThrows(
            ResourceNotFoundInDbException.class,
            () -> database.select(Patient.class, TEST_PATIENT_1_ID));
    assertEquals(
        "Resource not found with type "
            + ResourceType.Patient.name()
            + " and id "
            + TEST_PATIENT_1_ID
            + "!",
        resourceNotFoundInDbException.getMessage());
  }

  @Test
  public void delete_nonExistentResource_shouldThrowInvalidLocalChangeException() throws Exception {
    InvalidLocalChangeException invalidLocalChangeException =
        assertThrows(
            InvalidLocalChangeException.class,
            () -> database.delete(Patient.class, "non_existent_id"));
    assertEquals(
        "Can not DELETE non-existent resource Patient/non_existent_id",
        invalidLocalChangeException.getMessage());
  }
}
