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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SupplyDelivery
import com.google.fhir.r4.core.SupplyDelivery.SuppliedItem
import com.google.fhir.r4.core.SupplyDeliveryStatusCode
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

object SupplyDeliveryConverter {
  @JvmStatic
  private fun SupplyDelivery.SuppliedItem.ItemX.supplyDeliverySuppliedItemItemToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SupplyDelivery.suppliedItem.item[x]")
  }

  @JvmStatic
  private fun Type.supplyDeliverySuppliedItemItemToProto(): SupplyDelivery.SuppliedItem.ItemX {
    val protoValue = SupplyDelivery.SuppliedItem.ItemX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
        protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun SupplyDelivery.OccurrenceX.supplyDeliveryOccurrenceToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SupplyDelivery.occurrence[x]")
  }

  @JvmStatic
  private fun Type.supplyDeliveryOccurrenceToProto(): SupplyDelivery.OccurrenceX {
    val protoValue = SupplyDelivery.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
        protoValue.timing = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun SupplyDelivery.toHapi(): org.hl7.fhir.r4.model.SupplyDelivery {
    val hapiValue = org.hl7.fhir.r4.model.SupplyDelivery()
    hapiValue.id = id.value
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
    if (basedOnCount > 0) {
        hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (partOfCount > 0) {
        hapiValue.partOf = partOfList.map { it.toHapi() }
    }
      hapiValue.status = org.hl7.fhir.r4.model.SupplyDelivery.SupplyDeliveryStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPatient()) {
        hapiValue.patient = patient.toHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasSuppliedItem()) {
        hapiValue.suppliedItem = suppliedItem.toHapi()
    }
    if (hasOccurrence()) {
        hapiValue.occurrence = occurrence.supplyDeliveryOccurrenceToHapi()
    }
    if (hasSupplier()) {
        hapiValue.supplier = supplier.toHapi()
    }
    if (hasDestination()) {
        hapiValue.destination = destination.toHapi()
    }
    if (receiverCount > 0) {
        hapiValue.receiver = receiverList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.SupplyDelivery.toProto(): SupplyDelivery {
    val protoValue = SupplyDelivery.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
      protoValue.status = SupplyDelivery.StatusCode.newBuilder()
          .setValue(
              SupplyDeliveryStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasPatient()) {
        protoValue.patient = patient.toProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasSuppliedItem()) {
        protoValue.suppliedItem = suppliedItem.toProto()
    }
    if (hasOccurrence()) {
        protoValue.occurrence = occurrence.supplyDeliveryOccurrenceToProto()
    }
    if (hasSupplier()) {
        protoValue.supplier = supplier.toProto()
    }
    if (hasDestination()) {
        protoValue.destination = destination.toProto()
    }
    if (hasReceiver()) {
      protoValue.addAllReceiver(receiver.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SupplyDelivery.SupplyDeliverySuppliedItemComponent.toProto():
    SupplyDelivery.SuppliedItem {
    val protoValue =
      SupplyDelivery.SuppliedItem.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasQuantity()) {
        protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasItem()) {
        protoValue.item = item.supplyDeliverySuppliedItemItemToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun SupplyDelivery.SuppliedItem.toHapi():
    org.hl7.fhir.r4.model.SupplyDelivery.SupplyDeliverySuppliedItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.SupplyDelivery.SupplyDeliverySuppliedItemComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasQuantity()) {
        hapiValue.quantity = quantity.toHapi()
    }
    if (hasItem()) {
        hapiValue.item = item.supplyDeliverySuppliedItemItemToHapi()
    }
    return hapiValue
  }
}
