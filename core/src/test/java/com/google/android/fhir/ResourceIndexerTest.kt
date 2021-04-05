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

package com.google.android.fhir

import android.os.Build
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.index.ResourceIndexer
import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.PositionIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
import com.google.android.fhir.resource.TestingUtils
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Invoice
import org.hl7.fhir.r4.model.Location
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Unit tests for {@link ResourceIndexerImpl}. */
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
  fun index_location_shouldIndexPosition() {
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

  private companion object {
    // See: https://www.hl7.org/fhir/valueset-currencies.html
    const val FHIR_CURRENCY_SYSTEM = "urn:iso:std:iso:4217"
  }
}
