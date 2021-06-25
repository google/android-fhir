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
import com.google.fhir.r4.core.CapabilityStatement
import com.google.fhir.r4.core.CapabilityStatementKindCode
import com.google.fhir.r4.core.ContactPoint
import com.google.fhir.r4.core.ContactPointSystemCode
import com.google.fhir.r4.core.ContactPointUseCode
import com.google.fhir.r4.core.OperationDefinition
import com.google.fhir.r4.core.Person
import com.google.fhir.shaded.protobuf.GeneratedMessageV3
import com.google.fhir.shaded.protobuf.ProtocolMessageEnum
import java.io.Serializable
import org.hl7.fhir.instance.model.api.IBaseEnumeration
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.codesystems.AddressType
import org.hl7.fhir.r4.model.codesystems.AddressTypeEnumFactory
import org.hl7.fhir.r4.model.codesystems.AdministrativeGender
import org.hl7.fhir.r4.model.codesystems.AdministrativeGenderEnumFactory
import org.hl7.fhir.r4.model.codesystems.BindingStrength
import org.hl7.fhir.r4.model.codesystems.BindingStrengthEnumFactory
import org.hl7.fhir.r4.model.codesystems.CapabilityStatementKind
import org.hl7.fhir.r4.model.codesystems.CapabilityStatementKindEnumFactory
import org.hl7.fhir.r4.model.codesystems.ContactPointSystem
import org.hl7.fhir.r4.model.codesystems.ContactPointSystemEnumFactory
import org.hl7.fhir.r4.model.codesystems.ContactPointUse
import org.hl7.fhir.r4.model.codesystems.ContactPointUseEnumFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class EnumConverterTest {

  @Parameterized.Parameter(0) lateinit var hapi: IBaseEnumeration<*>
  @Parameterized.Parameter(1) lateinit var proto: GeneratedMessageV3

  @Test
  fun test_enum_converter_test_outter() {
    Truth.assertThat(convert(hapi, proto::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, hapi::class.java, hapi.value::class.java).value)
      .isEquivalentAccordingToCompareTo(hapi.value)
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun test_enum_converter_test_inner() {
    // Probably not the best idea to use reflection is tests , we could just pass more parameters to
    // the Test Class
    val protoEnumValue =
      proto::class.java.getDeclaredMethod("getValue").invoke(proto) as ProtocolMessageEnum
    Truth.assertThat(convert(hapi.value, protoEnumValue::class.java)).isEqualTo(protoEnumValue)
    Truth.assertThat(convert(protoEnumValue, hapi.value::class.java))
      .isEquivalentAccordingToCompareTo(hapi.value)
  }
  private companion object {
    @Parameterized.Parameters
    @JvmStatic

    // TODO add more tests
    fun testParams(): List<Array<out Serializable>> {
      return AddressType.values().filter { it.name != "NULL" }.map {
        arrayOf(
          Enumeration(AddressTypeEnumFactory(), it),
          com.google.fhir.r4.core.Address.TypeCode.newBuilder()
            .setValue(AddressTypeCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase()))
            .build()
        )
      } +
        Address.AddressUse.values().filter { it.name != "NULL" }.map {
          arrayOf(
            Enumeration(Address.AddressUseEnumFactory(), it),
            com.google.fhir.r4.core.Address.UseCode.newBuilder()
              .setValue(AddressUseCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase()))
              .build()
          )
        } +
        AdministrativeGender.values().filter { it.name != "NULL" }.map {
          arrayOf(
            Enumeration(AdministrativeGenderEnumFactory(), it),
            Person.GenderCode.newBuilder()
              .setValue(
                AdministrativeGenderCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
              )
              .build()
          )
        } +
        BindingStrength.values().filter { it.name != "NULL" }.map {
          arrayOf(
            Enumeration(BindingStrengthEnumFactory(), it),
            OperationDefinition.Parameter.Binding.StrengthCode.newBuilder()
              .setValue(
                BindingStrengthCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
              )
              .build()
          )
        } +
        CapabilityStatementKind.values().filter { it.name != "NULL" }.map {
          arrayOf(
            Enumeration(CapabilityStatementKindEnumFactory(), it),
            CapabilityStatement.KindCode.newBuilder()
              .setValue(
                CapabilityStatementKindCode.Value.valueOf(
                  it.toCode().replace('-', '_').toUpperCase()
                )
              )
              .build()
          )
        } +
        ContactPointSystem.values().filter { it.name != "NULL" }.map {
          arrayOf(
            Enumeration(ContactPointSystemEnumFactory(), it),
            ContactPoint.SystemCode.newBuilder()
              .setValue(
                ContactPointSystemCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
              )
              .build()
          )
        } +
        ContactPointUse.values().filter { it.name != "NULL" }.map {
          arrayOf(
            Enumeration(ContactPointUseEnumFactory(), it),
            ContactPoint.UseCode.newBuilder()
              .setValue(
                ContactPointUseCode.Value.valueOf(it.toCode().replace('-', '_').toUpperCase())
              )
              .build()
          )
        }
    }
  }
}
