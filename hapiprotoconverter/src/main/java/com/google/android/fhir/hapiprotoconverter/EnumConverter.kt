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

import android.annotation.SuppressLint
import com.google.fhir.shaded.protobuf.ProtocolMessageEnum

@SuppressLint("DefaultLocale")
fun <T : ProtocolMessageEnum> convert(hapiEnum: Enum<*>, protoEnumClass: Class<T>): T {
  // Ensures that protoClass and hapiClass represent the same datatype
  /* In proto the actual enum is in the class AdministrativeGenderCode.Value so if we want to check
  if the types are interconvertible we'll have to check that their enclosing classes match the
  given condition */
  require(
    (protoEnumClass.enclosingClass?.simpleName ?: protoEnumClass.name).removeSuffix(
      protoEnumSuffix
    ) == hapiEnum::class.java.simpleName
  ) { "Cannot convert ${hapiEnum::class.java.name} to ${protoEnumClass.enclosingClass?.name}" }

  @Suppress("UNCHECKED_CAST")
  return protoEnumClass
    .getDeclaredField(
      (hapiEnum::class.java.getMethod("toCode").invoke(hapiEnum) as String)
        .toUpperCase()
        .replace('-', '_')
    )
    .get(null) as
    T
}

fun <T : Enum<*>> convert(protoEnum: ProtocolMessageEnum, hapiEnumClass: Class<T>): T {
  // Ensures that protoClass and hapiClass represent the same datatype
  /* In proto the actual enum is in the class AdministrativeGenderCode.Value so if we want to check
  if the types are interconvertible we'll have to check that their enclosing classes match the
  given condition */

  require(
    (protoEnum::class.java.enclosingClass?.simpleName ?: protoEnum::class.java.name).removeSuffix(
      protoEnumSuffix
    ) == hapiEnumClass.simpleName
  ) { "Cannot convert ${protoEnum::class.java.name} to ${hapiEnumClass::class.java.name}" }

  @Suppress("UNCHECKED_CAST")
  return hapiEnumClass
    .getMethod("valueOf", String::class.java)
    .invoke(null, protoEnum.valueDescriptor.name.replace("_", "")) as
    T
}

/**
 * Suffix that needs to be removed from the proto Enum so that the simple name matches the
 * corresponding hapi Enum type
 *
 * For example AdministrativeGender in hapi is equivalent to AdministrativeGenderCode in Fhir proto
 */
const val protoEnumSuffix = "Code"
