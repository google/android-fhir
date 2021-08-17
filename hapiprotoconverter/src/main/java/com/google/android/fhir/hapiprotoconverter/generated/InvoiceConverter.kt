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
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Invoice
import com.google.fhir.r4.core.Invoice.LineItem
import com.google.fhir.r4.core.Invoice.LineItem.PriceComponent
import com.google.fhir.r4.core.InvoicePriceComponentTypeCode
import com.google.fhir.r4.core.InvoiceStatusCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type

object InvoiceConverter {
  private fun Invoice.LineItem.ChargeItemX.invoiceLineItemChargeItemToHapi(): Type {
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Invoice.lineItem.chargeItem[x]")
  }

  private fun Type.invoiceLineItemChargeItemToProto(): Invoice.LineItem.ChargeItemX {
    val protoValue = Invoice.LineItem.ChargeItemX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  fun Invoice.toHapi(): org.hl7.fhir.r4.model.Invoice {
    val hapiValue = org.hl7.fhir.r4.model.Invoice()
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
    hapiValue.status =
      org.hl7.fhir.r4.model.Invoice.InvoiceStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasCancelledReason()) {
      hapiValue.cancelledReasonElement = cancelledReason.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasRecipient()) {
      hapiValue.recipient = recipient.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (participantCount > 0) {
      hapiValue.participant = participantList.map { it.toHapi() }
    }
    if (hasIssuer()) {
      hapiValue.issuer = issuer.toHapi()
    }
    if (hasAccount()) {
      hapiValue.account = account.toHapi()
    }
    if (lineItemCount > 0) {
      hapiValue.lineItem = lineItemList.map { it.toHapi() }
    }
    if (hasTotalNet()) {
      hapiValue.totalNet = totalNet.toHapi()
    }
    if (hasTotalGross()) {
      hapiValue.totalGross = totalGross.toHapi()
    }
    if (hasPaymentTerms()) {
      hapiValue.paymentTermsElement = paymentTerms.toHapi()
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Invoice.toProto(): Invoice {
    val protoValue = Invoice.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.status =
      Invoice.StatusCode.newBuilder()
        .setValue(
          InvoiceStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCancelledReason()) {
      protoValue.cancelledReason = cancelledReasonElement.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasRecipient()) {
      protoValue.recipient = recipient.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasParticipant()) {
      protoValue.addAllParticipant(participant.map { it.toProto() })
    }
    if (hasIssuer()) {
      protoValue.issuer = issuer.toProto()
    }
    if (hasAccount()) {
      protoValue.account = account.toProto()
    }
    if (hasLineItem()) {
      protoValue.addAllLineItem(lineItem.map { it.toProto() })
    }
    if (hasTotalNet()) {
      protoValue.totalNet = totalNet.toProto()
    }
    if (hasTotalGross()) {
      protoValue.totalGross = totalGross.toProto()
    }
    if (hasPaymentTerms()) {
      protoValue.paymentTerms = paymentTermsElement.toProto()
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Invoice.InvoiceParticipantComponent.toProto():
    Invoice.Participant {
    val protoValue = Invoice.Participant.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRole()) {
      protoValue.role = role.toProto()
    }
    if (hasActor()) {
      protoValue.actor = actor.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Invoice.InvoiceLineItemComponent.toProto(): Invoice.LineItem {
    val protoValue = Invoice.LineItem.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasChargeItem()) {
      protoValue.chargeItem = chargeItem.invoiceLineItemChargeItemToProto()
    }
    if (hasPriceComponent()) {
      protoValue.addAllPriceComponent(priceComponent.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Invoice.InvoiceLineItemPriceComponentComponent.toProto():
    Invoice.LineItem.PriceComponent {
    val protoValue =
      Invoice.LineItem.PriceComponent.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.type =
      Invoice.LineItem.PriceComponent.TypeCode.newBuilder()
        .setValue(
          InvoicePriceComponentTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasFactor()) {
      protoValue.factor = factorElement.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.toProto()
    }
    return protoValue.build()
  }

  private fun Invoice.Participant.toHapi():
    org.hl7.fhir.r4.model.Invoice.InvoiceParticipantComponent {
    val hapiValue = org.hl7.fhir.r4.model.Invoice.InvoiceParticipantComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasRole()) {
      hapiValue.role = role.toHapi()
    }
    if (hasActor()) {
      hapiValue.actor = actor.toHapi()
    }
    return hapiValue
  }

  private fun Invoice.LineItem.toHapi(): org.hl7.fhir.r4.model.Invoice.InvoiceLineItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.Invoice.InvoiceLineItemComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (hasChargeItem()) {
      hapiValue.chargeItem = chargeItem.invoiceLineItemChargeItemToHapi()
    }
    if (priceComponentCount > 0) {
      hapiValue.priceComponent = priceComponentList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun Invoice.LineItem.PriceComponent.toHapi():
    org.hl7.fhir.r4.model.Invoice.InvoiceLineItemPriceComponentComponent {
    val hapiValue = org.hl7.fhir.r4.model.Invoice.InvoiceLineItemPriceComponentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.type =
      org.hl7.fhir.r4.model.Invoice.InvoicePriceComponentType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasFactor()) {
      hapiValue.factorElement = factor.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.toHapi()
    }
    return hapiValue
  }
}
