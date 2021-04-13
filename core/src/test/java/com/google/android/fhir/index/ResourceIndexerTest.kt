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
import com.google.android.fhir.index.entities.PositionIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
import com.google.android.fhir.logicalId
import com.google.android.fhir.resource.TestingUtils
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Device
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Invoice
import org.hl7.fhir.r4.model.Location
import org.hl7.fhir.r4.model.Money
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RiskAssessment
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Substance
import org.hl7.fhir.r4.model.UriType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Integration tests for {@link ResourceIndexerImpl}. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ResourceIndexerTest {

  private lateinit var testInvoice: Invoice
  private lateinit var testQuestionnaire: Questionnaire
  private lateinit var testPatient: Patient
  private lateinit var testLocation: Location
  @Before
  fun setUp() {
    val testingUtils = TestingUtils(FhirContext.forR4().newJsonParser())
    // TODO: Improve sample data reading. Current approach has a downside of failing all tests if
    // one file name is mistyped.
    testInvoice = testingUtils.readFromFile(Invoice::class.java, "/quantity_test_invoice.json")
    testQuestionnaire =
      testingUtils.readFromFile(Questionnaire::class.java, "/uri_test_questionnaire.json")
    testPatient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    testLocation = testingUtils.readFromFile(Location::class.java, "/location-example-hl7hq.json")
  }
  /** Integration tests for ResourceIndexer. */
  @Test
  fun index_invoice() {
    val resourceIndices = ResourceIndexer.index(testInvoice)

    assertThat(resourceIndices.resourceId).isEqualTo(testInvoice.logicalId)
    assertThat(resourceIndices.resourceType).isEqualTo(testInvoice.resourceType)

    assertThat(resourceIndices.quantityIndices)
      .containsExactly(
        // Search parameter names flatten camel case so "totalGross" becomes "totalgross"
        QuantityIndex(
          "totalgross",
          "Invoice.totalGross",
          FHIR_CURRENCY_SYSTEM,
          testInvoice.totalGross.currency,
          testInvoice.totalGross.value
        ),
        QuantityIndex(
          "totalnet",
          "Invoice.totalNet",
          FHIR_CURRENCY_SYSTEM,
          testInvoice.totalNet.currency,
          testInvoice.totalNet.value
        )
      )

    assertThat(resourceIndices.numberIndices).isEmpty()

    assertThat(resourceIndices.tokenIndices)
      .containsExactly(
        TokenIndex(
          "identifier",
          "Invoice.identifier",
          testInvoice.identifierFirstRep.system,
          testInvoice.identifierFirstRep.value
        ),
        TokenIndex(
          "participant-role",
          "Invoice.participant.role",
          testInvoice.participantFirstRep.role.codingFirstRep.system,
          testInvoice.participantFirstRep.role.codingFirstRep.code
        )
      )

    assertThat(resourceIndices.uriIndices).isEmpty()

    assertThat(resourceIndices.dateIndices).isEmpty()

    assertThat(resourceIndices.referenceIndices)
      .containsExactly(
        ReferenceIndex("subject", "Invoice.subject", testInvoice.subject.reference),
        ReferenceIndex(
          "participant",
          "Invoice.participant.actor",
          testInvoice.participantFirstRep.actor.reference
        ),
        ReferenceIndex("account", "Invoice.account", testInvoice.account.reference)
      )

    assertThat(resourceIndices.stringIndices).isEmpty()

    assertThat(resourceIndices.positionIndices).isEmpty()
  }

  @Test
  fun index_questionnaire() {
    val resourceIndices = ResourceIndexer.index(testQuestionnaire)

    assertThat(resourceIndices.resourceType).isEqualTo(testQuestionnaire.resourceType)
    assertThat(resourceIndices.resourceId).isEqualTo(testQuestionnaire.logicalId)

    assertThat(resourceIndices.uriIndices)
      .containsExactly(
        UriIndex("url", "Questionnaire.url", "http://hl7.org/fhir/Questionnaire/3141")
      )

    assertThat(resourceIndices.numberIndices).isEmpty()

    assertThat(resourceIndices.tokenIndices).isEmpty()

    assertThat(resourceIndices.dateIndices).isEmpty()

    assertThat(resourceIndices.referenceIndices).isEmpty()

    assertThat(resourceIndices.stringIndices)
      .containsExactly(StringIndex("title", "Questionnaire.title", testQuestionnaire.title))

    assertThat(resourceIndices.positionIndices).isEmpty()

    assertThat(resourceIndices.quantityIndices).isEmpty()
  }

  @Test
  fun index_patient() {
    val resourceIndices = ResourceIndexer.index(testPatient)

    assertThat(resourceIndices.resourceType).isEqualTo(testPatient.resourceType)
    assertThat(resourceIndices.resourceId).isEqualTo(testPatient.logicalId)

    assertThat(resourceIndices.dateIndices)
      .containsExactly(
        DateIndex(
          "birthdate",
          "Patient.birthDate",
          testPatient.birthDateElement.value.time,
          testPatient.birthDateElement.value.time,
          testPatient.birthDateElement.precision
        )
      )

    assertThat(resourceIndices.numberIndices).isEmpty()

    assertThat(resourceIndices.tokenIndices)
      .containsExactly(
        TokenIndex(
          "identifier",
          "Patient.identifier",
          testPatient.identifierFirstRep.system,
          testPatient.identifierFirstRep.value
        ),
        TokenIndex(
          "deceased",
          "Patient.deceased.exists() and Patient.deceased != false",
          null,
          testPatient.deceased.primitiveValue()
        ),
        TokenIndex("active", "Patient.active", null, testPatient.active.toString()),
        TokenIndex(
          "language",
          "Patient.communication.language",
          testPatient.communicationFirstRep.language.codingFirstRep.system,
          testPatient.communicationFirstRep.language.codingFirstRep.code
        )
      )

    assertThat(resourceIndices.uriIndices).isEmpty()

    assertThat(resourceIndices.quantityIndices).isEmpty()

    assertThat(resourceIndices.referenceIndices)
      .containsExactly(
        ReferenceIndex(
          "organization",
          "Patient.managingOrganization",
          testPatient.managingOrganization.reference
        )
      )

    assertThat(resourceIndices.stringIndices)
      .containsExactly(
        StringIndex("given", "Patient.name.given", testPatient.nameFirstRep.givenAsSingleString),
        StringIndex("address", "Patient.address", testPatient.addressFirstRep.toString()),
        StringIndex(
          "address-postalcode",
          "Patient.address.postalCode",
          testPatient.addressFirstRep.postalCode
        ),
        StringIndex(
          "address-country",
          "Patient.address.country",
          testPatient.addressFirstRep.country
        ),
        StringIndex("phonetic", "Patient.name", testPatient.nameFirstRep.toString()),
        StringIndex("name", "Patient.name", testPatient.nameFirstRep.toString()),
        StringIndex("family", "Patient.name.family", testPatient.nameFirstRep.family),
        StringIndex("address-city", "Patient.address.city", testPatient.addressFirstRep.city)
      )

    assertThat(resourceIndices.positionIndices).isEmpty()
  }

  @Test
  fun index_location() {
    val resourceIndices = ResourceIndexer.index(testLocation)
    assertThat(resourceIndices.resourceType).isEqualTo(testLocation.resourceType)
    assertThat(resourceIndices.resourceId).isEqualTo(testLocation.logicalId)

    assertThat(resourceIndices.positionIndices).containsExactly(PositionIndex(-83.69471, 42.2565))

    assertThat(resourceIndices.numberIndices).isEmpty()

    assertThat(resourceIndices.tokenIndices)
      .containsExactly(
        TokenIndex(
          "type",
          "Location.type",
          testLocation.typeFirstRep.codingFirstRep.system,
          testLocation.typeFirstRep.codingFirstRep.code
        )
      )

    assertThat(resourceIndices.uriIndices).isEmpty()

    assertThat(resourceIndices.dateIndices).isEmpty()

    assertThat(resourceIndices.referenceIndices).isEmpty()

    assertThat(resourceIndices.quantityIndices).isEmpty()

    assertThat(resourceIndices.stringIndices)
      .containsExactly(
        StringIndex("address", "Location.address", testLocation.address.toString()),
        StringIndex("address-state", "Location.address.state", testLocation.address.state),
        StringIndex(
          "address-postalcode",
          "Location.address.postalCode",
          testLocation.address.postalCode
        ),
        StringIndex("name", "Location.name | Location.alias", testLocation.name),
        StringIndex("address-country", "Location.address.country", testLocation.address.country),
        StringIndex("address-city", "Location.address.city", testLocation.address.city)
      )
  }

  /** Unit tests for resource indexer */
  @Test
  fun index_device_shouldIndexURI() {
    val device =
      Device().apply {
        id = "someID"
        url = "www.someDomainName.someDomain"
      }
    val resourceIndices = ResourceIndexer.index(device)
    assertThat(resourceIndices.uriIndices)
      .contains(UriIndex("url", "Device.url", "www.someDomainName.someDomain"))
  }

  @Test
  fun index_device_emptyURI_shouldNotIndexURI() {
    val device =
      Device().apply {
        id = "someID"
        url = ""
      }
    val resourceIndices = ResourceIndexer.index(device)
    assertThat(resourceIndices.uriIndices.any { index -> index.name == "url" }).isFalse()
  }

  @Test
  fun index_device_nullURI_shouldNotIndexURI() {
    val device =
      Device().apply {
        id = "someID"
        url = null
      }
    val resourceIndices = ResourceIndexer.index(device)
    assertThat(resourceIndices.uriIndices.any { index -> index.name == "url" }).isFalse()
  }

  @Test
  fun index_invoice_shouldIndexMoney() {
    val testInvoice =
      Invoice().apply {
        id = "some_Non_NULL_ID"
        totalNet = Money().setCurrency("EU").setValue(300)
      }

    val resourceIndices = ResourceIndexer.index(testInvoice)
    assertThat(resourceIndices.quantityIndices)
      .contains(
        QuantityIndex(
          "totalnet",
          "Invoice.totalNet",
          FHIR_CURRENCY_SYSTEM,
          "EU",
          BigDecimal.valueOf(300)
        )
      )
  }

  @Test
  fun index_substance_shouldIndexQuantity() {
    val substance =
      Substance().apply {
        id = "someID"
        instance.add(Substance.SubstanceInstanceComponent().setQuantity(Quantity(1000)))
      }

    val resourceIndices = ResourceIndexer.index(substance)

    assertThat(resourceIndices.quantityIndices)
      .contains(
        QuantityIndex("quantity", "Substance.instance.quantity", "", "", BigDecimal.valueOf(1000))
      )
  }

  @Test
  fun index_Invoice_shouldIndexIdentifier() {
    val invoice =
      Invoice().apply {
        id = "someid"
        identifier =
          mutableListOf(
            Identifier()
              .setSystemElement(UriType("someSystem"))
              .setValueElement(StringType("someValue"))
          )
      }
    val resourceIndices = ResourceIndexer.index(invoice)
    assertThat(resourceIndices.tokenIndices)
      .contains(TokenIndex("identifier", "Invoice.identifier", "someSystem", "someValue"))
  }

  @Test
  fun index_riskAssessment_ShouldIndexProbability() {
    val riskAssessment =
      RiskAssessment().apply {
        id = "someID"
        prediction =
          mutableListOf(
            RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(0.9))
          )
      }

    val resourceIndices = ResourceIndexer.index(riskAssessment)

    assertThat(resourceIndices.numberIndices)
      .contains(
        NumberIndex("probability", "RiskAssessment.prediction.probability", BigDecimal.valueOf(0.9))
      )
  }

  @Test
  fun index_Patient_ShouldIndexDeceased() {
    val patient =
      Patient().apply {
        id = "someID"
        deceased = BooleanType(true)
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(resourceIndices.tokenIndices)
      .contains(
        TokenIndex(
          "deceased",
          "Patient.deceased.exists() and Patient.deceased != false",
          null,
          "true"
        )
      )
  }

  @Test
  fun index_location_shouldIndexPosition() {
    val location =
      Location().apply {
        id = "someID"
        position = Location.LocationPositionComponent(DecimalType(90.0), DecimalType(90.0))
      }
    val resourceIndices = ResourceIndexer.index(location)
    assertThat(resourceIndices.positionIndices).contains(PositionIndex(90.0, 90.0))
  }

  @Test
  fun index_patient_shouldIndexGivenName() {

    val patient =
      Patient().apply {
        id = "someID"
        addName(HumanName().addGiven("some name"))
      }

    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(resourceIndices.stringIndices)
      .contains(StringIndex("given", "Patient.name.given", "some name"))
  }

  @Test
  fun index_patient_shouldIndexManagingOrganization() {
    val patient =
      Patient().apply {
        id = "someID"
        managingOrganization = Reference().setReference("some organization")
      }
    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(resourceIndices.referenceIndices)
      .contains(ReferenceIndex("organization", "Patient.managingOrganization", "some organization"))
  }

  @Test
  fun index_observation_shouldIndexSubject() {
    val observation =
      Observation().apply {
        id = "someID"
        subject = Reference().setReference("Patient/someID")
      }
    val resourceIndices = ResourceIndexer.index(observation)
    assertThat(resourceIndices.referenceIndices)
      .contains(ReferenceIndex("subject", "Observation.subject", "Patient/someID"))
  }

  @Test
  fun index_observation_shouldIndexCode() {
    val observation =
      Observation().apply {
        id = "someID"
        code =
          CodeableConcept()
            .addCoding(
              Coding()
                .setCode("1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                .setSystem("http://openmrs.org/concepts")
            )
      }
    val resourceIndices = ResourceIndexer.index(observation)
    assertThat(resourceIndices.tokenIndices)
      .contains(
        TokenIndex(
          "code",
          "Observation.code",
          "http://openmrs.org/concepts",
          "1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        )
      )
  }

  @Test
  fun index_patient_nullGivenName_shouldNotIndexGivenName() {
    val patient =
      Patient().apply {
        id = "someID"
        addName(HumanName().addGiven(null))
      }

    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(
        resourceIndices.stringIndices.any { stringIndex ->
          stringIndex.path.equals("Patient.name.given")
        }
      )
      .isFalse()

    assertThat(
        resourceIndices.stringIndices.any { stringIndex -> stringIndex.name.equals("given") }
      )
      .isFalse()
  }

  @Test
  fun index_patient_nullOrganisation_shouldNotIndexOrganisation() {
    val patient =
      Patient().apply {
        id = "someID"
        managingOrganization = null
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(
        resourceIndices.referenceIndices.any { referenceIndex ->
          referenceIndex.path.equals("Patient.managingOrganization")
        }
      )
      .isFalse()

    assertThat(
      resourceIndices.referenceIndices.any { referenceIndex ->
        referenceIndex.name.equals("organization")
      }
    )
  }

  @Test
  fun index_patient_emptyGivenName_shouldNotIndexGivenName() {
    val patient =
      Patient().apply {
        id = "someID"
        addName(HumanName().addGiven(""))
      }

    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(
        resourceIndices.stringIndices.any { stringIndex ->
          stringIndex.path.equals("Patient.name.given")
        }
      )
      .isFalse()
    assertThat(
      resourceIndices.stringIndices.any { stringIndex -> stringIndex.name.equals("given") }
    )
  }

  @Test
  fun index_patient_emptyOrganisation_shouldNotIndexOrganisation() {
    val patient =
      Patient().apply {
        id = "someID"
        managingOrganization = Reference().setReference("")
      }
    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(
        resourceIndices.referenceIndices.any { referenceIndex ->
          referenceIndex.path.equals("Patient.managingOrganization")
        }
      )
      .isFalse()
    assertThat(
        resourceIndices.referenceIndices.any { referenceIndex ->
          referenceIndex.name.equals("organization")
        }
      )
      .isFalse()
  }

  @Test
  fun index_observation_nullCode_shouldNotIndexCode() {
    val observation =
      Observation().apply {
        id = "someID"
        code = null
      }
    val resourceIndices = ResourceIndexer.index(observation)
    assertThat(
        resourceIndices.stringIndices.any { stringIndex ->
          stringIndex.path.equals("Observation.code")
        }
      )
      .isFalse()
    assertThat(resourceIndices.stringIndices.any { stringIndex -> stringIndex.name.equals("code") })
      .isFalse()
  }

  @Test
  fun index_observation_emptyCode_shouldNotIndexCode() {
    val observation =
      Observation().apply {
        id = "someID"
        code = CodeableConcept().addCoding(null)
      }
    val resourceIndices = ResourceIndexer.index(observation)
    assertThat(
        resourceIndices.stringIndices.any { stringIndex ->
          stringIndex.path.equals("Observation.code")
        }
      )
      .isFalse()
    assertThat(resourceIndices.stringIndices.any { stringIndex -> stringIndex.name.equals("code") })
      .isFalse()
  }

  private companion object {
    // See: https://www.hl7.org/fhir/valueset-currencies.html
    const val FHIR_CURRENCY_SYSTEM = "urn:iso:std:iso:4217"
  }
}
