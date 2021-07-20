package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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

public object PaymentNoticeConverter {
  public fun PaymentNotice.toHapi(): org.hl7.fhir.r4.model.PaymentNotice {
    val hapiValue = org.hl7.fhir.r4.model.PaymentNotice()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.PaymentNotice.PaymentNoticeStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setRequest(request.toHapi())
    hapiValue.setResponse(response.toHapi())
    hapiValue.setCreatedElement(created.toHapi())
    hapiValue.setProvider(provider.toHapi())
    hapiValue.setPayment(payment.toHapi())
    hapiValue.setPaymentDateElement(paymentDate.toHapi())
    hapiValue.setPayee(payee.toHapi())
    hapiValue.setRecipient(recipient.toHapi())
    hapiValue.setAmount(amount.toHapi())
    hapiValue.setPaymentStatus(paymentStatus.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.PaymentNotice.toProto(): PaymentNotice {
    val protoValue = PaymentNotice.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(PaymentNotice.StatusCode.newBuilder().setValue(FinancialResourceStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setRequest(request.toProto())
    .setResponse(response.toProto())
    .setCreated(createdElement.toProto())
    .setProvider(provider.toProto())
    .setPayment(payment.toProto())
    .setPaymentDate(paymentDateElement.toProto())
    .setPayee(payee.toProto())
    .setRecipient(recipient.toProto())
    .setAmount(amount.toProto())
    .setPaymentStatus(paymentStatus.toProto())
    .build()
    return protoValue
  }
}
