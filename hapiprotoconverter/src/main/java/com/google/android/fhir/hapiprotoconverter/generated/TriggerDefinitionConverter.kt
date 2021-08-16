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

import com.google.android.fhir.hapiprotoconverter.generated.DataRequirementConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DataRequirementConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExpressionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExpressionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Timing
import com.google.fhir.r4.core.TriggerDefinition
import com.google.fhir.r4.core.TriggerTypeCode
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Type

object TriggerDefinitionConverter {
  @JvmStatic
  private fun TriggerDefinition.TimingX.triggerDefinitionTimingToHapi(): Type {
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for TriggerDefinition.timing[x]")
  }

  @JvmStatic
  private fun Type.triggerDefinitionTimingToProto(): TriggerDefinition.TimingX {
    val protoValue = TriggerDefinition.TimingX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Timing) {
        protoValue.timing = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    if (this is DateType) {
        protoValue.date = this.toProto()
    }
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun TriggerDefinition.toHapi(): org.hl7.fhir.r4.model.TriggerDefinition {
    val hapiValue = org.hl7.fhir.r4.model.TriggerDefinition()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
      hapiValue.type = org.hl7.fhir.r4.model.TriggerDefinition.TriggerType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasName()) {
        hapiValue.nameElement = name.toHapi()
    }
    if (hasTiming()) {
        hapiValue.timing = timing.triggerDefinitionTimingToHapi()
    }
    if (dataCount > 0) {
        hapiValue.data = dataList.map { it.toHapi() }
    }
    if (hasCondition()) {
        hapiValue.condition = condition.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.TriggerDefinition.toProto(): TriggerDefinition {
    val protoValue = TriggerDefinition.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
      protoValue.type = TriggerDefinition.TypeCode.newBuilder()
          .setValue(
              TriggerTypeCode.Value.valueOf(
                  type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasName()) {
        protoValue.name = nameElement.toProto()
    }
    if (hasTiming()) {
        protoValue.timing = timing.triggerDefinitionTimingToProto()
    }
    if (hasData()) {
      protoValue.addAllData(data.map { it.toProto() })
    }
    if (hasCondition()) {
        protoValue.condition = condition.toProto()
    }
    return protoValue.build()
  }
}
