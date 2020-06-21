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
import ca.uhn.fhir.context.FhirContext;
import com.google.common.truth.Truth;
import com.google.fhirengine.index.CodeIndex;
import com.google.fhirengine.index.DateIndex;
import com.google.fhirengine.index.ReferenceIndex;
import com.google.fhirengine.index.ResourceIndices;
import com.google.fhirengine.index.StringIndex;
import com.google.fhirengine.index.UriIndex;
import java.math.BigDecimal;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Invoice;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Substance;
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

  private FhirIndexerImpl fhirIndexer = new FhirIndexerImpl();

  private static final String TEST_PATIENT_DATE_INDEX =
      "{\n"
          + "  \"resourceType\": \"Patient\",\n"
          + "  \"id\": \"f001\",\n"
          + "  \"text\": {\n"
          + "    \"status\": \"generated\",\n"
          + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f001</p><p><b>identifier</b>: 738472983 (USUAL), ?? (USUAL)</p><p><b>active</b>: true</p><p><b>name</b>: Pieter van de Heuvel </p><p><b>telecom</b>: ph: 0648352638(MOBILE), p.heuvel@gmail.com(HOME)</p><p><b>gender</b>: male</p><p><b>birthDate</b>: 17/11/1944</p><p><b>deceased</b>: false</p><p><b>address</b>: Van Egmondkade 23 Amsterdam 1024 RJ NLD (HOME)</p><p><b>maritalStatus</b>: Getrouwd <span>(Details : {http://terminology.hl7.org/CodeSystem/v3-MaritalStatus code 'M' = 'Married', given as 'Married'})</span></p><p><b>multipleBirth</b>: true</p><h3>Contacts</h3><table><tr><td>-</td><td><b>Relationship</b></td><td><b>Name</b></td><td><b>Telecom</b></td></tr><tr><td>*</td><td>Emergency Contact <span>(Details : {http://terminology.hl7.org/CodeSystem/v2-0131 code 'C' = 'Emergency Contact)</span></td><td>Sarah Abels </td><td>ph: 0690383372(MOBILE)</td></tr></table><h3>Communications</h3><table><tr><td>-</td><td><b>Language</b></td><td><b>Preferred</b></td></tr><tr><td>*</td><td>Nederlands <span>(Details : {urn:ietf:bcp:47 code 'nl' = 'Dutch', given as 'Dutch'})</span></td><td>true</td></tr></table><p><b>managingOrganization</b>: <a>Burgers University Medical Centre</a></p></div>\"\n"
          + "  },\n"
          + "  \"identifier\": [\n"
          + "    {\n"
          + "      \"use\": \"usual\",\n"
          + "      \"system\": \"urn:oid:2.16.840.1.113883.2.4.6.3\",\n"
          + "      \"value\": \"738472983\"\n"
          + "    },\n"
          + "    {\n"
          + "      \"use\": \"usual\",\n"
          + "      \"system\": \"urn:oid:2.16.840.1.113883.2.4.6.3\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"active\": true,\n"
          + "  \"name\": [\n"
          + "    {\n"
          + "      \"use\": \"usual\",\n"
          + "      \"family\": \"van de Heuvel\",\n"
          + "      \"given\": [\n"
          + "        \"Pieter\"\n"
          + "      ],\n"
          + "      \"suffix\": [\n"
          + "        \"MSc\"\n"
          + "      ]\n"
          + "    }\n"
          + "  ],\n"
          + "  \"telecom\": [\n"
          + "    {\n"
          + "      \"system\": \"phone\",\n"
          + "      \"value\": \"0648352638\",\n"
          + "      \"use\": \"mobile\"\n"
          + "    },\n"
          + "    {\n"
          + "      \"system\": \"email\",\n"
          + "      \"value\": \"p.heuvel@gmail.com\",\n"
          + "      \"use\": \"home\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"gender\": \"male\",\n"
          + "  \"birthDate\": \"1944-11-17\",\n"
          + "  \"deceasedBoolean\": false,\n"
          + "  \"address\": [\n"
          + "    {\n"
          + "      \"use\": \"home\",\n"
          + "      \"line\": [\n"
          + "        \"Van Egmondkade 23\"\n"
          + "      ],\n"
          + "      \"city\": \"Amsterdam\",\n"
          + "      \"postalCode\": \"1024 RJ\",\n"
          + "      \"country\": \"NLD\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"maritalStatus\": {\n"
          + "    \"coding\": [\n"
          + "      {\n"
          + "        \"system\": \"http://terminology.hl7.org/CodeSystem/v3-MaritalStatus\",\n"
          + "        \"code\": \"M\",\n"
          + "        \"display\": \"Married\"\n"
          + "      }\n"
          + "    ],\n"
          + "    \"text\": \"Getrouwd\"\n"
          + "  },\n"
          + "  \"multipleBirthBoolean\": true,\n"
          + "  \"contact\": [\n"
          + "    {\n"
          + "      \"relationship\": [\n"
          + "        {\n"
          + "          \"coding\": [\n"
          + "            {\n"
          + "              \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0131\",\n"
          + "              \"code\": \"C\"\n"
          + "            }\n"
          + "          ]\n"
          + "        }\n"
          + "      ],\n"
          + "      \"name\": {\n"
          + "        \"use\": \"usual\",\n"
          + "        \"family\": \"Abels\",\n"
          + "        \"given\": [\n"
          + "          \"Sarah\"\n"
          + "        ]\n"
          + "      },\n"
          + "      \"telecom\": [\n"
          + "        {\n"
          + "          \"system\": \"phone\",\n"
          + "          \"value\": \"0690383372\",\n"
          + "          \"use\": \"mobile\"\n"
          + "        }\n"
          + "      ]\n"
          + "    }\n"
          + "  ],\n"
          + "  \"communication\": [\n"
          + "    {\n"
          + "      \"language\": {\n"
          + "        \"coding\": [\n"
          + "          {\n"
          + "            \"system\": \"urn:ietf:bcp:47\",\n"
          + "            \"code\": \"nl\",\n"
          + "            \"display\": \"Dutch\"\n"
          + "          }\n"
          + "        ],\n"
          + "        \"text\": \"Nederlands\"\n"
          + "      },\n"
          + "      \"preferred\": true\n"
          + "    }\n"
          + "  ],\n"
          + "  \"managingOrganization\": {\n"
          + "    \"reference\": \"Organization/f001\",\n"
          + "    \"display\": \"Burgers University Medical Centre\"\n"
          + "  }\n"
          + "}";

  private static Patient dateTestPatient;

  @Before
  public void setUp() throws Exception {
    dateTestPatient =
        FhirContext.forR4().newJsonParser().parseResource(Patient.class, TEST_PATIENT_DATE_INDEX);
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

  @Test
  public void index_patient_birthDate_shouldIndexBirthDate() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(dateTestPatient);
    DateType birthDateElement = dateTestPatient.getBirthDateElement();
    Truth.assertThat(resourceIndices.getDateIndices())
        .containsExactly(
            new DateIndex(
                "birthdate",
                "Patient.birthDate",
                birthDateElement.getValue().getTime(),
                birthDateElement.getValue().getTime(),
                birthDateElement.getPrecision()));
  }

  // TODO: improve the tests further.
}
