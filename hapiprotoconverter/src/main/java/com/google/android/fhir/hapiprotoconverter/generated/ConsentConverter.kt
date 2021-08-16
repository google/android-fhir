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

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.Consent
import com.google.fhir.r4.core.Consent.Provision
import com.google.fhir.r4.core.Consent.Provision.ProvisionData
import com.google.fhir.r4.core.ConsentDataMeaningCode
import com.google.fhir.r4.core.ConsentProvisionTypeCode
import com.google.fhir.r4.core.ConsentStateCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Type

public object ConsentConverter {
  @JvmStatic
  private fun Consent.SourceX.consentSourceToHapi(): Type {
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Consent.source[x]")
  }

  @JvmStatic
  private fun Type.consentSourceToProto(): Consent.SourceX {
    val protoValue = Consent.SourceX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun Consent.toHapi(): org.hl7.fhir.r4.model.Consent {
    val hapiValue = org.hl7.fhir.r4.model.Consent()
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
      org.hl7.fhir.r4.model.Consent.ConsentState.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasScope()) {
      hapiValue.setScope(scope.toHapi())
    }
    if (categoryCount > 0) {
      hapiValue.setCategory(categoryList.map { it.toHapi() })
    }
    if (hasPatient()) {
      hapiValue.setPatient(patient.toHapi())
    }
    if (hasDateTime()) {
      hapiValue.setDateTimeElement(dateTime.toHapi())
    }
    if (performerCount > 0) {
      hapiValue.setPerformer(performerList.map { it.toHapi() })
    }
    if (organizationCount > 0) {
      hapiValue.setOrganization(organizationList.map { it.toHapi() })
    }
    if (hasSource()) {
      hapiValue.setSource(source.consentSourceToHapi())
    }
    if (policyCount > 0) {
      hapiValue.setPolicy(policyList.map { it.toHapi() })
    }
    if (hasPolicyRule()) {
      hapiValue.setPolicyRule(policyRule.toHapi())
    }
    if (verificationCount > 0) {
      hapiValue.setVerification(verificationList.map { it.toHapi() })
    }
    if (hasProvision()) {
      hapiValue.setProvision(provision.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Consent.toProto(): Consent {
    val protoValue = Consent.newBuilder().setId(Id.newBuilder().setValue(id))
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
      Consent.StatusCode.newBuilder()
        .setValue(
          ConsentStateCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasScope()) {
      protoValue.setScope(scope.toProto())
    }
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasPatient()) {
      protoValue.setPatient(patient.toProto())
    }
    if (hasDateTime()) {
      protoValue.setDateTime(dateTimeElement.toProto())
    }
    if (hasPerformer()) {
      protoValue.addAllPerformer(performer.map { it.toProto() })
    }
    if (hasOrganization()) {
      protoValue.addAllOrganization(organization.map { it.toProto() })
    }
    if (hasSource()) {
      protoValue.setSource(source.consentSourceToProto())
    }
    if (hasPolicy()) {
      protoValue.addAllPolicy(policy.map { it.toProto() })
    }
    if (hasPolicyRule()) {
      protoValue.setPolicyRule(policyRule.toProto())
    }
    if (hasVerification()) {
      protoValue.addAllVerification(verification.map { it.toProto() })
    }
    if (hasProvision()) {
      protoValue.setProvision(provision.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Consent.ConsentPolicyComponent.toProto(): Consent.Policy {
    val protoValue = Consent.Policy.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAuthority()) {
      protoValue.setAuthority(authorityElement.toProto())
    }
    if (hasUri()) {
      protoValue.setUri(uriElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Consent.ConsentVerificationComponent.toProto():
    Consent.Verification {
    val protoValue = Consent.Verification.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasVerified()) {
      protoValue.setVerified(verifiedElement.toProto())
    }
    if (hasVerifiedWith()) {
      protoValue.setVerifiedWith(verifiedWith.toProto())
    }
    if (hasVerificationDate()) {
      protoValue.setVerificationDate(verificationDateElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Consent.provisionComponent.toProto(): Consent.Provision {
    val protoValue = Consent.Provision.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.setType(
      Consent.Provision.TypeCode.newBuilder()
        .setValue(
          ConsentProvisionTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasPeriod()) {
      protoValue.setPeriod(period.toProto())
    }
    if (hasActor()) {
      protoValue.addAllActor(actor.map { it.toProto() })
    }
    if (hasAction()) {
      protoValue.addAllAction(action.map { it.toProto() })
    }
    if (hasSecurityLabel()) {
      protoValue.addAllSecurityLabel(securityLabel.map { it.toProto() })
    }
    if (hasPurpose()) {
      protoValue.addAllPurpose(purpose.map { it.toProto() })
    }
    if (hasClass_()) {
      protoValue.addAllClassValue(class_.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasDataPeriod()) {
      protoValue.setDataPeriod(dataPeriod.toProto())
    }
    if (hasData()) {
      protoValue.addAllData(data.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Consent.provisionActorComponent.toProto():
    Consent.Provision.ProvisionActor {
    val protoValue =
      Consent.Provision.ProvisionActor.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRole()) {
      protoValue.setRole(role.toProto())
    }
    if (hasReference()) {
      protoValue.setReference(reference.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Consent.provisionDataComponent.toProto():
    Consent.Provision.ProvisionData {
    val protoValue =
      Consent.Provision.ProvisionData.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.setMeaning(
      Consent.Provision.ProvisionData.MeaningCode.newBuilder()
        .setValue(
          ConsentDataMeaningCode.Value.valueOf(
            meaning.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasReference()) {
      protoValue.setReference(reference.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Consent.Policy.toHapi(): org.hl7.fhir.r4.model.Consent.ConsentPolicyComponent {
    val hapiValue = org.hl7.fhir.r4.model.Consent.ConsentPolicyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasAuthority()) {
      hapiValue.setAuthorityElement(authority.toHapi())
    }
    if (hasUri()) {
      hapiValue.setUriElement(uri.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Consent.Verification.toHapi():
    org.hl7.fhir.r4.model.Consent.ConsentVerificationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Consent.ConsentVerificationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasVerified()) {
      hapiValue.setVerifiedElement(verified.toHapi())
    }
    if (hasVerifiedWith()) {
      hapiValue.setVerifiedWith(verifiedWith.toHapi())
    }
    if (hasVerificationDate()) {
      hapiValue.setVerificationDateElement(verificationDate.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Consent.Provision.toHapi(): org.hl7.fhir.r4.model.Consent.provisionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Consent.provisionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setType(
      org.hl7.fhir.r4.model.Consent.ConsentProvisionType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasPeriod()) {
      hapiValue.setPeriod(period.toHapi())
    }
    if (actorCount > 0) {
      hapiValue.setActor(actorList.map { it.toHapi() })
    }
    if (actionCount > 0) {
      hapiValue.setAction(actionList.map { it.toHapi() })
    }
    if (securityLabelCount > 0) {
      hapiValue.setSecurityLabel(securityLabelList.map { it.toHapi() })
    }
    if (purposeCount > 0) {
      hapiValue.setPurpose(purposeList.map { it.toHapi() })
    }
    if (classValueCount > 0) {
      hapiValue.setClass_(classValueList.map { it.toHapi() })
    }
    if (codeCount > 0) {
      hapiValue.setCode(codeList.map { it.toHapi() })
    }
    if (hasDataPeriod()) {
      hapiValue.setDataPeriod(dataPeriod.toHapi())
    }
    if (dataCount > 0) {
      hapiValue.setData(dataList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun Consent.Provision.ProvisionActor.toHapi():
    org.hl7.fhir.r4.model.Consent.provisionActorComponent {
    val hapiValue = org.hl7.fhir.r4.model.Consent.provisionActorComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasRole()) {
      hapiValue.setRole(role.toHapi())
    }
    if (hasReference()) {
      hapiValue.setReference(reference.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Consent.Provision.ProvisionData.toHapi():
    org.hl7.fhir.r4.model.Consent.provisionDataComponent {
    val hapiValue = org.hl7.fhir.r4.model.Consent.provisionDataComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setMeaning(
      org.hl7.fhir.r4.model.Consent.ConsentDataMeaning.valueOf(
        meaning.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasReference()) {
      hapiValue.setReference(reference.toHapi())
    }
    return hapiValue
  }
}
