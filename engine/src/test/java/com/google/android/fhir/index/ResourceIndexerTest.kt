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
import com.google.android.fhir.epochDay
import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.DateTimeIndex
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
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Device
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.Invoice
import org.hl7.fhir.r4.model.Location
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.MolecularSequence
import org.hl7.fhir.r4.model.Money
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RiskAssessment
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Substance
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.UriType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Integration & Unit tests for {@link ResourceIndexerImpl}. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ResourceIndexerTest {

  /** Unit tests for resource indexer */
  @Test
  fun index_lastUpdated() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        meta = Meta().setLastUpdated(InstantType("2001-09-01T23:09:09.000+05:30").value)
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(resourceIndices.dateTimeIndices)
      .contains(
        DateTimeIndex(
          "_lastUpdated",
          "Patient.meta.lastUpdated",
          InstantType("2001-09-01T23:09:09.000+05:30").value.time,
          InstantType("2001-09-01T23:09:09.000+05:30").value.time
        )
      )
  }

  @Test
  fun index_number_integer() {
    val value = 22125510
    val molecularSequence =
      MolecularSequence().apply {
        id = "non-null-ID"
        referenceSeq.windowStart = value
      }

    val resourceIndices = ResourceIndexer.index(molecularSequence)

    assertThat(resourceIndices.numberIndices)
      .contains(
        NumberIndex(
          "window-start",
          "MolecularSequence.referenceSeq.windowStart",
          BigDecimal.valueOf(value.toLong())
        )
      )
  }

  @Test
  fun index_number_decimal() {
    val value = BigDecimal.valueOf(0.9)
    val riskAssessment =
      RiskAssessment().apply {
        id = "someID"
        prediction =
          mutableListOf(
            RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(value))
          )
      }

    val resourceIndices = ResourceIndexer.index(riskAssessment)

    assertThat(resourceIndices.numberIndices)
      .contains(NumberIndex("probability", "RiskAssessment.prediction.probability", value))
  }

  @Test
  fun index_number_null() {
    val value = null
    val molecularSequence =
      MolecularSequence().apply {
        id = "non-null-ID"
        referenceSeq = value
      }

    val resourceIndices = ResourceIndexer.index(molecularSequence)

    assertThat(resourceIndices.numberIndices.any { it.name == "window-start" }).isFalse()
    assertThat(
        resourceIndices.numberIndices.any {
          it.path == "MolecularSequence.referenceSeq.windowStart"
        }
      )
      .isFalse()
  }
  @Test
  fun index_date() {
    val date = DateType("2001-09-01")
    val patient =
      Patient().apply {
        id = "non-null ID"
        birthDate = date.value
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(resourceIndices.dateIndices)
      .contains(
        DateIndex(
          "birthdate",
          "Patient.birthDate",
          date.value.epochDay,
          date.precision.add(date.value, 1).epochDay - 1
        )
      )
  }

  @Test
  fun index_date_null() {
    val patient =
      Patient().apply {
        id = "non-null-id"
        birthDate = null
      }
    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(resourceIndices.dateIndices.any { it.name == "birthdate" }).isFalse()
    assertThat(resourceIndices.dateIndices.any { it.path == "Patient.birthDate" }).isFalse()
  }

  @Test
  fun index_dateTime_dateTime() {
    val dateTime = DateTimeType("2001-12-29T12:20:30+07:00")
    val observation =
      Observation().apply {
        id = "non-null ID"
        effective = dateTime
      }

    val resourceIndices = ResourceIndexer.index(observation)

    observation.effectiveDateTimeType
    assertThat(resourceIndices.dateTimeIndices)
      .contains(
        DateTimeIndex(
          "date",
          "Observation.effective",
          dateTime.value.time,
          dateTime.precision.add(dateTime.value, 1).time - 1
        )
      )
  }

  @Test
  fun index_dateTime_instant() {
    val instant = InstantType("2001-03-04T23:30:00.910+05:30")
    val observation =
      Observation().apply {
        id = "non-null ID"
        effective = instant
      }

    val resourceIndices = ResourceIndexer.index(observation)

    assertThat(resourceIndices.dateTimeIndices)
      .contains(
        DateTimeIndex("date", "Observation.effective", instant.value.time, instant.value.time)
      )
  }

  @Test
  fun index_dateTime_period() {
    val period =
      Period().apply {
        startElement = DateTimeType("2001-09-08T20:30:09+05:30")
        endElement = DateTimeType("2001-10-01T21:39:09+05:30")
      }
    val observation =
      Observation().apply {
        id = "non-null ID"
        effective = period
      }

    val resourceIndices = ResourceIndexer.index(observation)

    assertThat(resourceIndices.dateTimeIndices)
      .contains(
        DateTimeIndex(
          "date",
          "Observation.effective",
          period.start.time,
          period.endElement.precision.add(period.end, 1).time - 1
        )
      )
  }

  @Test
  fun index_dateTime_period_noStart() {
    val period =
      Period().apply {
        startElement = null
        endElement = DateTimeType("2001-10-01T21:39:09+05:30")
      }
    val observation =
      Observation().apply {
        id = "non-null ID"
        effective = period
      }

    val resourceIndices = ResourceIndexer.index(observation)

    assertThat(resourceIndices.dateTimeIndices)
      .contains(
        DateTimeIndex(
          "date",
          "Observation.effective",
          0,
          period.endElement.precision.add(period.end, 1).time - 1
        )
      )
  }

  @Test
  fun index_dateTime_period_noEnd() {
    val period = Period().apply { startElement = DateTimeType("2001-09-08T20:30:09+05:30") }
    val observation =
      Observation().apply {
        id = "non-null ID"
        effective = period
      }

    val resourceIndices = ResourceIndexer.index(observation)

    assertThat(resourceIndices.dateTimeIndices)
      .contains(DateTimeIndex("date", "Observation.effective", period.start.time, Long.MAX_VALUE))
  }
  @Test
  fun index_dateTime_timing() {
    val timing =
      Timing().apply {
        addEvent(DateTimeType("2001-11-05T21:53:10+09:00").value)
        addEvent(DateTimeType("2002-09-01T20:30:18+09:00").value)
        addEvent(DateTimeType("2003-10-24T18:30:40+09:00").value)
      }
    val observation =
      Observation().apply {
        id = "non-null ID"
        effective = timing
      }

    val resourceIndices = ResourceIndexer.index(observation)

    assertThat(resourceIndices.dateTimeIndices)
      .contains(
        DateTimeIndex(
          "date",
          "Observation.effective",
          timing.event.minOf { it.value.time },
          timing.event.maxOf { it.precision.add(it.value, 1).time } - 1
        )
      )
  }

  @Test
  fun index_dateTime_null() {
    val observation =
      Observation().apply {
        id = "non-null-id"
        effective = null
      }
    val resourceIndices = ResourceIndexer.index(observation)

    assertThat(resourceIndices.dateTimeIndices.any { it.name == "date" }).isFalse()
    assertThat(resourceIndices.dateTimeIndices.any { it.path == "Observation.effective" }).isFalse()
  }

  @Test
  fun index_string() {
    val nameString = "John"
    val patient =
      Patient().apply {
        id = "non-null-ID"
        addName(HumanName().addGiven(nameString))
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(resourceIndices.stringIndices)
      .contains(StringIndex("given", "Patient.name.given", nameString))
  }

  @Test
  fun index_string_null() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        addName(HumanName().addGiven(null))
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(
        resourceIndices.stringIndices.any { stringIndex ->
          stringIndex.path == "Patient.name.given"
        }
      )
      .isFalse()

    assertThat(resourceIndices.stringIndices.any { stringIndex -> stringIndex.name == "given" })
      .isFalse()
  }

  @Test
  fun index_string_empty() {
    val patient =
      Patient().apply {
        id = "non_null_ID"
        addName(HumanName().addGiven(""))
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(
        resourceIndices.stringIndices.any { stringIndex ->
          stringIndex.path == "Patient.name.given"
        }
      )
      .isFalse()
    assertThat(resourceIndices.stringIndices.any { stringIndex -> stringIndex.name == "given" })
      .isFalse()
  }

  @Test
  fun index_token_boolean() {
    val patient =
      Patient().apply {
        id = "non_null_ID"
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
  fun index_token_identifier() {
    val system = "someSystem"
    val value = "someValue"
    val invoice =
      Invoice().apply {
        id = "someid"
        identifier =
          mutableListOf(
            Identifier().setSystemElement(UriType(system)).setValueElement(StringType(value))
          )
      }

    val resourceIndices = ResourceIndexer.index(invoice)

    assertThat(resourceIndices.tokenIndices)
      .contains(TokenIndex("identifier", "Invoice.identifier", system, value))
  }

  @Test
  fun index_token_codableConcept() {
    val codeString = "1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    val systemString = "http://openmrs.org/concepts"
    val observation =
      Observation().apply {
        id = "non-null-ID"
        code = CodeableConcept().addCoding(Coding().setCode(codeString).setSystem(systemString))
      }

    val resourceIndices = ResourceIndexer.index(observation)

    assertThat(resourceIndices.tokenIndices)
      .contains(TokenIndex("code", "Observation.code", systemString, codeString))
  }

  @Test
  fun index_token_null() {
    val observation =
      Observation().apply {
        id = "non-null-ID"
        code = null
      }

    val resourceIndices = ResourceIndexer.index(observation)

    assertThat(
        resourceIndices.tokenIndices.any { tokenIndex -> tokenIndex.path == "Observation.code" }
      )
      .isFalse()
    assertThat(resourceIndices.tokenIndices.any { tokenIndex -> tokenIndex.name.equals("code") })
      .isFalse()
  }

  @Test
  fun index_token_empty() {
    val observation =
      Observation().apply {
        id = "someID"
        code = CodeableConcept().addCoding(Coding())
      }

    val resourceIndices = ResourceIndexer.index(observation)

    assertThat(
        resourceIndices.tokenIndices.any { tokenIndex -> tokenIndex.path == "Observation.code" }
      )
      .isFalse()
    assertThat(resourceIndices.tokenIndices.any { tokenIndex -> tokenIndex.name == "code" })
      .isFalse()
  }

  @Test
  fun index_reference() {
    val organizationString = "someOrganization"
    val patient =
      Patient().apply {
        id = "someID"
        managingOrganization = Reference().setReference(organizationString)
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(resourceIndices.referenceIndices)
      .contains(ReferenceIndex("organization", "Patient.managingOrganization", organizationString))
  }

  @Test
  fun index_reference_null() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        managingOrganization = null
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(
        resourceIndices.referenceIndices.any { referenceIndex ->
          referenceIndex.path == "Patient.managingOrganization"
        }
      )
      .isFalse()

    assertThat(
        resourceIndices.referenceIndices.any { referenceIndex ->
          referenceIndex.name == "organization"
        }
      )
      .isFalse()
  }

  @Test
  fun index_reference_empty() {
    val patient =
      Patient().apply {
        id = "someID"
        managingOrganization = Reference().setReference("")
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(
        resourceIndices.referenceIndices.any { referenceIndex ->
          referenceIndex.path == "Patient.managingOrganization"
        }
      )
      .isFalse()
    assertThat(
        resourceIndices.referenceIndices.any { referenceIndex ->
          referenceIndex.name == "organization"
        }
      )
      .isFalse()
  }

  @Test
  fun index_quantity_money() {
    val currency = "EU"
    val value = BigDecimal.valueOf(300)
    val testInvoice =
      Invoice().apply {
        id = "non_NULL_ID"
        totalNet = Money().setCurrency(currency).setValue(value)
      }

    val resourceIndices = ResourceIndexer.index(testInvoice)

    assertThat(resourceIndices.quantityIndices)
      .contains(
        QuantityIndex("totalnet", "Invoice.totalNet", FHIR_CURRENCY_SYSTEM, currency, value)
      )
  }

  @Test
  fun index_quantity_quantity() {
    val value = (100).toLong()
    val substance =
      Substance().apply {
        id = "non-null-ID"
        instance.add(Substance.SubstanceInstanceComponent().setQuantity(Quantity(value)))
      }

    val resourceIndices = ResourceIndexer.index(substance)

    assertThat(resourceIndices.quantityIndices)
      .contains(
        QuantityIndex("quantity", "Substance.instance.quantity", "", "", BigDecimal.valueOf(value))
      )
  }

  @Test
  fun index_quantity_null() {
    val substance =
      Substance().apply {
        id = "non-null-ID"
        instance.add(Substance.SubstanceInstanceComponent().setQuantity(null))
      }

    val resourceIndices = ResourceIndexer.index(substance)

    assertThat(
        resourceIndices.quantityIndices.any { quantityIndex -> quantityIndex.name == "quantity" }
      )
      .isFalse()
    assertThat(
        resourceIndices.quantityIndices.any { quantityIndex ->
          quantityIndex.path == "Substance.instance.quantity"
        }
      )
      .isFalse()
  }

  @Test
  fun index_uri() {
    val urlString = "www.someDomainName.someDomain"
    val device =
      Device().apply {
        id = "non-null-ID"
        url = urlString
      }

    val resourceIndices = ResourceIndexer.index(device)

    assertThat(resourceIndices.uriIndices).contains(UriIndex("url", "Device.url", urlString))
  }

  @Test
  fun index_uri_null() {
    val device =
      Device().apply {
        id = "non-null-ID"
        url = null
      }

    val resourceIndices = ResourceIndexer.index(device)

    assertThat(resourceIndices.uriIndices.any { index -> index.name == "url" }).isFalse()
  }

  @Test
  fun index_uri_empty() {
    val device =
      Device().apply {
        id = "non-null-ID"
        url = ""
      }

    val resourceIndices = ResourceIndexer.index(device)

    assertThat(resourceIndices.uriIndices.any { index -> index.name == "url" }).isFalse()
  }

  @Test
  fun index_position() {
    val latitude = 90.0
    val longitude = 90.0
    val location =
      Location().apply {
        id = "someID"
        position = Location.LocationPositionComponent(DecimalType(latitude), DecimalType(longitude))
      }

    val resourceIndices = ResourceIndexer.index(location)

    assertThat(resourceIndices.positionIndices).contains(PositionIndex(latitude, longitude))
  }

  @Test
  fun index_location_null() {
    val location =
      Location().apply {
        id = "non-null-ID"
        position = null
      }

    val resourceIndices = ResourceIndexer.index(location)

    assertThat(resourceIndices.positionIndices).isEmpty()
  }

  /** Integration tests for ResourceIndexer. */
  @Test
  fun index_invoice() {
    val testInvoice =
      TestingUtils(FhirContext.forR4().newJsonParser())
        .readFromFile(Invoice::class.java, "/quantity_test_invoice.json")

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

    assertThat(resourceIndices.dateTimeIndices)
      .containsExactly(
        DateTimeIndex(
          "date",
          "Invoice.date",
          testInvoice.date.time,
          testInvoice.dateElement.precision.add(testInvoice.date, 1).time - 1
        )
      )

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
    val testQuestionnaire =
      TestingUtils(FhirContext.forR4().newJsonParser())
        .readFromFile(Questionnaire::class.java, "/uri_test_questionnaire.json")

    val resourceIndices = ResourceIndexer.index(testQuestionnaire)

    assertThat(resourceIndices.resourceType).isEqualTo(testQuestionnaire.resourceType)

    assertThat(resourceIndices.resourceId).isEqualTo(testQuestionnaire.logicalId)

    assertThat(resourceIndices.uriIndices)
      .containsExactly(
        UriIndex("url", "Questionnaire.url", "http://hl7.org/fhir/Questionnaire/3141")
      )

    assertThat(resourceIndices.numberIndices).isEmpty()

    assertThat(resourceIndices.tokenIndices).isEmpty()

    assertThat(resourceIndices.dateTimeIndices)
      .containsExactly(
        DateTimeIndex(
          "date",
          "Questionnaire.date",
          testQuestionnaire.date.time,
          testQuestionnaire.dateElement.precision.add(testQuestionnaire.date, 1).time - 1
        )
      )

    assertThat(resourceIndices.referenceIndices).isEmpty()

    assertThat(resourceIndices.stringIndices)
      .containsExactly(StringIndex("title", "Questionnaire.title", testQuestionnaire.title))

    assertThat(resourceIndices.positionIndices).isEmpty()

    assertThat(resourceIndices.quantityIndices).isEmpty()
  }

  @Test
  fun index_patient() {
    val testPatient =
      TestingUtils(FhirContext.forR4().newJsonParser())
        .readFromFile(Patient::class.java, "/date_test_patient.json")

    val resourceIndices = ResourceIndexer.index(testPatient)

    assertThat(resourceIndices.resourceType).isEqualTo(testPatient.resourceType)

    assertThat(resourceIndices.resourceId).isEqualTo(testPatient.logicalId)

    assertThat(resourceIndices.dateIndices)
      .containsExactly(
        DateIndex(
          "birthdate",
          "Patient.birthDate",
          testPatient.birthDate.epochDay,
          testPatient.birthDateElement.precision.add(testPatient.birthDate, 1).epochDay - 1
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
        StringIndex("address", "Patient.address", "Van Egmondkade 23, Amsterdam, NLD, 1024 RJ"),
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
        StringIndex("phonetic", "Patient.name", "Pieter van de Heuvel MSc"),
        StringIndex("name", "Patient.name", "Pieter van de Heuvel MSc"),
        StringIndex("family", "Patient.name.family", testPatient.nameFirstRep.family),
        StringIndex("address-city", "Patient.address.city", testPatient.addressFirstRep.city)
      )

    assertThat(resourceIndices.positionIndices).isEmpty()
  }

  @Test
  fun index_location() {
    val testLocation =
      TestingUtils(FhirContext.forR4().newJsonParser())
        .readFromFile(Location::class.java, "/location-example-hl7hq.json")

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

    assertThat(resourceIndices.dateTimeIndices).isEmpty()

    assertThat(resourceIndices.referenceIndices).isEmpty()

    assertThat(resourceIndices.quantityIndices).isEmpty()

    assertThat(resourceIndices.stringIndices)
      .containsExactly(
        StringIndex(
          "address",
          "Location.address",
          "3300 Washtenaw Avenue, Suite 227, Ann Arbor, MI, USA, 48104"
        ),
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

  @Test
  fun index_string_humanName() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        addName(
          HumanName().apply {
            prefix = listOf(StringType("Mr."))
            given = listOf(StringType("Pieter"))
            family = "van de Heuvel"
            suffix = listOf(StringType("MSc"))
          }
        )
      }

    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(resourceIndices.stringIndices)
      .containsAtLeast(
        StringIndex("name", "Patient.name", "Mr. Pieter van de Heuvel MSc"),
        StringIndex("phonetic", "Patient.name", "Mr. Pieter van de Heuvel MSc")
      )
  }

  @Test
  fun index_string_humanName_nullValues_shouldNotIndexHumanName() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        addName(HumanName())
      }

    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(resourceIndices.stringIndices.any { it.name == "name" }).isFalse()
    assertThat(resourceIndices.stringIndices.any { it.name == "phonetic" }).isFalse()
  }

  @Test
  fun index_string_humanName_emptyValues_shouldNotIndexHumanName() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        addName(
          HumanName().apply {
            prefix = listOf()
            given = listOf()
            family = ""
            suffix = listOf()
          }
        )
      }

    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(resourceIndices.stringIndices.any { it.path == "name" }).isFalse()
    assertThat(resourceIndices.stringIndices.any { it.name == "phonetic" }).isFalse()
  }

  @Test
  fun index_string_humanName_multipleListValues() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        addName(
          HumanName().apply {
            prefix =
              listOf(
                null,
                StringType(null),
                StringType(""),
                StringType(" "),
                StringType("Prof."),
                StringType("Dr.")
              )
            given =
              listOf(null, StringType(null), StringType(""), StringType(" "), StringType("Pieter"))
            family = "van de Heuvel"
            suffix =
              listOf(
                null,
                StringType(null),
                StringType(""),
                StringType(" "),
                StringType("MSc"),
                StringType("Phd")
              )
          }
        )
      }

    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(resourceIndices.stringIndices)
      .containsAtLeast(
        StringIndex("name", "Patient.name", "Prof. Dr. Pieter van de Heuvel MSc Phd"),
        StringIndex("phonetic", "Patient.name", "Prof. Dr. Pieter van de Heuvel MSc Phd")
      )
  }

  @Test
  fun index_string_address() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        address =
          listOf(
            Address().apply {
              line = listOf(StringType("Van Egmondkade 23"))
              district = "Amsterdam"
              city = "Amsterdam"
              postalCode = "1024 RJ"
              country = "NLD"
            }
          )
      }

    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(resourceIndices.stringIndices)
      .contains(
        StringIndex(
          "address",
          "Patient.address",
          "Van Egmondkade 23, Amsterdam, Amsterdam, NLD, 1024 RJ"
        )
      )
  }

  @Test
  fun index_string_address_nullValues_shouldNotIndexAddress() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        address = listOf(Address())
      }

    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(resourceIndices.stringIndices.any { it.name == "address" }).isFalse()
  }

  @Test
  fun index_string_address_emptyValues_shouldNotIndexAddress() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        address =
          listOf(
            Address().apply {
              line = listOf()
              district = ""
              city = ""
              postalCode = ""
              country = ""
            }
          )
      }

    val resourceIndices = ResourceIndexer.index(patient)
    assertThat(resourceIndices.stringIndices.any { it.name == "address" }).isFalse()
  }

  @Test
  fun index_duplicateString_deduplicateStringIndices() {
    val givenValue = "Nickole"
    val patient =
      Patient().apply {
        id = "2126234"
        addName(HumanName().addGiven(givenValue))
        addName(HumanName().addGiven(givenValue))
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(
        resourceIndices.stringIndices.filter {
          it.name == "given" && it.path == "Patient.name.given" && it.value == givenValue
        }
      )
      .hasSize(1)
  }

  @Test
  fun index_duplicateNumber_deduplicateNumberIndices() {
    val windowValue = 22125510
    val startValue = 100
    val molecularSequence =
      MolecularSequence().apply {
        id = "2126234"
        referenceSeq.windowStart = windowValue
        addVariant(
          MolecularSequence.MolecularSequenceVariantComponent().apply { start = startValue }
        )
        addVariant(
          MolecularSequence.MolecularSequenceVariantComponent().apply { start = startValue }
        )
      }

    val resourceIndices = ResourceIndexer.index(molecularSequence)

    assertThat(
        resourceIndices.numberIndices.filter {
          it.name == "variant-start" &&
            it.path == "MolecularSequence.variant.start" &&
            it.value == BigDecimal.valueOf(startValue.toLong())
        }
      )
      .hasSize(1)
  }

  @Test
  fun index_duplicateToken_deduplicateTokenIndices() {
    val systemIdentity = "https://github.com/synthetichealth/synthea"
    val indexValue = "000000039481"
    val patient =
      Patient().apply {
        id = "2126234"
        addIdentifier(
          Identifier().apply {
            system = systemIdentity
            value = indexValue
          }
        )
        addIdentifier(
          Identifier().apply {
            system = systemIdentity
            value = indexValue
          }
        )
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(
        resourceIndices.tokenIndices.filter {
          it.name == "identifier" &&
            it.path == "Patient.identifier" &&
            it.system == systemIdentity &&
            it.value == indexValue
        }
      )
      .hasSize(1)
  }

  @Test
  fun index_duplicateQuantity_deduplicateQuantityIndices() {
    val systemValue = "system"
    val unitValue = "unit"
    val values = listOf<Long>(100, 200, 300)
    val substance =
      Substance().apply {
        id = "2126234"
        instance.add(
          Substance.SubstanceInstanceComponent()
            .setQuantity(
              Quantity(values[0]).apply {
                system = systemValue
                unit = unitValue
              }
            )
        )
        instance.add(
          Substance.SubstanceInstanceComponent()
            .setQuantity(
              Quantity(values[0]).apply {
                system = systemValue
                unit = unitValue
              }
            )
        )
        instance.add(Substance.SubstanceInstanceComponent().setQuantity(Quantity(values[1])))
        instance.add(Substance.SubstanceInstanceComponent().setQuantity(Quantity(values[2])))
      }

    val resourceIndices = ResourceIndexer.index(substance)

    assertThat(
        resourceIndices.quantityIndices.filter {
          it.name == "quantity" &&
            it.path == "Substance.instance.quantity" &&
            it.system == systemValue &&
            it.unit == unitValue &&
            it.value == BigDecimal.valueOf(values[0])
        }
      )
      .hasSize(1)
  }

  @Test
  fun index_duplicateReferences_deduplicateReferenceIndices() {
    val values = listOf("reference_1", "reference_2")
    val patient =
      Patient().apply {
        id = "2126234"
        addGeneralPractitioner(Reference().apply { reference = values[0] })
        addGeneralPractitioner(Reference().apply { reference = values[0] })
        addGeneralPractitioner(Reference().apply { reference = values[1] })
      }

    val resourceIndices = ResourceIndexer.index(patient)

    assertThat(
        resourceIndices.referenceIndices.filter {
          it.name == "general-practitioner" &&
            it.path == "Patient.generalPractitioner" &&
            it.value == values[0]
        }
      )
      .hasSize(1)
  }

  private companion object {
    // See: https://www.hl7.org/fhir/valueset-currencies.html
    const val FHIR_CURRENCY_SYSTEM = "urn:iso:std:iso:4217"
  }
}
