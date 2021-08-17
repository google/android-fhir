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
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.QuestionnaireResponse
import com.google.fhir.r4.core.QuestionnaireResponse.Item
import com.google.fhir.r4.core.QuestionnaireResponse.Item.Answer
import com.google.fhir.r4.core.QuestionnaireResponseStatusCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

object QuestionnaireResponseConverter {
  private fun QuestionnaireResponse.Item.Answer.ValueX.questionnaireResponseItemAnswerValueToHapi():
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
    throw IllegalArgumentException("Invalid Type for QuestionnaireResponse.item.answer.value[x]")
  }

  private fun Type.questionnaireResponseItemAnswerValueToProto():
    QuestionnaireResponse.Item.Answer.ValueX {
    val protoValue = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
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

  fun QuestionnaireResponse.toHapi(): org.hl7.fhir.r4.model.QuestionnaireResponse {
    val hapiValue = org.hl7.fhir.r4.model.QuestionnaireResponse()
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
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (partOfCount > 0) {
      hapiValue.partOf = partOfList.map { it.toHapi() }
    }
    if (hasQuestionnaire()) {
      hapiValue.questionnaireElement = questionnaire.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasAuthored()) {
      hapiValue.authoredElement = authored.toHapi()
    }
    if (hasAuthor()) {
      hapiValue.author = author.toHapi()
    }
    if (hasSource()) {
      hapiValue.source = source.toHapi()
    }
    if (itemCount > 0) {
      hapiValue.item = itemList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.QuestionnaireResponse.toProto(): QuestionnaireResponse {
    val protoValue = QuestionnaireResponse.newBuilder()
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
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
    if (hasQuestionnaire()) {
      protoValue.questionnaire = questionnaireElement.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        QuestionnaireResponse.StatusCode.newBuilder()
          .setValue(
            QuestionnaireResponseStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasAuthored()) {
      protoValue.authored = authoredElement.toProto()
    }
    if (hasAuthor()) {
      protoValue.author = author.toProto()
    }
    if (hasSource()) {
      protoValue.source = source.toProto()
    }
    if (hasItem()) {
      protoValue.addAllItem(item.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent.toProto():
    QuestionnaireResponse.Item {
    val protoValue = QuestionnaireResponse.Item.newBuilder()
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
    if (hasText()) {
      protoValue.text = textElement.toProto()
    }
    if (hasAnswer()) {
      protoValue.addAllAnswer(answer.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent.toProto():
    QuestionnaireResponse.Item.Answer {
    val protoValue = QuestionnaireResponse.Item.Answer.newBuilder()
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
      protoValue.value = value.questionnaireResponseItemAnswerValueToProto()
    }
    return protoValue.build()
  }

  private fun QuestionnaireResponse.Item.toHapi():
    org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
    if (hasText()) {
      hapiValue.textElement = text.toHapi()
    }
    if (answerCount > 0) {
      hapiValue.answer = answerList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun QuestionnaireResponse.Item.Answer.toHapi():
    org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
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
      hapiValue.value = value.questionnaireResponseItemAnswerValueToHapi()
    }
    return hapiValue
  }
}
