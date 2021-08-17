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

import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Subscription
import com.google.fhir.r4.core.Subscription.Channel
import com.google.fhir.r4.core.SubscriptionChannelTypeCode
import com.google.fhir.r4.core.SubscriptionStatusCode

object SubscriptionConverter {
  fun Subscription.toHapi(): org.hl7.fhir.r4.model.Subscription {
    val hapiValue = org.hl7.fhir.r4.model.Subscription()
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
    hapiValue.status =
      org.hl7.fhir.r4.model.Subscription.SubscriptionStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (contactCount > 0) {
      hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (hasEnd()) {
      hapiValue.endElement = end.toHapi()
    }
    if (hasReason()) {
      hapiValue.reasonElement = reason.toHapi()
    }
    if (hasCriteria()) {
      hapiValue.criteriaElement = criteria.toHapi()
    }
    if (hasError()) {
      hapiValue.errorElement = error.toHapi()
    }
    if (hasChannel()) {
      hapiValue.channel = channel.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Subscription.toProto(): Subscription {
    val protoValue = Subscription.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.status =
      Subscription.StatusCode.newBuilder()
        .setValue(
          SubscriptionStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasEnd()) {
      protoValue.end = endElement.toProto()
    }
    if (hasReason()) {
      protoValue.reason = reasonElement.toProto()
    }
    if (hasCriteria()) {
      protoValue.criteria = criteriaElement.toProto()
    }
    if (hasError()) {
      protoValue.error = errorElement.toProto()
    }
    if (hasChannel()) {
      protoValue.channel = channel.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Subscription.SubscriptionChannelComponent.toProto():
    Subscription.Channel {
    val protoValue = Subscription.Channel.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.type =
      Subscription.Channel.TypeCode.newBuilder()
        .setValue(
          SubscriptionChannelTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasEndpoint()) {
      protoValue.endpoint = endpointElement.toProto()
    }
    protoValue.payload =
      Subscription.Channel.PayloadCode.newBuilder().setValue(payload.protoCodeCheck()).build()
    if (hasHeader()) {
      protoValue.addAllHeader(header.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun Subscription.Channel.toHapi():
    org.hl7.fhir.r4.model.Subscription.SubscriptionChannelComponent {
    val hapiValue = org.hl7.fhir.r4.model.Subscription.SubscriptionChannelComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.type =
      org.hl7.fhir.r4.model.Subscription.SubscriptionChannelType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasEndpoint()) {
      hapiValue.endpointElement = endpoint.toHapi()
    }
    hapiValue.payload = payload.value.hapiCodeCheck()
    if (headerCount > 0) {
      hapiValue.header = headerList.map { it.toHapi() }
    }
    return hapiValue
  }
}
