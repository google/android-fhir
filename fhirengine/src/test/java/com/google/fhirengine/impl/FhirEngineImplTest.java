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

import android.content.Context;
import android.os.Build;
import androidx.test.core.app.ApplicationProvider;
import com.google.fhirengine.FhirEngine;
import com.google.fhirengine.ResourceAlreadyExistsException;
import com.google.fhirengine.ResourceNotFoundException;
import com.google.fhirengine.cql.CqlModule;
import com.google.fhirengine.db.Database;
import com.google.fhirengine.db.impl.DatabaseModule;
import com.google.fhirengine.index.impl.FhirIndexerModule;
import com.google.fhirengine.resource.ResourceModule;
import com.google.fhirengine.resource.TestingUtils;
import dagger.BindsInstance;
import dagger.Component;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** Unit tests for {@link DatabaseImpl}. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
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

  @Inject FhirEngine fhirEngine;

  @Inject TestingUtils testingUtils;

  @Singleton
  @Component(modules = {FhirEngineModule.class, CqlModule.class, DatabaseModule.class,
      FhirIndexerModule.class, ResourceModule.class})
  public interface TestComponent {

    Database getDatabase();

    void inject(FhirEngineImplTest fhirEngineImplTest);

    @Component.Builder
    interface Builder {
      @BindsInstance
      Builder withContext(Context context);

      TestComponent build();
    }
  }

  @Before
  public void setUp() throws Exception {
    DaggerFhirEngineImplTest_TestComponent.builder()
        .withContext(ApplicationProvider.getApplicationContext())
        .build()
        .inject(this);
    fhirEngine.save(TEST_PATIENT_1);
  }

  @Test
  public void save_shouldSaveResource() throws Exception {
    fhirEngine.save(TEST_PATIENT_2);
    testingUtils.assertResourceEquals(
        TEST_PATIENT_2, fhirEngine.load(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void save_existingResource_shouldThrowResourceAlreadyExistsException() throws Exception {
    ResourceAlreadyExistsException resourceAlreadyExistsInDbException =
        assertThrows(ResourceAlreadyExistsException.class, () -> fhirEngine.save(TEST_PATIENT_1));
    assertEquals(
        "Resource with type "
            + ResourceType.Patient.name()
            + " and id "
            + TEST_PATIENT_1_ID
            + " already exists!",
        resourceAlreadyExistsInDbException.getMessage());
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
