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
import java.util.Date
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Timing
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class LuceneIndexerTest {

  @Before
  fun setup() {
    LuceneIndexer.resourceIndexer(
      "/home/vend-maimoona/Documents/projects/android-fhir/engine/zzluceneindexer"
    )
  }

  @Test
  fun index_id() {
    println("Starting ${Date()}")

    LuceneIndexer.optimize()

    println("Started ${Date()}")

    for (i in 1..9000) LuceneIndexer.indexResource(buildPatient(i))

    for (i in 1..9000) LuceneIndexer.indexResource(buildObservation(i))

    println("Ending ${Date()}")

    LuceneIndexer.close()

    println("Ended ${Date()}")
  }

  fun buildObservation(i: Int) =
    Observation().apply {
      id = "obssss$i"
      code = CodeableConcept().addCoding(Coding("http://coding.org", "oo$i-code", "CODEE"))
      basedOn =
        listOf(
          Reference().apply { reference = "1-$i-Enc" },
          Reference().apply { reference = "2-$i-Enc" }
        )
      category =
        listOf(
          CodeableConcept().addCoding(Coding("http://coding.org", "oo$i-cat-1", "CATEGG1")),
          CodeableConcept().addCoding(Coding("http://coding.org", "oo$i-cat-2", "CATEGG2"))
        )
      if (i % 3 == 0) effective = InstantType().apply { this.value = Date() }
      else if (i % 4 == 0)
        effective =
          Period().apply {
            start = Date()
            end = Date()
          }
      else if (i % 2 == 0)
        effective = Timing().apply { this.event = listOf(DateTimeType.now(), DateTimeType.now()) }
      else effective = DateTimeType.now()

      value =
        Quantity().apply {
          this.value = 3.toBigDecimal()
          this.system = "http://units.org"
          this.unit = "g"
        }
    }

  fun buildPatient(i: Int) =
    Patient().apply {
      id = "3f511720-$i"
      active = true
      addAddress().apply {
        this.city = "ceetee$i"
        this.country = "cantree$i"
        this.state = "seetate$i"
        this.type = Address.AddressType.PHYSICAL
        this.district = "distreect$i"
        this.addLine("l1").addLine("l2")
        this.period =
          Period().apply {
            start = Date()
            end = Date()
          }
        this.use = Address.AddressUse.BILLING
      }
      addName().apply {
        this.addGiven("NAAM$i").addGiven("ISM$i")
        this.family = "FAAMLE$i"
      }
      birthDate = Date(System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 365 * i))
      gender =
        if (i % 2 == 0) Enumerations.AdministrativeGender.MALE
        else Enumerations.AdministrativeGender.FEMALE

      addGeneralPractitioner(Reference().apply { reference = "Practitioner/1$i" })
      addGeneralPractitioner(Reference().apply { reference = "Practitioner/2$i" })

      addIdentifier(
        Identifier().apply {
          this.type = CodeableConcept().addCoding(Coding("http://coding.org", "oo11idd", "IDENT"))
          this.value = "IDE$i$i"
          this.system = "http://id.org"
        }
      )
    }
}
