package com.google.fhirengine.db.impl;

import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.google.fhir.r4.core.AdministrativeGenderCode;
import com.google.fhir.r4.core.Id;
import com.google.fhir.r4.core.Patient;
import com.google.fhirengine.db.ResourceAlreadyExistsInDbException;
import com.google.fhirengine.db.ResourceNotFoundInDbException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/** Unit tests for {@link DatabaseImpl}. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class DatabaseImplTest {
  private static final String TEST_PATIENT_1_ID = "test_patient_1";
  private static final Patient TEST_PATIENT_1 = Patient.newBuilder()
      .setId(Id.newBuilder().setValue(TEST_PATIENT_1_ID))
      .setGender(Patient.GenderCode.newBuilder().setValue(AdministrativeGenderCode.Value.MALE))
      .build();
  private static final String TEST_PATIENT_2_ID = "test_patient_2";
  private static final Patient TEST_PATIENT_2 = Patient.newBuilder()
      .setId(Id.newBuilder().setValue(TEST_PATIENT_2_ID))
      .setGender(Patient.GenderCode.newBuilder().setValue(AdministrativeGenderCode.Value.MALE))
      .build();

  private DatabaseImpl database;

  @Before
  public void setUp() throws Exception {
    database = new DatabaseImpl(ApplicationProvider.getApplicationContext());
    database.insert(TEST_PATIENT_1);
  }

  @Test
  public void insert_shouldInsertResource() throws Exception {
    database.insert(TEST_PATIENT_2);
    assertEquals(TEST_PATIENT_2, database.select(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void insert_existingResource_shouldThrowResourceAlreadyExistsException() throws Exception {
    ResourceAlreadyExistsInDbException resourceAlreadyExistsInDbException =
        assertThrows(ResourceAlreadyExistsInDbException.class, () -> database.insert(TEST_PATIENT_1));
    assertEquals(
        "Resource with type " + Patient.class.getName() + " and id " + TEST_PATIENT_1_ID +
            " already exists!",
        resourceAlreadyExistsInDbException.getMessage());
  }

  @Test
  public void select_nonexistentResource_shouldThrowResourceNotFondException() throws Exception {
    ResourceNotFoundInDbException resourceNotFoundInDbException =
        assertThrows(ResourceNotFoundInDbException.class, () ->
            database.select(Patient.class, "nonexistent_patient"));
    assertEquals(
        "Resource not found with type com.google.fhir.r4.core.Patient and id nonexistent_patient!",
        resourceNotFoundInDbException.getMessage());
  }

  @Test
  public void select_shouldReturnResource() throws Exception {
    assertEquals(TEST_PATIENT_1, database.select(Patient.class, TEST_PATIENT_1_ID));
  }
}