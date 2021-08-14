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

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.ChargeItem
import com.google.fhir.r4.core.ChargeItemStatusCode
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object ChargeItemConverter {
  @JvmStatic
  private fun ChargeItem.OccurrenceX.chargeItemOccurrenceToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ChargeItem.occurrence[x]")
  }

  @JvmStatic
  private fun Type.chargeItemOccurrenceToProto(): ChargeItem.OccurrenceX {
    val protoValue = ChargeItem.OccurrenceX.newBuilder()
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

  @JvmStatic
  private fun ChargeItem.ProductX.chargeItemProductToHapi(): Type {
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ChargeItem.product[x]")
  }

  @JvmStatic
  private fun Type.chargeItemProductToProto(): ChargeItem.ProductX {
    val protoValue = ChargeItem.ProductX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun ChargeItem.toHapi(): org.hl7.fhir.r4.model.ChargeItem {
    val hapiValue = org.hl7.fhir.r4.model.ChargeItem()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setDefinitionUri(definitionUriList.map { it.toHapi() })
    hapiValue.setDefinitionCanonical(definitionCanonicalList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.ChargeItem.ChargeItemStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setPartOf(partOfList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setContext(context.toHapi())
    hapiValue.setOccurrence(occurrence.chargeItemOccurrenceToHapi())
    hapiValue.setPerformer(performerList.map { it.toHapi() })
    hapiValue.setPerformingOrganization(performingOrganization.toHapi())
    hapiValue.setRequestingOrganization(requestingOrganization.toHapi())
    hapiValue.setCostCenter(costCenter.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setBodysite(bodysiteList.map { it.toHapi() })
    hapiValue.setFactorOverrideElement(factorOverride.toHapi())
    hapiValue.setPriceOverride(priceOverride.toHapi())
    hapiValue.setOverrideReasonElement(overrideReason.toHapi())
    hapiValue.setEnterer(enterer.toHapi())
    hapiValue.setEnteredDateElement(enteredDate.toHapi())
    hapiValue.setReason(reasonList.map { it.toHapi() })
    hapiValue.setService(serviceList.map { it.toHapi() })
    hapiValue.setProduct(product.chargeItemProductToHapi())
    hapiValue.setAccount(accountList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setSupportingInformation(supportingInformationList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ChargeItem.toProto(): ChargeItem {
    val protoValue =
      ChargeItem.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllDefinitionUri(definitionUri.map { it.toProto() })
        .addAllDefinitionCanonical(definitionCanonical.map { it.toProto() })
        .setStatus(
          ChargeItem.StatusCode.newBuilder()
            .setValue(
              ChargeItemStatusCode.Value.valueOf(
                status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .addAllPartOf(partOf.map { it.toProto() })
        .setCode(code.toProto())
        .setSubject(subject.toProto())
        .setContext(context.toProto())
        .setOccurrence(occurrence.chargeItemOccurrenceToProto())
        .addAllPerformer(performer.map { it.toProto() })
        .setPerformingOrganization(performingOrganization.toProto())
        .setRequestingOrganization(requestingOrganization.toProto())
        .setCostCenter(costCenter.toProto())
        .setQuantity(quantity.toProto())
        .addAllBodysite(bodysite.map { it.toProto() })
        .setFactorOverride(factorOverrideElement.toProto())
        .setPriceOverride(priceOverride.toProto())
        .setOverrideReason(overrideReasonElement.toProto())
        .setEnterer(enterer.toProto())
        .setEnteredDate(enteredDateElement.toProto())
        .addAllReason(reason.map { it.toProto() })
        .addAllService(service.map { it.toProto() })
        .setProduct(product.chargeItemProductToProto())
        .addAllAccount(account.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .addAllSupportingInformation(supportingInformation.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ChargeItem.ChargeItemPerformerComponent.toProto():
    ChargeItem.Performer {
    val protoValue =
      ChargeItem.Performer.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setFunction(function.toProto())
        .setActor(actor.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun ChargeItem.Performer.toHapi():
    org.hl7.fhir.r4.model.ChargeItem.ChargeItemPerformerComponent {
    val hapiValue = org.hl7.fhir.r4.model.ChargeItem.ChargeItemPerformerComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setFunction(function.toHapi())
    hapiValue.setActor(actor.toHapi())
    return hapiValue
  }
}
