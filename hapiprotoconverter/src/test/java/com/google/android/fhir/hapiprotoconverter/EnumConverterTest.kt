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
import com.google.fhir.r4.core.NameUseCode
import com.google.fhir.shaded.protobuf.ProtocolMessageEnum
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.codesystems.AdministrativeGender
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
        arrayOf(AdministrativeGender.MALE, AdministrativeGenderCode.Value.MALE),
        arrayOf(AdministrativeGender.OTHER, AdministrativeGenderCode.Value.OTHER),
        arrayOf(AdministrativeGender.UNKNOWN, AdministrativeGenderCode.Value.UNKNOWN),
        arrayOf(NameUse.NICKNAME, NameUseCode.Value.NICKNAME),
        arrayOf(NameUse.ANONYMOUS, NameUseCode.Value.ANONYMOUS),
        arrayOf(NameUse.MAIDEN, NameUseCode.Value.MAIDEN),
        arrayOf(NameUse.OFFICIAL, NameUseCode.Value.OFFICIAL),
        arrayOf(NameUse.OLD, NameUseCode.Value.OLD),
        arrayOf(NameUse.TEMP, NameUseCode.Value.TEMP),
        arrayOf(NameUse.USUAL, NameUseCode.Value.USUAL),
        arrayOf(Address.AddressType.BOTH, AddressTypeCode.Value.BOTH),
        arrayOf(Address.AddressType.PHYSICAL, AddressTypeCode.Value.PHYSICAL),
        arrayOf(Address.AddressType.POSTAL, AddressTypeCode.Value.POSTAL),
        arrayOf(Address.AddressUse.BILLING, AddressUseCode.Value.BILLING),
        arrayOf(Address.AddressUse.HOME, AddressUseCode.Value.HOME),
        arrayOf(Address.AddressUse.OLD, AddressUseCode.Value.OLD),
        arrayOf(Address.AddressUse.TEMP, AddressUseCode.Value.TEMP),
        arrayOf(Address.AddressUse.WORK, AddressUseCode.Value.WORK),
      )
    }
  }
}
