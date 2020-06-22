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
import ca.uhn.fhir.parser.IParser;
import com.google.common.truth.Truth;
import com.google.fhirengine.index.CodeIndex;
import com.google.fhirengine.index.DateIndex;
import com.google.fhirengine.index.NumberIndex;
import com.google.fhirengine.index.ReferenceIndex;
import com.google.fhirengine.index.ResourceIndices;
import com.google.fhirengine.index.StringIndex;
import java.math.BigDecimal;
import org.hl7.fhir.r4.model.ChargeItem;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.MolecularSequence;
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

  public static final String TEST_PATIENT_LASTUPDATED_INDEX =
      "{\n"
          + "  \"resourceType\": \"Patient\",\n"
          + "  \"id\": \"mom\",\n"
          + "  \"meta\": {\n"
          + "    \"lastUpdated\": \"2012-05-29T23:45:32Z\"\n"
          + "  },\n"
          + "  \"text\": {\n"
          + "    \"status\": \"generated\",\n"
          + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: mom</p><p><b>meta</b>: </p><p><b>identifier</b>: Social Security number = 444222222</p><p><b>active</b>: true</p><p><b>name</b>: Eve Everywoman (OFFICIAL)</p><p><b>telecom</b>: ph: 555-555-2003(WORK)</p><p><b>gender</b>: female</p><p><b>birthDate</b>: 31/05/1973</p><p><b>address</b>: 2222 Home Street (HOME)</p><p><b>managingOrganization</b>: <a>Organization/hl7</a></p><h3>Links</h3><table><tr><td>-</td><td><b>Other</b></td><td><b>Type</b></td></tr><tr><td>*</td><td><a>RelatedPerson/newborn-mom</a></td><td>seealso</td></tr></table></div>\"\n"
          + "  },\n"
          + "  \"identifier\": [\n"
          + "    {\n"
          + "      \"type\": {\n"
          + "        \"coding\": [\n"
          + "          {\n"
          + "            \"system\": \"http://terminology.hl7.org/CodeSystem/v2-0203\",\n"
          + "            \"code\": \"SS\"\n"
          + "          }\n"
          + "        ]\n"
          + "      },\n"
          + "      \"system\": \"http://hl7.org/fhir/sid/us-ssn\",\n"
          + "      \"value\": \"444222222\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"active\": true,\n"
          + "  \"name\": [\n"
          + "    {\n"
          + "      \"use\": \"official\",\n"
          + "      \"family\": \"Everywoman\",\n"
          + "      \"given\": [\n"
          + "        \"Eve\"\n"
          + "      ]\n"
          + "    }\n"
          + "  ],\n"
          + "  \"telecom\": [\n"
          + "    {\n"
          + "      \"system\": \"phone\",\n"
          + "      \"value\": \"555-555-2003\",\n"
          + "      \"use\": \"work\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"gender\": \"female\",\n"
          + "  \"birthDate\": \"1973-05-31\",\n"
          + "  \"address\": [\n"
          + "    {\n"
          + "      \"use\": \"home\",\n"
          + "      \"line\": [\n"
          + "        \"2222 Home Street\"\n"
          + "      ]\n"
          + "    }\n"
          + "  ],\n"
          + "  \"managingOrganization\": {\n"
          + "    \"reference\": \"Organization/hl7\"\n"
          + "  },\n"
          + "  \"link\": [\n"
          + "    {\n"
          + "      \"other\": {\n"
          + "        \"reference\": \"RelatedPerson/newborn-mom\"\n"
          + "      },\n"
          + "      \"type\": \"seealso\"\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  public static final String TEST_CHARGE_ITEM_NUMBER_INDEX =
      "{\n"
          + "  \"resourceType\": \"ChargeItem\",\n"
          + "  \"id\": \"example\",\n"
          + "  \"text\": {\n"
          + "    \"status\": \"generated\",\n"
          + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">Example of ChargeItem Usage in Context of the German EBM Billing code system</div>\"\n"
          + "  },\n"
          + "  \"identifier\": [\n"
          + "    {\n"
          + "      \"system\": \"http://myHospital.org/ChargeItems\",\n"
          + "      \"value\": \"654321\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"definitionUri\": [\n"
          + "    \"http://www.kbv.de/tools/ebm/html/01520_2904360860826220813632.html\"\n"
          + "  ],\n"
          + "  \"status\": \"billable\",\n"
          + "  \"code\": {\n"
          + "    \"coding\": [\n"
          + "      {\n"
          + "        \"code\": \"01510\",\n"
          + "        \"display\": \"Zusatzpauschale für Beobachtung nach diagnostischer Koronarangiografie\"\n"
          + "      }\n"
          + "    ]\n"
          + "  },\n"
          + "  \"subject\": {\n"
          + "    \"reference\": \"Patient/example\"\n"
          + "  },\n"
          + "  \"context\": {\n"
          + "    \"reference\": \"Encounter/example\"\n"
          + "  },\n"
          + "  \"occurrencePeriod\": {\n"
          + "    \"start\": \"2017-01-25T08:00:00+01:00\",\n"
          + "    \"end\": \"2017-01-25T12:35:00+01:00\"\n"
          + "  },\n"
          + "  \"performer\": [\n"
          + "    {\n"
          + "      \"function\": {\n"
          + "        \"coding\": [\n"
          + "          {\n"
          + "            \"system\": \"http://snomed.info/sct\",\n"
          + "            \"code\": \"17561000\",\n"
          + "            \"display\": \"Cardiologist\"\n"
          + "          }\n"
          + "        ]\n"
          + "      },\n"
          + "      \"actor\": {\n"
          + "        \"reference\": \"Practitioner/example\"\n"
          + "      }\n"
          + "    },\n"
          + "    {\n"
          + "      \"function\": {\n"
          + "        \"coding\": [\n"
          + "          {\n"
          + "            \"system\": \"http://snomed.info/sct\",\n"
          + "            \"code\": \"224542009\",\n"
          + "            \"display\": \"Coronary Care Nurse\"\n"
          + "          }\n"
          + "        ]\n"
          + "      },\n"
          + "      \"actor\": {\n"
          + "        \"reference\": \"Practitioner/example\"\n"
          + "      }\n"
          + "    }\n"
          + "  ],\n"
          + "  \"performingOrganization\": {\n"
          + "    \"identifier\": {\n"
          + "      \"system\": \"http://myhospital/NamingSystem/departments\",\n"
          + "      \"value\": \"CARD_INTERMEDIATE_CARE\"\n"
          + "    }\n"
          + "  },\n"
          + "  \"requestingOrganization\": {\n"
          + "    \"identifier\": {\n"
          + "      \"system\": \"http://myhospital/NamingSystem/departments\",\n"
          + "      \"value\": \"CARD_U1\"\n"
          + "    }\n"
          + "  },\n"
          + "  \"quantity\": {\n"
          + "    \"value\": 1\n"
          + "  },\n"
          + "  \"factorOverride\": 0.8,\n"
          + "  \"priceOverride\": {\n"
          + "    \"value\": 40,\n"
          + "    \"currency\": \"EUR\"\n"
          + "  },\n"
          + "  \"overrideReason\": \"Patient is Cardiologist's golf buddy, so he gets a 20% discount!\",\n"
          + "  \"enterer\": {\n"
          + "    \"reference\": \"Practitioner/example\"\n"
          + "  },\n"
          + "  \"enteredDate\": \"2017-01-25T23:55:04+01:00\",\n"
          + "  \"reason\": [\n"
          + "    {\n"
          + "      \"coding\": [\n"
          + "        {\n"
          + "          \"system\": \"http://hl7.org/fhir/sid/icd-10\",\n"
          + "          \"code\": \"123456\",\n"
          + "          \"display\": \"DIAG-1\"\n"
          + "        }\n"
          + "      ]\n"
          + "    }\n"
          + "  ],\n"
          + "  \"service\": [\n"
          + "    {\n"
          + "      \"reference\": \"Procedure/example\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"account\": [\n"
          + "    {\n"
          + "      \"reference\": \"Account/example\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"note\": [\n"
          + "    {\n"
          + "      \"authorReference\": {\n"
          + "        \"reference\": \"Practitioner/example\"\n"
          + "      },\n"
          + "      \"time\": \"2017-01-25T23:55:04+01:00\",\n"
          + "      \"text\": \"The code is only applicable for periods longer than 4h\"\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  public static final String TEST_MOLECULAR_SEQUENCE_NUMBER_INDEX =
      "{\n"
          + "  \"resourceType\": \"MolecularSequence\",\n"
          + "  \"id\": \"example\",\n"
          + "  \"text\": {\n"
          + "    \"status\": \"generated\",\n"
          + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: example</p><p><b>type</b>: dna</p><p><b>coordinateSystem</b>: 0</p><p><b>patient</b>: <a>Patient/example</a></p><h3>ReferenceSeqs</h3><table><tr><td>-</td><td><b>ReferenceSeqId</b></td><td><b>Strand</b></td><td><b>WindowStart</b></td><td><b>WindowEnd</b></td></tr><tr><td>*</td><td>NC_000009.11 <span>(Details : {http://www.ncbi.nlm.nih.gov/nuccore code 'NC_000009.11' = 'NC_000009.11)</span></td><td>watson</td><td>22125500</td><td>22125510</td></tr></table><h3>Variants</h3><table><tr><td>-</td><td><b>Start</b></td><td><b>End</b></td><td><b>ObservedAllele</b></td><td><b>ReferenceAllele</b></td></tr><tr><td>*</td><td>22125503</td><td>22125504</td><td>C</td><td>G</td></tr></table><h3>Repositories</h3><table><tr><td>-</td><td><b>Type</b></td><td><b>Url</b></td><td><b>Name</b></td><td><b>VariantsetId</b></td></tr><tr><td>*</td><td>openapi</td><td><a>http://grch37.rest.ensembl.org/ga4gh/variants/3:rs1333049?content-type=application/json</a></td><td>GA4GH API</td><td>3:rs1333049</td></tr></table></div>\"\n"
          + "  },\n"
          + "  \"type\": \"dna\",\n"
          + "  \"coordinateSystem\": 0,\n"
          + "  \"patient\": {\n"
          + "    \"reference\": \"Patient/example\"\n"
          + "  },\n"
          + "  \"referenceSeq\": {\n"
          + "    \"referenceSeqId\": {\n"
          + "      \"coding\": [\n"
          + "        {\n"
          + "          \"system\": \"http://www.ncbi.nlm.nih.gov/nuccore\",\n"
          + "          \"code\": \"NC_000009.11\"\n"
          + "        }\n"
          + "      ]\n"
          + "    },\n"
          + "    \"strand\": \"watson\",\n"
          + "    \"windowStart\": 22125500,\n"
          + "    \"windowEnd\": 22125510\n"
          + "  },\n"
          + "  \"variant\": [\n"
          + "    {\n"
          + "      \"start\": 22125503,\n"
          + "      \"end\": 22125504,\n"
          + "      \"observedAllele\": \"C\",\n"
          + "      \"referenceAllele\": \"G\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"repository\": [\n"
          + "    {\n"
          + "      \"type\": \"openapi\",\n"
          + "      \"url\": \"http://grch37.rest.ensembl.org/ga4gh/variants/3:rs1333049?content-type=application/json\",\n"
          + "      \"name\": \"GA4GH API\",\n"
          + "      \"variantsetId\": \"3:rs1333049\"\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  private static Patient dateTestPatient;
  private static Patient lastUpdatedTestPatient;
  private static ChargeItem numberTestChargeItem;
  private static MolecularSequence numberTestMolecularSequence;

  @Before
  public void setUp() throws Exception {
    IParser iParser = FhirContext.forR4().newJsonParser();
    dateTestPatient = iParser.parseResource(Patient.class, TEST_PATIENT_DATE_INDEX);
    lastUpdatedTestPatient = iParser.parseResource(Patient.class, TEST_PATIENT_LASTUPDATED_INDEX);
    numberTestChargeItem = iParser.parseResource(ChargeItem.class, TEST_CHARGE_ITEM_NUMBER_INDEX);
    numberTestMolecularSequence =
        iParser.parseResource(MolecularSequence.class, TEST_MOLECULAR_SEQUENCE_NUMBER_INDEX);
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
        .contains(
            new DateIndex(
                "birthdate",
                "Patient.birthDate",
                birthDateElement.getValue().getTime(),
                birthDateElement.getValue().getTime(),
                birthDateElement.getPrecision()));
  }

  @Test
  public void index_patient_lastUpdated_shouldIndexLastUpdated() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(lastUpdatedTestPatient);
    InstantType lastUpdatedElement = lastUpdatedTestPatient.getMeta().getLastUpdatedElement();
    Truth.assertThat(resourceIndices.getDateIndices())
        .contains(
            new DateIndex(
                "lastUpdated",
                "Patient.meta.lastUpdated",
                lastUpdatedElement.getValue().getTime(),
                lastUpdatedElement.getValue().getTime(),
                lastUpdatedElement.getPrecision()));
  }

  @Test
  public void index_chargeItem_shouldIndexFactorOverride() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(numberTestChargeItem);
    Truth.assertThat(resourceIndices.getNumberIndices())
        .contains(
            new NumberIndex("factor-override", "ChargeItem.factorOverride", new BigDecimal("0.8")));
  }

  @Test
  public void index_chargeItem_shouldIndex() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(numberTestMolecularSequence);
    Truth.assertThat(resourceIndices.getNumberIndices())
        .containsAtLeast(
            new NumberIndex(
                "window-end",
                "MolecularSequence.referenceSeq.windowEnd",
                new BigDecimal("22125510")),
            new NumberIndex(
                "window-start",
                "MolecularSequence.referenceSeq.windowStart",
                new BigDecimal("22125500")),
            new NumberIndex(
                "variant-end", "MolecularSequence.variant.end", new BigDecimal("22125504")),
            new NumberIndex(
                "variant-start", "MolecularSequence.variant.start", new BigDecimal("22125503")));
  }

  // TODO: improve the tests further.
}
