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

import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Address
import com.google.fhir.r4.core.ClaimProcessingCode
import com.google.fhir.r4.core.ClaimResponse
import com.google.fhir.r4.core.ClaimResponse.AddedItem
import com.google.fhir.r4.core.ClaimResponse.AddedItem.AddedItemDetail
import com.google.fhir.r4.core.ClaimResponse.Item
import com.google.fhir.r4.core.ClaimResponse.Item.ItemDetail
import com.google.fhir.r4.core.ClaimResponse.Note
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.NoteTypeCode
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.UseCode
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

object ClaimResponseConverter {
  @JvmStatic
  private fun ClaimResponse.AddedItem.ServicedX.claimResponseAddItemServicedToHapi(): Type {
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ClaimResponse.addItem.serviced[x]")
  }

  @JvmStatic
  private fun Type.claimResponseAddItemServicedToProto(): ClaimResponse.AddedItem.ServicedX {
    val protoValue = ClaimResponse.AddedItem.ServicedX.newBuilder()
    if (this is DateType) {
      protoValue.date = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ClaimResponse.AddedItem.LocationX.claimResponseAddItemLocationToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.address != Address.newBuilder().defaultInstanceForType) {
      return (this.address).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ClaimResponse.addItem.location[x]")
  }

  @JvmStatic
  private fun Type.claimResponseAddItemLocationToProto(): ClaimResponse.AddedItem.LocationX {
    val protoValue = ClaimResponse.AddedItem.LocationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.address = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun ClaimResponse.toHapi(): org.hl7.fhir.r4.model.ClaimResponse {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse()
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
      org.hl7.fhir.r4.model.ClaimResponse.ClaimResponseStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasSubType()) {
      hapiValue.subType = subType.toHapi()
    }
    hapiValue.use =
      org.hl7.fhir.r4.model.ClaimResponse.Use.valueOf(
        use.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (hasCreated()) {
      hapiValue.createdElement = created.toHapi()
    }
    if (hasInsurer()) {
      hapiValue.insurer = insurer.toHapi()
    }
    if (hasRequestor()) {
      hapiValue.requestor = requestor.toHapi()
    }
    if (hasRequest()) {
      hapiValue.request = request.toHapi()
    }
    hapiValue.outcome =
      org.hl7.fhir.r4.model.ClaimResponse.RemittanceOutcome.valueOf(
        outcome.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasDisposition()) {
      hapiValue.dispositionElement = disposition.toHapi()
    }
    if (hasPreAuthRef()) {
      hapiValue.preAuthRefElement = preAuthRef.toHapi()
    }
    if (hasPreAuthPeriod()) {
      hapiValue.preAuthPeriod = preAuthPeriod.toHapi()
    }
    if (hasPayeeType()) {
      hapiValue.payeeType = payeeType.toHapi()
    }
    if (itemCount > 0) {
      hapiValue.item = itemList.map { it.toHapi() }
    }
    if (addItemCount > 0) {
      hapiValue.addItem = addItemList.map { it.toHapi() }
    }
    if (totalCount > 0) {
      hapiValue.total = totalList.map { it.toHapi() }
    }
    if (hasPayment()) {
      hapiValue.payment = payment.toHapi()
    }
    if (hasFundsReserve()) {
      hapiValue.fundsReserve = fundsReserve.toHapi()
    }
    if (hasFormCode()) {
      hapiValue.formCode = formCode.toHapi()
    }
    if (hasForm()) {
      hapiValue.form = form.toHapi()
    }
    if (processNoteCount > 0) {
      hapiValue.processNote = processNoteList.map { it.toHapi() }
    }
    if (communicationRequestCount > 0) {
      hapiValue.communicationRequest = communicationRequestList.map { it.toHapi() }
    }
    if (insuranceCount > 0) {
      hapiValue.insurance = insuranceList.map { it.toHapi() }
    }
    if (errorCount > 0) {
      hapiValue.error = errorList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.ClaimResponse.toProto(): ClaimResponse {
    val protoValue = ClaimResponse.newBuilder().setId(Id.newBuilder().setValue(id))
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
      ClaimResponse.StatusCode.newBuilder()
        .setValue(
          FinancialResourceStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasSubType()) {
      protoValue.subType = subType.toProto()
    }
    protoValue.use =
      ClaimResponse.UseCode.newBuilder()
        .setValue(
          UseCode.Value.valueOf(use.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
        )
        .build()
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasCreated()) {
      protoValue.created = createdElement.toProto()
    }
    if (hasInsurer()) {
      protoValue.insurer = insurer.toProto()
    }
    if (hasRequestor()) {
      protoValue.requestor = requestor.toProto()
    }
    if (hasRequest()) {
      protoValue.request = request.toProto()
    }
    protoValue.outcome =
      ClaimResponse.OutcomeCode.newBuilder()
        .setValue(
          ClaimProcessingCode.Value.valueOf(
            outcome.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasDisposition()) {
      protoValue.disposition = dispositionElement.toProto()
    }
    if (hasPreAuthRef()) {
      protoValue.preAuthRef = preAuthRefElement.toProto()
    }
    if (hasPreAuthPeriod()) {
      protoValue.preAuthPeriod = preAuthPeriod.toProto()
    }
    if (hasPayeeType()) {
      protoValue.payeeType = payeeType.toProto()
    }
    if (hasItem()) {
      protoValue.addAllItem(item.map { it.toProto() })
    }
    if (hasAddItem()) {
      protoValue.addAllAddItem(addItem.map { it.toProto() })
    }
    if (hasTotal()) {
      protoValue.addAllTotal(total.map { it.toProto() })
    }
    if (hasPayment()) {
      protoValue.payment = payment.toProto()
    }
    if (hasFundsReserve()) {
      protoValue.fundsReserve = fundsReserve.toProto()
    }
    if (hasFormCode()) {
      protoValue.formCode = formCode.toProto()
    }
    if (hasForm()) {
      protoValue.form = form.toProto()
    }
    if (hasProcessNote()) {
      protoValue.addAllProcessNote(processNote.map { it.toProto() })
    }
    if (hasCommunicationRequest()) {
      protoValue.addAllCommunicationRequest(communicationRequest.map { it.toProto() })
    }
    if (hasInsurance()) {
      protoValue.addAllInsurance(insurance.map { it.toProto() })
    }
    if (hasError()) {
      protoValue.addAllError(error.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.ItemComponent.toProto(): ClaimResponse.Item {
    val protoValue = ClaimResponse.Item.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasItemSequence()) {
      protoValue.itemSequence = itemSequenceElement.toProto()
    }
    if (hasNoteNumber()) {
      protoValue.addAllNoteNumber(noteNumber.map { it.toProto() })
    }
    if (hasAdjudication()) {
      protoValue.addAllAdjudication(adjudication.map { it.toProto() })
    }
    if (hasDetail()) {
      protoValue.addAllDetail(detail.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.AdjudicationComponent.toProto():
    ClaimResponse.Item.Adjudication {
    val protoValue =
      ClaimResponse.Item.Adjudication.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasReason()) {
      protoValue.reason = reason.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.toProto()
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.ItemDetailComponent.toProto():
    ClaimResponse.Item.ItemDetail {
    val protoValue =
      ClaimResponse.Item.ItemDetail.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDetailSequence()) {
      protoValue.detailSequence = detailSequenceElement.toProto()
    }
    if (hasNoteNumber()) {
      protoValue.addAllNoteNumber(noteNumber.map { it.toProto() })
    }
    if (hasSubDetail()) {
      protoValue.addAllSubDetail(subDetail.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.SubDetailComponent.toProto():
    ClaimResponse.Item.ItemDetail.SubDetail {
    val protoValue =
      ClaimResponse.Item.ItemDetail.SubDetail.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSubDetailSequence()) {
      protoValue.subDetailSequence = subDetailSequenceElement.toProto()
    }
    if (hasNoteNumber()) {
      protoValue.addAllNoteNumber(noteNumber.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.AddedItemComponent.toProto():
    ClaimResponse.AddedItem {
    val protoValue = ClaimResponse.AddedItem.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasItemSequence()) {
      protoValue.addAllItemSequence(itemSequence.map { it.toProto() })
    }
    if (hasDetailSequence()) {
      protoValue.addAllDetailSequence(detailSequence.map { it.toProto() })
    }
    if (hasSubdetailSequence()) {
      protoValue.addAllSubdetailSequence(subdetailSequence.map { it.toProto() })
    }
    if (hasProvider()) {
      protoValue.addAllProvider(provider.map { it.toProto() })
    }
    if (hasProductOrService()) {
      protoValue.productOrService = productOrService.toProto()
    }
    if (hasModifier()) {
      protoValue.addAllModifier(modifier.map { it.toProto() })
    }
    if (hasProgramCode()) {
      protoValue.addAllProgramCode(programCode.map { it.toProto() })
    }
    if (hasServiced()) {
      protoValue.serviced = serviced.claimResponseAddItemServicedToProto()
    }
    if (hasLocation()) {
      protoValue.location = location.claimResponseAddItemLocationToProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasUnitPrice()) {
      protoValue.unitPrice = unitPrice.toProto()
    }
    if (hasFactor()) {
      protoValue.factor = factorElement.toProto()
    }
    if (hasNet()) {
      protoValue.net = net.toProto()
    }
    if (hasBodySite()) {
      protoValue.bodySite = bodySite.toProto()
    }
    if (hasSubSite()) {
      protoValue.addAllSubSite(subSite.map { it.toProto() })
    }
    if (hasNoteNumber()) {
      protoValue.addAllNoteNumber(noteNumber.map { it.toProto() })
    }
    if (hasDetail()) {
      protoValue.addAllDetail(detail.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.AddedItemDetailComponent.toProto():
    ClaimResponse.AddedItem.AddedItemDetail {
    val protoValue =
      ClaimResponse.AddedItem.AddedItemDetail.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasProductOrService()) {
      protoValue.productOrService = productOrService.toProto()
    }
    if (hasModifier()) {
      protoValue.addAllModifier(modifier.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasUnitPrice()) {
      protoValue.unitPrice = unitPrice.toProto()
    }
    if (hasFactor()) {
      protoValue.factor = factorElement.toProto()
    }
    if (hasNet()) {
      protoValue.net = net.toProto()
    }
    if (hasNoteNumber()) {
      protoValue.addAllNoteNumber(noteNumber.map { it.toProto() })
    }
    if (hasSubDetail()) {
      protoValue.addAllSubDetail(subDetail.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.AddedItemSubDetailComponent.toProto():
    ClaimResponse.AddedItem.AddedItemDetail.AddedItemSubDetail {
    val protoValue =
      ClaimResponse.AddedItem.AddedItemDetail.AddedItemSubDetail.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasProductOrService()) {
      protoValue.productOrService = productOrService.toProto()
    }
    if (hasModifier()) {
      protoValue.addAllModifier(modifier.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasUnitPrice()) {
      protoValue.unitPrice = unitPrice.toProto()
    }
    if (hasFactor()) {
      protoValue.factor = factorElement.toProto()
    }
    if (hasNet()) {
      protoValue.net = net.toProto()
    }
    if (hasNoteNumber()) {
      protoValue.addAllNoteNumber(noteNumber.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.TotalComponent.toProto(): ClaimResponse.Total {
    val protoValue = ClaimResponse.Total.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.PaymentComponent.toProto():
    ClaimResponse.Payment {
    val protoValue = ClaimResponse.Payment.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasAdjustment()) {
      protoValue.adjustment = adjustment.toProto()
    }
    if (hasAdjustmentReason()) {
      protoValue.adjustmentReason = adjustmentReason.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.toProto()
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.NoteComponent.toProto(): ClaimResponse.Note {
    val protoValue = ClaimResponse.Note.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasNumber()) {
      protoValue.number = numberElement.toProto()
    }
    protoValue.type =
      ClaimResponse.Note.TypeCode.newBuilder()
        .setValue(
          NoteTypeCode.Value.valueOf(type.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
        )
        .build()
    if (hasText()) {
      protoValue.text = textElement.toProto()
    }
    if (hasLanguage()) {
      protoValue.language = language.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.InsuranceComponent.toProto():
    ClaimResponse.Insurance {
    val protoValue = ClaimResponse.Insurance.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasFocal()) {
      protoValue.focal = focalElement.toProto()
    }
    if (hasCoverage()) {
      protoValue.coverage = coverage.toProto()
    }
    if (hasBusinessArrangement()) {
      protoValue.businessArrangement = businessArrangementElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ClaimResponse.ErrorComponent.toProto(): ClaimResponse.Error {
    val protoValue = ClaimResponse.Error.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasItemSequence()) {
      protoValue.itemSequence = itemSequenceElement.toProto()
    }
    if (hasDetailSequence()) {
      protoValue.detailSequence = detailSequenceElement.toProto()
    }
    if (hasSubDetailSequence()) {
      protoValue.subDetailSequence = subDetailSequenceElement.toProto()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ClaimResponse.Item.toHapi(): org.hl7.fhir.r4.model.ClaimResponse.ItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.ItemComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasItemSequence()) {
      hapiValue.itemSequenceElement = itemSequence.toHapi()
    }
    if (noteNumberCount > 0) {
      hapiValue.noteNumber = noteNumberList.map { it.toHapi() }
    }
    if (adjudicationCount > 0) {
      hapiValue.adjudication = adjudicationList.map { it.toHapi() }
    }
    if (detailCount > 0) {
      hapiValue.detail = detailList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Item.Adjudication.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.AdjudicationComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.AdjudicationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasReason()) {
      hapiValue.reason = reason.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.toHapi()
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Item.ItemDetail.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.ItemDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.ItemDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDetailSequence()) {
      hapiValue.detailSequenceElement = detailSequence.toHapi()
    }
    if (noteNumberCount > 0) {
      hapiValue.noteNumber = noteNumberList.map { it.toHapi() }
    }
    if (subDetailCount > 0) {
      hapiValue.subDetail = subDetailList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Item.ItemDetail.SubDetail.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.SubDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.SubDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSubDetailSequence()) {
      hapiValue.subDetailSequenceElement = subDetailSequence.toHapi()
    }
    if (noteNumberCount > 0) {
      hapiValue.noteNumber = noteNumberList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.AddedItem.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.AddedItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.AddedItemComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (itemSequenceCount > 0) {
      hapiValue.itemSequence = itemSequenceList.map { it.toHapi() }
    }
    if (detailSequenceCount > 0) {
      hapiValue.detailSequence = detailSequenceList.map { it.toHapi() }
    }
    if (subdetailSequenceCount > 0) {
      hapiValue.subdetailSequence = subdetailSequenceList.map { it.toHapi() }
    }
    if (providerCount > 0) {
      hapiValue.provider = providerList.map { it.toHapi() }
    }
    if (hasProductOrService()) {
      hapiValue.productOrService = productOrService.toHapi()
    }
    if (modifierCount > 0) {
      hapiValue.modifier = modifierList.map { it.toHapi() }
    }
    if (programCodeCount > 0) {
      hapiValue.programCode = programCodeList.map { it.toHapi() }
    }
    if (hasServiced()) {
      hapiValue.serviced = serviced.claimResponseAddItemServicedToHapi()
    }
    if (hasLocation()) {
      hapiValue.location = location.claimResponseAddItemLocationToHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasUnitPrice()) {
      hapiValue.unitPrice = unitPrice.toHapi()
    }
    if (hasFactor()) {
      hapiValue.factorElement = factor.toHapi()
    }
    if (hasNet()) {
      hapiValue.net = net.toHapi()
    }
    if (hasBodySite()) {
      hapiValue.bodySite = bodySite.toHapi()
    }
    if (subSiteCount > 0) {
      hapiValue.subSite = subSiteList.map { it.toHapi() }
    }
    if (noteNumberCount > 0) {
      hapiValue.noteNumber = noteNumberList.map { it.toHapi() }
    }
    if (detailCount > 0) {
      hapiValue.detail = detailList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.AddedItem.AddedItemDetail.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.AddedItemDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.AddedItemDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasProductOrService()) {
      hapiValue.productOrService = productOrService.toHapi()
    }
    if (modifierCount > 0) {
      hapiValue.modifier = modifierList.map { it.toHapi() }
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasUnitPrice()) {
      hapiValue.unitPrice = unitPrice.toHapi()
    }
    if (hasFactor()) {
      hapiValue.factorElement = factor.toHapi()
    }
    if (hasNet()) {
      hapiValue.net = net.toHapi()
    }
    if (noteNumberCount > 0) {
      hapiValue.noteNumber = noteNumberList.map { it.toHapi() }
    }
    if (subDetailCount > 0) {
      hapiValue.subDetail = subDetailList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.AddedItem.AddedItemDetail.AddedItemSubDetail.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.AddedItemSubDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.AddedItemSubDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasProductOrService()) {
      hapiValue.productOrService = productOrService.toHapi()
    }
    if (modifierCount > 0) {
      hapiValue.modifier = modifierList.map { it.toHapi() }
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasUnitPrice()) {
      hapiValue.unitPrice = unitPrice.toHapi()
    }
    if (hasFactor()) {
      hapiValue.factorElement = factor.toHapi()
    }
    if (hasNet()) {
      hapiValue.net = net.toHapi()
    }
    if (noteNumberCount > 0) {
      hapiValue.noteNumber = noteNumberList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Total.toHapi(): org.hl7.fhir.r4.model.ClaimResponse.TotalComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.TotalComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Payment.toHapi(): org.hl7.fhir.r4.model.ClaimResponse.PaymentComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.PaymentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasAdjustment()) {
      hapiValue.adjustment = adjustment.toHapi()
    }
    if (hasAdjustmentReason()) {
      hapiValue.adjustmentReason = adjustmentReason.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.toHapi()
    }
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Note.toHapi(): org.hl7.fhir.r4.model.ClaimResponse.NoteComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.NoteComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasNumber()) {
      hapiValue.numberElement = number.toHapi()
    }
    hapiValue.type = Enumerations.NoteType.valueOf(type.value.name.hapiCodeCheck().replace("_", ""))
    if (hasText()) {
      hapiValue.textElement = text.toHapi()
    }
    if (hasLanguage()) {
      hapiValue.language = language.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Insurance.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.InsuranceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.InsuranceComponent()
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
    if (hasFocal()) {
      hapiValue.focalElement = focal.toHapi()
    }
    if (hasCoverage()) {
      hapiValue.coverage = coverage.toHapi()
    }
    if (hasBusinessArrangement()) {
      hapiValue.businessArrangementElement = businessArrangement.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Error.toHapi(): org.hl7.fhir.r4.model.ClaimResponse.ErrorComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.ErrorComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasItemSequence()) {
      hapiValue.itemSequenceElement = itemSequence.toHapi()
    }
    if (hasDetailSequence()) {
      hapiValue.detailSequenceElement = detailSequence.toHapi()
    }
    if (hasSubDetailSequence()) {
      hapiValue.subDetailSequenceElement = subDetailSequence.toHapi()
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    return hapiValue
  }
}
