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
    if (hasUrl()) {
      hapiValue.setUrlElement(url.toHapi())
    }
    if (hasVersion()) {
      hapiValue.setVersionElement(version.toHapi())
    }
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.Contract.ContractStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasLegalState()) {
      hapiValue.setLegalState(legalState.toHapi())
    }
    if (hasInstantiatesCanonical()) {
      hapiValue.setInstantiatesCanonical(instantiatesCanonical.toHapi())
    }
    if (hasInstantiatesUri()) {
      hapiValue.setInstantiatesUriElement(instantiatesUri.toHapi())
    }
    if (hasContentDerivative()) {
      hapiValue.setContentDerivative(contentDerivative.toHapi())
    }
    if (hasIssued()) {
      hapiValue.setIssuedElement(issued.toHapi())
    }
    if (hasApplies()) {
      hapiValue.setApplies(applies.toHapi())
    }
    if (hasExpirationType()) {
      hapiValue.setExpirationType(expirationType.toHapi())
    }
    if (subjectCount > 0) {
      hapiValue.setSubject(subjectList.map { it.toHapi() })
    }
    if (authorityCount > 0) {
      hapiValue.setAuthority(authorityList.map { it.toHapi() })
    }
    if (domainCount > 0) {
      hapiValue.setDomain(domainList.map { it.toHapi() })
    }
    if (siteCount > 0) {
      hapiValue.setSite(siteList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasTitle()) {
      hapiValue.setTitleElement(title.toHapi())
    }
    if (hasSubtitle()) {
      hapiValue.setSubtitleElement(subtitle.toHapi())
    }
    if (aliasCount > 0) {
      hapiValue.setAlias(aliasList.map { it.toHapi() })
    }
    if (hasAuthor()) {
      hapiValue.setAuthor(author.toHapi())
    }
    if (hasScope()) {
      hapiValue.setScope(scope.toHapi())
    }
    if (hasTopic()) {
      hapiValue.setTopic(topic.contractTopicToHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (subTypeCount > 0) {
      hapiValue.setSubType(subTypeList.map { it.toHapi() })
    }
    if (hasContentDefinition()) {
      hapiValue.setContentDefinition(contentDefinition.toHapi())
    }
    if (termCount > 0) {
      hapiValue.setTerm(termList.map { it.toHapi() })
    }
    if (supportingInfoCount > 0) {
      hapiValue.setSupportingInfo(supportingInfoList.map { it.toHapi() })
    }
    if (relevantHistoryCount > 0) {
      hapiValue.setRelevantHistory(relevantHistoryList.map { it.toHapi() })
    }
    if (signerCount > 0) {
      hapiValue.setSigner(signerList.map { it.toHapi() })
    }
    if (friendlyCount > 0) {
      hapiValue.setFriendly(friendlyList.map { it.toHapi() })
    }
    if (legalCount > 0) {
      hapiValue.setLegal(legalList.map { it.toHapi() })
    }
    if (ruleCount > 0) {
      hapiValue.setRule(ruleList.map { it.toHapi() })
    }
    if (hasLegallyBinding()) {
      hapiValue.setLegallyBinding(legallyBinding.contractLegallyBindingToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Contract.toProto(): Contract {
    val protoValue = Contract.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasUrl()) {
      protoValue.setUrl(urlElement.toProto())
    }
    if (hasVersion()) {
      protoValue.setVersion(versionElement.toProto())
    }
    protoValue.setStatus(
      Contract.StatusCode.newBuilder()
        .setValue(
          ContractResourceStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasLegalState()) {
      protoValue.setLegalState(legalState.toProto())
    }
    if (hasInstantiatesCanonical()) {
      protoValue.setInstantiatesCanonical(instantiatesCanonical.toProto())
    }
    if (hasInstantiatesUri()) {
      protoValue.setInstantiatesUri(instantiatesUriElement.toProto())
    }
    if (hasContentDerivative()) {
      protoValue.setContentDerivative(contentDerivative.toProto())
    }
    if (hasIssued()) {
      protoValue.setIssued(issuedElement.toProto())
    }
    if (hasApplies()) {
      protoValue.setApplies(applies.toProto())
    }
    if (hasExpirationType()) {
      protoValue.setExpirationType(expirationType.toProto())
    }
    if (hasSubject()) {
      protoValue.addAllSubject(subject.map { it.toProto() })
    }
    if (hasAuthority()) {
      protoValue.addAllAuthority(authority.map { it.toProto() })
    }
    if (hasDomain()) {
      protoValue.addAllDomain(domain.map { it.toProto() })
    }
    if (hasSite()) {
      protoValue.addAllSite(site.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasTitle()) {
      protoValue.setTitle(titleElement.toProto())
    }
    if (hasSubtitle()) {
      protoValue.setSubtitle(subtitleElement.toProto())
    }
    if (hasAlias()) {
      protoValue.addAllAlias(alias.map { it.toProto() })
    }
    if (hasAuthor()) {
      protoValue.setAuthor(author.toProto())
    }
    if (hasScope()) {
      protoValue.setScope(scope.toProto())
    }
    if (hasTopic()) {
      protoValue.setTopic(topic.contractTopicToProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasSubType()) {
      protoValue.addAllSubType(subType.map { it.toProto() })
    }
    if (hasContentDefinition()) {
      protoValue.setContentDefinition(contentDefinition.toProto())
    }
    if (hasTerm()) {
      protoValue.addAllTerm(term.map { it.toProto() })
    }
    if (hasSupportingInfo()) {
      protoValue.addAllSupportingInfo(supportingInfo.map { it.toProto() })
    }
    if (hasRelevantHistory()) {
      protoValue.addAllRelevantHistory(relevantHistory.map { it.toProto() })
    }
    if (hasSigner()) {
      protoValue.addAllSigner(signer.map { it.toProto() })
    }
    if (hasFriendly()) {
      protoValue.addAllFriendly(friendly.map { it.toProto() })
    }
    if (hasLegal()) {
      protoValue.addAllLegal(legal.map { it.toProto() })
    }
    if (hasRule()) {
      protoValue.addAllRule(rule.map { it.toProto() })
    }
    if (hasLegallyBinding()) {
      protoValue.setLegallyBinding(legallyBinding.contractLegallyBindingToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ContentDefinitionComponent.toProto():
    Contract.ContentDefinition {
    val protoValue = Contract.ContentDefinition.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasSubType()) {
      protoValue.setSubType(subType.toProto())
    }
    if (hasPublisher()) {
      protoValue.setPublisher(publisher.toProto())
    }
    if (hasPublicationDate()) {
      protoValue.setPublicationDate(publicationDateElement.toProto())
    }
    protoValue.setPublicationStatus(
      Contract.ContentDefinition.PublicationStatusCode.newBuilder()
        .setValue(
          ContractResourcePublicationStatusCode.Value.valueOf(
            publicationStatus.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasCopyright()) {
      protoValue.setCopyright(copyrightElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.TermComponent.toProto(): Contract.Term {
    val protoValue = Contract.Term.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.setIdentifier(identifier.toProto())
    }
    if (hasIssued()) {
      protoValue.setIssued(issuedElement.toProto())
    }
    if (hasApplies()) {
      protoValue.setApplies(applies.toProto())
    }
    if (hasTopic()) {
      protoValue.setTopic(topic.contractTermTopicToProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasSubType()) {
      protoValue.setSubType(subType.toProto())
    }
    if (hasText()) {
      protoValue.setText(textElement.toProto())
    }
    if (hasSecurityLabel()) {
      protoValue.addAllSecurityLabel(securityLabel.map { it.toProto() })
    }
    if (hasOffer()) {
      protoValue.setOffer(offer.toProto())
    }
    if (hasAsset()) {
      protoValue.addAllAsset(asset.map { it.toProto() })
    }
    if (hasAction()) {
      protoValue.addAllAction(action.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.SecurityLabelComponent.toProto():
    Contract.Term.SecurityLabel {
    val protoValue =
      Contract.Term.SecurityLabel.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasNumber()) {
      protoValue.addAllNumber(number.map { it.toProto() })
    }
    if (hasClassification()) {
      protoValue.setClassification(classification.toProto())
    }
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasControl()) {
      protoValue.addAllControl(control.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ContractOfferComponent.toProto():
    Contract.Term.ContractOffer {
    val protoValue =
      Contract.Term.ContractOffer.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasParty()) {
      protoValue.addAllParty(party.map { it.toProto() })
    }
    if (hasTopic()) {
      protoValue.setTopic(topic.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasDecision()) {
      protoValue.setDecision(decision.toProto())
    }
    if (hasDecisionMode()) {
      protoValue.addAllDecisionMode(decisionMode.map { it.toProto() })
    }
    if (hasAnswer()) {
      protoValue.addAllAnswer(answer.map { it.toProto() })
    }
    if (hasText()) {
      protoValue.setText(textElement.toProto())
    }
    if (hasLinkId()) {
      protoValue.addAllLinkId(linkId.map { it.toProto() })
    }
    if (hasSecurityLabelNumber()) {
      protoValue.addAllSecurityLabelNumber(securityLabelNumber.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ContractPartyComponent.toProto():
    Contract.Term.ContractOffer.ContractParty {
    val protoValue =
      Contract.Term.ContractOffer.ContractParty.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasReference()) {
      protoValue.addAllReference(reference.map { it.toProto() })
    }
    if (hasRole()) {
      protoValue.setRole(role.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.AnswerComponent.toProto():
    Contract.Term.ContractOffer.Answer {
    val protoValue =
      Contract.Term.ContractOffer.Answer.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasValue()) {
      protoValue.setValue(value.contractTermOfferAnswerValueToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ContractAssetComponent.toProto():
    Contract.Term.ContractAsset {
    val protoValue =
      Contract.Term.ContractAsset.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasScope()) {
      protoValue.setScope(scope.toProto())
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasTypeReference()) {
      protoValue.addAllTypeReference(typeReference.map { it.toProto() })
    }
    if (hasSubtype()) {
      protoValue.addAllSubtype(subtype.map { it.toProto() })
    }
    if (hasRelationship()) {
      protoValue.setRelationship(relationship.toProto())
    }
    if (hasContext()) {
      protoValue.addAllContext(context.map { it.toProto() })
    }
    if (hasCondition()) {
      protoValue.setCondition(conditionElement.toProto())
    }
    if (hasPeriodType()) {
      protoValue.addAllPeriodType(periodType.map { it.toProto() })
    }
    if (hasPeriod()) {
      protoValue.addAllPeriod(period.map { it.toProto() })
    }
    if (hasUsePeriod()) {
      protoValue.addAllUsePeriod(usePeriod.map { it.toProto() })
    }
    if (hasText()) {
      protoValue.setText(textElement.toProto())
    }
    if (hasLinkId()) {
      protoValue.addAllLinkId(linkId.map { it.toProto() })
    }
    if (hasSecurityLabelNumber()) {
      protoValue.addAllSecurityLabelNumber(securityLabelNumber.map { it.toProto() })
    }
    if (hasValuedItem()) {
      protoValue.addAllValuedItem(valuedItem.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.AssetContextComponent.toProto():
    Contract.Term.ContractAsset.AssetContext {
    val protoValue =
      Contract.Term.ContractAsset.AssetContext.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasReference()) {
      protoValue.setReference(reference.toProto())
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasText()) {
      protoValue.setText(textElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ValuedItemComponent.toProto():
    Contract.Term.ContractAsset.ValuedItem {
    val protoValue =
      Contract.Term.ContractAsset.ValuedItem.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasEntity()) {
      protoValue.setEntity(entity.contractTermAssetValuedItemEntityToProto())
    }
    if (hasIdentifier()) {
      protoValue.setIdentifier(identifier.toProto())
    }
    if (hasEffectiveTime()) {
      protoValue.setEffectiveTime(effectiveTimeElement.toProto())
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
    if (hasPoints()) {
      protoValue.setPoints(pointsElement.toProto())
    }
    if (hasNet()) {
      protoValue.setNet(net.toProto())
    }
    if (hasPayment()) {
      protoValue.setPayment(paymentElement.toProto())
    }
    if (hasPaymentDate()) {
      protoValue.setPaymentDate(paymentDateElement.toProto())
    }
    if (hasResponsible()) {
      protoValue.setResponsible(responsible.toProto())
    }
    if (hasRecipient()) {
      protoValue.setRecipient(recipient.toProto())
    }
    if (hasLinkId()) {
      protoValue.addAllLinkId(linkId.map { it.toProto() })
    }
    if (hasSecurityLabelNumber()) {
      protoValue.addAllSecurityLabelNumber(securityLabelNumber.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ActionComponent.toProto(): Contract.Term.Action {
    val protoValue = Contract.Term.Action.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDoNotPerform()) {
      protoValue.setDoNotPerform(doNotPerformElement.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasSubject()) {
      protoValue.addAllSubject(subject.map { it.toProto() })
    }
    if (hasIntent()) {
      protoValue.setIntent(intent.toProto())
    }
    if (hasLinkId()) {
      protoValue.addAllLinkId(linkId.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.setStatus(status.toProto())
    }
    if (hasContext()) {
      protoValue.setContext(context.toProto())
    }
    if (hasContextLinkId()) {
      protoValue.addAllContextLinkId(contextLinkId.map { it.toProto() })
    }
    if (hasOccurrence()) {
      protoValue.setOccurrence(occurrence.contractTermActionOccurrenceToProto())
    }
    if (hasRequester()) {
      protoValue.addAllRequester(requester.map { it.toProto() })
    }
    if (hasRequesterLinkId()) {
      protoValue.addAllRequesterLinkId(requesterLinkId.map { it.toProto() })
    }
    if (hasPerformerType()) {
      protoValue.addAllPerformerType(performerType.map { it.toProto() })
    }
    if (hasPerformerRole()) {
      protoValue.setPerformerRole(performerRole.toProto())
    }
    if (hasPerformer()) {
      protoValue.setPerformer(performer.toProto())
    }
    if (hasPerformerLinkId()) {
      protoValue.addAllPerformerLinkId(performerLinkId.map { it.toProto() })
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasReason()) {
      protoValue.addAllReason(reason.map { it.toProto() })
    }
    if (hasReasonLinkId()) {
      protoValue.addAllReasonLinkId(reasonLinkId.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasSecurityLabelNumber()) {
      protoValue.addAllSecurityLabelNumber(securityLabelNumber.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ActionSubjectComponent.toProto():
    Contract.Term.Action.ActionSubject {
    val protoValue =
      Contract.Term.Action.ActionSubject.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasReference()) {
      protoValue.addAllReference(reference.map { it.toProto() })
    }
    if (hasRole()) {
      protoValue.setRole(role.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.SignatoryComponent.toProto(): Contract.Signatory {
    val protoValue = Contract.Signatory.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasParty()) {
      protoValue.setParty(party.toProto())
    }
    if (hasSignature()) {
      protoValue.addAllSignature(signature.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.FriendlyLanguageComponent.toProto():
    Contract.FriendlyLanguage {
    val protoValue = Contract.FriendlyLanguage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasContent()) {
      protoValue.setContent(content.contractFriendlyContentToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.LegalLanguageComponent.toProto():
    Contract.LegalLanguage {
    val protoValue = Contract.LegalLanguage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasContent()) {
      protoValue.setContent(content.contractLegalContentToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Contract.ComputableLanguageComponent.toProto():
    Contract.ComputableLanguage {
    val protoValue =
      Contract.ComputableLanguage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasContent()) {
      protoValue.setContent(content.contractRuleContentToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.ContentDefinition.toHapi():
    org.hl7.fhir.r4.model.Contract.ContentDefinitionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContentDefinitionComponent()
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
    if (hasSubType()) {
      hapiValue.setSubType(subType.toHapi())
    }
    if (hasPublisher()) {
      hapiValue.setPublisher(publisher.toHapi())
    }
    if (hasPublicationDate()) {
      hapiValue.setPublicationDateElement(publicationDate.toHapi())
    }
    hapiValue.setPublicationStatus(
      org.hl7.fhir.r4.model.Contract.ContractPublicationStatus.valueOf(
        publicationStatus.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasCopyright()) {
      hapiValue.setCopyrightElement(copyright.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.toHapi(): org.hl7.fhir.r4.model.Contract.TermComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.TermComponent()
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
    if (hasIssued()) {
      hapiValue.setIssuedElement(issued.toHapi())
    }
    if (hasApplies()) {
      hapiValue.setApplies(applies.toHapi())
    }
    if (hasTopic()) {
      hapiValue.setTopic(topic.contractTermTopicToHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasSubType()) {
      hapiValue.setSubType(subType.toHapi())
    }
    if (hasText()) {
      hapiValue.setTextElement(text.toHapi())
    }
    if (securityLabelCount > 0) {
      hapiValue.setSecurityLabel(securityLabelList.map { it.toHapi() })
    }
    if (hasOffer()) {
      hapiValue.setOffer(offer.toHapi())
    }
    if (assetCount > 0) {
      hapiValue.setAsset(assetList.map { it.toHapi() })
    }
    if (actionCount > 0) {
      hapiValue.setAction(actionList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.SecurityLabel.toHapi():
    org.hl7.fhir.r4.model.Contract.SecurityLabelComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.SecurityLabelComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (numberCount > 0) {
      hapiValue.setNumber(numberList.map { it.toHapi() })
    }
    if (hasClassification()) {
      hapiValue.setClassification(classification.toHapi())
    }
    if (categoryCount > 0) {
      hapiValue.setCategory(categoryList.map { it.toHapi() })
    }
    if (controlCount > 0) {
      hapiValue.setControl(controlList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.toHapi():
    org.hl7.fhir.r4.model.Contract.ContractOfferComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContractOfferComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    if (partyCount > 0) {
      hapiValue.setParty(partyList.map { it.toHapi() })
    }
    if (hasTopic()) {
      hapiValue.setTopic(topic.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasDecision()) {
      hapiValue.setDecision(decision.toHapi())
    }
    if (decisionModeCount > 0) {
      hapiValue.setDecisionMode(decisionModeList.map { it.toHapi() })
    }
    if (answerCount > 0) {
      hapiValue.setAnswer(answerList.map { it.toHapi() })
    }
    if (hasText()) {
      hapiValue.setTextElement(text.toHapi())
    }
    if (linkIdCount > 0) {
      hapiValue.setLinkId(linkIdList.map { it.toHapi() })
    }
    if (securityLabelNumberCount > 0) {
      hapiValue.setSecurityLabelNumber(securityLabelNumberList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.ContractParty.toHapi():
    org.hl7.fhir.r4.model.Contract.ContractPartyComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContractPartyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (referenceCount > 0) {
      hapiValue.setReference(referenceList.map { it.toHapi() })
    }
    if (hasRole()) {
      hapiValue.setRole(role.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.Answer.toHapi():
    org.hl7.fhir.r4.model.Contract.AnswerComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.AnswerComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasValue()) {
      hapiValue.setValue(value.contractTermOfferAnswerValueToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.toHapi():
    org.hl7.fhir.r4.model.Contract.ContractAssetComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContractAssetComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasScope()) {
      hapiValue.setScope(scope.toHapi())
    }
    if (typeCount > 0) {
      hapiValue.setType(typeList.map { it.toHapi() })
    }
    if (typeReferenceCount > 0) {
      hapiValue.setTypeReference(typeReferenceList.map { it.toHapi() })
    }
    if (subtypeCount > 0) {
      hapiValue.setSubtype(subtypeList.map { it.toHapi() })
    }
    if (hasRelationship()) {
      hapiValue.setRelationship(relationship.toHapi())
    }
    if (contextCount > 0) {
      hapiValue.setContext(contextList.map { it.toHapi() })
    }
    if (hasCondition()) {
      hapiValue.setConditionElement(condition.toHapi())
    }
    if (periodTypeCount > 0) {
      hapiValue.setPeriodType(periodTypeList.map { it.toHapi() })
    }
    if (periodCount > 0) {
      hapiValue.setPeriod(periodList.map { it.toHapi() })
    }
    if (usePeriodCount > 0) {
      hapiValue.setUsePeriod(usePeriodList.map { it.toHapi() })
    }
    if (hasText()) {
      hapiValue.setTextElement(text.toHapi())
    }
    if (linkIdCount > 0) {
      hapiValue.setLinkId(linkIdList.map { it.toHapi() })
    }
    if (securityLabelNumberCount > 0) {
      hapiValue.setSecurityLabelNumber(securityLabelNumberList.map { it.toHapi() })
    }
    if (valuedItemCount > 0) {
      hapiValue.setValuedItem(valuedItemList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.AssetContext.toHapi():
    org.hl7.fhir.r4.model.Contract.AssetContextComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.AssetContextComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasReference()) {
      hapiValue.setReference(reference.toHapi())
    }
    if (codeCount > 0) {
      hapiValue.setCode(codeList.map { it.toHapi() })
    }
    if (hasText()) {
      hapiValue.setTextElement(text.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.ValuedItem.toHapi():
    org.hl7.fhir.r4.model.Contract.ValuedItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ValuedItemComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasEntity()) {
      hapiValue.setEntity(entity.contractTermAssetValuedItemEntityToHapi())
    }
    if (hasIdentifier()) {
      hapiValue.setIdentifier(identifier.toHapi())
    }
    if (hasEffectiveTime()) {
      hapiValue.setEffectiveTimeElement(effectiveTime.toHapi())
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
    if (hasPoints()) {
      hapiValue.setPointsElement(points.toHapi())
    }
    if (hasNet()) {
      hapiValue.setNet(net.toHapi())
    }
    if (hasPayment()) {
      hapiValue.setPaymentElement(payment.toHapi())
    }
    if (hasPaymentDate()) {
      hapiValue.setPaymentDateElement(paymentDate.toHapi())
    }
    if (hasResponsible()) {
      hapiValue.setResponsible(responsible.toHapi())
    }
    if (hasRecipient()) {
      hapiValue.setRecipient(recipient.toHapi())
    }
    if (linkIdCount > 0) {
      hapiValue.setLinkId(linkIdList.map { it.toHapi() })
    }
    if (securityLabelNumberCount > 0) {
      hapiValue.setSecurityLabelNumber(securityLabelNumberList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.Action.toHapi(): org.hl7.fhir.r4.model.Contract.ActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ActionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasDoNotPerform()) {
      hapiValue.setDoNotPerformElement(doNotPerform.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (subjectCount > 0) {
      hapiValue.setSubject(subjectList.map { it.toHapi() })
    }
    if (hasIntent()) {
      hapiValue.setIntent(intent.toHapi())
    }
    if (linkIdCount > 0) {
      hapiValue.setLinkId(linkIdList.map { it.toHapi() })
    }
    if (hasStatus()) {
      hapiValue.setStatus(status.toHapi())
    }
    if (hasContext()) {
      hapiValue.setContext(context.toHapi())
    }
    if (contextLinkIdCount > 0) {
      hapiValue.setContextLinkId(contextLinkIdList.map { it.toHapi() })
    }
    if (hasOccurrence()) {
      hapiValue.setOccurrence(occurrence.contractTermActionOccurrenceToHapi())
    }
    if (requesterCount > 0) {
      hapiValue.setRequester(requesterList.map { it.toHapi() })
    }
    if (requesterLinkIdCount > 0) {
      hapiValue.setRequesterLinkId(requesterLinkIdList.map { it.toHapi() })
    }
    if (performerTypeCount > 0) {
      hapiValue.setPerformerType(performerTypeList.map { it.toHapi() })
    }
    if (hasPerformerRole()) {
      hapiValue.setPerformerRole(performerRole.toHapi())
    }
    if (hasPerformer()) {
      hapiValue.setPerformer(performer.toHapi())
    }
    if (performerLinkIdCount > 0) {
      hapiValue.setPerformerLinkId(performerLinkIdList.map { it.toHapi() })
    }
    if (reasonCodeCount > 0) {
      hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    }
    if (reasonReferenceCount > 0) {
      hapiValue.setReasonReference(reasonReferenceList.map { it.toHapi() })
    }
    if (reasonCount > 0) {
      hapiValue.setReason(reasonList.map { it.toHapi() })
    }
    if (reasonLinkIdCount > 0) {
      hapiValue.setReasonLinkId(reasonLinkIdList.map { it.toHapi() })
    }
    if (noteCount > 0) {
      hapiValue.setNote(noteList.map { it.toHapi() })
    }
    if (securityLabelNumberCount > 0) {
      hapiValue.setSecurityLabelNumber(securityLabelNumberList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.Action.ActionSubject.toHapi():
    org.hl7.fhir.r4.model.Contract.ActionSubjectComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ActionSubjectComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (referenceCount > 0) {
      hapiValue.setReference(referenceList.map { it.toHapi() })
    }
    if (hasRole()) {
      hapiValue.setRole(role.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Signatory.toHapi(): org.hl7.fhir.r4.model.Contract.SignatoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.SignatoryComponent()
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
    if (hasParty()) {
      hapiValue.setParty(party.toHapi())
    }
    if (signatureCount > 0) {
      hapiValue.setSignature(signatureList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.FriendlyLanguage.toHapi():
    org.hl7.fhir.r4.model.Contract.FriendlyLanguageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.FriendlyLanguageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasContent()) {
      hapiValue.setContent(content.contractFriendlyContentToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.LegalLanguage.toHapi():
    org.hl7.fhir.r4.model.Contract.LegalLanguageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.LegalLanguageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasContent()) {
      hapiValue.setContent(content.contractLegalContentToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.ComputableLanguage.toHapi():
    org.hl7.fhir.r4.model.Contract.ComputableLanguageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ComputableLanguageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasContent()) {
      hapiValue.setContent(content.contractRuleContentToHapi())
    }
    return hapiValue
  }
}
