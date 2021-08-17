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

import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContributorConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContributorConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CountConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CountConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DataRequirementConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DataRequirementConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DistanceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DistanceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExpressionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExpressionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.HumanNameConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.HumanNameConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.OidConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.OidConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ParameterDefinitionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ParameterDefinitionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RelatedArtifactConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RelatedArtifactConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SampledDataConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SampledDataConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SignatureConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SignatureConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TriggerDefinitionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TriggerDefinitionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UuidConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UuidConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Parameters
import com.google.fhir.r4.core.Parameters.Parameter
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Age
import org.hl7.fhir.r4.model.Annotation
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Base64BinaryType
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ContactDetail
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.Contributor
import org.hl7.fhir.r4.model.Count
import org.hl7.fhir.r4.model.DataRequirement
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Distance
import org.hl7.fhir.r4.model.Dosage
import org.hl7.fhir.r4.model.Duration
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.MarkdownType
import org.hl7.fhir.r4.model.Money
import org.hl7.fhir.r4.model.OidType
import org.hl7.fhir.r4.model.ParameterDefinition
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Range
import org.hl7.fhir.r4.model.Ratio
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RelatedArtifact
import org.hl7.fhir.r4.model.SampledData
import org.hl7.fhir.r4.model.Signature
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.TriggerDefinition
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UnsignedIntType
import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.r4.model.UrlType
import org.hl7.fhir.r4.model.UsageContext
import org.hl7.fhir.r4.model.UuidType

object ParametersConverter {
  private fun Parameters.Parameter.ValueX.parametersParameterValueToHapi(): Type {
    if (hasBase64Binary()) {
      return (this.base64Binary).toHapi()
    }
    if (hasBoolean()) {
      return (this.boolean).toHapi()
    }
    if (hasCanonical()) {
      return (this.canonical).toHapi()
    }
    if (hasCode()) {
      return (this.code).toHapi()
    }
    if (hasDate()) {
      return (this.date).toHapi()
    }
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasDecimal()) {
      return (this.decimal).toHapi()
    }
    if (hasId()) {
      return (this.id).toHapi()
    }
    if (hasInstant()) {
      return (this.instant).toHapi()
    }
    if (hasInteger()) {
      return (this.integer).toHapi()
    }
    if (hasMarkdown()) {
      return (this.markdown).toHapi()
    }
    if (hasOid()) {
      return (this.oid).toHapi()
    }
    if (hasPositiveInt()) {
      return (this.positiveInt).toHapi()
    }
    if (hasStringValue()) {
      return (this.stringValue).toHapi()
    }
    if (hasTime()) {
      return (this.time).toHapi()
    }
    if (hasUnsignedInt()) {
      return (this.unsignedInt).toHapi()
    }
    if (hasUri()) {
      return (this.uri).toHapi()
    }
    if (hasUrl()) {
      return (this.url).toHapi()
    }
    if (hasUuid()) {
      return (this.uuid).toHapi()
    }
    if (hasAddress()) {
      return (this.address).toHapi()
    }
    if (hasAge()) {
      return (this.age).toHapi()
    }
    if (hasAnnotation()) {
      return (this.annotation).toHapi()
    }
    if (hasAttachment()) {
      return (this.attachment).toHapi()
    }
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasCoding()) {
      return (this.coding).toHapi()
    }
    if (hasContactPoint()) {
      return (this.contactPoint).toHapi()
    }
    if (hasCount()) {
      return (this.count).toHapi()
    }
    if (hasDistance()) {
      return (this.distance).toHapi()
    }
    if (hasDuration()) {
      return (this.duration).toHapi()
    }
    if (hasHumanName()) {
      return (this.humanName).toHapi()
    }
    if (hasIdentifier()) {
      return (this.identifier).toHapi()
    }
    if (hasMoney()) {
      return (this.money).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    if (hasQuantity()) {
      return (this.quantity).toHapi()
    }
    if (hasRange()) {
      return (this.range).toHapi()
    }
    if (hasRatio()) {
      return (this.ratio).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    if (hasSampledData()) {
      return (this.sampledData).toHapi()
    }
    if (hasSignature()) {
      return (this.signature).toHapi()
    }
    if (hasTiming()) {
      return (this.timing).toHapi()
    }
    if (hasContactDetail()) {
      return (this.contactDetail).toHapi()
    }
    if (hasContributor()) {
      return (this.contributor).toHapi()
    }
    if (hasDataRequirement()) {
      return (this.dataRequirement).toHapi()
    }
    if (hasExpression()) {
      return (this.expression).toHapi()
    }
    if (hasParameterDefinition()) {
      return (this.parameterDefinition).toHapi()
    }
    if (hasRelatedArtifact()) {
      return (this.relatedArtifact).toHapi()
    }
    if (hasTriggerDefinition()) {
      return (this.triggerDefinition).toHapi()
    }
    if (hasUsageContext()) {
      return (this.usageContext).toHapi()
    }
    if (hasDosage()) {
      return (this.dosage).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Parameters.parameter.value[x]")
  }

  private fun Type.parametersParameterValueToProto(): Parameters.Parameter.ValueX {
    val protoValue = Parameters.Parameter.ValueX.newBuilder()
    if (this is Base64BinaryType) {
      protoValue.base64Binary = this.toProto()
    }
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    if (this is CanonicalType) {
      protoValue.canonical = this.toProto()
    }
    if (this is CodeType) {
      protoValue.code = this.toProto()
    }
    if (this is DateType) {
      protoValue.date = this.toProto()
    }
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is DecimalType) {
      protoValue.decimal = this.toProto()
    }
    if (this is IdType) {
      protoValue.id = this.toProto()
    }
    if (this is InstantType) {
      protoValue.instant = this.toProto()
    }
    if (this is IntegerType) {
      protoValue.integer = this.toProto()
    }
    if (this is MarkdownType) {
      protoValue.markdown = this.toProto()
    }
    if (this is OidType) {
      protoValue.oid = this.toProto()
    }
    if (this is PositiveIntType) {
      protoValue.positiveInt = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    if (this is TimeType) {
      protoValue.time = this.toProto()
    }
    if (this is UnsignedIntType) {
      protoValue.unsignedInt = this.toProto()
    }
    if (this is UriType) {
      protoValue.uri = this.toProto()
    }
    if (this is UrlType) {
      protoValue.url = this.toProto()
    }
    if (this is UuidType) {
      protoValue.uuid = this.toProto()
    }
    if (this is Address) {
      protoValue.address = this.toProto()
    }
    if (this is Age) {
      protoValue.age = this.toProto()
    }
    if (this is Annotation) {
      protoValue.annotation = this.toProto()
    }
    if (this is Attachment) {
      protoValue.attachment = this.toProto()
    }
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is ContactPoint) {
      protoValue.contactPoint = this.toProto()
    }
    if (this is Count) {
      protoValue.count = this.toProto()
    }
    if (this is Distance) {
      protoValue.distance = this.toProto()
    }
    if (this is Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is HumanName) {
      protoValue.humanName = this.toProto()
    }
    if (this is Identifier) {
      protoValue.identifier = this.toProto()
    }
    if (this is Money) {
      protoValue.money = this.toProto()
    }
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    if (this is Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is Range) {
      protoValue.range = this.toProto()
    }
    if (this is Ratio) {
      protoValue.ratio = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is SampledData) {
      protoValue.sampledData = this.toProto()
    }
    if (this is Signature) {
      protoValue.signature = this.toProto()
    }
    if (this is Timing) {
      protoValue.timing = this.toProto()
    }
    if (this is ContactDetail) {
      protoValue.contactDetail = this.toProto()
    }
    if (this is Contributor) {
      protoValue.contributor = this.toProto()
    }
    if (this is DataRequirement) {
      protoValue.dataRequirement = this.toProto()
    }
    if (this is Expression) {
      protoValue.expression = this.toProto()
    }
    if (this is ParameterDefinition) {
      protoValue.parameterDefinition = this.toProto()
    }
    if (this is RelatedArtifact) {
      protoValue.relatedArtifact = this.toProto()
    }
    if (this is TriggerDefinition) {
      protoValue.triggerDefinition = this.toProto()
    }
    if (this is UsageContext) {
      protoValue.usageContext = this.toProto()
    }
    if (this is Dosage) {
      protoValue.dosage = this.toProto()
    }
    return protoValue.build()
  }

  fun Parameters.toHapi(): org.hl7.fhir.r4.model.Parameters {
    val hapiValue = org.hl7.fhir.r4.model.Parameters()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (hasMeta()) {
      hapiValue.meta = meta.toHapi()
    }
    if (hasImplicitRules()) {
      hapiValue.implicitRulesElement = implicitRules.toHapi()
    }
    if (parameterCount > 0) {
      hapiValue.parameter = parameterList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Parameters.toProto(): Parameters {
    val protoValue = Parameters.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
    if (hasMeta()) {
      protoValue.meta = meta.toProto()
    }
    if (hasImplicitRules()) {
      protoValue.implicitRules = implicitRulesElement.toProto()
    }
    if (hasParameter()) {
      protoValue.addAllParameter(parameter.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent.toProto():
    Parameters.Parameter {
    val protoValue = Parameters.Parameter.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasValue()) {
      protoValue.value = value.parametersParameterValueToProto()
    }
    return protoValue.build()
  }

  private fun Parameters.Parameter.toHapi():
    org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent {
    val hapiValue = org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasValue()) {
      hapiValue.value = value.parametersParameterValueToHapi()
    }
    return hapiValue
  }
}
