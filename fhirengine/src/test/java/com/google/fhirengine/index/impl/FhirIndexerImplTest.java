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
import com.google.fhirengine.index.QuantityIndex;
import com.google.fhirengine.index.ReferenceIndex;
import com.google.fhirengine.index.ResourceIndices;
import com.google.fhirengine.index.StringIndex;
import com.google.fhirengine.index.UriIndex;
import java.math.BigDecimal;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
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

  public static final String QTY_TEST_SUBSTANCE_STR =
      "{\n"
          + "  \"resourceType\": \"Substance\",\n"
          + "  \"id\": \"f204\",\n"
          + "  \"text\": {\n"
          + "    \"status\": \"generated\",\n"
          + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f204</p><p><b>identifier</b>: 15970</p><p><b>category</b>: Chemical <span>(Details : {http://terminology.hl7.org/CodeSystem/substance-category code 'chemical' = 'Chemical', given as 'Chemical'})</span></p><p><b>code</b>: Silver nitrate 20% solution (product) <span>(Details : {SNOMED CT code '333346007' = 'Silver nitrate 20% solution', given as 'Silver nitrate 20% solution (product)'})</span></p><p><b>description</b>: Solution for silver nitrate stain</p><h3>Instances</h3><table><tr><td>-</td><td><b>Identifier</b></td><td><b>Expiry</b></td><td><b>Quantity</b></td></tr><tr><td>*</td><td>AB94687</td><td>01/01/2018</td><td>100 mL<span> (Details: UCUM code mL = 'mL')</span></td></tr></table></div>\"\n"
          + "  },\n"
          + "  \"identifier\": [\n"
          + "    {\n"
          + "      \"system\": \"http://acme.org/identifiers/substances\",\n"
          + "      \"value\": \"15970\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"category\": [\n"
          + "    {\n"
          + "      \"coding\": [\n"
          + "        {\n"
          + "          \"system\": \"http://terminology.hl7.org/CodeSystem/substance-category\",\n"
          + "          \"code\": \"chemical\",\n"
          + "          \"display\": \"Chemical\"\n"
          + "        }\n"
          + "      ]\n"
          + "    }\n"
          + "  ],\n"
          + "  \"code\": {\n"
          + "    \"coding\": [\n"
          + "      {\n"
          + "        \"system\": \"http://snomed.info/sct\",\n"
          + "        \"code\": \"333346007\",\n"
          + "        \"display\": \"Silver nitrate 20% solution (product)\"\n"
          + "      }\n"
          + "    ]\n"
          + "  },\n"
          + "  \"description\": \"Solution for silver nitrate stain\",\n"
          + "  \"instance\": [\n"
          + "    {\n"
          + "      \"identifier\": {\n"
          + "        \"system\": \"http://acme.org/identifiers/substances/lot\",\n"
          + "        \"value\": \"AB94687\"\n"
          + "      },\n"
          + "      \"expiry\": \"2018-01-01\",\n"
          + "      \"quantity\": {\n"
          + "        \"value\": 100,\n"
          + "        \"unit\": \"mL\",\n"
          + "        \"system\": \"http://unitsofmeasure.org\",\n"
          + "        \"code\": \"mL\"\n"
          + "      }\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  public static final String QTY_TEST_INVOICE =
      "{\n"
          + "  \"resourceType\": \"Invoice\",\n"
          + "  \"id\": \"example\",\n"
          + "  \"text\": {\n"
          + "    \"status\": \"generated\",\n"
          + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">Example of Invoice</div>\"\n"
          + "  },\n"
          + "  \"identifier\": [\n"
          + "    {\n"
          + "      \"system\": \"http://myHospital.org/Invoices\",\n"
          + "      \"value\": \"654321\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"status\": \"issued\",\n"
          + "  \"subject\": {\n"
          + "    \"reference\": \"Patient/example\"\n"
          + "  },\n"
          + "  \"date\": \"2017-01-25T08:00:00+01:00\",\n"
          + "  \"participant\": [\n"
          + "    {\n"
          + "      \"role\": {\n"
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
          + "    }\n"
          + "  ],\n"
          + "  \"issuer\": {\n"
          + "    \"identifier\": {\n"
          + "      \"system\": \"http://myhospital/NamingSystem/departments\",\n"
          + "      \"value\": \"CARD_INTERMEDIATE_CARE\"\n"
          + "    }\n"
          + "  },\n"
          + "  \"account\": {\n"
          + "    \"reference\": \"Account/example\"\n"
          + "  },\n"
          + "  \"totalNet\": {\n"
          + "    \"value\": 40.22,\n"
          + "    \"currency\": \"EUR\"\n"
          + "  },\n"
          + "  \"totalGross\": {\n"
          + "    \"value\": 48,\n"
          + "    \"currency\": \"EUR\"\n"
          + "  }\n"
          + "}";

  public static final String URI_TEST_QUESTIONNAIRE =
      "{\n"
          + "  \"resourceType\": \"Questionnaire\",\n"
          + "  \"id\": \"3141\",\n"
          + "  \"text\": {\n"
          + "    \"status\": \"generated\",\n"
          + "    \"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">\\n      <pre>\\n            1.Comorbidity?\\n              1.1 Cardial Comorbidity\\n                1.1.1 Angina?\\n                1.1.2 MI?\\n              1.2 Vascular Comorbidity?\\n              ...\\n            Histopathology\\n              Abdominal\\n                pT category?\\n              ...\\n          </pre>\\n    </div>\"\n"
          + "  },\n"
          + "  \"url\": \"http://hl7.org/fhir/Questionnaire/3141\",\n"
          + "  \"title\": \"Cancer Quality Forum Questionnaire 2012\",\n"
          + "  \"status\": \"draft\",\n"
          + "  \"subjectType\": [\n"
          + "    \"Patient\"\n"
          + "  ],\n"
          + "  \"date\": \"2012-01\",\n"
          + "  \"item\": [\n"
          + "    {\n"
          + "      \"linkId\": \"2\",\n"
          + "      \"code\": [\n"
          + "        {\n"
          + "          \"system\": \"http://example.org/system/code/sections\",\n"
          + "          \"code\": \"HISTOPATHOLOGY\"\n"
          + "        }\n"
          + "      ],\n"
          + "      \"type\": \"group\",\n"
          + "      \"item\": [\n"
          + "        {\n"
          + "          \"linkId\": \"2.1\",\n"
          + "          \"code\": [\n"
          + "            {\n"
          + "              \"system\": \"http://example.org/system/code/sections\",\n"
          + "              \"code\": \"ABDOMINAL\"\n"
          + "            }\n"
          + "          ],\n"
          + "          \"type\": \"group\",\n"
          + "          \"item\": [\n"
          + "            {\n"
          + "              \"linkId\": \"2.1.2\",\n"
          + "              \"code\": [\n"
          + "                {\n"
          + "                  \"system\": \"http://example.org/system/code/questions\",\n"
          + "                  \"code\": \"STADPT\",\n"
          + "                  \"display\": \"pT category\"\n"
          + "                }\n"
          + "              ],\n"
          + "              \"type\": \"choice\"\n"
          + "            }\n"
          + "          ]\n"
          + "        }\n"
          + "      ]\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  // See: https://www.hl7.org/fhir/valueset-currencies.html
  private static final String FHIR_CURRENCY_SYSTEM = "urn:iso:std:iso:4217";

  private Substance qtyTestSubstance;
  private Invoice qtyTestInvoice;
  private Questionnaire uriTestQuestionnaire;

  @Before
  public void setUp() throws Exception {
    IParser iParser = FhirContext.forR4().newJsonParser();
    qtyTestSubstance = iParser.parseResource(Substance.class, QTY_TEST_SUBSTANCE_STR);
    qtyTestInvoice = iParser.parseResource(Invoice.class, QTY_TEST_INVOICE);
    uriTestQuestionnaire = iParser.parseResource(Questionnaire.class, URI_TEST_QUESTIONNAIRE);
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
  public void index_invoice_shouldIndexMoneyQuantity() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(qtyTestInvoice);
    Truth.assertThat(resourceIndices.getQuantityIndices())
        .containsAtLeast(
            // Search parameter names flatten camel case so "totalGross" becomes "totalgross"
            new QuantityIndex(
                "totalgross",
                "Invoice.totalGross",
                FHIR_CURRENCY_SYSTEM,
                "EUR",
                new BigDecimal("48")),
            new QuantityIndex(
                "totalnet",
                "Invoice.totalNet",
                FHIR_CURRENCY_SYSTEM,
                "EUR",
                new BigDecimal("40.22")));
  }

  @Test
  public void index_substance_shouldIndexQuantityQuantity() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(qtyTestSubstance);
    Truth.assertThat(resourceIndices.getQuantityIndices())
        .contains(
            new QuantityIndex(
                "quantity",
                "Substance.instance.quantity",
                "http://unitsofmeasure.org",
                "mL",
                new BigDecimal("100")));
  }

  @Test
  public void index_questionnaire_shouldIndexUri() throws Exception {
    ResourceIndices resourceIndices = fhirIndexer.index(uriTestQuestionnaire);
    Truth.assertThat(resourceIndices.getUriIndices())
        .contains(
            new UriIndex("url", "Questionnaire.url", "http://hl7.org/fhir/Questionnaire/3141"));
  }

  /* TODO: add tests for
   *     * QuantityIndex: Range, Ratio
   */
}
