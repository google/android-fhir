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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.Type

object ChargeItemConverter {
  private fun ChargeItem.OccurrenceX.chargeItemOccurrenceToHapi(): Type {
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    if (hasTiming()) {
      return (this.timing).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ChargeItem.occurrence[x]")
  }

  private fun Type.chargeItemOccurrenceToProto(): ChargeItem.OccurrenceX {
    val protoValue = ChargeItem.OccurrenceX.newBuilder()
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

  private fun ChargeItem.ProductX.chargeItemProductToHapi(): Type {
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ChargeItem.product[x]")
  }

  private fun Type.chargeItemProductToProto(): ChargeItem.ProductX {
    val protoValue = ChargeItem.ProductX.newBuilder()
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  fun ChargeItem.toHapi(): org.hl7.fhir.r4.model.ChargeItem {
    val hapiValue = org.hl7.fhir.r4.model.ChargeItem()
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
    if (definitionUriCount > 0) {
      hapiValue.definitionUri = definitionUriList.map { it.toHapi() }
    }
    if (definitionCanonicalCount > 0) {
      hapiValue.definitionCanonical = definitionCanonicalList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.ChargeItem.ChargeItemStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (partOfCount > 0) {
      hapiValue.partOf = partOfList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasContext()) {
      hapiValue.context = context.toHapi()
    }
    if (hasOccurrence()) {
      hapiValue.occurrence = occurrence.chargeItemOccurrenceToHapi()
    }
    if (performerCount > 0) {
      hapiValue.performer = performerList.map { it.toHapi() }
    }
    if (hasPerformingOrganization()) {
      hapiValue.performingOrganization = performingOrganization.toHapi()
    }
    if (hasRequestingOrganization()) {
      hapiValue.requestingOrganization = requestingOrganization.toHapi()
    }
    if (hasCostCenter()) {
      hapiValue.costCenter = costCenter.toHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (bodysiteCount > 0) {
      hapiValue.bodysite = bodysiteList.map { it.toHapi() }
    }
    if (hasFactorOverride()) {
      hapiValue.factorOverrideElement = factorOverride.toHapi()
    }
    if (hasPriceOverride()) {
      hapiValue.priceOverride = priceOverride.toHapi()
    }
    if (hasOverrideReason()) {
      hapiValue.overrideReasonElement = overrideReason.toHapi()
    }
    if (hasEnterer()) {
      hapiValue.enterer = enterer.toHapi()
    }
    if (hasEnteredDate()) {
      hapiValue.enteredDateElement = enteredDate.toHapi()
    }
    if (reasonCount > 0) {
      hapiValue.reason = reasonList.map { it.toHapi() }
    }
    if (serviceCount > 0) {
      hapiValue.service = serviceList.map { it.toHapi() }
    }
    if (hasProduct()) {
      hapiValue.product = product.chargeItemProductToHapi()
    }
    if (accountCount > 0) {
      hapiValue.account = accountList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (supportingInformationCount > 0) {
      hapiValue.supportingInformation = supportingInformationList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.ChargeItem.toProto(): ChargeItem {
    val protoValue = ChargeItem.newBuilder()
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
    if (hasDefinitionUri()) {
      protoValue.addAllDefinitionUri(definitionUri.map { it.toProto() })
    }
    if (hasDefinitionCanonical()) {
      protoValue.addAllDefinitionCanonical(definitionCanonical.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        ChargeItem.StatusCode.newBuilder()
          .setValue(
            ChargeItemStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasContext()) {
      protoValue.context = context.toProto()
    }
    if (hasOccurrence()) {
      protoValue.occurrence = occurrence.chargeItemOccurrenceToProto()
    }
    if (hasPerformer()) {
      protoValue.addAllPerformer(performer.map { it.toProto() })
    }
    if (hasPerformingOrganization()) {
      protoValue.performingOrganization = performingOrganization.toProto()
    }
    if (hasRequestingOrganization()) {
      protoValue.requestingOrganization = requestingOrganization.toProto()
    }
    if (hasCostCenter()) {
      protoValue.costCenter = costCenter.toProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = quantity.toProto()
    }
    if (hasBodysite()) {
      protoValue.addAllBodysite(bodysite.map { it.toProto() })
    }
    if (hasFactorOverride()) {
      protoValue.factorOverride = factorOverrideElement.toProto()
    }
    if (hasPriceOverride()) {
      protoValue.priceOverride = priceOverride.toProto()
    }
    if (hasOverrideReason()) {
      protoValue.overrideReason = overrideReasonElement.toProto()
    }
    if (hasEnterer()) {
      protoValue.enterer = enterer.toProto()
    }
    if (hasEnteredDate()) {
      protoValue.enteredDate = enteredDateElement.toProto()
    }
    if (hasReason()) {
      protoValue.addAllReason(reason.map { it.toProto() })
    }
    if (hasService()) {
      protoValue.addAllService(service.map { it.toProto() })
    }
    if (hasProduct()) {
      protoValue.product = product.chargeItemProductToProto()
    }
    if (hasAccount()) {
      protoValue.addAllAccount(account.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasSupportingInformation()) {
      protoValue.addAllSupportingInformation(supportingInformation.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ChargeItem.ChargeItemPerformerComponent.toProto():
    ChargeItem.Performer {
    val protoValue = ChargeItem.Performer.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasFunction()) {
      protoValue.function = function.toProto()
    }
    if (hasActor()) {
      protoValue.actor = actor.toProto()
    }
    return protoValue.build()
  }

  private fun ChargeItem.Performer.toHapi():
    org.hl7.fhir.r4.model.ChargeItem.ChargeItemPerformerComponent {
    val hapiValue = org.hl7.fhir.r4.model.ChargeItem.ChargeItemPerformerComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasFunction()) {
      hapiValue.function = function.toHapi()
    }
    if (hasActor()) {
      hapiValue.actor = actor.toHapi()
    }
    return hapiValue
  }
}
