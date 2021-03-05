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

package com.google.android.fhir.index

import android.os.Build
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
import com.google.android.fhir.resource.TestingUtils
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.ChargeItem
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Invoice
import org.hl7.fhir.r4.model.MolecularSequence
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Substance
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Unit tests for {@link ResourceIndexerImpl}. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ResourceIndexerTest {
    private lateinit var qtyTestSubstance: Substance
    private lateinit var qtyTestInvoice: Invoice
    private lateinit var uriTestQuestionnaire: Questionnaire
    private lateinit var dateTestPatient: Patient
    private lateinit var lastUpdatedTestPatient: Patient
    private lateinit var numberTestChargeItem: ChargeItem
    private lateinit var numberTestMolecularSequence: MolecularSequence

    @Before
    fun setUp() {
        val testingUtils = TestingUtils(FhirContext.forR4().newJsonParser())
        // TODO: Improve sample data reading. Current approach has a downside of failing all tests if
        // one file name is mistyped.
        qtyTestSubstance =
            testingUtils.readFromFile(Substance::class.java, "/quantity_test_substance.json")
        qtyTestInvoice =
            testingUtils.readFromFile(Invoice::class.java, "/quantity_test_invoice.json")
        uriTestQuestionnaire =
            testingUtils.readFromFile(Questionnaire::class.java, "/uri_test_questionnaire.json")
        dateTestPatient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
        lastUpdatedTestPatient =
            testingUtils.readFromFile(Patient::class.java, "/lastupdated_ts_test_patient.json")
        numberTestChargeItem =
            testingUtils.readFromFile(ChargeItem::class.java, "/number_test_charge_item.json")
        numberTestMolecularSequence =
            testingUtils.readFromFile(MolecularSequence::class.java,
                "/number_test_molecular_sequence.json")
    }

    @Test
    fun index_patient_shouldIndexGivenName() {
        val resourceIndices = ResourceIndexer.index(TEST_PATIENT_1)
        assertThat(resourceIndices.stringIndices)
            .contains(
                StringIndex("given", "Patient.name.given", TEST_PATIENT_1_GIVEN_NAME_1)
            )
    }

    @Test
    fun index_patient_shouldIndexManagingOrganization() {
        val resourceIndices = ResourceIndexer.index(TEST_PATIENT_1)
        assertThat(resourceIndices.referenceIndices)
            .contains(
                ReferenceIndex("organization", "Patient.managingOrganization", TEST_PATIENT_1_ORG)
            )
    }

    @Test
    fun index_observation_shouldIndexSubject() {
        val resourceIndices = ResourceIndexer.index(TEST_OBSERVATION_1)
        assertThat(resourceIndices.referenceIndices)
            .contains(
                ReferenceIndex("subject", "Observation.subject", "Patient/" + TEST_PATIENT_1_ID)
            )
    }

    @Test
    fun index_observation_shouldIndexCode() {
        val resourceIndices = ResourceIndexer.index(TEST_OBSERVATION_1)
        assertThat(resourceIndices.tokenIndices)
            .contains(
                TokenIndex("code", "Observation.code", TEST_CODE_SYSTEM_1, TEST_CODE_VALUE_1)
            )
    }

    @Test
    fun index_patient_nullGivenName_shouldNotIndexGivenName() {
        val resourceIndices = ResourceIndexer.index(TEST_PATIENT_NULL_FIELDS)
        assertThat(
            resourceIndices.stringIndices.any { stringIndex ->
                stringIndex.path.equals("Patient.name.given")
            }
        ).isFalse()
        assertThat(
            resourceIndices.stringIndices.any { stringIndex ->
                stringIndex.name.equals("given")
            }
        ).isFalse()
    }

    @Test
    fun index_patient_nullOrganisation_shouldNotIndexOrganisation() {
        val resourceIndices = ResourceIndexer.index(TEST_PATIENT_NULL_FIELDS)
        assertThat(
            resourceIndices.referenceIndices.any { referenceIndex ->
                referenceIndex.path.equals("Patient.managingOrganization")
            }
        ).isFalse()
        assertThat(
            resourceIndices.referenceIndices.any { referenceIndex ->
                referenceIndex.name.equals("organization")
            }
        )
    }

    @Test
    fun index_patient_emptyGivenName_shouldNotIndexGivenName() {
        val resourceIndices = ResourceIndexer.index(TEST_PATIENT_NULL_FIELDS)
        assertThat(
            resourceIndices.stringIndices.any { stringIndex ->
                stringIndex.path.equals("Patient.name.given")
            }
        ).isFalse()
        assertThat(
            resourceIndices.stringIndices.any { stringIndex -> stringIndex.name.equals("given") }
        )
    }

    @Test
    fun index_patient_emptyOrganisation_shouldNotIndexOrganisation() {
        val resourceIndices = ResourceIndexer.index(TEST_PATIENT_EMPTY_FIELDS)
        assertThat(
            resourceIndices.referenceIndices.any { referenceIndex ->
                referenceIndex.path.equals("Patient.managingOrganization")
            }
        ).isFalse()
        assertThat(
            resourceIndices.referenceIndices.any { referenceIndex ->
                referenceIndex.name.equals("organization")
            }
        ).isFalse()
    }

    @Test
    fun index_observation_nullCode_shouldNotIndexCode() {
        val resourceIndices = ResourceIndexer.index(TEST_OBSERVATION_NULL_CODE)
        assertThat(
            resourceIndices.stringIndices.any { stringIndex ->
                stringIndex.path.equals("Observation.code")
            }
        ).isFalse()
        assertThat(
            resourceIndices.stringIndices.any { stringIndex ->
                stringIndex.name.equals("code")
            }
        ).isFalse()
    }

    @Test
    fun index_observation_emptyCode_shouldNotIndexCode() {
        val resourceIndices = ResourceIndexer.index(TEST_OBSERVATION_EMPTY_CODE)
        assertThat(
            resourceIndices.stringIndices.any { stringIndex ->
                stringIndex.path.equals("Observation.code")
            }
        ).isFalse()
        assertThat(
            resourceIndices.stringIndices.any { stringIndex ->
                stringIndex.name.equals("code")
            }
        ).isFalse()
    }

    @Test
    fun index_invoice_shouldIndexMoneyQuantity() {
        val resourceIndices = ResourceIndexer.index(qtyTestInvoice)
        assertThat(resourceIndices.quantityIndices)
            .containsAtLeast(
                // Search parameter names flatten camel case so "totalGross" becomes "totalgross"
                QuantityIndex(
                    "totalgross",
                    "Invoice.totalGross",
                    FHIR_CURRENCY_SYSTEM,
                    "EUR",
                    BigDecimal("48")
                ),
                QuantityIndex(
                    "totalnet",
                    "Invoice.totalNet",
                    FHIR_CURRENCY_SYSTEM,
                    "EUR",
                    BigDecimal("40.22")
                )
            )
    }

    @Test
    fun index_substance_shouldIndexQuantityQuantity() {
        val resourceIndices = ResourceIndexer.index(qtyTestSubstance)
        assertThat(resourceIndices.quantityIndices)
            .contains(
                QuantityIndex(
                    "quantity",
                    "Substance.instance.quantity",
                    "http://unitsofmeasure.org",
                    "mL",
                    BigDecimal("100")
                )
            )
    }

    @Test
    fun index_questionnaire_shouldIndexUri() {
        val resourceIndices = ResourceIndexer.index(uriTestQuestionnaire)
        assertThat(resourceIndices.uriIndices)
            .contains(
                UriIndex("url", "Questionnaire.url", "http://hl7.org/fhir/Questionnaire/3141")
            )
    }

    @Test
    fun index_patient_birthDate_shouldIndexBirthDate() {
        val resourceIndices = ResourceIndexer.index(dateTestPatient)
        val birthDateElement = dateTestPatient.getBirthDateElement()
        assertThat(resourceIndices.dateIndices)
            .contains(
                DateIndex(
                    "birthdate",
                    "Patient.birthDate",
                    birthDateElement.getValue().getTime(),
                    birthDateElement.getValue().getTime(),
                    birthDateElement.getPrecision()
                )
            )
    }

    @Test
    fun index_patient_lastUpdated_shouldIndexLastUpdated() {
        val resourceIndices = ResourceIndexer.index(lastUpdatedTestPatient)
        val lastUpdatedElement = lastUpdatedTestPatient.getMeta().getLastUpdatedElement()
        assertThat(resourceIndices.dateIndices)
            .contains(
                DateIndex(
                    "lastUpdated",
                    "Patient.meta.lastUpdated",
                    lastUpdatedElement.getValue().getTime(),
                    lastUpdatedElement.getValue().getTime(),
                    lastUpdatedElement.getPrecision()
                )
            )
    }

    @Test
    fun index_chargeItem_shouldIndexFactorOverride() {
        val resourceIndices = ResourceIndexer.index(numberTestChargeItem)
        assertThat(resourceIndices.numberIndices)
            .contains(
                NumberIndex("factor-override", "ChargeItem.factorOverride", BigDecimal("0.8"))
            )
    }

    @Test
    fun index_molecularSequence_shouldIndexWindowAndVariant() {
        val resourceIndices = ResourceIndexer.index(numberTestMolecularSequence)
        assertThat(resourceIndices.numberIndices)
            .containsAtLeast(
                NumberIndex(
                    "window-end",
                    "MolecularSequence.referenceSeq.windowEnd",
                    BigDecimal("22125510")
                ),
                NumberIndex(
                    "window-start",
                    "MolecularSequence.referenceSeq.windowStart",
                    BigDecimal("22125500")
                ),
                NumberIndex(
                    "variant-end", "MolecularSequence.variant.end", BigDecimal("22125504")
                ),
                NumberIndex(
                    "variant-start", "MolecularSequence.variant.start", BigDecimal("22125503")
                )
            )
    }

    // TODO: improve the tests further.

    private companion object {
        const val TEST_CODE_SYSTEM_1 = "http://openmrs.org/concepts"
        const val TEST_CODE_VALUE_1 = "1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"

        val TEST_PATIENT_NULL_ID = Patient()

        init {
            TEST_PATIENT_NULL_ID.id = null
        }

        // TEST_PATIENT_1 loosely based on https://www.hl7.org/fhir/patient-example-b.json.html
        const val TEST_PATIENT_1_ID = "Patient/pat2"
        const val TEST_PATIENT_1_IDENTIFIER_SYSTEM = "urn:oid:0.1.2.3.4.5.6.7"
        const val TEST_PATIENT_1_IDENTIFIER_VALUE = "123456"
        const val TEST_PATIENT_1_IDENTIFIER_TYPE_CODING_CODE = "MR"
        const val TEST_PATIENT_1_IDENTIFIER_TYPE_CODING_SYSTEM =
            "http://terminology.hl7.org/CodeSystem/v2-0203"
        const val TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_CODE = "A"
        const val TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_SYSTEM =
            "http://terminology.hl7.org/CodeSystem/v2-0001"
        const val TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_DISPLAY = "Ambiguous"
        const val TEST_PATIENT_1_EMAIL = "donald_duck@disney.com"
        const val TEST_PATIENT_1_PHONE = "+1-877-764-2539"
        const val TEST_PATIENT_1_GIVEN_NAME_1 = "Donald"
        const val TEST_PATIENT_1_GIVEN_NAME_2 = "D"
        const val TEST_PATIENT_1_FAMILY = "Duck"
        const val TEST_PATIENT_1_ORG = "Organization/1"

        val TEST_PATIENT_1 = Patient()

        init {
            TEST_PATIENT_1.id = TEST_PATIENT_1_ID
            TEST_PATIENT_1.addIdentifier(
                Identifier()
                    .setUse(Identifier.IdentifierUse.USUAL)
                    .setType(
                        CodeableConcept()
                            .addCoding(
                                Coding()
                                    .setCode(TEST_PATIENT_1_IDENTIFIER_TYPE_CODING_CODE)
                                    .setSystem(TEST_PATIENT_1_IDENTIFIER_TYPE_CODING_SYSTEM)))
                    .setSystem(TEST_PATIENT_1_IDENTIFIER_SYSTEM)
                    .setValue(TEST_PATIENT_1_IDENTIFIER_VALUE))
            TEST_PATIENT_1.active = true
            TEST_PATIENT_1.addName(
                HumanName()
                    .addGiven(TEST_PATIENT_1_GIVEN_NAME_1)
                    .addGiven(TEST_PATIENT_1_GIVEN_NAME_2)
                    .setFamily(TEST_PATIENT_1_FAMILY))
            TEST_PATIENT_1.gender = Enumerations.AdministrativeGender.OTHER
            TEST_PATIENT_1
                .getGenderElement()
                .addExtension(
                    Extension()
                        .setValue(
                            CodeableConcept()
                                .addCoding(
                                    Coding()
                                        .setCode(TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_CODE)
                                        .setSystem(TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_SYSTEM)
                                        .setDisplay(
                                            TEST_PATIENT_1_GENDER_EXT_VALUE_CODING_DISPLAY))))
            TEST_PATIENT_1.addTelecom(
                ContactPoint()
                    .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                    .setValue(TEST_PATIENT_1_EMAIL))
            TEST_PATIENT_1.addTelecom(
                ContactPoint()
                    .setSystem(ContactPoint.ContactPointSystem.PHONE)
                    .setValue(TEST_PATIENT_1_PHONE))
            TEST_PATIENT_1.managingOrganization = Reference().setReference(TEST_PATIENT_1_ORG)
        }

        val TEST_PATIENT_NULL_FIELDS = Patient()

        init {
            TEST_PATIENT_NULL_FIELDS.id = "non_null_id"
            TEST_PATIENT_NULL_FIELDS.addName(HumanName().addGiven(null))
            TEST_PATIENT_NULL_FIELDS.managingOrganization = Reference().setReference(null)
        }

        val TEST_PATIENT_EMPTY_FIELDS = Patient()

        init {
            TEST_PATIENT_EMPTY_FIELDS.id = "anonymous_patient"
            TEST_PATIENT_EMPTY_FIELDS.addName(HumanName().addGiven(""))
            TEST_PATIENT_EMPTY_FIELDS.managingOrganization = Reference().setReference("")
        }

        const val TEST_OBSERVATION_1_ID = "test_observation_1"
        val TEST_OBSERVATION_1 = Observation()

        init {
            TEST_OBSERVATION_1.id = TEST_OBSERVATION_1_ID
            TEST_OBSERVATION_1.subject = Reference().setReference("Patient/" + TEST_PATIENT_1_ID)
            TEST_OBSERVATION_1.code = CodeableConcept().addCoding(
                Coding().setSystem(TEST_CODE_SYSTEM_1).setCode(TEST_CODE_VALUE_1))
        }

        val TEST_OBSERVATION_NULL_CODE = Observation()

        init {
            TEST_OBSERVATION_NULL_CODE.id = "non_null_id"
            TEST_OBSERVATION_NULL_CODE.code =
                CodeableConcept().addCoding(Coding().setSystem(null).setCode(null))
        }

        val TEST_OBSERVATION_EMPTY_CODE = Observation()

        init {
            TEST_OBSERVATION_EMPTY_CODE.id = "non_empty_id"
            TEST_OBSERVATION_EMPTY_CODE.code =
                CodeableConcept().addCoding(Coding().setSystem("").setCode(""))
        }

        // See: https://www.hl7.org/fhir/valueset-currencies.html
        const val FHIR_CURRENCY_SYSTEM = "urn:iso:std:iso:4217"
    }
}
