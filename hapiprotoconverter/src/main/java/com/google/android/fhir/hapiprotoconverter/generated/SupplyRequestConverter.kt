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

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SupplyRequest
import com.google.fhir.r4.core.SupplyRequest.Parameter
import com.google.fhir.r4.core.SupplyRequestStatusCode
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Range
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.Type

object SupplyRequestConverter {
  private fun SupplyRequest.ItemX.supplyRequestItemToHapi(): Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SupplyRequest.item[x]")
  }

  private fun Type.supplyRequestItemToProto(): SupplyRequest.ItemX {
    val protoValue = SupplyRequest.ItemX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun SupplyRequest.Parameter.ValueX.supplyRequestParameterValueToHapi(): Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasQuantity()) {
      return (this.quantity).toHapi()
    }
    if (hasRange()) {
      return (this.range).toHapi()
    }
    if (hasBoolean()) {
      return (this.boolean).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SupplyRequest.parameter.value[x]")
  }

  private fun Type.supplyRequestParameterValueToProto(): SupplyRequest.Parameter.ValueX {
    val protoValue = SupplyRequest.Parameter.ValueX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is Range) {
      protoValue.range = this.toProto()
    }
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    return protoValue.build()
  }

  private fun SupplyRequest.OccurrenceX.supplyRequestOccurrenceToHapi(): Type {
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    if (hasTiming()) {
      return (this.timing).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SupplyRequest.occurrence[x]")
  }

  private fun Type.supplyRequestOccurrenceToProto(): SupplyRequest.OccurrenceX {
    val protoValue = SupplyRequest.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    if (this is Timing) {
      protoValue.timing = this.toProto()
    }
    return protoValue.build()
  }

  fun SupplyRequest.toHapi(): org.hl7.fhir.r4.model.SupplyRequest {
    val hapiValue = org.hl7.fhir.r4.model.SupplyRequest()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (hasMeta()) {
      hapiValue.meta = meta.toHapi()
    }
    if (hasImplicitRules()) {
      hapiValue.implicitRulesElement = implicitRules.toHapi()
    }
    if (hasText()) {
      hapiValue.text = text.toHapi()
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.SupplyRequest.SupplyRequestStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasPriority()) {
      hapiValue.priority =
        org.hl7.fhir.r4.model.SupplyRequest.RequestPriority.valueOf(
          priority.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasItem()) {
      hapiValue.item = item.supplyRequestItemToHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (parameterCount > 0) {
      hapiValue.parameter = parameterList.map { it.toHapi() }
    }
    if (hasOccurrence()) {
      hapiValue.occurrence = occurrence.supplyRequestOccurrenceToHapi()
    }
    if (hasAuthoredOn()) {
      hapiValue.authoredOnElement = authoredOn.toHapi()
    }
    if (hasRequester()) {
      hapiValue.requester = requester.toHapi()
    }
    if (supplierCount > 0) {
      hapiValue.supplier = supplierList.map { it.toHapi() }
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (hasDeliverFrom()) {
      hapiValue.deliverFrom = deliverFrom.toHapi()
    }
    if (hasDeliverTo()) {
      hapiValue.deliverTo = deliverTo.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.SupplyRequest.toProto(): SupplyRequest {
    val protoValue = SupplyRequest.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
    if (hasMeta()) {
      protoValue.meta = meta.toProto()
    }
    if (hasImplicitRules()) {
      protoValue.implicitRules = implicitRulesElement.toProto()
    }
    if (hasText()) {
      protoValue.text = text.toProto()
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        SupplyRequest.StatusCode.newBuilder()
          .setValue(
            SupplyRequestStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasPriority()) {
      protoValue.priority =
        SupplyRequest.PriorityCode.newBuilder()
          .setValue(
            RequestPriorityCode.Value.valueOf(
              priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasItem()) {
      protoValue.item = item.supplyRequestItemToProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = quantity.toProto()
    }
    if (hasParameter()) {
      protoValue.addAllParameter(parameter.map { it.toProto() })
    }
    if (hasOccurrence()) {
      protoValue.occurrence = occurrence.supplyRequestOccurrenceToProto()
    }
    if (hasAuthoredOn()) {
      protoValue.authoredOn = authoredOnElement.toProto()
    }
    if (hasRequester()) {
      protoValue.requester = requester.toProto()
    }
    if (hasSupplier()) {
      protoValue.addAllSupplier(supplier.map { it.toProto() })
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasDeliverFrom()) {
      protoValue.deliverFrom = deliverFrom.toProto()
    }
    if (hasDeliverTo()) {
      protoValue.deliverTo = deliverTo.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SupplyRequest.SupplyRequestParameterComponent.toProto():
    SupplyRequest.Parameter {
    val protoValue = SupplyRequest.Parameter.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasValue()) {
      protoValue.value = value.supplyRequestParameterValueToProto()
    }
    return protoValue.build()
  }

  private fun SupplyRequest.Parameter.toHapi():
    org.hl7.fhir.r4.model.SupplyRequest.SupplyRequestParameterComponent {
    val hapiValue = org.hl7.fhir.r4.model.SupplyRequest.SupplyRequestParameterComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasValue()) {
      hapiValue.value = value.supplyRequestParameterValueToHapi()
    }
    return hapiValue
  }
}
