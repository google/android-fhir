/*
 * Copyright 2022 Google LLC
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
import com.google.android.fhir.testing.readFromFile
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.ActivityDefinition
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Device
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Extension
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
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RelatedArtifact
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

  private val resourceIndexer = ResourceIndexer(SearchParamDefinitionsProviderImpl())

  /** Unit tests for resource indexer */
  @Test
  fun index_id() {
    val patient = Patient().apply { id = "3f511720-43c4-451a-830b-7f4817c619fb" }
    val resourceIndices = resourceIndexer.index(patient)
    assertThat(resourceIndices.tokenIndices)
      .contains(TokenIndex("_id", "Patient.id", null, "3f511720-43c4-451a-830b-7f4817c619fb"))
  }

  @Test
  fun index_lastUpdated() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        meta = Meta().setLastUpdated(InstantType("2001-09-01T23:09:09.000+05:30").value)
      }

    val resourceIndices = resourceIndexer.index(patient)

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
  fun index_profile() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        meta = Meta().setProfile(mutableListOf(CanonicalType("Profile/lipid")))
      }
    val resourceIndices = resourceIndexer.index(patient)
    assertThat(resourceIndices.referenceIndices)
      .contains(ReferenceIndex("_profile", "Patient.meta.profile", "Profile/lipid"))
  }

  @Test
  fun index_profile_empty() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        meta = Meta().setProfile(mutableListOf(CanonicalType("")))
      }
    val resourceIndices = resourceIndexer.index(patient)
    assertThat(resourceIndices.referenceIndices.any { it.name == "_profile" }).isFalse()
  }

  @Test
  fun index_tag() {
    val codeString = "1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    val systemString = "http://openmrs.org/concepts"
    val patient =
      Patient().apply {
        id = "non-null-ID"
        meta = Meta().setTag(mutableListOf(Coding(systemString, codeString, "display")))
      }
    val resourceIndices = resourceIndexer.index(patient)

    assertThat(resourceIndices.tokenIndices)
      .contains(TokenIndex("_tag", "Patient.meta.tag", systemString, codeString))
  }

  @Test
  fun index_tag_empty() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        meta = Meta().setTag(mutableListOf(Coding("", "", "")))
      }
    val resourceIndices = resourceIndexer.index(patient)

    assertThat(resourceIndices.tokenIndices.any { it.name == "_tag" }).isFalse()
  }

  @Test
  fun index_number_integer() {
    val value = 22125510
    val molecularSequence =
      MolecularSequence().apply {
        id = "non-null-ID"
        referenceSeq.windowStart = value
      }

    val resourceIndices = resourceIndexer.index(molecularSequence)

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

    val resourceIndices = resourceIndexer.index(riskAssessment)

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

    val resourceIndices = resourceIndexer.index(molecularSequence)

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

    val resourceIndices = resourceIndexer.index(patient)

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
    val resourceIndices = resourceIndexer.index(patient)

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

    val resourceIndices = resourceIndexer.index(observation)

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

    val resourceIndices = resourceIndexer.index(observation)

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

    val resourceIndices = resourceIndexer.index(observation)

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

    val resourceIndices = resourceIndexer.index(observation)

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

    val resourceIndices = resourceIndexer.index(observation)

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

    val resourceIndices = resourceIndexer.index(observation)

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
  fun index_dateTime_repeated_timing_is_ignored() {
    val timing =
      Timing().apply {
        repeat =
          Timing.TimingRepeatComponent().apply {
            frequency = 1
            period = BigDecimal.ONE
            periodUnit = Timing.UnitsOfTime.D
          }
      }
    val observation =
      Observation().apply {
        id = "non-null ID"
        effective = timing
      }

    val resourceIndices = resourceIndexer.index(observation)
    assertThat(resourceIndices.dateTimeIndices).isEmpty()
  }

  @Test
  fun index_dateTime_string() {
    val observation =
      CarePlan().apply {
        id = "non-null ID"
        addActivity(
          CarePlan.CarePlanActivityComponent().apply {
            detail =
              CarePlan.CarePlanActivityDetailComponent().apply {
                scheduled = StringType("2011-06-27T09:30:10+01:00")
              }
          }
        )
      }

    val resourceIndices = resourceIndexer.index(observation)
    val dateTime = DateTimeType("2011-06-27T09:30:10+01:00")
    assertThat(resourceIndices.dateTimeIndices)
      .contains(
        DateTimeIndex(
          "activity-date",
          "CarePlan.activity.detail.scheduled",
          dateTime.value.time,
          dateTime.precision.add(dateTime.value, 1).time - 1
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
    val resourceIndices = resourceIndexer.index(observation)

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

    val resourceIndices = resourceIndexer.index(patient)

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

    val resourceIndices = resourceIndexer.index(patient)

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

    val resourceIndices = resourceIndexer.index(patient)

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

    val resourceIndices = resourceIndexer.index(patient)

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

    val resourceIndices = resourceIndexer.index(invoice)

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

    val resourceIndices = resourceIndexer.index(observation)

    assertThat(resourceIndices.tokenIndices)
      .contains(TokenIndex("code", "Observation.code", systemString, codeString))
  }

  @Test
  fun index_token_Coding() {
    val codeString = "1427AAAAA"
    val systemString = "http://openmrs.org/concepts"
    val encounter =
      Encounter().apply {
        id = "non-null-ID"
        class_ =
          Coding().apply {
            system = systemString
            code = codeString
            display = "Display"
          }
      }
    val resourceIndices = resourceIndexer.index(encounter)

    assertThat(resourceIndices.tokenIndices)
      .contains(TokenIndex("class", "Encounter.class", systemString, codeString))
  }

  @Test
  fun index_token_null() {
    val observation =
      Observation().apply {
        id = "non-null-ID"
        code = null
      }

    val resourceIndices = resourceIndexer.index(observation)

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

    val resourceIndices = resourceIndexer.index(observation)

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

    val resourceIndices = resourceIndexer.index(patient)

    assertThat(resourceIndices.referenceIndices)
      .contains(ReferenceIndex("organization", "Patient.managingOrganization", organizationString))
  }

  @Test
  fun index_reference_canonical_type() {
    val relatedArtifact =
      RelatedArtifact().apply {
        this.id = "someRelatedArtifact"
        this.resource = "Questionnaire/someQuestionnaire"
        this.type = RelatedArtifact.RelatedArtifactType.DEPENDSON
      }

    val activityDefinition =
      ActivityDefinition().apply {
        this.id = "someActivityDefinition"
        this.addLibrary("Library/someLibrary")

        this.addRelatedArtifact(relatedArtifact)
      }

    val resourceIndices = resourceIndexer.index(activityDefinition)

    val indexPath =
      "ActivityDefinition.relatedArtifact.where(type='depends-on').resource | ActivityDefinition.library"
    val indexName = ActivityDefinition.SP_DEPENDS_ON

    assertThat(resourceIndices.referenceIndices)
      .containsExactly(
        ReferenceIndex(indexName, indexPath, "Library/someLibrary"),
        ReferenceIndex(indexName, indexPath, "Questionnaire/someQuestionnaire")
      )
  }

  @Test
  fun index_reference_uri_type() {
    val planDefinition =
      PlanDefinition().apply {
        this.id = "somePlanDefinition"
        this.addAction().definition = UriType("http://action1.com")
        this.addAction().definition = UriType("http://action2.com")
      }

    val resourceIndices = resourceIndexer.index(planDefinition)

    val indexPath = "PlanDefinition.action.definition"
    val indexName = PlanDefinition.SP_DEFINITION

    assertThat(resourceIndices.referenceIndices)
      .containsExactly(
        ReferenceIndex(indexName, indexPath, "http://action1.com"),
        ReferenceIndex(indexName, indexPath, "http://action2.com"),
      )
  }

  @Test
  fun index_reference_null() {
    val patient =
      Patient().apply {
        id = "non-null-ID"
        managingOrganization = null
      }

    val resourceIndices = resourceIndexer.index(patient)

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

    val resourceIndices = resourceIndexer.index(patient)

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
  fun index_gender() {
    val patient =
      Patient().apply {
        id = "someID"
        gender = Enumerations.AdministrativeGender.UNKNOWN
      }

    val resourceIndices = resourceIndexer.index(patient)

    assertThat(resourceIndices.tokenIndices)
      .contains(
        TokenIndex(
          "gender",
          "Patient.gender",
          "http://hl7.org/fhir/administrative-gender",
          "unknown"
        )
      )
  }

  @Test
  fun index_gender_null() {
    val patient = Patient().apply { id = "someID" }

    val resourceIndices = resourceIndexer.index(patient)

    assertThat(resourceIndices.tokenIndices.any { it.name == "gender" }).isFalse()
  }

  @Test
  fun index_quantity_money() {
    val testInvoice =
      Invoice().apply {
        id = "non_NULL_ID"
        totalNet = Money().setCurrency("EU").setValue(BigDecimal.valueOf(300))
      }

    val resourceIndices = resourceIndexer.index(testInvoice)

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
  fun index_quantity_quantity_noUnitOrCode() {
    val substance =
      Substance().apply {
        id = "non-null-ID"
        instance.add(Substance.SubstanceInstanceComponent().setQuantity(Quantity(100L)))
      }

    val resourceIndices = resourceIndexer.index(substance)

    assertThat(resourceIndices.quantityIndices)
      .contains(
        QuantityIndex("quantity", "Substance.instance.quantity", "", "", BigDecimal.valueOf(100L))
      )
  }

  @Test
  fun index_quantity_quantity_unit() {
    val substance =
      Substance().apply {
        id = "non-null-ID"
        instance.add(
          Substance.SubstanceInstanceComponent().setQuantity(Quantity(null, 100L, null, null, "kg"))
        )
      }

    val resourceIndices = resourceIndexer.index(substance)

    assertThat(resourceIndices.quantityIndices)
      .contains(
        QuantityIndex("quantity", "Substance.instance.quantity", "", "kg", BigDecimal.valueOf(100L))
      )
  }

  @Test
  fun index_quantity_null() {
    val substance =
      Substance().apply {
        id = "non-null-ID"
        instance.add(Substance.SubstanceInstanceComponent().setQuantity(null))
      }

    val resourceIndices = resourceIndexer.index(substance)

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
  fun index_quantity_quantity_code_canonicalized() {
    val substance =
      Substance().apply {
        id = "non-null-ID"
        instance.add(
          Substance.SubstanceInstanceComponent()
            .setQuantity(Quantity(100L).setSystem("http://unitsofmeasure.org").setCode("mg"))
        )
      }

    val resourceIndices = resourceIndexer.index(substance)

    assertThat(resourceIndices.quantityIndices)
      .contains(
        QuantityIndex(
          "quantity",
          "Substance.instance.quantity",
          "http://unitsofmeasure.org",
          "g",
          BigDecimal("0.100")
        )
      )
  }

  @Test
  fun index_quantity_quantity_code_notCanonicalized() {
    val substance =
      Substance().apply {
        id = "non-null-ID"
        instance.add(
          Substance.SubstanceInstanceComponent()
            .setQuantity(
              Quantity(100L).setSystem("http://unitsofmeasure.org").setCode("randomUnit")
            )
        )
      }

    val resourceIndices = resourceIndexer.index(substance)

    assertThat(resourceIndices.quantityIndices)
      .contains(
        QuantityIndex(
          "quantity",
          "Substance.instance.quantity",
          "http://unitsofmeasure.org",
          "randomUnit",
          BigDecimal.valueOf(100L)
        )
      )
  }

  @Test
  fun index_uri() {
    val device =
      Device().apply {
        id = "non-null-ID"
        url = "www.someDomainName.someDomain"
      }

    val resourceIndices = resourceIndexer.index(device)

    assertThat(resourceIndices.uriIndices)
      .contains(UriIndex("url", "Device.url", "www.someDomainName.someDomain"))
  }

  @Test
  fun index_uri_null() {
    val device =
      Device().apply {
        id = "non-null-ID"
        url = null
      }

    val resourceIndices = resourceIndexer.index(device)

    assertThat(resourceIndices.uriIndices.any { index -> index.name == "url" }).isFalse()
  }

  @Test
  fun index_uri_empty() {
    val device =
      Device().apply {
        id = "non-null-ID"
        url = ""
      }

    val resourceIndices = resourceIndexer.index(device)

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

    val resourceIndices = resourceIndexer.index(location)

    assertThat(resourceIndices.positionIndices).contains(PositionIndex(latitude, longitude))
  }

  @Test
  fun index_location_null() {
    val location =
      Location().apply {
        id = "non-null-ID"
        position = null
      }

    val resourceIndices = resourceIndexer.index(location)

    assertThat(resourceIndices.positionIndices).isEmpty()
  }

  /** Integration tests for ResourceIndexer. */
  @Test
  fun index_invoice() {
    val testInvoice = readFromFile(Invoice::class.java, "/quantity_test_invoice.json")

    val resourceIndices = resourceIndexer.index(testInvoice)

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
        ),
        TokenIndex("status", "Invoice.status", "http://hl7.org/fhir/invoice-status", "issued"),
        TokenIndex("_id", "Invoice.id", null, "example")
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
    val testQuestionnaire = readFromFile(Questionnaire::class.java, "/uri_test_questionnaire.json")

    val resourceIndices = resourceIndexer.index(testQuestionnaire)

    assertThat(resourceIndices.resourceType).isEqualTo(testQuestionnaire.resourceType)

    assertThat(resourceIndices.resourceId).isEqualTo(testQuestionnaire.logicalId)

    assertThat(resourceIndices.uriIndices)
      .containsExactly(
        UriIndex("url", "Questionnaire.url", "http://hl7.org/fhir/Questionnaire/3141")
      )

    assertThat(resourceIndices.numberIndices).isEmpty()

    assertThat(resourceIndices.tokenIndices)
      .containsExactly(
        TokenIndex("subject-type", "Questionnaire.subjectType", "", "Patient"),
        TokenIndex(
          "status",
          "Questionnaire.status",
          "http://hl7.org/fhir/publication-status",
          "draft"
        ),
        TokenIndex("_id", "Questionnaire.id", null, "3141"),
        TokenIndex(
          "code",
          "Questionnaire.item.code",
          "http://example.org/system/code/sections",
          "HISTOPATHOLOGY"
        )
      )

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
    val testPatient = readFromFile(Patient::class.java, "/date_test_patient.json")

    val resourceIndices = resourceIndexer.index(testPatient)

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
        ),
        TokenIndex(
          "gender",
          "Patient.gender",
          testPatient.gender.system,
          testPatient.gender.toCode()
        ),
        TokenIndex(
          "address-use",
          "Patient.address.use",
          testPatient.addressFirstRep.use.system,
          testPatient.addressFirstRep.use.toCode()
        ),
        TokenIndex("_id", "Patient.id", null, "f001")
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
    val testLocation = readFromFile(Location::class.java, "/location-example-hl7hq.json")

    val resourceIndices = resourceIndexer.index(testLocation)

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
        ),
        TokenIndex(
          "status",
          "Location.status",
          testLocation.status.system,
          testLocation.status.toCode()
        ),
        TokenIndex("_id", "Location.id", null, "hl7")
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

    val resourceIndices = resourceIndexer.index(patient)
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

    val resourceIndices = resourceIndexer.index(patient)
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

    val resourceIndices = resourceIndexer.index(patient)
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

    val resourceIndices = resourceIndexer.index(patient)
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

    val resourceIndices = resourceIndexer.index(patient)
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

    val resourceIndices = resourceIndexer.index(patient)
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

    val resourceIndices = resourceIndexer.index(patient)
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

    val resourceIndices = resourceIndexer.index(patient)

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

    val resourceIndices = resourceIndexer.index(molecularSequence)

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

    val resourceIndices = resourceIndexer.index(patient)

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

    val resourceIndices = resourceIndexer.index(substance)

    assertThat(
        resourceIndices.quantityIndices.filter {
          it.name == "quantity" &&
            it.path == "Substance.instance.quantity" &&
            it.system == systemValue &&
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

    val resourceIndices = resourceIndexer.index(patient)

    assertThat(
        resourceIndices.referenceIndices.filter {
          it.name == "general-practitioner" &&
            it.path == "Patient.generalPractitioner" &&
            it.value == values[0]
        }
      )
      .hasSize(1)
  }

  @Test
  fun index_quantity_observation_valueQuantity() {
    val observation =
      Observation().apply {
        addComponent().apply {
          this.valueQuantity.apply {
            value = BigDecimal.valueOf(70)
            system = "http://unitsofmeasure.org"
          }
        }
        addComponent().apply {
          this.valueQuantity.apply {
            value = BigDecimal.valueOf(110)
            system = "http://unitsofmeasure.org"
          }
        }
      }
    // The indexer creates 2 QuantityIndex per valueQuantity in this particular example because each
    // Observation.component.value can be indexed for both [Observation.SP_COMPONENT_VALUE_QUANTITY]
    // and [Observation.SP_COMBO_VALUE_QUANTITY]
    val resourceIndices = resourceIndexer.index(observation)

    assertThat(resourceIndices.quantityIndices)
      .containsExactly(
        QuantityIndex(
          name = Observation.SP_COMPONENT_VALUE_QUANTITY,
          path =
            "(Observation.component.value as Quantity) " +
              "| (Observation.component.value as SampledData)",
          system = "http://unitsofmeasure.org",
          code = "",
          value = BigDecimal.valueOf(70)
        ),
        QuantityIndex(
          name = Observation.SP_COMPONENT_VALUE_QUANTITY,
          path =
            "(Observation.component.value as Quantity) " +
              "| (Observation.component.value as SampledData)",
          system = "http://unitsofmeasure.org",
          code = "",
          value = BigDecimal.valueOf(110)
        ),
        QuantityIndex(
          name = Observation.SP_COMBO_VALUE_QUANTITY,
          path =
            "(Observation.value as Quantity) " +
              "| (Observation.value as SampledData) " +
              "| (Observation.component.value as Quantity) " +
              "| (Observation.component.value as SampledData)",
          system = "http://unitsofmeasure.org",
          code = "",
          value = BigDecimal.valueOf(70)
        ),
        QuantityIndex(
          name = Observation.SP_COMBO_VALUE_QUANTITY,
          path =
            "(Observation.value as Quantity) " +
              "| (Observation.value as SampledData) " +
              "| (Observation.component.value as Quantity) " +
              "| (Observation.component.value as SampledData)",
          system = "http://unitsofmeasure.org",
          code = "",
          value = BigDecimal.valueOf(110)
        )
      )
  }

  @Test
  fun index_custom_search_param() {
    val patient =
      Patient().apply {
        addIdentifier(
          Identifier().apply {
            system = "https://custom-identifier-namespace"
            value = "OfficialIdentifier_DarcySmith_0001"
          }
        )
        addName(
          HumanName().apply {
            use = HumanName.NameUse.OFFICIAL
            family = "Smith"
            addGiven("Darcy")
            gender = Enumerations.AdministrativeGender.FEMALE
            birthDateElement = DateType("1970-01-01")
          }
        )
        addExtension(
          Extension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName"
            setValue(StringType("Marca"))
          }
        )
      }

    val resourceIndices =
      ResourceIndexer(
          SearchParamDefinitionsProviderImpl(
            customParams =
              mapOf(
                "Patient" to
                  listOf(
                    SearchParamDefinition(
                      name = "mothers-maiden-name",
                      type = Enumerations.SearchParamType.STRING,
                      path =
                        "Patient.extension('http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName').value.as(String)"
                    ),
                    SearchParamDefinition(
                      name = "identifierPartial",
                      type = Enumerations.SearchParamType.STRING,
                      path = "Patient.identifier.value"
                    )
                  )
              )
          )
        )
        .index(patient)

    assertThat(resourceIndices.stringIndices)
      .containsExactly(
        StringIndex(
          name = "mothers-maiden-name",
          path =
            "Patient.extension('http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName').value.as(String)",
          value = "Marca"
        ),
        StringIndex(
          name = "identifierPartial",
          path = "Patient.identifier.value",
          value = "OfficialIdentifier_DarcySmith_0001"
        ),
        StringIndex(name = "family", path = "Patient.name.family", value = "Smith"),
        StringIndex(name = "name", path = "Patient.name", value = "Darcy Smith"),
        StringIndex(name = "phonetic", path = "Patient.name", value = "Darcy Smith"),
        StringIndex(name = "given", path = "Patient.name.given", value = "Darcy")
      )
  }

  private companion object {
    // See: https://www.hl7.org/fhir/valueset-currencies.html
    const val FHIR_CURRENCY_SYSTEM = "urn:iso:std:iso:4217"
  }
}
