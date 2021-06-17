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

package com.google.android.fhir.hapiprotoconverter

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.common.truth.Truth
import com.google.fhir.common.JsonFormat
import com.google.fhir.r4.core.HumanName
import com.google.fhir.r4.core.Patient
import org.junit.Before
import org.junit.Test

/** Contains tests for JsonConverter.kt */
class JsonConverterTest {
  private lateinit var hapiParser: IParser
  private lateinit var protoParser: JsonFormat.Parser
  private lateinit var protoPrinter: JsonFormat.Printer

  @Before
  fun before_test() {
    val context = FhirContext.forR4()
    hapiParser = context.newJsonParser()
    protoParser = JsonFormat.getParser()
    protoPrinter = JsonFormat.getPrinter()
  }

  @Test
  fun convert_patient_hapi_to_proto() {
    val input =
      org.hl7.fhir.r4.model.Patient().apply {
        id = "1"
        addName(org.hl7.fhir.r4.model.HumanName().setFamily("Doe").addGiven("John"))
      }

    val expectedOutput =
      Patient.newBuilder()
        .setId(com.google.fhir.r4.core.Id.newBuilder().setValue("Patient/1"))
        .addName(
          HumanName.newBuilder()
            .addGiven(com.google.fhir.r4.core.String.newBuilder().setValue("John"))
            .setFamily(com.google.fhir.r4.core.String.newBuilder().setValue("Doe"))
        )
        .build()

    Truth.assertThat(convert<Patient>(input, hapiParser, protoParser)).isEqualTo(expectedOutput)
  }

  @Test
  fun convert_patient_proto_to_hapi() {

    val input =
      Patient.newBuilder()
        .setId(com.google.fhir.r4.core.Id.newBuilder().setValue("1"))
        .addName(
          HumanName.newBuilder()
            .addGiven(com.google.fhir.r4.core.String.newBuilder().setValue("John"))
            .setFamily(com.google.fhir.r4.core.String.newBuilder().setValue("Doe"))
        )
        .build()

    val expectedOutput =
      org.hl7.fhir.r4.model.Patient().apply {
        id = "1"
        addName(org.hl7.fhir.r4.model.HumanName().setFamily("Doe").addGiven("John"))
      }

    val actualOutput = convert<org.hl7.fhir.r4.model.Patient>(input, hapiParser, protoPrinter)

    Truth.assertThat(actualOutput.id.toString()).isEqualTo("Patient/${expectedOutput.id}")
    Truth.assertThat(actualOutput.nameFirstRep.given.toString())
      .isEqualTo(expectedOutput.nameFirstRep.given.toString())
    Truth.assertThat(actualOutput.nameFirstRep.family.toString())
      .isEqualTo(expectedOutput.nameFirstRep.family.toString())
  }
}
