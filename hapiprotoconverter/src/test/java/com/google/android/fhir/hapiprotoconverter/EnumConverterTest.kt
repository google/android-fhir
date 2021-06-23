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

import com.google.common.truth.Truth
import com.google.fhir.r4.core.AddressTypeCode
import com.google.fhir.r4.core.AddressUseCode
import com.google.fhir.r4.core.AdministrativeGenderCode
import com.google.fhir.r4.core.BindingStrengthCode
import com.google.fhir.r4.core.CapabilityStatementKindCode
import com.google.fhir.r4.core.ConditionalDeleteStatusCode
import com.google.fhir.r4.core.ConditionalReadStatusCode
import com.google.fhir.r4.core.ConstraintSeverityCode
import com.google.fhir.r4.core.ContactPointSystemCode
import com.google.fhir.r4.core.ContactPointUseCode
import com.google.fhir.r4.core.DataAbsentReasonCode
import com.google.fhir.r4.core.DaysOfWeekCode
import com.google.fhir.r4.core.DiscriminatorTypeCode
import com.google.fhir.r4.core.DocumentModeCode
import com.google.fhir.r4.core.NameUseCode
import com.google.fhir.shaded.protobuf.ProtocolMessageEnum
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.codesystems.AddressType
import org.hl7.fhir.r4.model.codesystems.AdministrativeGender
import org.hl7.fhir.r4.model.codesystems.BindingStrength
import org.hl7.fhir.r4.model.codesystems.CapabilityStatementKind
import org.hl7.fhir.r4.model.codesystems.ConditionalDeleteStatus
import org.hl7.fhir.r4.model.codesystems.ConditionalReadStatus
import org.hl7.fhir.r4.model.codesystems.ConstraintSeverity
import org.hl7.fhir.r4.model.codesystems.ContactPointSystem
import org.hl7.fhir.r4.model.codesystems.ContactPointUse
import org.hl7.fhir.r4.model.codesystems.DataAbsentReason
import org.hl7.fhir.r4.model.codesystems.DaysOfWeek
import org.hl7.fhir.r4.model.codesystems.DiscriminatorType
import org.hl7.fhir.r4.model.codesystems.DocumentMode
import org.hl7.fhir.r4.model.codesystems.NameUse
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class EnumConverterTest {

  @Parameterized.Parameter(0) lateinit var hapi: Enum<*>
  @Parameterized.Parameter(1) lateinit var proto: ProtocolMessageEnum

  @Test
  fun test_enum_converter_test() {
    Truth.assertThat(convert(hapi, proto::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, hapi::class.java)).isEqualTo(hapi)
  }

  private companion object {
    @Parameterized.Parameters
    @JvmStatic

    // TODO add more tests
    fun testParams(): Collection<Array<Enum<*>>> {
      return listOf(
        arrayOf(AdministrativeGender.FEMALE, AdministrativeGenderCode.Value.FEMALE),
        arrayOf(NameUse.NICKNAME, NameUseCode.Value.NICKNAME),
      ) +
        AddressType.values().filter { it.name != "NULL" }.map {
          arrayOf(it, AddressTypeCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase()))
        } +
        Address.AddressUse.values().filter { it.name != "NULL" }.map {
          arrayOf(it, AddressUseCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase()))
        } +
        AdministrativeGender.values().filter { it.name != "NULL" }.map {
          arrayOf(
            it,
            AdministrativeGenderCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
          )
        } +
        BindingStrength.values().filter { it.name != "NULL" }.map {
          arrayOf(
            it,
            BindingStrengthCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
          )
        } +
        //        BundleType.TRANSACTIONRESPONSE.values().filter { it.name != "NULL" }.map {
        //          arrayOf(it,
        // BundleTypeCode.ValuevalueOf(it.toCode().replace('-','_').toUpperCase()))
        //        } +
        CapabilityStatementKind.values().filter { it.name != "NULL" }.map {
          arrayOf(
            it,
            CapabilityStatementKindCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
          )
        } +
        //        CodeSystem.CodeSystemContentMode.NOTPRESENT.toCode.values().filter { it.name !=
        // "NULL" }.map {
        //          arrayOf(it,
        // CodeSystemContentModeCode.Value.NOT_PRESENT.valueOf(it.toCode().replace('-','_').toUpperCase()))
        //        } +
        ConditionalDeleteStatus.values().filter { it.name != "NULL" }.map {
          arrayOf(
            it,
            ConditionalDeleteStatusCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
          )
        } +
        ConditionalReadStatus.values().filter { it.name != "NULL" }.map {
          arrayOf(
            it,
            ConditionalReadStatusCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
          )
        } +
        ConstraintSeverity.values().filter { it.name != "NULL" }.map {
          arrayOf(
            it,
            ConstraintSeverityCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
          )
        } +
        ContactPointSystem.values().filter { it.name != "NULL" }.map {
          arrayOf(
            it,
            ContactPointSystemCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
          )
        } +
        ContactPointUse.values().filter { it.name != "NULL" }.map {
          arrayOf(
            it,
            ContactPointUseCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
          )
        } +
        DataAbsentReason.values().filter { it.name != "NULL" }.map {
          arrayOf(
            it,
            DataAbsentReasonCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
          )
        } +
        DaysOfWeek.values().filter { it.name != "NULL" }.map {
          arrayOf(it, DaysOfWeekCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase()))
        } +
        // TODO handle this case

        //        Enumerations.FHIRDefinedType.values().filter { it.name != "NULL" }.map {
        //          arrayOf(it,
        // FHIRDefinedTypeCode.Value.valueOf(it.toCode().replace('-','_').toUpperCase()))
        //        } +
        DiscriminatorType.values().filter { it.name != "NULL" }.map {
          arrayOf(
            it,
            DiscriminatorTypeCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
          )
        } +
        DocumentMode.values().filter { it.name != "NULL" }.map {
          arrayOf(it, DocumentModeCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase()))
        }
    }
  }
}
