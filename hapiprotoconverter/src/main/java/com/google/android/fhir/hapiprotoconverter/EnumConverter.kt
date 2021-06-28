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
/**
 * returns the hapi enum representation of @param hapiEnum
 * @param hapiEnum enum that needs to be converted to proto
 * @param protoEnumClass corresponding proto class that the hapi Enum will be converted to
 *
 * Note The value of the enum i.e. is represented differently in hapi and fhir protos for example
 * CodeSystemContentMode.NOTPRESENT in Hapi is equivalent to
 * CodeSystemContentModeCode.Value.NOT_PRESENT. However we can use the toCode method of the hapi
 * Enum returns `not-present` which we can convert to the proto value
 */
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

/**
 * returns the hapi enum representation of @param hapiEnum
 * @param protoEnum enum that needs to be converted to hapi
 * @param hapiEnumClass corresponding hapi class that the proto Enum will be converted to
 */
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
 * The name of the proto class that contains the actual enum values
 *
 * For example NameUse.NickName is equivalent to NameUseCode$Value.NickName
 */
private const val protoEnumValueClass = "Value"

/**
 * Converts the outer class of the HapiEnum to a ProtoEnumOuterClass Just converting Enum values
 * will not be sufficient to achieve complete conversion form hapi to proto (and vice versa) Fields
 * that accept an enumeratedValue have the type Enumeration<*> in hapi and have a corresponding
 * protoMessage in protos
 *
 * @param hapiOuterEnum the outer hapi enum that needs to be converted to proto
 * @param protoOuterEnumClass the outer proto class to which hapiEnum will be converted to
 *
 * For example the field use in HumanName has the Type Enumeration<HumanName.NameUse> and it's
 * corresponding fhir proto would be HumanName.UseCode
 */
@Suppress("UNCHECKED_CAST")
fun <T : GeneratedMessageV3> convert(
  hapiOuterEnum: IBaseEnumeration<*>,
  protoOuterEnumClass: Class<T>
): T {
  // create new builder
  val newBuilder =
    protoOuterEnumClass.getDeclaredMethod("newBuilder").invoke(null) as
      GeneratedMessageV3.Builder<*>

  /* Inner class of the protoEnum. For example Inner class for UseCode is NameUseCode which is
  located in the same package as the proto Enum outer class we can get the class name
  (NameUseCode) from the hapi class which is NameUseEnumFactory. Finally in this class we need a
  static subclass Value */
  val protoEnumInnerClass =
    Class.forName(
      "${protoOuterEnumClass.`package`!!.name}.${hapiOuterEnum.enumFactory::class.java.simpleName.removeSuffix(
        enumFactorySuffix
      )}${protoEnumSuffix}\$$protoEnumValueClass"
    )

  // set Value to the new Builder
  newBuilder::class
    .java
    .getDeclaredMethod("setValue", protoEnumInnerClass)
    .invoke(
      newBuilder,
      convert(hapiOuterEnum.value, protoEnumInnerClass as Class<out ProtocolMessageEnum>)
    )

  return newBuilder.build() as T
}

/**
 * Converts outer class of protoEnum to outer class of a hapi enum
 *
 * @param protoOuterEnum the outer proto enum that needs to be converted to hapi
 *
 * @param hapiOuterEnumClass the outer hapi class that the protoEnum will be converted to
 *
 * @param hapiEnumClass the inner hapi class
 *
 * For example the field use in HumanName has the Type Enumeration<HumanName.NameUse> and it's
 * corresponding fhir proto would be HumanName.UseCode. HumanName.Name use will be the inner class
 */
// TODO check if it is possible to infer param enumClass just from hapiEnumClass
fun <T : IBaseEnumeration<*>> convert(
  protoOuterEnum: GeneratedMessageV3,
  hapiOuterEnumClass: Class<T>,
  hapiEnumClass: Class<out Enum<*>>
): T {

  // enum factory of the hapi enum
  val enumFactory =
    Class.forName("${hapiEnumClass.name}$enumFactorySuffix") // example NameUseEnumFactory
  // create a new instance of the hapi enum
  val hapiEnum =
    hapiOuterEnumClass
      .getConstructor(enumFactory.interfaces[0])
      .newInstance(enumFactory.newInstance())
  // set value to the hapi enum
  hapiEnum.value =
    convert(
      protoOuterEnum::class.java.getDeclaredMethod("getValue").invoke(protoOuterEnum) as
        ProtocolMessageEnum,
      hapiEnumClass
    )
  return hapiEnum
}

/**
 * The suffix that the needs to be added to the enum class to get the corresponding enum factory
 * class
 *
 * For example HumanName.NameUse and its corresponding enum factory HumanName.NameUseEnumFactory
 */
private const val enumFactorySuffix = "EnumFactory"
