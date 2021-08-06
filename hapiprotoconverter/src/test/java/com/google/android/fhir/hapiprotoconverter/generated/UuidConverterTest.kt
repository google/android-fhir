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

package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.PrimitiveTestData
import com.google.android.fhir.hapiprotoconverter.generated.UuidConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UuidConverter.toProto
import com.google.common.truth.Truth
import com.google.fhir.r4.core.Uuid
import kotlin.Any
import kotlin.collections.List
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.UuidType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
public class UuidConverterTest(private val hapi: UuidType, private val proto: Uuid) {
  @Test
  public fun hapi() {
    Truth.assertThat(proto.toHapi().value).isEqualTo(hapi.value)
  }

  @Test
  public fun proto() {
    Truth.assertThat(hapi.toProto()).isEqualTo(proto)
  }

  public companion object {
    @Parameterized.Parameters
    @JvmStatic
    public fun `data`(): List<Any> = PrimitiveTestData.UUID_DATA
  }
}
