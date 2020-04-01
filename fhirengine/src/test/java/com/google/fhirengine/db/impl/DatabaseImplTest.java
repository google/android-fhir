package com.google.fhirengine.db.impl;

import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.google.fhirengine.db.ResourceAlreadyExistsException;
import com.google.fhirengine.db.ResourceNotFoundException;

import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/** Unit tests for {@link DatabaseImpl}. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
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

  private final IParser iParser = FhirContext.forR4().newJsonParser();

  private DatabaseImpl database;

  @Before
  public void setUp() throws Exception {
    database = new DatabaseImpl(ApplicationProvider.getApplicationContext(), iParser);
    database.insert(TEST_PATIENT_1);
  }

  @Test
  public void insert_shouldInsertResource() throws Exception {
    database.insert(TEST_PATIENT_2);
    assertResourceEquals(TEST_PATIENT_2, database.select(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void insert_existingResource_shouldThrowResourceAlreadyExistsException()
      throws Exception {
    ResourceAlreadyExistsException resourceAlreadyExistsException =
        assertThrows(ResourceAlreadyExistsException.class, () -> database.insert(TEST_PATIENT_1));
    assertEquals(
        "Resource with type " + TEST_PATIENT_1.getResourceType().name() + " and id " +
            TEST_PATIENT_1_ID +
            " already exists!",
        resourceAlreadyExistsException.getMessage());
  }

  @Test
  public void select_invalidResourceType_shouldThrowIllegalArgumentException() throws Exception {
    IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
        () -> database.select(Resource.class, "resource_id"));
    assertEquals("Cannot resolve resource type for " + Resource.class.getName(),
        illegalArgumentException.getMessage());
  }

  @Test
  public void select_nonexistentResource_shouldThrowResourceNotFondException() throws Exception {
    ResourceNotFoundException resourceNotFoundException =
        assertThrows(ResourceNotFoundException.class, () ->
            database.select(Patient.class, "nonexistent_patient"));
    assertEquals("Resource not found with type " + ResourceType.Patient.name() +
            " and id nonexistent_patient!",
        resourceNotFoundException.getMessage());
  }

  @Test
  public void select_shouldReturnResource() throws Exception {
    assertResourceEquals(TEST_PATIENT_1, database.select(Patient.class, TEST_PATIENT_1_ID));
  }

  /** Asserts that the {@code expected} and the {@code actual} FHIR resources are equal. */
  private void assertResourceEquals(Resource expected, Resource actual) {
    assertEquals(iParser.encodeResourceToString(expected),
        iParser.encodeResourceToString(actual));
  }
}
