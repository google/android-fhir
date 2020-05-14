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

package com.google.fhirengine.index.impl;

import android.os.Build;
import com.google.common.truth.Truth;
import com.google.fhirengine.index.ReferenceIndex;
import com.google.fhirengine.index.ResourceIndices;
import com.google.fhirengine.index.StringIndex;
import com.google.fhirengine.resource.ResourceModule;
import dagger.Component;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** Unit tests for {@link FhirIndexerImpl}. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class FhirIndexerImplTest {
  private static final String TEST_CODE_SYSTEM_1 = "http://openmrs.org/concepts";
  private static final String TEST_CODE_VALUE_1 = "1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

  private static final Patient TEST_PATIENT_NULL_ID;

  // TEST_PATIENT_1 loosely based on https://www.hl7.org/fhir/patient-example-b.json.html
  private static final String TEST_PATIENT_1_ID = "Patient/pat2";
  private static final String TEST_PATIENT_1_IDENTIFIER_SYSTEM = "urn:oid:0.1.2.3.4.5.6.7";
  private static final String TEST_PATIENT_1_IDENTIFIER_VALUE = "123456";
  private static final String TEST_PATIENT_1_IDENTIFIER_TYPE_CODING_CODE = "MR";
  private static final String TEST_PATIENT_1_IDENTIFIER_TYPE_CODING_SYSTEM =
      "http://terminology.hl7.org/CodeSystem/v2-0203";
  private static final String TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_CODE = "A";
  private static final String TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_SYSTEM =
      "http://terminology.hl7.org/CodeSystem/v2-0001";
  private static final String TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_DISPLAY = "Ambiguous";
  private static final String TEST_PATIENT_1_EMAIL = "donald_duck@disney.com";
  private static final String TEST_PATIENT_1_PHONE = "+1-877-764-2539";
  private static final String TEST_PATIENT_1_GIVEN_NAME_1 = "Donald";
  private static final String TEST_PATIENT_1_GIVEN_NAME_2 = "D";
  private static final String TEST_PATIENT_1_FAMILY = "Duck";
  private static final String TEST_PATIENT_1_ORG = "Organization/1";
  private static final Patient TEST_PATIENT_1;

  private static final Patient TEST_PATIENT_NULL_FIELDS;
  private static final Patient TEST_PATIENT_EMPTY_FIELDS;

  static {
    TEST_PATIENT_1 = new Patient();
    TEST_PATIENT_1.setId(TEST_PATIENT_1_ID);
    TEST_PATIENT_1.addIdentifier(
        new Identifier()
            .setUse(Identifier.IdentifierUse.USUAL)
            .setType(
                new CodeableConcept()
                    .addCoding(
                        new Coding()
                            .setCode(TEST_PATIENT_1_IDENTIFIER_TYPE_CODING_CODE)
                            .setSystem(TEST_PATIENT_1_IDENTIFIER_TYPE_CODING_SYSTEM)))
            .setSystem(TEST_PATIENT_1_IDENTIFIER_SYSTEM)
            .setValue(TEST_PATIENT_1_IDENTIFIER_VALUE));
    TEST_PATIENT_1.setActive(true);
    TEST_PATIENT_1.addName(
        new HumanName()
            .addGiven(TEST_PATIENT_1_GIVEN_NAME_1)
            .addGiven(TEST_PATIENT_1_GIVEN_NAME_2)
            .setFamily(TEST_PATIENT_1_FAMILY));
    TEST_PATIENT_1.setGender(Enumerations.AdministrativeGender.OTHER);
    TEST_PATIENT_1
        .getGenderElement()
        .addExtension(
            new Extension()
                .setValue(
                    new CodeableConcept()
                        .addCoding(
                            new Coding()
                                .setCode(TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_CODE)
                                .setSystem(TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_SYSTEM)
                                .setDisplay(TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_DISPLAY))));

    TEST_PATIENT_1.addTelecom(
        new ContactPoint()
            .setSystem(ContactPoint.ContactPointSystem.EMAIL)
            .setValue(TEST_PATIENT_1_EMAIL));

    TEST_PATIENT_1.addTelecom(
        new ContactPoint()
            .setSystem(ContactPoint.ContactPointSystem.PHONE)
            .setValue(TEST_PATIENT_1_PHONE));

    TEST_PATIENT_1.setManagingOrganization(new Reference().setReference(TEST_PATIENT_1_ORG));

    TEST_PATIENT_NULL_ID = new Patient();
    TEST_PATIENT_NULL_ID.setId((String) null);

    TEST_PATIENT_NULL_FIELDS = new Patient();
    TEST_PATIENT_NULL_FIELDS.setId("non_null_id");
    TEST_PATIENT_NULL_FIELDS.addName(new HumanName().addGiven(null));
    TEST_PATIENT_NULL_FIELDS.setManagingOrganization(new Reference().setReference(null));

    TEST_PATIENT_EMPTY_FIELDS = new Patient();
    TEST_PATIENT_EMPTY_FIELDS.setId("anonymous_patient");
    TEST_PATIENT_EMPTY_FIELDS.addName(new HumanName().addGiven(""));
    TEST_PATIENT_EMPTY_FIELDS.setManagingOrganization(new Reference().setReference(""));
  }

  private static final String TEST_OBSERVATION_1_ID = "test_observation_1";
  private static final Observation TEST_OBSERVATION_1;
  private static final Observation TEST_OBSERVATION_NULL_CODE;
  private static final Observation TEST_OBSERVATION_EMPTY_CODE;

  static {
    TEST_OBSERVATION_1 = new Observation();
    TEST_OBSERVATION_1.setId(TEST_OBSERVATION_1_ID);
    TEST_OBSERVATION_1.setSubject(new Reference().setReference("Patient/" + TEST_PATIENT_1_ID));
    TEST_OBSERVATION_1.setCode(
        new CodeableConcept()
            .addCoding(new Coding().setSystem(TEST_CODE_SYSTEM_1).setCode(TEST_CODE_VALUE_1)));

    TEST_OBSERVATION_NULL_CODE = new Observation();
    TEST_OBSERVATION_NULL_CODE.setId("non_null_id");
    TEST_OBSERVATION_NULL_CODE.setCode(
        new CodeableConcept().addCoding(new Coding().setSystem(null).setCode(null)));

    TEST_OBSERVATION_EMPTY_CODE = new Observation();
    TEST_OBSERVATION_EMPTY_CODE.setId("non_empty_id");
    TEST_OBSERVATION_EMPTY_CODE.setCode(
        new CodeableConcept().addCoding(new Coding().setSystem("").setCode("")));
  }

  @Inject FhirIndexerImpl fhirIndexer;

  @Singleton
  @Component(modules = {FhirIndexerModule.class, ResourceModule.class})
  public interface TestComponent {
    void inject(com.google.fhirengine.index.impl.FhirIndexerImplTest fhirIndexerImplTest);
  }

  @Before
  public void setUp() throws Exception {
    DaggerFhirIndexerImplTest_TestComponent.builder().build().inject(this);
  }

  @Test
  public void index_patient_shouldIndexGivenName() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_PATIENT_1);
    Truth.assertThat(resourceIndices.getStringIndices())
        .contains(new StringIndex("given", "Patient.name.given", TEST_PATIENT_1_GIVEN_NAME_1));
  }

  @Test
  public void index_patient_shouldIndexManagingOrganization() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_PATIENT_1);
    Truth.assertThat(resourceIndices.getReferenceIndices())
        .contains(
            new ReferenceIndex("organization", "Patient.managingOrganization", TEST_PATIENT_1_ORG));
  }

  @Test
  public void index_observation_shouldIndexSubject() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_OBSERVATION_1);
    Truth.assertThat(resourceIndices.getReferenceIndices())
        .contains(
            new ReferenceIndex("subject", "Observation.subject", "Patient/" + TEST_PATIENT_1_ID));
  }

  @Test
  public void index_observation_shouldIndexCode() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_OBSERVATION_1);
    Truth.assertThat(resourceIndices.getCodeIndices())
        .contains(new CodeIndex("code", "Observation.code", TEST_CODE_SYSTEM_1, TEST_CODE_VALUE_1));
  }

  @Test
  public void index_null_shouldThrowIllegalArgumentException() throws Exception {
    Assert.assertThrows(IllegalArgumentException.class, () -> fhirIndexer.index(null));
  }

  @Test
  public void index_patient_nullGivenName_shouldNotIndexGivenName() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_PATIENT_NULL_FIELDS);
    Truth.assertThat(
        resourceIndices.getStringIndices().stream()
            .noneMatch(stringIndex -> stringIndex.getPath().equals("Patient.name.given")));
    Truth.assertThat(
        resourceIndices.getStringIndices().stream()
            .noneMatch(stringIndex -> stringIndex.getName().equals("given")));
  }

  @Test
  public void index_patient_nullOrganisation_shouldNotIndexOrganisation() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_PATIENT_NULL_FIELDS);
    Truth.assertThat(
        resourceIndices.getReferenceIndices().stream()
            .noneMatch(
                referenceIndex -> referenceIndex.getPath().equals("Patient.managingOrganization")));
    Truth.assertThat(
        resourceIndices.getReferenceIndices().stream()
            .noneMatch(referenceIndex -> referenceIndex.getName().equals("organization")));
  }

  @Test
  public void index_patient_emptyGivenName_shouldNotIndexGivenName() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_PATIENT_NULL_FIELDS);
    Truth.assertThat(
        resourceIndices.getStringIndices().stream()
            .noneMatch(stringIndex -> stringIndex.getPath().equals("Patient.name.given")));
    Truth.assertThat(
        resourceIndices.getStringIndices().stream()
            .noneMatch(stringIndex -> stringIndex.getName().equals("given")));
  }

  @Test
  public void index_patient_emptyOrganisation_shouldNotIndexOrganisation() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_PATIENT_EMPTY_FIELDS);
    Truth.assertThat(
        resourceIndices.getReferenceIndices().stream()
            .noneMatch(
                referenceIndex -> referenceIndex.getPath().equals("Patient.managingOrganization")));
    Truth.assertThat(
        resourceIndices.getReferenceIndices().stream()
            .noneMatch(referenceIndex -> referenceIndex.getName().equals("organization")));
  }

  @Test
  public void index_observation_nullCode_shouldNotIndexCode() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_OBSERVATION_NULL_CODE);
    Truth.assertThat(
        resourceIndices.getStringIndices().stream()
            .noneMatch(stringIndex -> stringIndex.getPath().equals("Observation.code")));
    Truth.assertThat(
        resourceIndices.getStringIndices().stream()
            .noneMatch(stringIndex -> stringIndex.getName().equals("code")));
  }

  @Test
  public void index_observation_emptyCode_shouldNotIndexCode() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(TEST_OBSERVATION_EMPTY_CODE);
    Truth.assertThat(
        resourceIndices.getStringIndices().stream()
            .noneMatch(stringIndex -> stringIndex.getPath().equals("Observation.code")));
    Truth.assertThat(
        resourceIndices.getStringIndices().stream()
            .noneMatch(stringIndex -> stringIndex.getName().equals("code")));
  }

  // TODO: improve the tests further.
}
