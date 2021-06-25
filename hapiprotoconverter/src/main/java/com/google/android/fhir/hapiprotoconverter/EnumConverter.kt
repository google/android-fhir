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
import com.google.fhir.shaded.protobuf.GeneratedMessageV3
import com.google.fhir.shaded.protobuf.ProtocolMessageEnum
import org.hl7.fhir.instance.model.api.IBaseEnumeration

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
  ) { "Cannot convert ${hapiEnum::class.java.name} to ${protoEnumClass.name}" }

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

/**
 * Converts the outer class of the HapiEnum to a ProtoEnumOuterClass Just converting Enum values
 * will not be sufficient to achieve complete conversion form hapi to proto (and vice versa) Fields
 * that accept an enumeratedValue have the type Enumeration<*> in hapi and have a corresponding
 * protoMessage in protos
 *
 * For example the field use in HumanName has the Type Enumeration<HumanName.NameUse> and it's
 * corresponding fhir proto would be HumanName.UseCode.
 */
@Suppress("UNCHECKED_CAST")
fun <T : GeneratedMessageV3> convert(hapiEnum: IBaseEnumeration<*>, protoEnumClass: Class<T>): T {
  // create new builder
  val newBuilder =
    protoEnumClass.getDeclaredMethod("newBuilder").invoke(null) as GeneratedMessageV3.Builder<*>

  /* Inner class of the protoEnum. For example Inner class for UseCode is NameUseCode which is
  located in the same package as the proto Enum outer class we can get the class name
  (NameUseCode) from the hapi class which is NameUseEnumFactory. Finally in this class we need a
  static subclass Value */
  val protoEnumInnerClass =
    Class.forName(
      "${protoEnumClass.`package`!!.name}.${hapiEnum.enumFactory::class.java.simpleName.removeSuffix("EnumFactory")}Code\$Value"
    )

  // set Value to the new Builder
  newBuilder::class
    .java
    .getDeclaredMethod("setValue", protoEnumInnerClass)
    .invoke(
      newBuilder,
      convert(hapiEnum.value, protoEnumInnerClass as Class<out ProtocolMessageEnum>)
    )

  return newBuilder.build() as T
}

/** Converts outer class of protoEnum to outer class of a hapi enum */
// TODO check if it is possible to infer param enumClass just from hapiEnumClass
fun <T : IBaseEnumeration<*>> convert(
  protoEnum: GeneratedMessageV3,
  hapiEnumClass: Class<T>,
  enumClass: Class<out Enum<*>>
): T {

  /*Inner Class or (enum factory) of the hapi enum*/
  val enumFactory = Class.forName(enumClass.name + "EnumFactory") // NameUseEnumFactory
  // create a new instance of the hapi enum
  val hapiEnum =
    hapiEnumClass.getConstructor(enumFactory.interfaces[0]).newInstance(enumFactory.newInstance())
  // set value to the hapi enum
  hapiEnum.value =
    convert(
      protoEnum::class.java.getDeclaredMethod("getValue").invoke(protoEnum) as ProtocolMessageEnum,
      enumClass
    )
  return hapiEnum
}
