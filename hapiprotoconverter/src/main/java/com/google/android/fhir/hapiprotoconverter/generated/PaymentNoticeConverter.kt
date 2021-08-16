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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PaymentNotice
import kotlin.jvm.JvmStatic

object PaymentNoticeConverter {
  @JvmStatic
  fun PaymentNotice.toHapi(): org.hl7.fhir.r4.model.PaymentNotice {
    val hapiValue = org.hl7.fhir.r4.model.PaymentNotice()
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
      hapiValue.status = org.hl7.fhir.r4.model.PaymentNotice.PaymentNoticeStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasRequest()) {
        hapiValue.request = request.toHapi()
    }
    if (hasResponse()) {
        hapiValue.response = response.toHapi()
    }
    if (hasCreated()) {
        hapiValue.createdElement = created.toHapi()
    }
    if (hasProvider()) {
        hapiValue.provider = provider.toHapi()
    }
    if (hasPayment()) {
        hapiValue.payment = payment.toHapi()
    }
    if (hasPaymentDate()) {
        hapiValue.paymentDateElement = paymentDate.toHapi()
    }
    if (hasPayee()) {
        hapiValue.payee = payee.toHapi()
    }
    if (hasRecipient()) {
        hapiValue.recipient = recipient.toHapi()
    }
    if (hasAmount()) {
        hapiValue.amount = amount.toHapi()
    }
    if (hasPaymentStatus()) {
        hapiValue.paymentStatus = paymentStatus.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.PaymentNotice.toProto(): PaymentNotice {
    val protoValue = PaymentNotice.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.status = PaymentNotice.StatusCode.newBuilder()
          .setValue(
              FinancialResourceStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasRequest()) {
        protoValue.request = request.toProto()
    }
    if (hasResponse()) {
        protoValue.response = response.toProto()
    }
    if (hasCreated()) {
        protoValue.created = createdElement.toProto()
    }
    if (hasProvider()) {
        protoValue.provider = provider.toProto()
    }
    if (hasPayment()) {
        protoValue.payment = payment.toProto()
    }
    if (hasPaymentDate()) {
        protoValue.paymentDate = paymentDateElement.toProto()
    }
    if (hasPayee()) {
        protoValue.payee = payee.toProto()
    }
    if (hasRecipient()) {
        protoValue.recipient = recipient.toProto()
    }
    if (hasAmount()) {
        protoValue.amount = amount.toProto()
    }
    if (hasPaymentStatus()) {
        protoValue.paymentStatus = paymentStatus.toProto()
    }
    return protoValue.build()
  }
}
