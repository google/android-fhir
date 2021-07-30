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

import com.google.fhir.r4.core.Base64Binary
import com.google.fhir.shaded.protobuf.ByteString
import org.hl7.fhir.r4.model.Base64BinaryType

public object Base64BinaryConverter {
  /** returns the proto Base64Binary equivalent of the hapi Base64BinaryType */
  public fun Base64BinaryType.toProto(): Base64Binary {
    val protoValue = Base64Binary.newBuilder()
    if (valueAsString != null) protoValue.setValue(ByteString.copyFromUtf8(valueAsString))
    return protoValue.build()
  }

  /** returns the hapi Base64BinaryType equivalent of the proto Base64Binary */
  public fun Base64Binary.toHapi(): Base64BinaryType {
    val hapiValue = Base64BinaryType()
    hapiValue.valueAsString = value.toStringUtf8()
    return hapiValue
  }
}
