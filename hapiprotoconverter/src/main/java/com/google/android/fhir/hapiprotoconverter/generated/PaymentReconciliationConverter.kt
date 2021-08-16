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

public object PaymentReconciliationConverter {
  @JvmStatic
  public fun PaymentReconciliation.toHapi(): org.hl7.fhir.r4.model.PaymentReconciliation {
    val hapiValue = org.hl7.fhir.r4.model.PaymentReconciliation()
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
      org.hl7.fhir.r4.model.PaymentReconciliation.PaymentReconciliationStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasPeriod()) {
      hapiValue.setPeriod(period.toHapi())
    }
    if (hasCreated()) {
      hapiValue.setCreatedElement(created.toHapi())
    }
    if (hasPaymentIssuer()) {
      hapiValue.setPaymentIssuer(paymentIssuer.toHapi())
    }
    if (hasRequest()) {
      hapiValue.setRequest(request.toHapi())
    }
    if (hasRequestor()) {
      hapiValue.setRequestor(requestor.toHapi())
    }
    hapiValue.setOutcome(
      Enumerations.RemittanceOutcome.valueOf(outcome.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasDisposition()) {
      hapiValue.setDispositionElement(disposition.toHapi())
    }
    if (hasPaymentDate()) {
      hapiValue.setPaymentDateElement(paymentDate.toHapi())
    }
    if (hasPaymentAmount()) {
      hapiValue.setPaymentAmount(paymentAmount.toHapi())
    }
    if (hasPaymentIdentifier()) {
      hapiValue.setPaymentIdentifier(paymentIdentifier.toHapi())
    }
    if (detailCount > 0) {
      hapiValue.setDetail(detailList.map { it.toHapi() })
    }
    if (hasFormCode()) {
      hapiValue.setFormCode(formCode.toHapi())
    }
    if (processNoteCount > 0) {
      hapiValue.setProcessNote(processNoteList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.PaymentReconciliation.toProto(): PaymentReconciliation {
    val protoValue = PaymentReconciliation.newBuilder().setId(Id.newBuilder().setValue(id))
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
      PaymentReconciliation.StatusCode.newBuilder()
        .setValue(
          FinancialResourceStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasPeriod()) {
      protoValue.setPeriod(period.toProto())
    }
    if (hasCreated()) {
      protoValue.setCreated(createdElement.toProto())
    }
    if (hasPaymentIssuer()) {
      protoValue.setPaymentIssuer(paymentIssuer.toProto())
    }
    if (hasRequest()) {
      protoValue.setRequest(request.toProto())
    }
    if (hasRequestor()) {
      protoValue.setRequestor(requestor.toProto())
    }
    protoValue.setOutcome(
      PaymentReconciliation.OutcomeCode.newBuilder()
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
    if (hasPaymentDate()) {
      protoValue.setPaymentDate(paymentDateElement.toProto())
    }
    if (hasPaymentAmount()) {
      protoValue.setPaymentAmount(paymentAmount.toProto())
    }
    if (hasPaymentIdentifier()) {
      protoValue.setPaymentIdentifier(paymentIdentifier.toProto())
    }
    if (hasDetail()) {
      protoValue.addAllDetail(detail.map { it.toProto() })
    }
    if (hasFormCode()) {
      protoValue.setFormCode(formCode.toProto())
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
      protoValue.setIdentifier(identifier.toProto())
    }
    if (hasPredecessor()) {
      protoValue.setPredecessor(predecessor.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasRequest()) {
      protoValue.setRequest(request.toProto())
    }
    if (hasSubmitter()) {
      protoValue.setSubmitter(submitter.toProto())
    }
    if (hasResponse()) {
      protoValue.setResponse(response.toProto())
    }
    if (hasDate()) {
      protoValue.setDate(dateElement.toProto())
    }
    if (hasResponsible()) {
      protoValue.setResponsible(responsible.toProto())
    }
    if (hasPayee()) {
      protoValue.setPayee(payee.toProto())
    }
    if (hasAmount()) {
      protoValue.setAmount(amount.toProto())
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
    protoValue.setType(
      PaymentReconciliation.Notes.TypeCode.newBuilder()
        .setValue(
          NoteTypeCode.Value.valueOf(type.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
        )
        .build()
    )
    if (hasText()) {
      protoValue.setText(textElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun PaymentReconciliation.Details.toHapi():
    org.hl7.fhir.r4.model.PaymentReconciliation.DetailsComponent {
    val hapiValue = org.hl7.fhir.r4.model.PaymentReconciliation.DetailsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasIdentifier()) {
      hapiValue.setIdentifier(identifier.toHapi())
    }
    if (hasPredecessor()) {
      hapiValue.setPredecessor(predecessor.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasRequest()) {
      hapiValue.setRequest(request.toHapi())
    }
    if (hasSubmitter()) {
      hapiValue.setSubmitter(submitter.toHapi())
    }
    if (hasResponse()) {
      hapiValue.setResponse(response.toHapi())
    }
    if (hasDate()) {
      hapiValue.setDateElement(date.toHapi())
    }
    if (hasResponsible()) {
      hapiValue.setResponsible(responsible.toHapi())
    }
    if (hasPayee()) {
      hapiValue.setPayee(payee.toHapi())
    }
    if (hasAmount()) {
      hapiValue.setAmount(amount.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun PaymentReconciliation.Notes.toHapi():
    org.hl7.fhir.r4.model.PaymentReconciliation.NotesComponent {
    val hapiValue = org.hl7.fhir.r4.model.PaymentReconciliation.NotesComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setType(
      Enumerations.NoteType.valueOf(type.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasText()) {
      hapiValue.setTextElement(text.toHapi())
    }
    return hapiValue
  }
}
