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
import com.google.android.fhir.hapiprotoconverter.generated.PatientConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PatientConverter.toProto
import com.google.fhir.common.JsonFormat
import com.google.fhir.r4.core.Patient
import java.time.Clock
import org.junit.Before
import org.junit.Test

class PatientConverterTest {
  private lateinit var hapiParser: IParser
  private lateinit var protoParser: JsonFormat.Parser
  private lateinit var protoPrinter: JsonFormat.Printer
  private var clock = Clock.systemDefaultZone()

  @Before
  fun before() {
    hapiParser = FhirContext.forR4().newJsonParser()
    protoParser = JsonFormat.getParser()
    protoPrinter = JsonFormat.getPrinter().omittingInsignificantWhitespace()
  }

  @Test
  fun hapiToProto() {
    val input = this::class.java.getResourceAsStream("/patient-example.json")!!.reader()
    val hapi = hapiParser.parseResource(org.hl7.fhir.r4.model.Patient::class.java, input)
    val t1 = clock.instant().toEpochMilli()
    hapi.toProto()
    val t2 = clock.instant().toEpochMilli()
    println(t2 - t1)
  }

  @Test
  fun protoToHapi() {
    val input = this::class.java.getResourceAsStream("/patient-example.json")!!.reader()
    val proto = protoParser.merge(input, Patient.newBuilder()).build()
    val t1 = clock.instant().toEpochMilli()
    proto.toHapi()
    val t2 = clock.instant().toEpochMilli()
    println(t2 - t1)
  }
}
