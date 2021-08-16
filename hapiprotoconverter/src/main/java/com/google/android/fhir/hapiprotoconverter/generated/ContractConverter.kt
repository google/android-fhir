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

object ContractConverter {
  @JvmStatic
  private fun Contract.TopicX.contractTopicToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.topic[x]")
  }

  @JvmStatic
  private fun Type.contractTopicToProto(): Contract.TopicX {
    val protoValue = Contract.TopicX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
        protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.Term.TopicX.contractTermTopicToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.term.topic[x]")
  }

  @JvmStatic
  private fun Type.contractTermTopicToProto(): Contract.Term.TopicX {
    val protoValue = Contract.Term.TopicX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
        protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.Answer.ValueX.contractTermOfferAnswerValueToHapi(): Type {
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.decimal != Decimal.newBuilder().defaultInstanceForType) {
      return (this.decimal).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.time != Time.newBuilder().defaultInstanceForType) {
      return (this.time).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.uri != Uri.newBuilder().defaultInstanceForType) {
      return (this.uri).toHapi()
    }
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.coding != Coding.newBuilder().defaultInstanceForType) {
      return (this.coding).toHapi()
    }
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.term.offer.answer.value[x]")
  }

  @JvmStatic
  private fun Type.contractTermOfferAnswerValueToProto():
    Contract.Term.ContractOffer.Answer.ValueX {
    val protoValue = Contract.Term.ContractOffer.Answer.ValueX.newBuilder()
    if (this is BooleanType) {
        protoValue.boolean = this.toProto()
    }
    if (this is DecimalType) {
        protoValue.decimal = this.toProto()
    }
    if (this is IntegerType) {
        protoValue.integer = this.toProto()
    }
    if (this is DateType) {
        protoValue.date = this.toProto()
    }
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    if (this is TimeType) {
        protoValue.time = this.toProto()
    }
    if (this is StringType) {
        protoValue.stringValue = this.toProto()
    }
    if (this is UriType) {
        protoValue.uri = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
        protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
        protoValue.coding = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
        protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.ValuedItem.EntityX.contractTermAssetValuedItemEntityToHapi():
    Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.term.asset.valuedItem.entity[x]")
  }

  @JvmStatic
  private fun Type.contractTermAssetValuedItemEntityToProto():
    Contract.Term.ContractAsset.ValuedItem.EntityX {
    val protoValue = Contract.Term.ContractAsset.ValuedItem.EntityX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
        protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.Term.Action.OccurrenceX.contractTermActionOccurrenceToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.term.action.occurrence[x]")
  }

  @JvmStatic
  private fun Type.contractTermActionOccurrenceToProto(): Contract.Term.Action.OccurrenceX {
    val protoValue = Contract.Term.Action.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
        protoValue.timing = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.FriendlyLanguage.ContentX.contractFriendlyContentToHapi(): Type {
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.friendly.content[x]")
  }

  @JvmStatic
  private fun Type.contractFriendlyContentToProto(): Contract.FriendlyLanguage.ContentX {
    val protoValue = Contract.FriendlyLanguage.ContentX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Attachment) {
        protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.LegalLanguage.ContentX.contractLegalContentToHapi(): Type {
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.legal.content[x]")
  }

  @JvmStatic
  private fun Type.contractLegalContentToProto(): Contract.LegalLanguage.ContentX {
    val protoValue = Contract.LegalLanguage.ContentX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Attachment) {
        protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.ComputableLanguage.ContentX.contractRuleContentToHapi(): Type {
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.rule.content[x]")
  }

  @JvmStatic
  private fun Type.contractRuleContentToProto(): Contract.ComputableLanguage.ContentX {
    val protoValue = Contract.ComputableLanguage.ContentX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Attachment) {
        protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.LegallyBindingX.contractLegallyBindingToHapi(): Type {
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Contract.legallyBinding[x]")
  }

  @JvmStatic
  private fun Type.contractLegallyBindingToProto(): Contract.LegallyBindingX {
    val protoValue = Contract.LegallyBindingX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Attachment) {
        protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Contract.toHapi(): org.hl7.fhir.r4.model.Contract {
    val hapiValue = org.hl7.fhir.r4.model.Contract()
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
    if (hasUrl()) {
        hapiValue.urlElement = url.toHapi()
    }
    if (hasVersion()) {
        hapiValue.versionElement = version.toHapi()
    }
      hapiValue.status = org.hl7.fhir.r4.model.Contract.ContractStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasLegalState()) {
        hapiValue.legalState = legalState.toHapi()
    }
    if (hasInstantiatesCanonical()) {
        hapiValue.instantiatesCanonical = instantiatesCanonical.toHapi()
    }
    if (hasInstantiatesUri()) {
        hapiValue.instantiatesUriElement = instantiatesUri.toHapi()
    }
    if (hasContentDerivative()) {
        hapiValue.contentDerivative = contentDerivative.toHapi()
    }
    if (hasIssued()) {
        hapiValue.issuedElement = issued.toHapi()
    }
    if (hasApplies()) {
        hapiValue.applies = applies.toHapi()
    }
    if (hasExpirationType()) {
        hapiValue.expirationType = expirationType.toHapi()
    }
    if (subjectCount > 0) {
        hapiValue.subject = subjectList.map { it.toHapi() }
    }
    if (authorityCount > 0) {
        hapiValue.authority = authorityList.map { it.toHapi() }
    }
    if (domainCount > 0) {
        hapiValue.domain = domainList.map { it.toHapi() }
    }
    if (siteCount > 0) {
        hapiValue.site = siteList.map { it.toHapi() }
    }
    if (hasName()) {
        hapiValue.nameElement = name.toHapi()
    }
    if (hasTitle()) {
        hapiValue.titleElement = title.toHapi()
    }
    if (hasSubtitle()) {
        hapiValue.subtitleElement = subtitle.toHapi()
    }
    if (aliasCount > 0) {
        hapiValue.alias = aliasList.map { it.toHapi() }
    }
    if (hasAuthor()) {
        hapiValue.author = author.toHapi()
    }
    if (hasScope()) {
        hapiValue.scope = scope.toHapi()
    }
    if (hasTopic()) {
        hapiValue.topic = topic.contractTopicToHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (subTypeCount > 0) {
        hapiValue.subType = subTypeList.map { it.toHapi() }
    }
    if (hasContentDefinition()) {
        hapiValue.contentDefinition = contentDefinition.toHapi()
    }
    if (termCount > 0) {
        hapiValue.term = termList.map { it.toHapi() }
    }
    if (supportingInfoCount > 0) {
        hapiValue.supportingInfo = supportingInfoList.map { it.toHapi() }
    }
    if (relevantHistoryCount > 0) {
        hapiValue.relevantHistory = relevantHistoryList.map { it.toHapi() }
    }
    if (signerCount > 0) {
        hapiValue.signer = signerList.map { it.toHapi() }
    }
    if (friendlyCount > 0) {
        hapiValue.friendly = friendlyList.map { it.toHapi() }
    }
    if (legalCount > 0) {
        hapiValue.legal = legalList.map { it.toHapi() }
    }
    if (ruleCount > 0) {
        hapiValue.rule = ruleList.map { it.toHapi() }
    }
    if (hasLegallyBinding()) {
        hapiValue.legallyBinding = legallyBinding.contractLegallyBindingToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Contract.toProto(): Contract {
    val protoValue = Contract.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasUrl()) {
        protoValue.url = urlElement.toProto()
    }
    if (hasVersion()) {
        protoValue.version = versionElement.toProto()
    }
      protoValue.status = Contract.StatusCode.newBuilder()
          .setValue(
              ContractResourceStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasLegalState()) {
        protoValue.legalState = legalState.toProto()
    }
    if (hasInstantiatesCanonical()) {
        protoValue.instantiatesCanonical = instantiatesCanonical.toProto()
    }
    if (hasInstantiatesUri()) {
        protoValue.instantiatesUri = instantiatesUriElement.toProto()
    }
    if (hasContentDerivative()) {
        protoValue.contentDerivative = contentDerivative.toProto()
    }
    if (hasIssued()) {
        protoValue.issued = issuedElement.toProto()
    }
    if (hasApplies()) {
        protoValue.applies = applies.toProto()
    }
    if (hasExpirationType()) {
        protoValue.expirationType = expirationType.toProto()
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
        protoValue.name = nameElement.toProto()
    }
    if (hasTitle()) {
        protoValue.title = titleElement.toProto()
    }
    if (hasSubtitle()) {
        protoValue.subtitle = subtitleElement.toProto()
    }
    if (hasAlias()) {
      protoValue.addAllAlias(alias.map { it.toProto() })
    }
    if (hasAuthor()) {
        protoValue.author = author.toProto()
    }
    if (hasScope()) {
        protoValue.scope = scope.toProto()
    }
    if (hasTopic()) {
        protoValue.topic = topic.contractTopicToProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasSubType()) {
      protoValue.addAllSubType(subType.map { it.toProto() })
    }
    if (hasContentDefinition()) {
        protoValue.contentDefinition = contentDefinition.toProto()
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
        protoValue.legallyBinding = legallyBinding.contractLegallyBindingToProto()
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
        protoValue.type = type.toProto()
    }
    if (hasSubType()) {
        protoValue.subType = subType.toProto()
    }
    if (hasPublisher()) {
        protoValue.publisher = publisher.toProto()
    }
    if (hasPublicationDate()) {
        protoValue.publicationDate = publicationDateElement.toProto()
    }
      protoValue.publicationStatus = Contract.ContentDefinition.PublicationStatusCode.newBuilder()
          .setValue(
              ContractResourcePublicationStatusCode.Value.valueOf(
                  publicationStatus.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasCopyright()) {
        protoValue.copyright = copyrightElement.toProto()
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
        protoValue.identifier = identifier.toProto()
    }
    if (hasIssued()) {
        protoValue.issued = issuedElement.toProto()
    }
    if (hasApplies()) {
        protoValue.applies = applies.toProto()
    }
    if (hasTopic()) {
        protoValue.topic = topic.contractTermTopicToProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasSubType()) {
        protoValue.subType = subType.toProto()
    }
    if (hasText()) {
        protoValue.text = textElement.toProto()
    }
    if (hasSecurityLabel()) {
      protoValue.addAllSecurityLabel(securityLabel.map { it.toProto() })
    }
    if (hasOffer()) {
        protoValue.offer = offer.toProto()
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
        protoValue.classification = classification.toProto()
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
        protoValue.topic = topic.toProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasDecision()) {
        protoValue.decision = decision.toProto()
    }
    if (hasDecisionMode()) {
      protoValue.addAllDecisionMode(decisionMode.map { it.toProto() })
    }
    if (hasAnswer()) {
      protoValue.addAllAnswer(answer.map { it.toProto() })
    }
    if (hasText()) {
        protoValue.text = textElement.toProto()
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
        protoValue.role = role.toProto()
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
        protoValue.value = value.contractTermOfferAnswerValueToProto()
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
        protoValue.scope = scope.toProto()
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
        protoValue.relationship = relationship.toProto()
    }
    if (hasContext()) {
      protoValue.addAllContext(context.map { it.toProto() })
    }
    if (hasCondition()) {
        protoValue.condition = conditionElement.toProto()
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
        protoValue.text = textElement.toProto()
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
        protoValue.reference = reference.toProto()
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasText()) {
        protoValue.text = textElement.toProto()
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
        protoValue.entity = entity.contractTermAssetValuedItemEntityToProto()
    }
    if (hasIdentifier()) {
        protoValue.identifier = identifier.toProto()
    }
    if (hasEffectiveTime()) {
        protoValue.effectiveTime = effectiveTimeElement.toProto()
    }
    if (hasQuantity()) {
        protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasUnitPrice()) {
        protoValue.unitPrice = unitPrice.toProto()
    }
    if (hasFactor()) {
        protoValue.factor = factorElement.toProto()
    }
    if (hasPoints()) {
        protoValue.points = pointsElement.toProto()
    }
    if (hasNet()) {
        protoValue.net = net.toProto()
    }
    if (hasPayment()) {
        protoValue.payment = paymentElement.toProto()
    }
    if (hasPaymentDate()) {
        protoValue.paymentDate = paymentDateElement.toProto()
    }
    if (hasResponsible()) {
        protoValue.responsible = responsible.toProto()
    }
    if (hasRecipient()) {
        protoValue.recipient = recipient.toProto()
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
        protoValue.doNotPerform = doNotPerformElement.toProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasSubject()) {
      protoValue.addAllSubject(subject.map { it.toProto() })
    }
    if (hasIntent()) {
        protoValue.intent = intent.toProto()
    }
    if (hasLinkId()) {
      protoValue.addAllLinkId(linkId.map { it.toProto() })
    }
    if (hasStatus()) {
        protoValue.status = status.toProto()
    }
    if (hasContext()) {
        protoValue.context = context.toProto()
    }
    if (hasContextLinkId()) {
      protoValue.addAllContextLinkId(contextLinkId.map { it.toProto() })
    }
    if (hasOccurrence()) {
        protoValue.occurrence = occurrence.contractTermActionOccurrenceToProto()
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
        protoValue.performerRole = performerRole.toProto()
    }
    if (hasPerformer()) {
        protoValue.performer = performer.toProto()
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
        protoValue.role = role.toProto()
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
        protoValue.type = type.toProto()
    }
    if (hasParty()) {
        protoValue.party = party.toProto()
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
        protoValue.content = content.contractFriendlyContentToProto()
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
        protoValue.content = content.contractLegalContentToProto()
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
        protoValue.content = content.contractRuleContentToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Contract.ContentDefinition.toHapi():
    org.hl7.fhir.r4.model.Contract.ContentDefinitionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContentDefinitionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasSubType()) {
        hapiValue.subType = subType.toHapi()
    }
    if (hasPublisher()) {
        hapiValue.publisher = publisher.toHapi()
    }
    if (hasPublicationDate()) {
        hapiValue.publicationDateElement = publicationDate.toHapi()
    }
      hapiValue.publicationStatus = org.hl7.fhir.r4.model.Contract.ContractPublicationStatus.valueOf(
          publicationStatus.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasCopyright()) {
        hapiValue.copyrightElement = copyright.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.toHapi(): org.hl7.fhir.r4.model.Contract.TermComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.TermComponent()
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
    if (hasIssued()) {
        hapiValue.issuedElement = issued.toHapi()
    }
    if (hasApplies()) {
        hapiValue.applies = applies.toHapi()
    }
    if (hasTopic()) {
        hapiValue.topic = topic.contractTermTopicToHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasSubType()) {
        hapiValue.subType = subType.toHapi()
    }
    if (hasText()) {
        hapiValue.textElement = text.toHapi()
    }
    if (securityLabelCount > 0) {
        hapiValue.securityLabel = securityLabelList.map { it.toHapi() }
    }
    if (hasOffer()) {
        hapiValue.offer = offer.toHapi()
    }
    if (assetCount > 0) {
        hapiValue.asset = assetList.map { it.toHapi() }
    }
    if (actionCount > 0) {
        hapiValue.action = actionList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.SecurityLabel.toHapi():
    org.hl7.fhir.r4.model.Contract.SecurityLabelComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.SecurityLabelComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (numberCount > 0) {
        hapiValue.number = numberList.map { it.toHapi() }
    }
    if (hasClassification()) {
        hapiValue.classification = classification.toHapi()
    }
    if (categoryCount > 0) {
        hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (controlCount > 0) {
        hapiValue.control = controlList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.toHapi():
    org.hl7.fhir.r4.model.Contract.ContractOfferComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContractOfferComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (identifierCount > 0) {
        hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (partyCount > 0) {
        hapiValue.party = partyList.map { it.toHapi() }
    }
    if (hasTopic()) {
        hapiValue.topic = topic.toHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasDecision()) {
        hapiValue.decision = decision.toHapi()
    }
    if (decisionModeCount > 0) {
        hapiValue.decisionMode = decisionModeList.map { it.toHapi() }
    }
    if (answerCount > 0) {
        hapiValue.answer = answerList.map { it.toHapi() }
    }
    if (hasText()) {
        hapiValue.textElement = text.toHapi()
    }
    if (linkIdCount > 0) {
        hapiValue.linkId = linkIdList.map { it.toHapi() }
    }
    if (securityLabelNumberCount > 0) {
        hapiValue.securityLabelNumber = securityLabelNumberList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.ContractParty.toHapi():
    org.hl7.fhir.r4.model.Contract.ContractPartyComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContractPartyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (referenceCount > 0) {
        hapiValue.reference = referenceList.map { it.toHapi() }
    }
    if (hasRole()) {
        hapiValue.role = role.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractOffer.Answer.toHapi():
    org.hl7.fhir.r4.model.Contract.AnswerComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.AnswerComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasValue()) {
        hapiValue.value = value.contractTermOfferAnswerValueToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.toHapi():
    org.hl7.fhir.r4.model.Contract.ContractAssetComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ContractAssetComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasScope()) {
        hapiValue.scope = scope.toHapi()
    }
    if (typeCount > 0) {
        hapiValue.type = typeList.map { it.toHapi() }
    }
    if (typeReferenceCount > 0) {
        hapiValue.typeReference = typeReferenceList.map { it.toHapi() }
    }
    if (subtypeCount > 0) {
        hapiValue.subtype = subtypeList.map { it.toHapi() }
    }
    if (hasRelationship()) {
        hapiValue.relationship = relationship.toHapi()
    }
    if (contextCount > 0) {
        hapiValue.context = contextList.map { it.toHapi() }
    }
    if (hasCondition()) {
        hapiValue.conditionElement = condition.toHapi()
    }
    if (periodTypeCount > 0) {
        hapiValue.periodType = periodTypeList.map { it.toHapi() }
    }
    if (periodCount > 0) {
        hapiValue.period = periodList.map { it.toHapi() }
    }
    if (usePeriodCount > 0) {
        hapiValue.usePeriod = usePeriodList.map { it.toHapi() }
    }
    if (hasText()) {
        hapiValue.textElement = text.toHapi()
    }
    if (linkIdCount > 0) {
        hapiValue.linkId = linkIdList.map { it.toHapi() }
    }
    if (securityLabelNumberCount > 0) {
        hapiValue.securityLabelNumber = securityLabelNumberList.map { it.toHapi() }
    }
    if (valuedItemCount > 0) {
        hapiValue.valuedItem = valuedItemList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.AssetContext.toHapi():
    org.hl7.fhir.r4.model.Contract.AssetContextComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.AssetContextComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasReference()) {
        hapiValue.reference = reference.toHapi()
    }
    if (codeCount > 0) {
        hapiValue.code = codeList.map { it.toHapi() }
    }
    if (hasText()) {
        hapiValue.textElement = text.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.ContractAsset.ValuedItem.toHapi():
    org.hl7.fhir.r4.model.Contract.ValuedItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ValuedItemComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasEntity()) {
        hapiValue.entity = entity.contractTermAssetValuedItemEntityToHapi()
    }
    if (hasIdentifier()) {
        hapiValue.identifier = identifier.toHapi()
    }
    if (hasEffectiveTime()) {
        hapiValue.effectiveTimeElement = effectiveTime.toHapi()
    }
    if (hasQuantity()) {
        hapiValue.quantity = quantity.toHapi()
    }
    if (hasUnitPrice()) {
        hapiValue.unitPrice = unitPrice.toHapi()
    }
    if (hasFactor()) {
        hapiValue.factorElement = factor.toHapi()
    }
    if (hasPoints()) {
        hapiValue.pointsElement = points.toHapi()
    }
    if (hasNet()) {
        hapiValue.net = net.toHapi()
    }
    if (hasPayment()) {
        hapiValue.paymentElement = payment.toHapi()
    }
    if (hasPaymentDate()) {
        hapiValue.paymentDateElement = paymentDate.toHapi()
    }
    if (hasResponsible()) {
        hapiValue.responsible = responsible.toHapi()
    }
    if (hasRecipient()) {
        hapiValue.recipient = recipient.toHapi()
    }
    if (linkIdCount > 0) {
        hapiValue.linkId = linkIdList.map { it.toHapi() }
    }
    if (securityLabelNumberCount > 0) {
        hapiValue.securityLabelNumber = securityLabelNumberList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.Action.toHapi(): org.hl7.fhir.r4.model.Contract.ActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ActionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDoNotPerform()) {
        hapiValue.doNotPerformElement = doNotPerform.toHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (subjectCount > 0) {
        hapiValue.subject = subjectList.map { it.toHapi() }
    }
    if (hasIntent()) {
        hapiValue.intent = intent.toHapi()
    }
    if (linkIdCount > 0) {
        hapiValue.linkId = linkIdList.map { it.toHapi() }
    }
    if (hasStatus()) {
        hapiValue.status = status.toHapi()
    }
    if (hasContext()) {
        hapiValue.context = context.toHapi()
    }
    if (contextLinkIdCount > 0) {
        hapiValue.contextLinkId = contextLinkIdList.map { it.toHapi() }
    }
    if (hasOccurrence()) {
        hapiValue.occurrence = occurrence.contractTermActionOccurrenceToHapi()
    }
    if (requesterCount > 0) {
        hapiValue.requester = requesterList.map { it.toHapi() }
    }
    if (requesterLinkIdCount > 0) {
        hapiValue.requesterLinkId = requesterLinkIdList.map { it.toHapi() }
    }
    if (performerTypeCount > 0) {
        hapiValue.performerType = performerTypeList.map { it.toHapi() }
    }
    if (hasPerformerRole()) {
        hapiValue.performerRole = performerRole.toHapi()
    }
    if (hasPerformer()) {
        hapiValue.performer = performer.toHapi()
    }
    if (performerLinkIdCount > 0) {
        hapiValue.performerLinkId = performerLinkIdList.map { it.toHapi() }
    }
    if (reasonCodeCount > 0) {
        hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
        hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (reasonCount > 0) {
        hapiValue.reason = reasonList.map { it.toHapi() }
    }
    if (reasonLinkIdCount > 0) {
        hapiValue.reasonLinkId = reasonLinkIdList.map { it.toHapi() }
    }
    if (noteCount > 0) {
        hapiValue.note = noteList.map { it.toHapi() }
    }
    if (securityLabelNumberCount > 0) {
        hapiValue.securityLabelNumber = securityLabelNumberList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Term.Action.ActionSubject.toHapi():
    org.hl7.fhir.r4.model.Contract.ActionSubjectComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ActionSubjectComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (referenceCount > 0) {
        hapiValue.reference = referenceList.map { it.toHapi() }
    }
    if (hasRole()) {
        hapiValue.role = role.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.Signatory.toHapi(): org.hl7.fhir.r4.model.Contract.SignatoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.SignatoryComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasParty()) {
        hapiValue.party = party.toHapi()
    }
    if (signatureCount > 0) {
        hapiValue.signature = signatureList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.FriendlyLanguage.toHapi():
    org.hl7.fhir.r4.model.Contract.FriendlyLanguageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.FriendlyLanguageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasContent()) {
        hapiValue.content = content.contractFriendlyContentToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.LegalLanguage.toHapi():
    org.hl7.fhir.r4.model.Contract.LegalLanguageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.LegalLanguageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasContent()) {
        hapiValue.content = content.contractLegalContentToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Contract.ComputableLanguage.toHapi():
    org.hl7.fhir.r4.model.Contract.ComputableLanguageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Contract.ComputableLanguageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasContent()) {
        hapiValue.content = content.contractRuleContentToHapi()
    }
    return hapiValue
  }
}
