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
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.TriggerDefinition
import com.google.fhir.r4.core.TriggerTypeCode
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.Type

object TriggerDefinitionConverter {
  private fun TriggerDefinition.TimingX.triggerDefinitionTimingToHapi(): Type {
    if (hasTiming()) {
      return (this.timing).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    if (hasDate()) {
      return (this.date).toHapi()
    }
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for TriggerDefinition.timing[x]")
  }

  private fun Type.triggerDefinitionTimingToProto(): TriggerDefinition.TimingX {
    val protoValue = TriggerDefinition.TimingX.newBuilder()
    if (this is Timing) {
      protoValue.timing = this.toProto()
    }
    if (this is Reference) {
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

  fun TriggerDefinition.toHapi(): org.hl7.fhir.r4.model.TriggerDefinition {
    val hapiValue = org.hl7.fhir.r4.model.TriggerDefinition()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type =
        org.hl7.fhir.r4.model.TriggerDefinition.TriggerType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
        )
    }
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

  fun org.hl7.fhir.r4.model.TriggerDefinition.toProto(): TriggerDefinition {
    val protoValue = TriggerDefinition.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type =
        TriggerDefinition.TypeCode.newBuilder()
          .setValue(
            TriggerTypeCode.Value.valueOf(
              type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
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
