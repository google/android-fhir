package com.google.fhirengine.impl;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.google.fhir.r4.core.AdministrativeGenderCode;
import com.google.fhir.r4.core.Id;
import com.google.fhir.r4.core.Patient;
import com.google.fhirengine.FhirEngine;
import com.google.fhirengine.ResourceAlreadyExistsException;
import com.google.fhirengine.ResourceNotFoundException;
import com.google.fhirengine.db.Database;
import com.google.fhirengine.db.impl.DatabaseImpl;
import com.google.fhirengine.db.impl.DatabaseModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/** Unit tests for {@link DatabaseImpl}. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class FhirEngineImplTest {
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

  private FhirEngine fhirEngine;

  @Singleton
  @Component(modules = {DatabaseModule.class})
  public interface TestComponent {

    Database getDatabase();

    @Component.Builder
    interface Builder {
      @BindsInstance
      Builder context(Context context);

      TestComponent build();
    }
  }

  @Before
  public void setUp() throws Exception {
    TestComponent a = DaggerFhirEngineImplTest_TestComponent.builder()
        .context(ApplicationProvider.getApplicationContext()).build();
    fhirEngine = new FhirEngineImpl(a.getDatabase());
    fhirEngine.save(TEST_PATIENT_1);
  }

  @Test
  public void save_shouldSaveResource() throws Exception {
    fhirEngine.save(TEST_PATIENT_2);
    assertEquals(TEST_PATIENT_2, fhirEngine.load(Patient.class, TEST_PATIENT_2_ID));
  }

  @Test
  public void save_existingResource_shouldThrowResourceAlreadyExistsException() throws Exception {
    ResourceAlreadyExistsException resourceAlreadyExistsInDbException =
        assertThrows(ResourceAlreadyExistsException.class,
            () -> fhirEngine.save(TEST_PATIENT_1));
    assertEquals(
        "Resource with type " + Patient.class.getName() + " and id " + TEST_PATIENT_1_ID +
            " already exists!",
        resourceAlreadyExistsInDbException.getMessage());
  }

  @Test
  public void load_nonexistentResource_shouldThrowResourceNotFondException() throws Exception {
    ResourceNotFoundException resourceNotFoundInDbException =
        assertThrows(ResourceNotFoundException.class, () ->
            fhirEngine.load(Patient.class, "nonexistent_patient"));
    assertEquals(
        "Resource not found with type com.google.fhir.r4.core.Patient and id nonexistent_patient!",
        resourceNotFoundInDbException.getMessage());
  }

  @Test
  public void load_shouldReturnResource() throws Exception {
    assertEquals(TEST_PATIENT_1, fhirEngine.load(Patient.class, TEST_PATIENT_1_ID));
  }
}