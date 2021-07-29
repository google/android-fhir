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
import org.hl7.fhir.r4.model.Enumerations

public object PaymentReconciliationConverter {
  public fun PaymentReconciliation.toHapi(): org.hl7.fhir.r4.model.PaymentReconciliation {
    val hapiValue = org.hl7.fhir.r4.model.PaymentReconciliation()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.PaymentReconciliation.PaymentReconciliationStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setCreatedElement(created.toHapi())
    hapiValue.setPaymentIssuer(paymentIssuer.toHapi())
    hapiValue.setRequest(request.toHapi())
    hapiValue.setRequestor(requestor.toHapi())
    hapiValue.setOutcome(
      Enumerations.RemittanceOutcome.valueOf(outcome.value.name.replace("_", ""))
    )
    hapiValue.setDispositionElement(disposition.toHapi())
    hapiValue.setPaymentDateElement(paymentDate.toHapi())
    hapiValue.setPaymentAmount(paymentAmount.toHapi())
    hapiValue.setPaymentIdentifier(paymentIdentifier.toHapi())
    hapiValue.setDetail(detailList.map { it.toHapi() })
    hapiValue.setFormCode(formCode.toHapi())
    hapiValue.setProcessNote(processNoteList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.PaymentReconciliation.toProto(): PaymentReconciliation {
    val protoValue =
      PaymentReconciliation.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          PaymentReconciliation.StatusCode.newBuilder()
            .setValue(
              FinancialResourceStatusCode.Value.valueOf(
                status.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setPeriod(period.toProto())
        .setCreated(createdElement.toProto())
        .setPaymentIssuer(paymentIssuer.toProto())
        .setRequest(request.toProto())
        .setRequestor(requestor.toProto())
        .setOutcome(
          PaymentReconciliation.OutcomeCode.newBuilder()
            .setValue(
              ClaimProcessingCode.Value.valueOf(outcome.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setDisposition(dispositionElement.toProto())
        .setPaymentDate(paymentDateElement.toProto())
        .setPaymentAmount(paymentAmount.toProto())
        .setPaymentIdentifier(paymentIdentifier.toProto())
        .addAllDetail(detail.map { it.toProto() })
        .setFormCode(formCode.toProto())
        .addAllProcessNote(processNote.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.PaymentReconciliation.DetailsComponent.toProto():
    PaymentReconciliation.Details {
    val protoValue =
      PaymentReconciliation.Details.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setIdentifier(identifier.toProto())
        .setPredecessor(predecessor.toProto())
        .setType(type.toProto())
        .setRequest(request.toProto())
        .setSubmitter(submitter.toProto())
        .setResponse(response.toProto())
        .setDate(dateElement.toProto())
        .setResponsible(responsible.toProto())
        .setPayee(payee.toProto())
        .setAmount(amount.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.PaymentReconciliation.NotesComponent.toProto():
    PaymentReconciliation.Notes {
    val protoValue =
      PaymentReconciliation.Notes.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(
          PaymentReconciliation.Notes.TypeCode.newBuilder()
            .setValue(NoteTypeCode.Value.valueOf(type.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .setText(textElement.toProto())
        .build()
    return protoValue
  }

  private fun PaymentReconciliation.Details.toHapi():
    org.hl7.fhir.r4.model.PaymentReconciliation.DetailsComponent {
    val hapiValue = org.hl7.fhir.r4.model.PaymentReconciliation.DetailsComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setPredecessor(predecessor.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setRequest(request.toHapi())
    hapiValue.setSubmitter(submitter.toHapi())
    hapiValue.setResponse(response.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setResponsible(responsible.toHapi())
    hapiValue.setPayee(payee.toHapi())
    hapiValue.setAmount(amount.toHapi())
    return hapiValue
  }

  private fun PaymentReconciliation.Notes.toHapi():
    org.hl7.fhir.r4.model.PaymentReconciliation.NotesComponent {
    val hapiValue = org.hl7.fhir.r4.model.PaymentReconciliation.NotesComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(Enumerations.NoteType.valueOf(type.value.name.replace("_", "")))
    hapiValue.setTextElement(text.toHapi())
    return hapiValue
  }
}
