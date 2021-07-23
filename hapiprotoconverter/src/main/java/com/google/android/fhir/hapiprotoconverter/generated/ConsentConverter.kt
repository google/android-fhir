package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.fhir.r4.core.ConsentDataMeaningCode
import com.google.fhir.r4.core.ConsentProvisionTypeCode
import com.google.fhir.r4.core.ConsentStateCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type

public object ConsentConverter {
  public fun Consent.SourceX.consentSourceToHapi(): Type {
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType ) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Consent.source[x]")
  }

  public fun Type.consentSourceToProto(): Consent.SourceX {
    val protoValue = Consent.SourceX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun Consent.toHapi(): org.hl7.fhir.r4.model.Consent {
    val hapiValue = org.hl7.fhir.r4.model.Consent()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Consent.ConsentState.valueOf(status.value.name.replace("_","")))
    hapiValue.setScope(scope.toHapi())
    hapiValue.setCategory(categoryList.map{it.toHapi()})
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setDateTimeElement(dateTime.toHapi())
    hapiValue.setPerformer(performerList.map{it.toHapi()})
    hapiValue.setOrganization(organizationList.map{it.toHapi()})
    hapiValue.setSource(source.consentSourceToHapi())
    hapiValue.setPolicy(policyList.map{it.toHapi()})
    hapiValue.setPolicyRule(policyRule.toHapi())
    hapiValue.setVerification(verificationList.map{it.toHapi()})
    hapiValue.setProvision(provision.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Consent.toProto(): Consent {
    val protoValue = Consent.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(Consent.StatusCode.newBuilder().setValue(ConsentStateCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setScope(scope.toProto())
    .addAllCategory(category.map{it.toProto()})
    .setPatient(patient.toProto())
    .setDateTime(dateTimeElement.toProto())
    .addAllPerformer(performer.map{it.toProto()})
    .addAllOrganization(organization.map{it.toProto()})
    .setSource(source.consentSourceToProto())
    .addAllPolicy(policy.map{it.toProto()})
    .setPolicyRule(policyRule.toProto())
    .addAllVerification(verification.map{it.toProto()})
    .setProvision(provision.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Consent.ConsentPolicyComponent.toProto(): Consent.Policy {
    val protoValue = Consent.Policy.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setAuthority(authorityElement.toProto())
    .setUri(uriElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Consent.ConsentVerificationComponent.toProto():
      Consent.Verification {
    val protoValue = Consent.Verification.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setVerified(verifiedElement.toProto())
    .setVerifiedWith(verifiedWith.toProto())
    .setVerificationDate(verificationDateElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Consent.provisionComponent.toProto(): Consent.Provision {
    val protoValue = Consent.Provision.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(Consent.Provision.TypeCode.newBuilder().setValue(ConsentProvisionTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPeriod(period.toProto())
    .addAllProvisionActor(provisionActor.map{it.toProto()})
    .addAllAction(action.map{it.toProto()})
    .addAllSecurityLabel(securityLabel.map{it.toProto()})
    .addAllPurpose(purpose.map{it.toProto()})
    .addAllClass(class.map{it.toProto()})
    .addAllCode(code.map{it.toProto()})
    .setDataPeriod(dataPeriod.toProto())
    .addAllProvisionData(provisionData.map{it.toProto()})
    .addAllProvision(provision.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Consent.provisionActorComponent.toProto():
      Consent.Provision.ProvisionActor {
    val protoValue = Consent.Provision.ProvisionActor.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setRole(role.toProto())
    .setReference(reference.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Consent.provisionDataComponent.toProto():
      Consent.Provision.ProvisionData {
    val protoValue = Consent.Provision.ProvisionData.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setMeaning(Consent.Provision.Data.MeaningCode.newBuilder().setValue(ConsentDataMeaningCode.Value.valueOf(meaning.toCode().replace("-",
        "_").toUpperCase())).build())
    .setReference(reference.toProto())
    .build()
    return protoValue
  }

  public fun Consent.Policy.toHapi(): org.hl7.fhir.r4.model.Consent.ConsentPolicyComponent {
    val hapiValue = org.hl7.fhir.r4.model.Consent.ConsentPolicyComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setAuthorityElement(authority.toHapi())
    hapiValue.setUriElement(uri.toHapi())
    return hapiValue
  }

  public fun Consent.Verification.toHapi():
      org.hl7.fhir.r4.model.Consent.ConsentVerificationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Consent.ConsentVerificationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setVerifiedElement(verified.toHapi())
    hapiValue.setVerifiedWith(verifiedWith.toHapi())
    hapiValue.setVerificationDateElement(verificationDate.toHapi())
    return hapiValue
  }

  public fun Consent.Provision.toHapi(): org.hl7.fhir.r4.model.Consent.provisionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Consent.provisionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(org.hl7.fhir.r4.model.Consent.ConsentProvisionType.valueOf(type.value.name.replace("_","")))
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setActor(actorList.map{it.toHapi()})
    hapiValue.setAction(actionList.map{it.toHapi()})
    hapiValue.setSecurityLabel(securityLabelList.map{it.toHapi()})
    hapiValue.setPurpose(purposeList.map{it.toHapi()})
    hapiValue.setClass(classList.map{it.toHapi()})
    hapiValue.setCode(codeList.map{it.toHapi()})
    hapiValue.setDataPeriod(dataPeriod.toHapi())
    hapiValue.setProvisionData(provisionDataList.map{it.toHapi()})
    hapiValue.setProvision(provisionList.map{it.toHapi()})
    return hapiValue
  }

  public fun Consent.Provision.ProvisionActor.toHapi():
      org.hl7.fhir.r4.model.Consent.provisionActorComponent {
    val hapiValue = org.hl7.fhir.r4.model.Consent.provisionActorComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setRole(role.toHapi())
    hapiValue.setReference(reference.toHapi())
    return hapiValue
  }

  public fun Consent.Provision.ProvisionData.toHapi():
      org.hl7.fhir.r4.model.Consent.provisionDataComponent {
    val hapiValue = org.hl7.fhir.r4.model.Consent.provisionDataComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setMeaning(org.hl7.fhir.r4.model.Consent.ConsentDataMeaning.valueOf(meaning.value.name.replace("_","")))
    hapiValue.setReference(reference.toHapi())
    return hapiValue
  }
}
