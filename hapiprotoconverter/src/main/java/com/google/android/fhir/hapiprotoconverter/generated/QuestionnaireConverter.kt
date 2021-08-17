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
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.EnableWhenBehaviorCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.Questionnaire.Item
import com.google.fhir.r4.core.Questionnaire.Item.AnswerOption
import com.google.fhir.r4.core.Questionnaire.Item.EnableWhen
import com.google.fhir.r4.core.Questionnaire.Item.Initial
import com.google.fhir.r4.core.QuestionnaireItemOperatorCode
import com.google.fhir.r4.core.QuestionnaireItemTypeCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

object QuestionnaireConverter {
  private fun Questionnaire.Item.EnableWhen.AnswerX.questionnaireItemEnableWhenAnswerToHapi():
    Type {
    if (hasBoolean()) {
      return (this.boolean).toHapi()
    }
    if (hasDecimal()) {
      return (this.decimal).toHapi()
    }
    if (hasInteger()) {
      return (this.integer).toHapi()
    }
    if (hasDate()) {
      return (this.date).toHapi()
    }
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasTime()) {
      return (this.time).toHapi()
    }
    if (hasStringValue()) {
      return (this.stringValue).toHapi()
    }
    if (hasCoding()) {
      return (this.coding).toHapi()
    }
    if (hasQuantity()) {
      return (this.quantity).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Questionnaire.item.enableWhen.answer[x]")
  }

  private fun Type.questionnaireItemEnableWhenAnswerToProto():
    Questionnaire.Item.EnableWhen.AnswerX {
    val protoValue = Questionnaire.Item.EnableWhen.AnswerX.newBuilder()
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
    if (this is Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun Questionnaire.Item.AnswerOption.ValueX.questionnaireItemAnswerOptionValueToHapi():
    Type {
    if (hasInteger()) {
      return (this.integer).toHapi()
    }
    if (hasDate()) {
      return (this.date).toHapi()
    }
    if (hasTime()) {
      return (this.time).toHapi()
    }
    if (hasStringValue()) {
      return (this.stringValue).toHapi()
    }
    if (hasCoding()) {
      return (this.coding).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Questionnaire.item.answerOption.value[x]")
  }

  private fun Type.questionnaireItemAnswerOptionValueToProto():
    Questionnaire.Item.AnswerOption.ValueX {
    val protoValue = Questionnaire.Item.AnswerOption.ValueX.newBuilder()
    if (this is IntegerType) {
      protoValue.integer = this.toProto()
    }
    if (this is DateType) {
      protoValue.date = this.toProto()
    }
    if (this is TimeType) {
      protoValue.time = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    if (this is Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun Questionnaire.Item.Initial.ValueX.questionnaireItemInitialValueToHapi(): Type {
    if (hasBoolean()) {
      return (this.boolean).toHapi()
    }
    if (hasDecimal()) {
      return (this.decimal).toHapi()
    }
    if (hasInteger()) {
      return (this.integer).toHapi()
    }
    if (hasDate()) {
      return (this.date).toHapi()
    }
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasTime()) {
      return (this.time).toHapi()
    }
    if (hasStringValue()) {
      return (this.stringValue).toHapi()
    }
    if (hasUri()) {
      return (this.uri).toHapi()
    }
    if (hasAttachment()) {
      return (this.attachment).toHapi()
    }
    if (hasCoding()) {
      return (this.coding).toHapi()
    }
    if (hasQuantity()) {
      return (this.quantity).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Questionnaire.item.initial.value[x]")
  }

  private fun Type.questionnaireItemInitialValueToProto(): Questionnaire.Item.Initial.ValueX {
    val protoValue = Questionnaire.Item.Initial.ValueX.newBuilder()
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
    if (this is Attachment) {
      protoValue.attachment = this.toProto()
    }
    if (this is Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  fun Questionnaire.toHapi(): org.hl7.fhir.r4.model.Questionnaire {
    val hapiValue = org.hl7.fhir.r4.model.Questionnaire()
    if (hasId()) {
      hapiValue.id = id.value
    }
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
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (derivedFromCount > 0) {
      hapiValue.derivedFrom = derivedFromList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    }
    if (hasExperimental()) {
      hapiValue.experimentalElement = experimental.toHapi()
    }
    if (subjectTypeCount > 0) {
      subjectTypeList.forEach { hapiValue.addSubjectType(it.value.name.hapiCodeCheck()) }
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasPublisher()) {
      hapiValue.publisherElement = publisher.toHapi()
    }
    if (contactCount > 0) {
      hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (useContextCount > 0) {
      hapiValue.useContext = useContextList.map { it.toHapi() }
    }
    if (jurisdictionCount > 0) {
      hapiValue.jurisdiction = jurisdictionList.map { it.toHapi() }
    }
    if (hasPurpose()) {
      hapiValue.purposeElement = purpose.toHapi()
    }
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    if (hasApprovalDate()) {
      hapiValue.approvalDateElement = approvalDate.toHapi()
    }
    if (hasLastReviewDate()) {
      hapiValue.lastReviewDateElement = lastReviewDate.toHapi()
    }
    if (hasEffectivePeriod()) {
      hapiValue.effectivePeriod = effectivePeriod.toHapi()
    }
    if (codeCount > 0) {
      hapiValue.code = codeList.map { it.toHapi() }
    }
    if (itemCount > 0) {
      hapiValue.item = itemList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Questionnaire.toProto(): Questionnaire {
    val protoValue = Questionnaire.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
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
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasDerivedFrom()) {
      protoValue.addAllDerivedFrom(derivedFrom.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        Questionnaire.StatusCode.newBuilder()
          .setValue(
            PublicationStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasExperimental()) {
      protoValue.experimental = experimentalElement.toProto()
    }
    if (hasSubjectType()) {
      protoValue.addAllSubjectType(
        subjectType.map {
          Questionnaire.SubjectTypeCode.newBuilder()
            .setValue(ResourceTypeCode.Value.valueOf(it.valueAsString.protoCodeCheck()))
            .build()
        }
      )
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasPublisher()) {
      protoValue.publisher = publisherElement.toProto()
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasUseContext()) {
      protoValue.addAllUseContext(useContext.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasPurpose()) {
      protoValue.purpose = purposeElement.toProto()
    }
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    if (hasApprovalDate()) {
      protoValue.approvalDate = approvalDateElement.toProto()
    }
    if (hasLastReviewDate()) {
      protoValue.lastReviewDate = lastReviewDateElement.toProto()
    }
    if (hasEffectivePeriod()) {
      protoValue.effectivePeriod = effectivePeriod.toProto()
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasItem()) {
      protoValue.addAllItem(item.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent.toProto():
    Questionnaire.Item {
    val protoValue = Questionnaire.Item.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasLinkId()) {
      protoValue.linkId = linkIdElement.toProto()
    }
    if (hasDefinition()) {
      protoValue.definition = definitionElement.toProto()
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasPrefix()) {
      protoValue.prefix = prefixElement.toProto()
    }
    if (hasText()) {
      protoValue.text = textElement.toProto()
    }
    if (hasType()) {
      protoValue.type =
        Questionnaire.Item.TypeCode.newBuilder()
          .setValue(
            QuestionnaireItemTypeCode.Value.valueOf(
              type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasEnableWhen()) {
      protoValue.addAllEnableWhen(enableWhen.map { it.toProto() })
    }
    if (hasEnableBehavior()) {
      protoValue.enableBehavior =
        Questionnaire.Item.EnableBehaviorCode.newBuilder()
          .setValue(
            EnableWhenBehaviorCode.Value.valueOf(
              enableBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasRequired()) {
      protoValue.required = requiredElement.toProto()
    }
    if (hasRepeats()) {
      protoValue.repeats = repeatsElement.toProto()
    }
    if (hasReadOnly()) {
      protoValue.readOnly = readOnlyElement.toProto()
    }
    if (hasMaxLength()) {
      protoValue.maxLength = maxLengthElement.toProto()
    }
    if (hasAnswerValueSet()) {
      protoValue.answerValueSet = answerValueSetElement.toProto()
    }
    if (hasAnswerOption()) {
      protoValue.addAllAnswerOption(answerOption.map { it.toProto() })
    }
    if (hasInitial()) {
      protoValue.addAllInitial(initial.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemEnableWhenComponent.toProto():
    Questionnaire.Item.EnableWhen {
    val protoValue = Questionnaire.Item.EnableWhen.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasQuestion()) {
      protoValue.question = questionElement.toProto()
    }
    if (hasOperator()) {
      protoValue.operator =
        Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
          .setValue(
            QuestionnaireItemOperatorCode.Value.valueOf(
              operator.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasAnswer()) {
      protoValue.answer = answer.questionnaireItemEnableWhenAnswerToProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemAnswerOptionComponent.toProto():
    Questionnaire.Item.AnswerOption {
    val protoValue = Questionnaire.Item.AnswerOption.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasValue()) {
      protoValue.value = value.questionnaireItemAnswerOptionValueToProto()
    }
    if (hasInitialSelected()) {
      protoValue.initialSelected = initialSelectedElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemInitialComponent.toProto():
    Questionnaire.Item.Initial {
    val protoValue = Questionnaire.Item.Initial.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasValue()) {
      protoValue.value = value.questionnaireItemInitialValueToProto()
    }
    return protoValue.build()
  }

  private fun Questionnaire.Item.toHapi():
    org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasLinkId()) {
      hapiValue.linkIdElement = linkId.toHapi()
    }
    if (hasDefinition()) {
      hapiValue.definitionElement = definition.toHapi()
    }
    if (codeCount > 0) {
      hapiValue.code = codeList.map { it.toHapi() }
    }
    if (hasPrefix()) {
      hapiValue.prefixElement = prefix.toHapi()
    }
    if (hasText()) {
      hapiValue.textElement = text.toHapi()
    }
    if (hasType()) {
      hapiValue.type =
        org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (enableWhenCount > 0) {
      hapiValue.enableWhen = enableWhenList.map { it.toHapi() }
    }
    if (hasEnableBehavior()) {
      hapiValue.enableBehavior =
        org.hl7.fhir.r4.model.Questionnaire.EnableWhenBehavior.valueOf(
          enableBehavior.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasRequired()) {
      hapiValue.requiredElement = required.toHapi()
    }
    if (hasRepeats()) {
      hapiValue.repeatsElement = repeats.toHapi()
    }
    if (hasReadOnly()) {
      hapiValue.readOnlyElement = readOnly.toHapi()
    }
    if (hasMaxLength()) {
      hapiValue.maxLengthElement = maxLength.toHapi()
    }
    if (hasAnswerValueSet()) {
      hapiValue.answerValueSetElement = answerValueSet.toHapi()
    }
    if (answerOptionCount > 0) {
      hapiValue.answerOption = answerOptionList.map { it.toHapi() }
    }
    if (initialCount > 0) {
      hapiValue.initial = initialList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun Questionnaire.Item.EnableWhen.toHapi():
    org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemEnableWhenComponent {
    val hapiValue = org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemEnableWhenComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasQuestion()) {
      hapiValue.questionElement = question.toHapi()
    }
    if (hasOperator()) {
      hapiValue.operator =
        org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemOperator.valueOf(
          operator.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasAnswer()) {
      hapiValue.answer = answer.questionnaireItemEnableWhenAnswerToHapi()
    }
    return hapiValue
  }

  private fun Questionnaire.Item.AnswerOption.toHapi():
    org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemAnswerOptionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemAnswerOptionComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasValue()) {
      hapiValue.value = value.questionnaireItemAnswerOptionValueToHapi()
    }
    if (hasInitialSelected()) {
      hapiValue.initialSelectedElement = initialSelected.toHapi()
    }
    return hapiValue
  }

  private fun Questionnaire.Item.Initial.toHapi():
    org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemInitialComponent {
    val hapiValue = org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemInitialComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasValue()) {
      hapiValue.value = value.questionnaireItemInitialValueToHapi()
    }
    return hapiValue
  }
}
