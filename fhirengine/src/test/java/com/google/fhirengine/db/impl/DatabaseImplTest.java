package com.google.fhirengine.db.impl;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.google.fhirengine.db.ResourceAlreadyExistsInDbException;
import com.google.fhirengine.db.ResourceNotFoundInDbException;
import com.google.fhirengine.impl.FhirEngineImplTest;
import com.google.fhirengine.resource.ResourceModule;
import com.google.fhirengine.resource.TestingUtils;

import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.uhn.fhir.parser.IParser;
import dagger.BindsInstance;
import dagger.Component;

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

  @Inject
  IParser iParser;
  @Inject
  TestingUtils testingUtils;
  @Inject
  DatabaseImpl database;

  @Singleton
  @Component(modules = {DatabaseModule.class, ResourceModule.class})
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
        .withContext(ApplicationProvider.getApplicationContext()).build().inject(this);
    database.insert(TEST_PATIENT_1);
  }

  @Test
  public void insert_shouldInsertResource() throws Exception {
    database.insert(TEST_PATIENT_2);
    testingUtils
        .assertResourceEquals(TEST_PATIENT_2, database.select(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void insert_existingResource_shouldThrowResourceAlreadyExistsException() throws Exception {
    ResourceAlreadyExistsInDbException resourceAlreadyExistsInDbException =
        assertThrows(ResourceAlreadyExistsInDbException.class,
            () -> database.insert(TEST_PATIENT_1));
    assertEquals(
        "Resource with type " + TEST_PATIENT_1.getResourceType().name() + " and id " +
            TEST_PATIENT_1_ID +
            " already exists!",
        resourceAlreadyExistsInDbException.getMessage());
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
    ResourceNotFoundInDbException resourceNotFoundInDbException =
        assertThrows(ResourceNotFoundInDbException.class, () ->
            database.select(Patient.class, "nonexistent_patient"));
    assertEquals("Resource not found with type " + ResourceType.Patient.name() +
            " and id nonexistent_patient!",
        resourceNotFoundInDbException.getMessage());
  }

  @Test
  public void select_shouldReturnResource() throws Exception {
    testingUtils
        .assertResourceEquals(TEST_PATIENT_1, database.select(Patient.class, TEST_PATIENT_1_ID));
  }
}
