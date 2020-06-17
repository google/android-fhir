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

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import ca.uhn.fhir.parser.IParser;
import com.google.fhirengine.db.Database;
import com.google.fhirengine.db.ResourceNotFoundInDbException;
import com.google.fhirengine.index.FhirIndexer;
import com.google.fhirengine.index.impl.FhirIndexerModule;
import com.google.fhirengine.resource.ResourceModule;
import com.google.fhirengine.resource.TestingUtils;
import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
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

  @Inject IParser iParser;
  @Inject TestingUtils testingUtils;
  @Inject Database database;

  @Module
  public static class TestDatabaseModule {

    /** Create custom database for test that uses an in-memory database */
    @Provides
    public static Database provideTestDatabase(
        Context context, IParser iParser, FhirIndexer fhirIndexer) {
      return new DatabaseImpl(context, iParser, fhirIndexer, true);
    }
  }

  @Singleton
  @Component(modules = {TestDatabaseModule.class, FhirIndexerModule.class, ResourceModule.class})
  public interface TestComponent {
    void inject(DatabaseImplTest databaseImplTest);

    @Component.Builder
    interface Builder {
      @BindsInstance
      Builder withContext(Context context);

      TestComponent build();
    }
  }

  @Before
  public void setUp() throws Exception {
    DaggerDatabaseImplTest_TestComponent.builder()
        .withContext(ApplicationProvider.getApplicationContext())
        .build()
        .inject(this);
    database.insert(TEST_PATIENT_1);
  }

  @Test
  public void insert_shouldInsertResource() throws Exception {
    database.insert(TEST_PATIENT_2);
    testingUtils.assertResourceEquals(
        TEST_PATIENT_2, database.select(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void insertAll_shouldInsertResources() throws Exception {
    List<Patient> patients = new ArrayList();
    patients.add(TEST_PATIENT_1);
    patients.add(TEST_PATIENT_2);
    database.insertAll(patients);
    testingUtils.assertResourceEquals(
        TEST_PATIENT_1, database.select(Patient.class, TEST_PATIENT_1_ID));
    testingUtils.assertResourceEquals(
        TEST_PATIENT_2, database.select(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void update_nonexistentResource_shouldUpdateResource() throws Exception {
    Patient patient = new Patient();
    patient.setId(TEST_PATIENT_1_ID);
    patient.setGender(Enumerations.AdministrativeGender.FEMALE);
    database.update(patient);
    testingUtils.assertResourceEquals(patient, database.select(Patient.class, TEST_PATIENT_1_ID));
  }

  @Test
  public void update_existingResource_shouldInsertResource() throws Exception {
    database.update(TEST_PATIENT_2);
    testingUtils.assertResourceEquals(
        TEST_PATIENT_2, database.select(Patient.class, TEST_PATIENT_2_ID));
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
  public void select_nonexistentResource_shouldThrowResourceNotFondException() throws Exception {
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
  public void select_shouldReturnResource() throws Exception {
    testingUtils.assertResourceEquals(
        TEST_PATIENT_1, database.select(Patient.class, TEST_PATIENT_1_ID));
  }
}
