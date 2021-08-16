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

public object ClaimResponseConverter {
  @JvmStatic
  private fun ClaimResponse.AddedItem.ServicedX.claimResponseAddItemServicedToHapi(): Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ClaimResponse.addItem.serviced[x]")
  }

  @JvmStatic
  private fun Type.claimResponseAddItemServicedToProto(): ClaimResponse.AddedItem.ServicedX {
    val protoValue = ClaimResponse.AddedItem.ServicedX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ClaimResponse.AddedItem.LocationX.claimResponseAddItemLocationToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getAddress() != Address.newBuilder().defaultInstanceForType) {
      return (this.getAddress()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ClaimResponse.addItem.location[x]")
  }

  @JvmStatic
  private fun Type.claimResponseAddItemLocationToProto(): ClaimResponse.AddedItem.LocationX {
    val protoValue = ClaimResponse.AddedItem.LocationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.setAddress(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun ClaimResponse.toHapi(): org.hl7.fhir.r4.model.ClaimResponse {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.setMeta(meta.toHapi())
    }
    if (hasImplicitRules()) {
      hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    }
    if (hasText()) {
      hapiValue.setText(text.toHapi())
    }
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.ClaimResponse.ClaimResponseStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasSubType()) {
      hapiValue.setSubType(subType.toHapi())
    }
    hapiValue.setUse(
      org.hl7.fhir.r4.model.ClaimResponse.Use.valueOf(
        use.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasPatient()) {
      hapiValue.setPatient(patient.toHapi())
    }
    if (hasCreated()) {
      hapiValue.setCreatedElement(created.toHapi())
    }
    if (hasInsurer()) {
      hapiValue.setInsurer(insurer.toHapi())
    }
    if (hasRequestor()) {
      hapiValue.setRequestor(requestor.toHapi())
    }
    if (hasRequest()) {
      hapiValue.setRequest(request.toHapi())
    }
    hapiValue.setOutcome(
      org.hl7.fhir.r4.model.ClaimResponse.RemittanceOutcome.valueOf(
        outcome.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasDisposition()) {
      hapiValue.setDispositionElement(disposition.toHapi())
    }
    if (hasPreAuthRef()) {
      hapiValue.setPreAuthRefElement(preAuthRef.toHapi())
    }
    if (hasPreAuthPeriod()) {
      hapiValue.setPreAuthPeriod(preAuthPeriod.toHapi())
    }
    if (hasPayeeType()) {
      hapiValue.setPayeeType(payeeType.toHapi())
    }
    if (itemCount > 0) {
      hapiValue.setItem(itemList.map { it.toHapi() })
    }
    if (addItemCount > 0) {
      hapiValue.setAddItem(addItemList.map { it.toHapi() })
    }
    if (totalCount > 0) {
      hapiValue.setTotal(totalList.map { it.toHapi() })
    }
    if (hasPayment()) {
      hapiValue.setPayment(payment.toHapi())
    }
    if (hasFundsReserve()) {
      hapiValue.setFundsReserve(fundsReserve.toHapi())
    }
    if (hasFormCode()) {
      hapiValue.setFormCode(formCode.toHapi())
    }
    if (hasForm()) {
      hapiValue.setForm(form.toHapi())
    }
    if (processNoteCount > 0) {
      hapiValue.setProcessNote(processNoteList.map { it.toHapi() })
    }
    if (communicationRequestCount > 0) {
      hapiValue.setCommunicationRequest(communicationRequestList.map { it.toHapi() })
    }
    if (insuranceCount > 0) {
      hapiValue.setInsurance(insuranceList.map { it.toHapi() })
    }
    if (errorCount > 0) {
      hapiValue.setError(errorList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ClaimResponse.toProto(): ClaimResponse {
    val protoValue = ClaimResponse.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.setMeta(meta.toProto())
    }
    if (hasImplicitRules()) {
      protoValue.setImplicitRules(implicitRulesElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(text.toProto())
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
    protoValue.setStatus(
      ClaimResponse.StatusCode.newBuilder()
        .setValue(
          FinancialResourceStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasSubType()) {
      protoValue.setSubType(subType.toProto())
    }
    protoValue.setUse(
      ClaimResponse.UseCode.newBuilder()
        .setValue(
          UseCode.Value.valueOf(use.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
        )
        .build()
    )
    if (hasPatient()) {
      protoValue.setPatient(patient.toProto())
    }
    if (hasCreated()) {
      protoValue.setCreated(createdElement.toProto())
    }
    if (hasInsurer()) {
      protoValue.setInsurer(insurer.toProto())
    }
    if (hasRequestor()) {
      protoValue.setRequestor(requestor.toProto())
    }
    if (hasRequest()) {
      protoValue.setRequest(request.toProto())
    }
    protoValue.setOutcome(
      ClaimResponse.OutcomeCode.newBuilder()
        .setValue(
          ClaimProcessingCode.Value.valueOf(
            outcome.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasDisposition()) {
      protoValue.setDisposition(dispositionElement.toProto())
    }
    if (hasPreAuthRef()) {
      protoValue.setPreAuthRef(preAuthRefElement.toProto())
    }
    if (hasPreAuthPeriod()) {
      protoValue.setPreAuthPeriod(preAuthPeriod.toProto())
    }
    if (hasPayeeType()) {
      protoValue.setPayeeType(payeeType.toProto())
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
      protoValue.setPayment(payment.toProto())
    }
    if (hasFundsReserve()) {
      protoValue.setFundsReserve(fundsReserve.toProto())
    }
    if (hasFormCode()) {
      protoValue.setFormCode(formCode.toProto())
    }
    if (hasForm()) {
      protoValue.setForm(form.toProto())
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
      protoValue.setItemSequence(itemSequenceElement.toProto())
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
      protoValue.setCategory(category.toProto())
    }
    if (hasReason()) {
      protoValue.setReason(reason.toProto())
    }
    if (hasAmount()) {
      protoValue.setAmount(amount.toProto())
    }
    if (hasValue()) {
      protoValue.setValue(valueElement.toProto())
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
      protoValue.setDetailSequence(detailSequenceElement.toProto())
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
      protoValue.setSubDetailSequence(subDetailSequenceElement.toProto())
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
      protoValue.setProductOrService(productOrService.toProto())
    }
    if (hasModifier()) {
      protoValue.addAllModifier(modifier.map { it.toProto() })
    }
    if (hasProgramCode()) {
      protoValue.addAllProgramCode(programCode.map { it.toProto() })
    }
    if (hasServiced()) {
      protoValue.setServiced(serviced.claimResponseAddItemServicedToProto())
    }
    if (hasLocation()) {
      protoValue.setLocation(location.claimResponseAddItemLocationToProto())
    }
    if (hasQuantity()) {
      protoValue.setQuantity((quantity as SimpleQuantity).toProto())
    }
    if (hasUnitPrice()) {
      protoValue.setUnitPrice(unitPrice.toProto())
    }
    if (hasFactor()) {
      protoValue.setFactor(factorElement.toProto())
    }
    if (hasNet()) {
      protoValue.setNet(net.toProto())
    }
    if (hasBodySite()) {
      protoValue.setBodySite(bodySite.toProto())
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
      protoValue.setProductOrService(productOrService.toProto())
    }
    if (hasModifier()) {
      protoValue.addAllModifier(modifier.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.setQuantity((quantity as SimpleQuantity).toProto())
    }
    if (hasUnitPrice()) {
      protoValue.setUnitPrice(unitPrice.toProto())
    }
    if (hasFactor()) {
      protoValue.setFactor(factorElement.toProto())
    }
    if (hasNet()) {
      protoValue.setNet(net.toProto())
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
      protoValue.setProductOrService(productOrService.toProto())
    }
    if (hasModifier()) {
      protoValue.addAllModifier(modifier.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.setQuantity((quantity as SimpleQuantity).toProto())
    }
    if (hasUnitPrice()) {
      protoValue.setUnitPrice(unitPrice.toProto())
    }
    if (hasFactor()) {
      protoValue.setFactor(factorElement.toProto())
    }
    if (hasNet()) {
      protoValue.setNet(net.toProto())
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
      protoValue.setCategory(category.toProto())
    }
    if (hasAmount()) {
      protoValue.setAmount(amount.toProto())
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
      protoValue.setType(type.toProto())
    }
    if (hasAdjustment()) {
      protoValue.setAdjustment(adjustment.toProto())
    }
    if (hasAdjustmentReason()) {
      protoValue.setAdjustmentReason(adjustmentReason.toProto())
    }
    if (hasDate()) {
      protoValue.setDate(dateElement.toProto())
    }
    if (hasAmount()) {
      protoValue.setAmount(amount.toProto())
    }
    if (hasIdentifier()) {
      protoValue.setIdentifier(identifier.toProto())
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
      protoValue.setNumber(numberElement.toProto())
    }
    protoValue.setType(
      ClaimResponse.Note.TypeCode.newBuilder()
        .setValue(
          NoteTypeCode.Value.valueOf(type.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
        )
        .build()
    )
    if (hasText()) {
      protoValue.setText(textElement.toProto())
    }
    if (hasLanguage()) {
      protoValue.setLanguage(language.toProto())
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
      protoValue.setSequence(sequenceElement.toProto())
    }
    if (hasFocal()) {
      protoValue.setFocal(focalElement.toProto())
    }
    if (hasCoverage()) {
      protoValue.setCoverage(coverage.toProto())
    }
    if (hasBusinessArrangement()) {
      protoValue.setBusinessArrangement(businessArrangementElement.toProto())
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
      protoValue.setItemSequence(itemSequenceElement.toProto())
    }
    if (hasDetailSequence()) {
      protoValue.setDetailSequence(detailSequenceElement.toProto())
    }
    if (hasSubDetailSequence()) {
      protoValue.setSubDetailSequence(subDetailSequenceElement.toProto())
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ClaimResponse.Item.toHapi(): org.hl7.fhir.r4.model.ClaimResponse.ItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.ItemComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasItemSequence()) {
      hapiValue.setItemSequenceElement(itemSequence.toHapi())
    }
    if (noteNumberCount > 0) {
      hapiValue.setNoteNumber(noteNumberList.map { it.toHapi() })
    }
    if (adjudicationCount > 0) {
      hapiValue.setAdjudication(adjudicationList.map { it.toHapi() })
    }
    if (detailCount > 0) {
      hapiValue.setDetail(detailList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Item.Adjudication.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.AdjudicationComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.AdjudicationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCategory()) {
      hapiValue.setCategory(category.toHapi())
    }
    if (hasReason()) {
      hapiValue.setReason(reason.toHapi())
    }
    if (hasAmount()) {
      hapiValue.setAmount(amount.toHapi())
    }
    if (hasValue()) {
      hapiValue.setValueElement(value.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Item.ItemDetail.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.ItemDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.ItemDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasDetailSequence()) {
      hapiValue.setDetailSequenceElement(detailSequence.toHapi())
    }
    if (noteNumberCount > 0) {
      hapiValue.setNoteNumber(noteNumberList.map { it.toHapi() })
    }
    if (subDetailCount > 0) {
      hapiValue.setSubDetail(subDetailList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Item.ItemDetail.SubDetail.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.SubDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.SubDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSubDetailSequence()) {
      hapiValue.setSubDetailSequenceElement(subDetailSequence.toHapi())
    }
    if (noteNumberCount > 0) {
      hapiValue.setNoteNumber(noteNumberList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.AddedItem.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.AddedItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.AddedItemComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (itemSequenceCount > 0) {
      hapiValue.setItemSequence(itemSequenceList.map { it.toHapi() })
    }
    if (detailSequenceCount > 0) {
      hapiValue.setDetailSequence(detailSequenceList.map { it.toHapi() })
    }
    if (subdetailSequenceCount > 0) {
      hapiValue.setSubdetailSequence(subdetailSequenceList.map { it.toHapi() })
    }
    if (providerCount > 0) {
      hapiValue.setProvider(providerList.map { it.toHapi() })
    }
    if (hasProductOrService()) {
      hapiValue.setProductOrService(productOrService.toHapi())
    }
    if (modifierCount > 0) {
      hapiValue.setModifier(modifierList.map { it.toHapi() })
    }
    if (programCodeCount > 0) {
      hapiValue.setProgramCode(programCodeList.map { it.toHapi() })
    }
    if (hasServiced()) {
      hapiValue.setServiced(serviced.claimResponseAddItemServicedToHapi())
    }
    if (hasLocation()) {
      hapiValue.setLocation(location.claimResponseAddItemLocationToHapi())
    }
    if (hasQuantity()) {
      hapiValue.setQuantity(quantity.toHapi())
    }
    if (hasUnitPrice()) {
      hapiValue.setUnitPrice(unitPrice.toHapi())
    }
    if (hasFactor()) {
      hapiValue.setFactorElement(factor.toHapi())
    }
    if (hasNet()) {
      hapiValue.setNet(net.toHapi())
    }
    if (hasBodySite()) {
      hapiValue.setBodySite(bodySite.toHapi())
    }
    if (subSiteCount > 0) {
      hapiValue.setSubSite(subSiteList.map { it.toHapi() })
    }
    if (noteNumberCount > 0) {
      hapiValue.setNoteNumber(noteNumberList.map { it.toHapi() })
    }
    if (detailCount > 0) {
      hapiValue.setDetail(detailList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.AddedItem.AddedItemDetail.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.AddedItemDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.AddedItemDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasProductOrService()) {
      hapiValue.setProductOrService(productOrService.toHapi())
    }
    if (modifierCount > 0) {
      hapiValue.setModifier(modifierList.map { it.toHapi() })
    }
    if (hasQuantity()) {
      hapiValue.setQuantity(quantity.toHapi())
    }
    if (hasUnitPrice()) {
      hapiValue.setUnitPrice(unitPrice.toHapi())
    }
    if (hasFactor()) {
      hapiValue.setFactorElement(factor.toHapi())
    }
    if (hasNet()) {
      hapiValue.setNet(net.toHapi())
    }
    if (noteNumberCount > 0) {
      hapiValue.setNoteNumber(noteNumberList.map { it.toHapi() })
    }
    if (subDetailCount > 0) {
      hapiValue.setSubDetail(subDetailList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.AddedItem.AddedItemDetail.AddedItemSubDetail.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.AddedItemSubDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.AddedItemSubDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasProductOrService()) {
      hapiValue.setProductOrService(productOrService.toHapi())
    }
    if (modifierCount > 0) {
      hapiValue.setModifier(modifierList.map { it.toHapi() })
    }
    if (hasQuantity()) {
      hapiValue.setQuantity(quantity.toHapi())
    }
    if (hasUnitPrice()) {
      hapiValue.setUnitPrice(unitPrice.toHapi())
    }
    if (hasFactor()) {
      hapiValue.setFactorElement(factor.toHapi())
    }
    if (hasNet()) {
      hapiValue.setNet(net.toHapi())
    }
    if (noteNumberCount > 0) {
      hapiValue.setNoteNumber(noteNumberList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Total.toHapi(): org.hl7.fhir.r4.model.ClaimResponse.TotalComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.TotalComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCategory()) {
      hapiValue.setCategory(category.toHapi())
    }
    if (hasAmount()) {
      hapiValue.setAmount(amount.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Payment.toHapi(): org.hl7.fhir.r4.model.ClaimResponse.PaymentComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.PaymentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasAdjustment()) {
      hapiValue.setAdjustment(adjustment.toHapi())
    }
    if (hasAdjustmentReason()) {
      hapiValue.setAdjustmentReason(adjustmentReason.toHapi())
    }
    if (hasDate()) {
      hapiValue.setDateElement(date.toHapi())
    }
    if (hasAmount()) {
      hapiValue.setAmount(amount.toHapi())
    }
    if (hasIdentifier()) {
      hapiValue.setIdentifier(identifier.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Note.toHapi(): org.hl7.fhir.r4.model.ClaimResponse.NoteComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.NoteComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasNumber()) {
      hapiValue.setNumberElement(number.toHapi())
    }
    hapiValue.setType(
      Enumerations.NoteType.valueOf(type.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasText()) {
      hapiValue.setTextElement(text.toHapi())
    }
    if (hasLanguage()) {
      hapiValue.setLanguage(language.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Insurance.toHapi():
    org.hl7.fhir.r4.model.ClaimResponse.InsuranceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.InsuranceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSequence()) {
      hapiValue.setSequenceElement(sequence.toHapi())
    }
    if (hasFocal()) {
      hapiValue.setFocalElement(focal.toHapi())
    }
    if (hasCoverage()) {
      hapiValue.setCoverage(coverage.toHapi())
    }
    if (hasBusinessArrangement()) {
      hapiValue.setBusinessArrangementElement(businessArrangement.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ClaimResponse.Error.toHapi(): org.hl7.fhir.r4.model.ClaimResponse.ErrorComponent {
    val hapiValue = org.hl7.fhir.r4.model.ClaimResponse.ErrorComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasItemSequence()) {
      hapiValue.setItemSequenceElement(itemSequence.toHapi())
    }
    if (hasDetailSequence()) {
      hapiValue.setDetailSequenceElement(detailSequence.toHapi())
    }
    if (hasSubDetailSequence()) {
      hapiValue.setSubDetailSequenceElement(subDetailSequence.toHapi())
    }
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    return hapiValue
  }
}
