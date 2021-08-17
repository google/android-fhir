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

import com.google.fhir.r4.core.Url
import org.hl7.fhir.r4.model.UrlType

object UrlConverter {
  /** returns the proto Url equivalent of the hapi UrlType */
  fun UrlType.toProto(): Url {
    val protoValue = Url.newBuilder()
    if (hasValue()) protoValue.value = value
    return protoValue.build()
  }

  /** returns the hapi UrlType equivalent of the proto Url */
  fun Url.toHapi(): UrlType {
    val hapiValue = UrlType()
    hapiValue.value = value
    return hapiValue
  }
}
