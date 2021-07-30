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
import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Decimal
import com.google.fhir.r4.core.EnableWhenBehaviorCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.Questionnaire.Item
import com.google.fhir.r4.core.Questionnaire.Item.AnswerOption
import com.google.fhir.r4.core.Questionnaire.Item.EnableWhen
import com.google.fhir.r4.core.Questionnaire.Item.Initial
import com.google.fhir.r4.core.QuestionnaireItemOperatorCode
import com.google.fhir.r4.core.QuestionnaireItemTypeCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Time
import com.google.fhir.r4.core.Uri
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

public object QuestionnaireConverter {
  private fun Questionnaire.Item.EnableWhen.AnswerX.questionnaireItemEnableWhenAnswerToHapi():
      Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType ) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType ) {
      return (this.getInteger()).toHapi()
    }
    if (this.getDate() != Date.newBuilder().defaultInstanceForType ) {
      return (this.getDate()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType ) {
      return (this.getTime()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getCoding() != Coding.newBuilder().defaultInstanceForType ) {
      return (this.getCoding()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Questionnaire.item.enableWhen.answer[x]")
  }

  private fun Type.questionnaireItemEnableWhenAnswerToProto():
      Questionnaire.Item.EnableWhen.AnswerX {
    val protoValue = Questionnaire.Item.EnableWhen.AnswerX.newBuilder()
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

  private fun Questionnaire.Item.AnswerOption.ValueX.questionnaireItemAnswerOptionValueToHapi():
      Type {
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType ) {
      return (this.getInteger()).toHapi()
    }
    if (this.getDate() != Date.newBuilder().defaultInstanceForType ) {
      return (this.getDate()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType ) {
      return (this.getTime()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getCoding() != Coding.newBuilder().defaultInstanceForType ) {
      return (this.getCoding()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Questionnaire.item.answerOption.value[x]")
  }

  private fun Type.questionnaireItemAnswerOptionValueToProto():
      Questionnaire.Item.AnswerOption.ValueX {
    val protoValue = Questionnaire.Item.AnswerOption.ValueX.newBuilder()
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is TimeType) {
      protoValue.setTime(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.setCoding(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  private fun Questionnaire.Item.Initial.ValueX.questionnaireItemInitialValueToHapi(): Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType ) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType ) {
      return (this.getInteger()).toHapi()
    }
    if (this.getDate() != Date.newBuilder().defaultInstanceForType ) {
      return (this.getDate()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType ) {
      return (this.getTime()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType ) {
      return (this.getUri()).toHapi()
    }
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType ) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getCoding() != Coding.newBuilder().defaultInstanceForType ) {
      return (this.getCoding()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Questionnaire.item.initial.value[x]")
  }

  private fun Type.questionnaireItemInitialValueToProto(): Questionnaire.Item.Initial.ValueX {
    val protoValue = Questionnaire.Item.Initial.ValueX.newBuilder()
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

  public fun Questionnaire.toHapi(): org.hl7.fhir.r4.model.Questionnaire {
    val hapiValue = org.hl7.fhir.r4.model.Questionnaire()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setDerivedFrom(derivedFromList.map{it.toHapi()})
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map{it.toHapi()})
    hapiValue.setJurisdiction(jurisdictionList.map{it.toHapi()})
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setApprovalDateElement(approvalDate.toHapi())
    hapiValue.setLastReviewDateElement(lastReviewDate.toHapi())
    hapiValue.setEffectivePeriod(effectivePeriod.toHapi())
    hapiValue.setCode(codeList.map{it.toHapi()})
    hapiValue.setItem(itemList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Questionnaire.toProto(): Questionnaire {
    val protoValue = Questionnaire.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setUrl(urlElement.toProto())
    .addAllIdentifier(identifier.map{it.toProto()})
    .setVersion(versionElement.toProto())
    .setName(nameElement.toProto())
    .setTitle(titleElement.toProto())
    .addAllDerivedFrom(derivedFrom.map{it.toProto()})
    .setStatus(Questionnaire.StatusCode.newBuilder().setValue(PublicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setExperimental(experimentalElement.toProto())
    .setDate(dateElement.toProto())
    .setPublisher(publisherElement.toProto())
    .addAllContact(contact.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .addAllUseContext(useContext.map{it.toProto()})
    .addAllJurisdiction(jurisdiction.map{it.toProto()})
    .setPurpose(purposeElement.toProto())
    .setCopyright(copyrightElement.toProto())
    .setApprovalDate(approvalDateElement.toProto())
    .setLastReviewDate(lastReviewDateElement.toProto())
    .setEffectivePeriod(effectivePeriod.toProto())
    .addAllCode(code.map{it.toProto()})
    .addAllItem(item.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent.toProto():
      Questionnaire.Item {
    val protoValue = Questionnaire.Item.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setLinkId(linkIdElement.toProto())
    .setDefinition(definitionElement.toProto())
    .addAllCode(code.map{it.toProto()})
    .setPrefix(prefixElement.toProto())
    .setText(textElement.toProto())
    .setType(Questionnaire.Item.TypeCode.newBuilder().setValue(QuestionnaireItemTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllEnableWhen(enableWhen.map{it.toProto()})
    .setEnableBehavior(Questionnaire.Item.EnableBehaviorCode.newBuilder().setValue(EnableWhenBehaviorCode.Value.valueOf(enableBehavior.toCode().replace("-",
        "_").toUpperCase())).build())
    .setRequired(requiredElement.toProto())
    .setRepeats(repeatsElement.toProto())
    .setReadOnly(readOnlyElement.toProto())
    .setMaxLength(maxLengthElement.toProto())
    .setAnswerValueSet(answerValueSetElement.toProto())
    .addAllAnswerOption(answerOption.map{it.toProto()})
    .addAllInitial(initial.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemEnableWhenComponent.toProto():
      Questionnaire.Item.EnableWhen {
    val protoValue = Questionnaire.Item.EnableWhen.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setQuestion(questionElement.toProto())
    .setOperator(Questionnaire.Item.EnableWhen.OperatorCode.newBuilder().setValue(QuestionnaireItemOperatorCode.Value.valueOf(operator.toCode().replace("-",
        "_").toUpperCase())).build())
    .setAnswer(answer.questionnaireItemEnableWhenAnswerToProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemAnswerOptionComponent.toProto():
      Questionnaire.Item.AnswerOption {
    val protoValue = Questionnaire.Item.AnswerOption.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setValue(value.questionnaireItemAnswerOptionValueToProto())
    .setInitialSelected(initialSelectedElement.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemInitialComponent.toProto():
      Questionnaire.Item.Initial {
    val protoValue = Questionnaire.Item.Initial.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setValue(value.questionnaireItemInitialValueToProto())
    .build()
    return protoValue
  }

  private fun Questionnaire.Item.toHapi():
      org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setLinkIdElement(linkId.toHapi())
    hapiValue.setDefinitionElement(definition.toHapi())
    hapiValue.setCode(codeList.map{it.toHapi()})
    hapiValue.setPrefixElement(prefix.toHapi())
    hapiValue.setTextElement(text.toHapi())
    hapiValue.setType(org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemType.valueOf(type.value.name.replace("_","")))
    hapiValue.setEnableWhen(enableWhenList.map{it.toHapi()})
    hapiValue.setEnableBehavior(org.hl7.fhir.r4.model.Questionnaire.EnableWhenBehavior.valueOf(enableBehavior.value.name.replace("_","")))
    hapiValue.setRequiredElement(required.toHapi())
    hapiValue.setRepeatsElement(repeats.toHapi())
    hapiValue.setReadOnlyElement(readOnly.toHapi())
    hapiValue.setMaxLengthElement(maxLength.toHapi())
    hapiValue.setAnswerValueSetElement(answerValueSet.toHapi())
    hapiValue.setAnswerOption(answerOptionList.map{it.toHapi()})
    hapiValue.setInitial(initialList.map{it.toHapi()})
    return hapiValue
  }

  private fun Questionnaire.Item.EnableWhen.toHapi():
      org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemEnableWhenComponent {
    val hapiValue = org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemEnableWhenComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setQuestionElement(question.toHapi())
    hapiValue.setOperator(org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemOperator.valueOf(operator.value.name.replace("_","")))
    hapiValue.setAnswer(answer.questionnaireItemEnableWhenAnswerToHapi())
    return hapiValue
  }

  private fun Questionnaire.Item.AnswerOption.toHapi():
      org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemAnswerOptionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemAnswerOptionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setValue(value.questionnaireItemAnswerOptionValueToHapi())
    hapiValue.setInitialSelectedElement(initialSelected.toHapi())
    return hapiValue
  }

  private fun Questionnaire.Item.Initial.toHapi():
      org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemInitialComponent {
    val hapiValue = org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemInitialComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setValue(value.questionnaireItemInitialValueToHapi())
    return hapiValue
  }
}
