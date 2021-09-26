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

package com.google.android.fhir.datacapture.utilities

import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.UriType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TypeConversionUtilTest {

  @Test
  fun stringType_toUriType() {
    val uri = StringType("fakeUri").toUriType()
    assertThat(uri.equalsDeep(UriType("fakeUri"))).isTrue()
  }

  @Test
  fun stringType_toCodeType() {
    val code = StringType("fakeCode").toCodeType()
    assertThat(code.equalsDeep(CodeType("fakeCode"))).isTrue()
  }

  @Test
  fun stringType_toIdType() {
    val id = StringType("fakeId").toIdType()
    assertThat(id.equalsDeep(IdType("fakeId"))).isTrue()
  }

  @Test
  fun coding_toCodeType() {
    val code = Coding("fakeSystem", "fakeCode", "fakeDisplay").toCodeType()
    assertThat(code.equalsDeep(CodeType("fakeCode"))).isTrue()
  }
}
