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

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SignatureConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SignatureConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.Contract
import com.google.fhir.r4.core.Contract.ComputableLanguage
import com.google.fhir.r4.core.Contract.ContentDefinition
import com.google.fhir.r4.core.Contract.FriendlyLanguage
import com.google.fhir.r4.core.Contract.LegalLanguage
import com.google.fhir.r4.core.Contract.Term
import com.google.fhir.r4.core.Contract.Term.Action
import com.google.fhir.r4.core.Contract.Term.ContractAsset
import com.google.fhir.r4.core.Contract.Term.ContractAsset.ValuedItem
import com.google.fhir.r4.core.Contract.Term.ContractOffer
import com.google.fhir.r4.core.Contract.Term.ContractOffer.Answer
import com.google.fhir.r4.core.ContractResourcePublicationStatusCode
import com.google.fhir.r4.core.ContractResourceStatusCode
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Decimal
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Time
import com.google.fhir.r4.core.Timing
import com.google.fhir.r4.core.Uri
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

public object ContractConverter {
  @JvmStatic
  private fun Contract.TopicX.contractTopicToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.topic[x]")
  }

  @JvmStatic
  private fun Type.contractTopicToProto(): Contract.TopicX {
    val protoValue = Contract.TopicX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.Term.TopicX.contractTermTopicToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.term.topic[x]")
  }

  @JvmStatic
  private fun Type.contractTermTopicToProto(): Contract.Term.TopicX {
    val protoValue = Contract.Term.TopicX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.Answer.ValueX.contractTermOfferAnswerValueToHapi(): Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType) {
      return (this.getInteger()).toHapi()
    }
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType) {
      return (this.getTime()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType) {
      return (this.getUri()).toHapi()
    }
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getCoding() != Coding.newBuilder().defaultInstanceForType) {
      return (this.getCoding()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.term.offer.answer.value[x]")
  }

  @JvmStatic
  private fun Type.contractTermOfferAnswerValueToProto():
    Contract.Term.ContractOffer.Answer.ValueX {
    val protoValue = Contract.Term.ContractOffer.Answer.ValueX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is TimeType) {
      protoValue.setTime(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is UriType) {
      protoValue.setUri(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.setCoding(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.ValuedItem.EntityX.contractTermAssetValuedItemEntityToHapi():
    Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.term.asset.valuedItem.entity[x]")
  }

  @JvmStatic
  private fun Type.contractTermAssetValuedItemEntityToProto():
    Contract.Term.ContractAsset.ValuedItem.EntityX {
    val protoValue = Contract.Term.ContractAsset.ValuedItem.EntityX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.Term.Action.OccurrenceX.contractTermActionOccurrenceToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.term.action.occurrence[x]")
  }

  @JvmStatic
  private fun Type.contractTermActionOccurrenceToProto(): Contract.Term.Action.OccurrenceX {
    val protoValue = Contract.Term.Action.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.FriendlyLanguage.ContentX.contractFriendlyContentToHapi(): Type {
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.friendly.content[x]")
  }

  @JvmStatic
  private fun Type.contractFriendlyContentToProto(): Contract.FriendlyLanguage.ContentX {
    val protoValue = Contract.FriendlyLanguage.ContentX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.LegalLanguage.ContentX.contractLegalContentToHapi(): Type {
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.legal.content[x]")
  }

  @JvmStatic
  private fun Type.contractLegalContentToProto(): Contract.LegalLanguage.ContentX {
    val protoValue = Contract.LegalLanguage.ContentX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.ComputableLanguage.ContentX.contractRuleContentToHapi(): Type {
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.rule.content[x]")
  }

  @JvmStatic
  private fun Type.contractRuleContentToProto(): Contract.ComputableLanguage.ContentX {
    val protoValue = Contract.ComputableLanguage.ContentX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.LegallyBindingX.contractLegallyBindingToHapi(): Type {
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.legallyBinding[x]")
  }

  @JvmStatic
  private fun Type.contractLegallyBindingToProto(): Contract.LegallyBindingX {
    val protoValue = Contract.LegallyBindingX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun Contract.toHapi(): org.hl7.fhir.r4.model.Contract {
    val hapiValue = org.hl7.fhir.r4.model.Contract()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.Contract.ContractStatus.valueOf(status.value.name.replace("_", ""))
    )
    hapiValue.setLegalState(legalState.toHapi())
    hapiValue.setInstantiatesCanonical(instantiatesCanonical.toHapi())
    hapiValue.setInstantiatesUriElement(instantiatesUri.toHapi())
    hapiValue.setContentDerivative(contentDerivative.toHapi())
    hapiValue.setIssuedElement(issued.toHapi())
    hapiValue.setApplies(applies.toHapi())
    hapiValue.setExpirationType(expirationType.toHapi())
    hapiValue.setSubject(subjectList.map { it.toHapi() })
    hapiValue.setAuthority(authorityList.map { it.toHapi() })
    hapiValue.setDomain(domainList.map { it.toHapi() })
    hapiValue.setSite(siteList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setSubtitleElement(subtitle.toHapi())
    hapiValue.setAlias(aliasList.map { it.toHapi() })
    hapiValue.setAuthor(author.toHapi())
    hapiValue.setScope(scope.toHapi())
    hapiValue.setTopic(topic.contractTopicToHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setSubType(subTypeList.map { it.toHapi() })
    hapiValue.setContentDefinition(contentDefinition.toHapi())
    hapiValue.setTerm(termList.map { it.toHapi() })
    hapiValue.setSupportingInfo(supportingInfoList.map { it.toHapi() })
    hapiValue.setRelevantHistory(relevantHistoryList.map { it.toHapi() })
    hapiValue.setSigner(signerList.map { it.toHapi() })
    hapiValue.setFriendly(friendlyList.map { it.toHapi() })
    hapiValue.setLegal(legalList.map { it.toHapi() })
    hapiValue.setRule(ruleList.map { it.toHapi() })
    hapiValue.setLegallyBinding(legallyBinding.contractLegallyBindingToHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Contract.toProto(): Contract {
    val protoValue =
      Contract.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setLanguage(languageElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .setVersion(versionElement.toProto())
        .setStatus(
          Contract.StatusCode.newBuilder()
            .setValue(
              ContractResourceStatusCode.Value.valueOf(
                status.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setLegalState(legalState.toProto())
        .setInstantiatesCanonical(instantiatesCanonical.toProto())
        .setInstantiatesUri(instantiatesUriElement.toProto())
        .setContentDerivative(contentDerivative.toProto())
        .setIssued(issuedElement.toProto())
        .setApplies(applies.toProto())
        .setExpirationType(expirationType.toProto())
        .addAllSubject(subject.map { it.toProto() })
        .addAllAuthority(authority.map { it.toProto() })
        .addAllDomain(domain.map { it.toProto() })
        .addAllSite(site.map { it.toProto() })
        .setName(nameElement.toProto())
        .setTitle(titleElement.toProto())
        .setSubtitle(subtitleElement.toProto())
        .addAllAlias(alias.map { it.toProto() })
        .setAuthor(author.toProto())
        .setScope(scope.toProto())
        .setTopic(topic.contractTopicToProto())
        .setType(type.toProto())
        .addAllSubType(subType.map { it.toProto() })
        .setContentDefinition(contentDefinition.toProto())
        .addAllTerm(term.map { it.toProto() })
        .addAllSupportingInfo(supportingInfo.map { it.toProto() })
        .addAllRelevantHistory(relevantHistory.map { it.toProto() })
        .addAllSigner(signer.map { it.toProto() })
        .addAllFriendly(friendly.map { it.toProto() })
        .addAllLegal(legal.map { it.toProto() })
        .addAllRule(rule.map { it.toProto() })
        .setLegallyBinding(legallyBinding.contractLegallyBindingToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ContentDefinitionComponent.toProto():
    Contract.ContentDefinition {
    val protoValue =
      Contract.ContentDefinition.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .setSubType(subType.toProto())
        .setPublisher(publisher.toProto())
        .setPublicationDate(publicationDateElement.toProto())
        .setPublicationStatus(
          Contract.ContentDefinition.PublicationStatusCode.newBuilder()
            .setValue(
              ContractResourcePublicationStatusCode.Value.valueOf(
                publicationStatus.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setCopyright(copyrightElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.TermComponent.toProto(): Contract.Term {
    val protoValue =
      Contract.Term.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setIdentifier(identifier.toProto())
        .setIssued(issuedElement.toProto())
        .setApplies(applies.toProto())
        .setTopic(topic.contractTermTopicToProto())
        .setType(type.toProto())
        .setSubType(subType.toProto())
        .setText(textElement.toProto())
        .addAllSecurityLabel(securityLabel.map { it.toProto() })
        .setOffer(offer.toProto())
        .addAllAsset(asset.map { it.toProto() })
        .addAllAction(action.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.SecurityLabelComponent.toProto():
    Contract.Term.SecurityLabel {
    val protoValue =
      Contract.Term.SecurityLabel.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllNumber(number.map { it.toProto() })
        .setClassification(classification.toProto())
        .addAllCategory(category.map { it.toProto() })
        .addAllControl(control.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ContractOfferComponent.toProto():
    Contract.Term.ContractOffer {
    val protoValue =
      Contract.Term.ContractOffer.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllParty(party.map { it.toProto() })
        .setTopic(topic.toProto())
        .setType(type.toProto())
        .setDecision(decision.toProto())
        .addAllDecisionMode(decisionMode.map { it.toProto() })
        .addAllAnswer(answer.map { it.toProto() })
        .setText(textElement.toProto())
        .addAllLinkId(linkId.map { it.toProto() })
        .addAllSecurityLabelNumber(securityLabelNumber.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ContractPartyComponent.toProto():
    Contract.Term.ContractOffer.ContractParty {
    val protoValue =
      Contract.Term.ContractOffer.ContractParty.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllReference(reference.map { it.toProto() })
        .setRole(role.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.AnswerComponent.toProto():
    Contract.Term.ContractOffer.Answer {
    val protoValue =
      Contract.Term.ContractOffer.Answer.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setValue(value.contractTermOfferAnswerValueToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ContractAssetComponent.toProto():
    Contract.Term.ContractAsset {
    val protoValue =
      Contract.Term.ContractAsset.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setScope(scope.toProto())
        .addAllType(type.map { it.toProto() })
        .addAllTypeReference(typeReference.map { it.toProto() })
        .addAllSubtype(subtype.map { it.toProto() })
        .setRelationship(relationship.toProto())
        .addAllContext(context.map { it.toProto() })
        .setCondition(conditionElement.toProto())
        .addAllPeriodType(periodType.map { it.toProto() })
        .addAllPeriod(period.map { it.toProto() })
        .addAllUsePeriod(usePeriod.map { it.toProto() })
        .setText(textElement.toProto())
        .addAllLinkId(linkId.map { it.toProto() })
        .addAllSecurityLabelNumber(securityLabelNumber.map { it.toProto() })
        .addAllValuedItem(valuedItem.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.AssetContextComponent.toProto():
    Contract.Term.ContractAsset.AssetContext {
    val protoValue =
      Contract.Term.ContractAsset.AssetContext.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setReference(reference.toProto())
        .addAllCode(code.map { it.toProto() })
        .setText(textElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ValuedItemComponent.toProto():
    Contract.Term.ContractAsset.ValuedItem {
    val protoValue =
      Contract.Term.ContractAsset.ValuedItem.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setEntity(entity.contractTermAssetValuedItemEntityToProto())
        .setIdentifier(identifier.toProto())
        .setEffectiveTime(effectiveTimeElement.toProto())
        .setQuantity((quantity as SimpleQuantity).toProto())
        .setUnitPrice(unitPrice.toProto())
        .setFactor(factorElement.toProto())
        .setPoints(pointsElement.toProto())
        .setNet(net.toProto())
        .setPayment(paymentElement.toProto())
        .setPaymentDate(paymentDateElement.toProto())
        .setResponsible(responsible.toProto())
        .setRecipient(recipient.toProto())
        .addAllLinkId(linkId.map { it.toProto() })
        .addAllSecurityLabelNumber(securityLabelNumber.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ActionComponent.toProto(): Contract.Term.Action {
    val protoValue =
      Contract.Term.Action.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDoNotPerform(doNotPerformElement.toProto())
        .setType(type.toProto())
        .addAllSubject(subject.map { it.toProto() })
        .setIntent(intent.toProto())
        .addAllLinkId(linkId.map { it.toProto() })
        .setStatus(status.toProto())
        .setContext(context.toProto())
        .addAllContextLinkId(contextLinkId.map { it.toProto() })
        .setOccurrence(occurrence.contractTermActionOccurrenceToProto())
        .addAllRequester(requester.map { it.toProto() })
        .addAllRequesterLinkId(requesterLinkId.map { it.toProto() })
        .addAllPerformerType(performerType.map { it.toProto() })
        .setPerformerRole(performerRole.toProto())
        .setPerformer(performer.toProto())
        .addAllPerformerLinkId(performerLinkId.map { it.toProto() })
        .addAllReasonCode(reasonCode.map { it.toProto() })
        .addAllReasonReference(reasonReference.map { it.toProto() })
        .addAllReason(reason.map { it.toProto() })
        .addAllReasonLinkId(reasonLinkId.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .addAllSecurityLabelNumber(securityLabelNumber.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ActionSubjectComponent.toProto():
    Contract.Term.Action.ActionSubject {
    val protoValue =
      Contract.Term.Action.ActionSubject.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllReference(reference.map { it.toProto() })
        .setRole(role.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.SignatoryComponent.toProto(): Contract.Signatory {
    val protoValue =
      Contract.Signatory.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .setParty(party.toProto())
        .addAllSignature(signature.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.FriendlyLanguageComponent.toProto():
    Contract.FriendlyLanguage {
    val protoValue =
      Contract.FriendlyLanguage.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setContent(content.contractFriendlyContentToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.LegalLanguageComponent.toProto():
    Contract.LegalLanguage {
    val protoValue =
      Contract.LegalLanguage.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setContent(content.contractLegalContentToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ComputableLanguageComponent.toProto():
    Contract.ComputableLanguage {
    val protoValue =
      Contract.ComputableLanguage.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setContent(content.contractRuleContentToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun Contract.ContentDefinition.toHapi():
    org.hl7.fhir.r4.model.Contract.ContentDefinitionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContentDefinitionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setSubType(subType.toHapi())
    hapiValue.setPublisher(publisher.toHapi())
    hapiValue.setPublicationDateElement(publicationDate.toHapi())
    hapiValue.setPublicationStatus(
      org.hl7.fhir.r4.model.Contract.ContractPublicationStatus.valueOf(
        publicationStatus.value.name.replace("_", "")
      )
    )
    hapiValue.setCopyrightElement(copyright.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.toHapi(): org.hl7.fhir.r4.model.Contract.TermComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.TermComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setIssuedElement(issued.toHapi())
    hapiValue.setApplies(applies.toHapi())
    hapiValue.setTopic(topic.contractTermTopicToHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setSubType(subType.toHapi())
    hapiValue.setTextElement(text.toHapi())
    hapiValue.setSecurityLabel(securityLabelList.map { it.toHapi() })
    hapiValue.setOffer(offer.toHapi())
    hapiValue.setAsset(assetList.map { it.toHapi() })
    hapiValue.setAction(actionList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.SecurityLabel.toHapi():
    org.hl7.fhir.r4.model.Contract.SecurityLabelComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.SecurityLabelComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNumber(numberList.map { it.toHapi() })
    hapiValue.setClassification(classification.toHapi())
    hapiValue.setCategory(categoryList.map { it.toHapi() })
    hapiValue.setControl(controlList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.toHapi():
    org.hl7.fhir.r4.model.Contract.ContractOfferComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContractOfferComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setParty(partyList.map { it.toHapi() })
    hapiValue.setTopic(topic.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setDecision(decision.toHapi())
    hapiValue.setDecisionMode(decisionModeList.map { it.toHapi() })
    hapiValue.setAnswer(answerList.map { it.toHapi() })
    hapiValue.setTextElement(text.toHapi())
    hapiValue.setLinkId(linkIdList.map { it.toHapi() })
    hapiValue.setSecurityLabelNumber(securityLabelNumberList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.ContractParty.toHapi():
    org.hl7.fhir.r4.model.Contract.ContractPartyComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContractPartyComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setReference(referenceList.map { it.toHapi() })
    hapiValue.setRole(role.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.Answer.toHapi():
    org.hl7.fhir.r4.model.Contract.AnswerComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.AnswerComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setValue(value.contractTermOfferAnswerValueToHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.toHapi():
    org.hl7.fhir.r4.model.Contract.ContractAssetComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContractAssetComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setScope(scope.toHapi())
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setTypeReference(typeReferenceList.map { it.toHapi() })
    hapiValue.setSubtype(subtypeList.map { it.toHapi() })
    hapiValue.setRelationship(relationship.toHapi())
    hapiValue.setContext(contextList.map { it.toHapi() })
    hapiValue.setConditionElement(condition.toHapi())
    hapiValue.setPeriodType(periodTypeList.map { it.toHapi() })
    hapiValue.setPeriod(periodList.map { it.toHapi() })
    hapiValue.setUsePeriod(usePeriodList.map { it.toHapi() })
    hapiValue.setTextElement(text.toHapi())
    hapiValue.setLinkId(linkIdList.map { it.toHapi() })
    hapiValue.setSecurityLabelNumber(securityLabelNumberList.map { it.toHapi() })
    hapiValue.setValuedItem(valuedItemList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.AssetContext.toHapi():
    org.hl7.fhir.r4.model.Contract.AssetContextComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.AssetContextComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setReference(reference.toHapi())
    hapiValue.setCode(codeList.map { it.toHapi() })
    hapiValue.setTextElement(text.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.ValuedItem.toHapi():
    org.hl7.fhir.r4.model.Contract.ValuedItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ValuedItemComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setEntity(entity.contractTermAssetValuedItemEntityToHapi())
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setEffectiveTimeElement(effectiveTime.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setPointsElement(points.toHapi())
    hapiValue.setNet(net.toHapi())
    hapiValue.setPaymentElement(payment.toHapi())
    hapiValue.setPaymentDateElement(paymentDate.toHapi())
    hapiValue.setResponsible(responsible.toHapi())
    hapiValue.setRecipient(recipient.toHapi())
    hapiValue.setLinkId(linkIdList.map { it.toHapi() })
    hapiValue.setSecurityLabelNumber(securityLabelNumberList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.Action.toHapi(): org.hl7.fhir.r4.model.Contract.ActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ActionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDoNotPerformElement(doNotPerform.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setSubject(subjectList.map { it.toHapi() })
    hapiValue.setIntent(intent.toHapi())
    hapiValue.setLinkId(linkIdList.map { it.toHapi() })
    hapiValue.setStatus(status.toHapi())
    hapiValue.setContext(context.toHapi())
    hapiValue.setContextLinkId(contextLinkIdList.map { it.toHapi() })
    hapiValue.setOccurrence(occurrence.contractTermActionOccurrenceToHapi())
    hapiValue.setRequester(requesterList.map { it.toHapi() })
    hapiValue.setRequesterLinkId(requesterLinkIdList.map { it.toHapi() })
    hapiValue.setPerformerType(performerTypeList.map { it.toHapi() })
    hapiValue.setPerformerRole(performerRole.toHapi())
    hapiValue.setPerformer(performer.toHapi())
    hapiValue.setPerformerLinkId(performerLinkIdList.map { it.toHapi() })
    hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    hapiValue.setReasonReference(reasonReferenceList.map { it.toHapi() })
    hapiValue.setReason(reasonList.map { it.toHapi() })
    hapiValue.setReasonLinkId(reasonLinkIdList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setSecurityLabelNumber(securityLabelNumberList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.Action.ActionSubject.toHapi():
    org.hl7.fhir.r4.model.Contract.ActionSubjectComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ActionSubjectComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setReference(referenceList.map { it.toHapi() })
    hapiValue.setRole(role.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Signatory.toHapi(): org.hl7.fhir.r4.model.Contract.SignatoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.SignatoryComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setParty(party.toHapi())
    hapiValue.setSignature(signatureList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Contract.FriendlyLanguage.toHapi():
    org.hl7.fhir.r4.model.Contract.FriendlyLanguageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.FriendlyLanguageComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setContent(content.contractFriendlyContentToHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Contract.LegalLanguage.toHapi():
    org.hl7.fhir.r4.model.Contract.LegalLanguageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.LegalLanguageComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setContent(content.contractLegalContentToHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Contract.ComputableLanguage.toHapi():
    org.hl7.fhir.r4.model.Contract.ComputableLanguageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ComputableLanguageComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setContent(content.contractRuleContentToHapi())
    return hapiValue
  }
}
