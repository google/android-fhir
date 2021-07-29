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
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

public object SupplyDeliveryConverter {
  private fun SupplyDelivery.SuppliedItem.ItemX.supplyDeliverySuppliedItemItemToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SupplyDelivery.suppliedItem.item[x]")
  }

  private fun Type.supplyDeliverySuppliedItemItemToProto(): SupplyDelivery.SuppliedItem.ItemX {
    val protoValue = SupplyDelivery.SuppliedItem.ItemX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  private fun SupplyDelivery.OccurrenceX.supplyDeliveryOccurrenceToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SupplyDelivery.occurrence[x]")
  }

  private fun Type.supplyDeliveryOccurrenceToProto(): SupplyDelivery.OccurrenceX {
    val protoValue = SupplyDelivery.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    return protoValue.build()
  }

  public fun SupplyDelivery.toHapi(): org.hl7.fhir.r4.model.SupplyDelivery {
    val hapiValue = org.hl7.fhir.r4.model.SupplyDelivery()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setBasedOn(basedOnList.map { it.toHapi() })
    hapiValue.setPartOf(partOfList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.SupplyDelivery.SupplyDeliveryStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setSuppliedItem(suppliedItem.toHapi())
    hapiValue.setOccurrence(occurrence.supplyDeliveryOccurrenceToHapi())
    hapiValue.setSupplier(supplier.toHapi())
    hapiValue.setDestination(destination.toHapi())
    hapiValue.setReceiver(receiverList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.SupplyDelivery.toProto(): SupplyDelivery {
    val protoValue =
      SupplyDelivery.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllBasedOn(basedOn.map { it.toProto() })
        .addAllPartOf(partOf.map { it.toProto() })
        .setStatus(
          SupplyDelivery.StatusCode.newBuilder()
            .setValue(
              SupplyDeliveryStatusCode.Value.valueOf(
                status.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setPatient(patient.toProto())
        .setType(type.toProto())
        .setSuppliedItem(suppliedItem.toProto())
        .setOccurrence(occurrence.supplyDeliveryOccurrenceToProto())
        .setSupplier(supplier.toProto())
        .setDestination(destination.toProto())
        .addAllReceiver(receiver.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.SupplyDelivery.SupplyDeliverySuppliedItemComponent.toProto():
    SupplyDelivery.SuppliedItem {
    val protoValue =
      SupplyDelivery.SuppliedItem.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setQuantity((quantity as SimpleQuantity).toProto())
        .setItem(item.supplyDeliverySuppliedItemItemToProto())
        .build()
    return protoValue
  }

  private fun SupplyDelivery.SuppliedItem.toHapi():
    org.hl7.fhir.r4.model.SupplyDelivery.SupplyDeliverySuppliedItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.SupplyDelivery.SupplyDeliverySuppliedItemComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setItem(item.supplyDeliverySuppliedItemItemToHapi())
    return hapiValue
  }
}
