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
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.PositionIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
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
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RiskAssessment
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Substance
import org.hl7.fhir.r4.model.UriType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Integration tests for {@link ResourceIndexerImpl}. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ResourceIndexerTest {

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
