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
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.ClaimProcessingCode
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.NoteTypeCode
import com.google.fhir.r4.core.PaymentReconciliation
import com.google.fhir.r4.core.PaymentReconciliation.Notes
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

object PaymentReconciliationConverter {
  @JvmStatic
  fun PaymentReconciliation.toHapi(): org.hl7.fhir.r4.model.PaymentReconciliation {
    val hapiValue = org.hl7.fhir.r4.model.PaymentReconciliation()
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
      hapiValue.status = org.hl7.fhir.r4.model.PaymentReconciliation.PaymentReconciliationStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPeriod()) {
        hapiValue.period = period.toHapi()
    }
    if (hasCreated()) {
        hapiValue.createdElement = created.toHapi()
    }
    if (hasPaymentIssuer()) {
        hapiValue.paymentIssuer = paymentIssuer.toHapi()
    }
    if (hasRequest()) {
        hapiValue.request = request.toHapi()
    }
    if (hasRequestor()) {
        hapiValue.requestor = requestor.toHapi()
    }
      hapiValue.outcome =
          Enumerations.RemittanceOutcome.valueOf(outcome.value.name.hapiCodeCheck().replace("_", ""))
    if (hasDisposition()) {
        hapiValue.dispositionElement = disposition.toHapi()
    }
    if (hasPaymentDate()) {
        hapiValue.paymentDateElement = paymentDate.toHapi()
    }
    if (hasPaymentAmount()) {
        hapiValue.paymentAmount = paymentAmount.toHapi()
    }
    if (hasPaymentIdentifier()) {
        hapiValue.paymentIdentifier = paymentIdentifier.toHapi()
    }
    if (detailCount > 0) {
        hapiValue.detail = detailList.map { it.toHapi() }
    }
    if (hasFormCode()) {
        hapiValue.formCode = formCode.toHapi()
    }
    if (processNoteCount > 0) {
        hapiValue.processNote = processNoteList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.PaymentReconciliation.toProto(): PaymentReconciliation {
    val protoValue = PaymentReconciliation.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.status = PaymentReconciliation.StatusCode.newBuilder()
          .setValue(
              FinancialResourceStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasPeriod()) {
        protoValue.period = period.toProto()
    }
    if (hasCreated()) {
        protoValue.created = createdElement.toProto()
    }
    if (hasPaymentIssuer()) {
        protoValue.paymentIssuer = paymentIssuer.toProto()
    }
    if (hasRequest()) {
        protoValue.request = request.toProto()
    }
    if (hasRequestor()) {
        protoValue.requestor = requestor.toProto()
    }
      protoValue.outcome = PaymentReconciliation.OutcomeCode.newBuilder()
          .setValue(
              ClaimProcessingCode.Value.valueOf(
                  outcome.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasDisposition()) {
        protoValue.disposition = dispositionElement.toProto()
    }
    if (hasPaymentDate()) {
        protoValue.paymentDate = paymentDateElement.toProto()
    }
    if (hasPaymentAmount()) {
        protoValue.paymentAmount = paymentAmount.toProto()
    }
    if (hasPaymentIdentifier()) {
        protoValue.paymentIdentifier = paymentIdentifier.toProto()
    }
    if (hasDetail()) {
      protoValue.addAllDetail(detail.map { it.toProto() })
    }
    if (hasFormCode()) {
        protoValue.formCode = formCode.toProto()
    }
    if (hasProcessNote()) {
      protoValue.addAllProcessNote(processNote.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PaymentReconciliation.DetailsComponent.toProto():
    PaymentReconciliation.Details {
    val protoValue =
      PaymentReconciliation.Details.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
        protoValue.identifier = identifier.toProto()
    }
    if (hasPredecessor()) {
        protoValue.predecessor = predecessor.toProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasRequest()) {
        protoValue.request = request.toProto()
    }
    if (hasSubmitter()) {
        protoValue.submitter = submitter.toProto()
    }
    if (hasResponse()) {
        protoValue.response = response.toProto()
    }
    if (hasDate()) {
        protoValue.date = dateElement.toProto()
    }
    if (hasResponsible()) {
        protoValue.responsible = responsible.toProto()
    }
    if (hasPayee()) {
        protoValue.payee = payee.toProto()
    }
    if (hasAmount()) {
        protoValue.amount = amount.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PaymentReconciliation.NotesComponent.toProto():
    PaymentReconciliation.Notes {
    val protoValue =
      PaymentReconciliation.Notes.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.type = PaymentReconciliation.Notes.TypeCode.newBuilder()
          .setValue(
              NoteTypeCode.Value.valueOf(type.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
          )
          .build()
    if (hasText()) {
        protoValue.text = textElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun PaymentReconciliation.Details.toHapi():
    org.hl7.fhir.r4.model.PaymentReconciliation.DetailsComponent {
    val hapiValue = org.hl7.fhir.r4.model.PaymentReconciliation.DetailsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasIdentifier()) {
        hapiValue.identifier = identifier.toHapi()
    }
    if (hasPredecessor()) {
        hapiValue.predecessor = predecessor.toHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasRequest()) {
        hapiValue.request = request.toHapi()
    }
    if (hasSubmitter()) {
        hapiValue.submitter = submitter.toHapi()
    }
    if (hasResponse()) {
        hapiValue.response = response.toHapi()
    }
    if (hasDate()) {
        hapiValue.dateElement = date.toHapi()
    }
    if (hasResponsible()) {
        hapiValue.responsible = responsible.toHapi()
    }
    if (hasPayee()) {
        hapiValue.payee = payee.toHapi()
    }
    if (hasAmount()) {
        hapiValue.amount = amount.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun PaymentReconciliation.Notes.toHapi():
    org.hl7.fhir.r4.model.PaymentReconciliation.NotesComponent {
    val hapiValue = org.hl7.fhir.r4.model.PaymentReconciliation.NotesComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.type = Enumerations.NoteType.valueOf(type.value.name.hapiCodeCheck().replace("_", ""))
    if (hasText()) {
        hapiValue.textElement = text.toHapi()
    }
    return hapiValue
  }
}
