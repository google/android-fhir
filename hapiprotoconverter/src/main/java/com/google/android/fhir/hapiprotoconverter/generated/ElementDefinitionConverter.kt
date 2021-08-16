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
import com.google.fhir.r4.core.Address
import com.google.fhir.r4.core.Age
import com.google.fhir.r4.core.AggregationModeCode
import com.google.fhir.r4.core.Annotation
import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.Base64Binary
import com.google.fhir.r4.core.BindingStrengthCode
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Canonical
import com.google.fhir.r4.core.Code
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.ConstraintSeverityCode
import com.google.fhir.r4.core.ContactDetail
import com.google.fhir.r4.core.ContactPoint
import com.google.fhir.r4.core.Contributor
import com.google.fhir.r4.core.Count
import com.google.fhir.r4.core.DataRequirement
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Decimal
import com.google.fhir.r4.core.DiscriminatorTypeCode
import com.google.fhir.r4.core.Distance
import com.google.fhir.r4.core.Dosage
import com.google.fhir.r4.core.Duration
import com.google.fhir.r4.core.ElementDefinition
import com.google.fhir.r4.core.ElementDefinition.Constraint
import com.google.fhir.r4.core.ElementDefinition.ElementDefinitionBinding
import com.google.fhir.r4.core.ElementDefinition.Example
import com.google.fhir.r4.core.ElementDefinition.Mapping
import com.google.fhir.r4.core.ElementDefinition.Slicing
import com.google.fhir.r4.core.ElementDefinition.Slicing.Discriminator
import com.google.fhir.r4.core.ElementDefinition.TypeRef
import com.google.fhir.r4.core.Expression
import com.google.fhir.r4.core.HumanName
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Identifier
import com.google.fhir.r4.core.Instant
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Markdown
import com.google.fhir.r4.core.Money
import com.google.fhir.r4.core.Oid
import com.google.fhir.r4.core.ParameterDefinition
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.PositiveInt
import com.google.fhir.r4.core.PropertyRepresentationCode
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.ReferenceVersionRulesCode
import com.google.fhir.r4.core.RelatedArtifact
import com.google.fhir.r4.core.SampledData
import com.google.fhir.r4.core.Signature
import com.google.fhir.r4.core.SlicingRulesCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Time
import com.google.fhir.r4.core.Timing
import com.google.fhir.r4.core.TriggerDefinition
import com.google.fhir.r4.core.UnsignedInt
import com.google.fhir.r4.core.Uri
import com.google.fhir.r4.core.Url
import com.google.fhir.r4.core.UsageContext
import com.google.fhir.r4.core.Uuid
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Base64BinaryType
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.MarkdownType
import org.hl7.fhir.r4.model.OidType
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UnsignedIntType
import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.r4.model.UrlType
import org.hl7.fhir.r4.model.UuidType

object ElementDefinitionConverter {
  @JvmStatic
  private fun ElementDefinition.DefaultValueX.elementDefinitionDefaultValueToHapi(): Type {
    if (this.base64Binary != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.base64Binary).toHapi()
    }
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.canonical != Canonical.newBuilder().defaultInstanceForType) {
      return (this.canonical).toHapi()
    }
    if (this.code != Code.newBuilder().defaultInstanceForType) {
      return (this.code).toHapi()
    }
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.decimal != Decimal.newBuilder().defaultInstanceForType) {
      return (this.decimal).toHapi()
    }
    if (this.id != Id.newBuilder().defaultInstanceForType) {
      return (this.id).toHapi()
    }
    if (this.instant != Instant.newBuilder().defaultInstanceForType) {
      return (this.instant).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.markdown != Markdown.newBuilder().defaultInstanceForType) {
      return (this.markdown).toHapi()
    }
    if (this.oid != Oid.newBuilder().defaultInstanceForType) {
      return (this.oid).toHapi()
    }
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.time != Time.newBuilder().defaultInstanceForType) {
      return (this.time).toHapi()
    }
    if (this.unsignedInt != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.unsignedInt).toHapi()
    }
    if (this.uri != Uri.newBuilder().defaultInstanceForType) {
      return (this.uri).toHapi()
    }
    if (this.url != Url.newBuilder().defaultInstanceForType) {
      return (this.url).toHapi()
    }
    if (this.uuid != Uuid.newBuilder().defaultInstanceForType) {
      return (this.uuid).toHapi()
    }
    if (this.address != Address.newBuilder().defaultInstanceForType) {
      return (this.address).toHapi()
    }
    if (this.age != Age.newBuilder().defaultInstanceForType) {
      return (this.age).toHapi()
    }
    if (this.annotation != Annotation.newBuilder().defaultInstanceForType) {
      return (this.annotation).toHapi()
    }
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.coding != Coding.newBuilder().defaultInstanceForType) {
      return (this.coding).toHapi()
    }
    if (this.contactPoint != ContactPoint.newBuilder().defaultInstanceForType) {
      return (this.contactPoint).toHapi()
    }
    if (this.count != Count.newBuilder().defaultInstanceForType) {
      return (this.count).toHapi()
    }
    if (this.distance != Distance.newBuilder().defaultInstanceForType) {
      return (this.distance).toHapi()
    }
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    if (this.humanName != HumanName.newBuilder().defaultInstanceForType) {
      return (this.humanName).toHapi()
    }
    if (this.identifier != Identifier.newBuilder().defaultInstanceForType) {
      return (this.identifier).toHapi()
    }
    if (this.money != Money.newBuilder().defaultInstanceForType) {
      return (this.money).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.ratio != Ratio.newBuilder().defaultInstanceForType) {
      return (this.ratio).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.sampledData != SampledData.newBuilder().defaultInstanceForType) {
      return (this.sampledData).toHapi()
    }
    if (this.signature != Signature.newBuilder().defaultInstanceForType) {
      return (this.signature).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    if (this.contactDetail != ContactDetail.newBuilder().defaultInstanceForType) {
      return (this.contactDetail).toHapi()
    }
    if (this.contributor != Contributor.newBuilder().defaultInstanceForType) {
      return (this.contributor).toHapi()
    }
    if (this.dataRequirement != DataRequirement.newBuilder().defaultInstanceForType) {
      return (this.dataRequirement).toHapi()
    }
    if (this.expression != Expression.newBuilder().defaultInstanceForType) {
      return (this.expression).toHapi()
    }
    if (this.parameterDefinition != ParameterDefinition.newBuilder().defaultInstanceForType) {
      return (this.parameterDefinition).toHapi()
    }
    if (this.relatedArtifact != RelatedArtifact.newBuilder().defaultInstanceForType) {
      return (this.relatedArtifact).toHapi()
    }
    if (this.triggerDefinition != TriggerDefinition.newBuilder().defaultInstanceForType) {
      return (this.triggerDefinition).toHapi()
    }
    if (this.usageContext != UsageContext.newBuilder().defaultInstanceForType) {
      return (this.usageContext).toHapi()
    }
    if (this.dosage != Dosage.newBuilder().defaultInstanceForType) {
      return (this.dosage).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.defaultValue[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionDefaultValueToProto(): ElementDefinition.DefaultValueX {
    val protoValue = ElementDefinition.DefaultValueX.newBuilder()
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
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.address = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.age = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Annotation) {
      protoValue.annotation = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ContactPoint) {
      protoValue.contactPoint = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Count) {
      protoValue.count = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Distance) {
      protoValue.distance = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.HumanName) {
      protoValue.humanName = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Identifier) {
      protoValue.identifier = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.money = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.ratio = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
      protoValue.sampledData = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Signature) {
      protoValue.signature = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.timing = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ContactDetail) {
      protoValue.contactDetail = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Contributor) {
      protoValue.contributor = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.DataRequirement) {
      protoValue.dataRequirement = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Expression) {
      protoValue.expression = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ParameterDefinition) {
      protoValue.parameterDefinition = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.RelatedArtifact) {
      protoValue.relatedArtifact = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.TriggerDefinition) {
      protoValue.triggerDefinition = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.UsageContext) {
      protoValue.usageContext = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Dosage) {
      protoValue.dosage = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.FixedX.elementDefinitionFixedToHapi(): Type {
    if (this.base64Binary != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.base64Binary).toHapi()
    }
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.canonical != Canonical.newBuilder().defaultInstanceForType) {
      return (this.canonical).toHapi()
    }
    if (this.code != Code.newBuilder().defaultInstanceForType) {
      return (this.code).toHapi()
    }
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.decimal != Decimal.newBuilder().defaultInstanceForType) {
      return (this.decimal).toHapi()
    }
    if (this.id != Id.newBuilder().defaultInstanceForType) {
      return (this.id).toHapi()
    }
    if (this.instant != Instant.newBuilder().defaultInstanceForType) {
      return (this.instant).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.markdown != Markdown.newBuilder().defaultInstanceForType) {
      return (this.markdown).toHapi()
    }
    if (this.oid != Oid.newBuilder().defaultInstanceForType) {
      return (this.oid).toHapi()
    }
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.time != Time.newBuilder().defaultInstanceForType) {
      return (this.time).toHapi()
    }
    if (this.unsignedInt != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.unsignedInt).toHapi()
    }
    if (this.uri != Uri.newBuilder().defaultInstanceForType) {
      return (this.uri).toHapi()
    }
    if (this.url != Url.newBuilder().defaultInstanceForType) {
      return (this.url).toHapi()
    }
    if (this.uuid != Uuid.newBuilder().defaultInstanceForType) {
      return (this.uuid).toHapi()
    }
    if (this.address != Address.newBuilder().defaultInstanceForType) {
      return (this.address).toHapi()
    }
    if (this.age != Age.newBuilder().defaultInstanceForType) {
      return (this.age).toHapi()
    }
    if (this.annotation != Annotation.newBuilder().defaultInstanceForType) {
      return (this.annotation).toHapi()
    }
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.coding != Coding.newBuilder().defaultInstanceForType) {
      return (this.coding).toHapi()
    }
    if (this.contactPoint != ContactPoint.newBuilder().defaultInstanceForType) {
      return (this.contactPoint).toHapi()
    }
    if (this.count != Count.newBuilder().defaultInstanceForType) {
      return (this.count).toHapi()
    }
    if (this.distance != Distance.newBuilder().defaultInstanceForType) {
      return (this.distance).toHapi()
    }
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    if (this.humanName != HumanName.newBuilder().defaultInstanceForType) {
      return (this.humanName).toHapi()
    }
    if (this.identifier != Identifier.newBuilder().defaultInstanceForType) {
      return (this.identifier).toHapi()
    }
    if (this.money != Money.newBuilder().defaultInstanceForType) {
      return (this.money).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.ratio != Ratio.newBuilder().defaultInstanceForType) {
      return (this.ratio).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.sampledData != SampledData.newBuilder().defaultInstanceForType) {
      return (this.sampledData).toHapi()
    }
    if (this.signature != Signature.newBuilder().defaultInstanceForType) {
      return (this.signature).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    if (this.contactDetail != ContactDetail.newBuilder().defaultInstanceForType) {
      return (this.contactDetail).toHapi()
    }
    if (this.contributor != Contributor.newBuilder().defaultInstanceForType) {
      return (this.contributor).toHapi()
    }
    if (this.dataRequirement != DataRequirement.newBuilder().defaultInstanceForType) {
      return (this.dataRequirement).toHapi()
    }
    if (this.expression != Expression.newBuilder().defaultInstanceForType) {
      return (this.expression).toHapi()
    }
    if (this.parameterDefinition != ParameterDefinition.newBuilder().defaultInstanceForType) {
      return (this.parameterDefinition).toHapi()
    }
    if (this.relatedArtifact != RelatedArtifact.newBuilder().defaultInstanceForType) {
      return (this.relatedArtifact).toHapi()
    }
    if (this.triggerDefinition != TriggerDefinition.newBuilder().defaultInstanceForType) {
      return (this.triggerDefinition).toHapi()
    }
    if (this.usageContext != UsageContext.newBuilder().defaultInstanceForType) {
      return (this.usageContext).toHapi()
    }
    if (this.dosage != Dosage.newBuilder().defaultInstanceForType) {
      return (this.dosage).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.fixed[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionFixedToProto(): ElementDefinition.FixedX {
    val protoValue = ElementDefinition.FixedX.newBuilder()
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
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.address = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.age = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Annotation) {
      protoValue.annotation = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ContactPoint) {
      protoValue.contactPoint = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Count) {
      protoValue.count = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Distance) {
      protoValue.distance = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.HumanName) {
      protoValue.humanName = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Identifier) {
      protoValue.identifier = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.money = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.ratio = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
      protoValue.sampledData = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Signature) {
      protoValue.signature = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.timing = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ContactDetail) {
      protoValue.contactDetail = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Contributor) {
      protoValue.contributor = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.DataRequirement) {
      protoValue.dataRequirement = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Expression) {
      protoValue.expression = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ParameterDefinition) {
      protoValue.parameterDefinition = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.RelatedArtifact) {
      protoValue.relatedArtifact = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.TriggerDefinition) {
      protoValue.triggerDefinition = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.UsageContext) {
      protoValue.usageContext = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Dosage) {
      protoValue.dosage = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.PatternX.elementDefinitionPatternToHapi(): Type {
    if (this.base64Binary != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.base64Binary).toHapi()
    }
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.canonical != Canonical.newBuilder().defaultInstanceForType) {
      return (this.canonical).toHapi()
    }
    if (this.code != Code.newBuilder().defaultInstanceForType) {
      return (this.code).toHapi()
    }
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.decimal != Decimal.newBuilder().defaultInstanceForType) {
      return (this.decimal).toHapi()
    }
    if (this.id != Id.newBuilder().defaultInstanceForType) {
      return (this.id).toHapi()
    }
    if (this.instant != Instant.newBuilder().defaultInstanceForType) {
      return (this.instant).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.markdown != Markdown.newBuilder().defaultInstanceForType) {
      return (this.markdown).toHapi()
    }
    if (this.oid != Oid.newBuilder().defaultInstanceForType) {
      return (this.oid).toHapi()
    }
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.time != Time.newBuilder().defaultInstanceForType) {
      return (this.time).toHapi()
    }
    if (this.unsignedInt != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.unsignedInt).toHapi()
    }
    if (this.uri != Uri.newBuilder().defaultInstanceForType) {
      return (this.uri).toHapi()
    }
    if (this.url != Url.newBuilder().defaultInstanceForType) {
      return (this.url).toHapi()
    }
    if (this.uuid != Uuid.newBuilder().defaultInstanceForType) {
      return (this.uuid).toHapi()
    }
    if (this.address != Address.newBuilder().defaultInstanceForType) {
      return (this.address).toHapi()
    }
    if (this.age != Age.newBuilder().defaultInstanceForType) {
      return (this.age).toHapi()
    }
    if (this.annotation != Annotation.newBuilder().defaultInstanceForType) {
      return (this.annotation).toHapi()
    }
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.coding != Coding.newBuilder().defaultInstanceForType) {
      return (this.coding).toHapi()
    }
    if (this.contactPoint != ContactPoint.newBuilder().defaultInstanceForType) {
      return (this.contactPoint).toHapi()
    }
    if (this.count != Count.newBuilder().defaultInstanceForType) {
      return (this.count).toHapi()
    }
    if (this.distance != Distance.newBuilder().defaultInstanceForType) {
      return (this.distance).toHapi()
    }
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    if (this.humanName != HumanName.newBuilder().defaultInstanceForType) {
      return (this.humanName).toHapi()
    }
    if (this.identifier != Identifier.newBuilder().defaultInstanceForType) {
      return (this.identifier).toHapi()
    }
    if (this.money != Money.newBuilder().defaultInstanceForType) {
      return (this.money).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.ratio != Ratio.newBuilder().defaultInstanceForType) {
      return (this.ratio).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.sampledData != SampledData.newBuilder().defaultInstanceForType) {
      return (this.sampledData).toHapi()
    }
    if (this.signature != Signature.newBuilder().defaultInstanceForType) {
      return (this.signature).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    if (this.contactDetail != ContactDetail.newBuilder().defaultInstanceForType) {
      return (this.contactDetail).toHapi()
    }
    if (this.contributor != Contributor.newBuilder().defaultInstanceForType) {
      return (this.contributor).toHapi()
    }
    if (this.dataRequirement != DataRequirement.newBuilder().defaultInstanceForType) {
      return (this.dataRequirement).toHapi()
    }
    if (this.expression != Expression.newBuilder().defaultInstanceForType) {
      return (this.expression).toHapi()
    }
    if (this.parameterDefinition != ParameterDefinition.newBuilder().defaultInstanceForType) {
      return (this.parameterDefinition).toHapi()
    }
    if (this.relatedArtifact != RelatedArtifact.newBuilder().defaultInstanceForType) {
      return (this.relatedArtifact).toHapi()
    }
    if (this.triggerDefinition != TriggerDefinition.newBuilder().defaultInstanceForType) {
      return (this.triggerDefinition).toHapi()
    }
    if (this.usageContext != UsageContext.newBuilder().defaultInstanceForType) {
      return (this.usageContext).toHapi()
    }
    if (this.dosage != Dosage.newBuilder().defaultInstanceForType) {
      return (this.dosage).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.pattern[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionPatternToProto(): ElementDefinition.PatternX {
    val protoValue = ElementDefinition.PatternX.newBuilder()
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
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.address = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.age = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Annotation) {
      protoValue.annotation = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ContactPoint) {
      protoValue.contactPoint = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Count) {
      protoValue.count = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Distance) {
      protoValue.distance = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.HumanName) {
      protoValue.humanName = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Identifier) {
      protoValue.identifier = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.money = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.ratio = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
      protoValue.sampledData = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Signature) {
      protoValue.signature = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.timing = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ContactDetail) {
      protoValue.contactDetail = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Contributor) {
      protoValue.contributor = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.DataRequirement) {
      protoValue.dataRequirement = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Expression) {
      protoValue.expression = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ParameterDefinition) {
      protoValue.parameterDefinition = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.RelatedArtifact) {
      protoValue.relatedArtifact = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.TriggerDefinition) {
      protoValue.triggerDefinition = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.UsageContext) {
      protoValue.usageContext = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Dosage) {
      protoValue.dosage = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.Example.ValueX.elementDefinitionExampleValueToHapi(): Type {
    if (this.base64Binary != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.base64Binary).toHapi()
    }
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.canonical != Canonical.newBuilder().defaultInstanceForType) {
      return (this.canonical).toHapi()
    }
    if (this.code != Code.newBuilder().defaultInstanceForType) {
      return (this.code).toHapi()
    }
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.decimal != Decimal.newBuilder().defaultInstanceForType) {
      return (this.decimal).toHapi()
    }
    if (this.id != Id.newBuilder().defaultInstanceForType) {
      return (this.id).toHapi()
    }
    if (this.instant != Instant.newBuilder().defaultInstanceForType) {
      return (this.instant).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.markdown != Markdown.newBuilder().defaultInstanceForType) {
      return (this.markdown).toHapi()
    }
    if (this.oid != Oid.newBuilder().defaultInstanceForType) {
      return (this.oid).toHapi()
    }
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.time != Time.newBuilder().defaultInstanceForType) {
      return (this.time).toHapi()
    }
    if (this.unsignedInt != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.unsignedInt).toHapi()
    }
    if (this.uri != Uri.newBuilder().defaultInstanceForType) {
      return (this.uri).toHapi()
    }
    if (this.url != Url.newBuilder().defaultInstanceForType) {
      return (this.url).toHapi()
    }
    if (this.uuid != Uuid.newBuilder().defaultInstanceForType) {
      return (this.uuid).toHapi()
    }
    if (this.address != Address.newBuilder().defaultInstanceForType) {
      return (this.address).toHapi()
    }
    if (this.age != Age.newBuilder().defaultInstanceForType) {
      return (this.age).toHapi()
    }
    if (this.annotation != Annotation.newBuilder().defaultInstanceForType) {
      return (this.annotation).toHapi()
    }
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.coding != Coding.newBuilder().defaultInstanceForType) {
      return (this.coding).toHapi()
    }
    if (this.contactPoint != ContactPoint.newBuilder().defaultInstanceForType) {
      return (this.contactPoint).toHapi()
    }
    if (this.count != Count.newBuilder().defaultInstanceForType) {
      return (this.count).toHapi()
    }
    if (this.distance != Distance.newBuilder().defaultInstanceForType) {
      return (this.distance).toHapi()
    }
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    if (this.humanName != HumanName.newBuilder().defaultInstanceForType) {
      return (this.humanName).toHapi()
    }
    if (this.identifier != Identifier.newBuilder().defaultInstanceForType) {
      return (this.identifier).toHapi()
    }
    if (this.money != Money.newBuilder().defaultInstanceForType) {
      return (this.money).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.ratio != Ratio.newBuilder().defaultInstanceForType) {
      return (this.ratio).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.sampledData != SampledData.newBuilder().defaultInstanceForType) {
      return (this.sampledData).toHapi()
    }
    if (this.signature != Signature.newBuilder().defaultInstanceForType) {
      return (this.signature).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    if (this.contactDetail != ContactDetail.newBuilder().defaultInstanceForType) {
      return (this.contactDetail).toHapi()
    }
    if (this.contributor != Contributor.newBuilder().defaultInstanceForType) {
      return (this.contributor).toHapi()
    }
    if (this.dataRequirement != DataRequirement.newBuilder().defaultInstanceForType) {
      return (this.dataRequirement).toHapi()
    }
    if (this.expression != Expression.newBuilder().defaultInstanceForType) {
      return (this.expression).toHapi()
    }
    if (this.parameterDefinition != ParameterDefinition.newBuilder().defaultInstanceForType) {
      return (this.parameterDefinition).toHapi()
    }
    if (this.relatedArtifact != RelatedArtifact.newBuilder().defaultInstanceForType) {
      return (this.relatedArtifact).toHapi()
    }
    if (this.triggerDefinition != TriggerDefinition.newBuilder().defaultInstanceForType) {
      return (this.triggerDefinition).toHapi()
    }
    if (this.usageContext != UsageContext.newBuilder().defaultInstanceForType) {
      return (this.usageContext).toHapi()
    }
    if (this.dosage != Dosage.newBuilder().defaultInstanceForType) {
      return (this.dosage).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.example.value[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionExampleValueToProto(): ElementDefinition.Example.ValueX {
    val protoValue = ElementDefinition.Example.ValueX.newBuilder()
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
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.address = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.age = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Annotation) {
      protoValue.annotation = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ContactPoint) {
      protoValue.contactPoint = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Count) {
      protoValue.count = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Distance) {
      protoValue.distance = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.HumanName) {
      protoValue.humanName = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Identifier) {
      protoValue.identifier = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.money = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.ratio = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
      protoValue.sampledData = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Signature) {
      protoValue.signature = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.timing = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ContactDetail) {
      protoValue.contactDetail = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Contributor) {
      protoValue.contributor = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.DataRequirement) {
      protoValue.dataRequirement = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Expression) {
      protoValue.expression = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.ParameterDefinition) {
      protoValue.parameterDefinition = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.RelatedArtifact) {
      protoValue.relatedArtifact = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.TriggerDefinition) {
      protoValue.triggerDefinition = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.UsageContext) {
      protoValue.usageContext = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Dosage) {
      protoValue.dosage = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.MinValueX.elementDefinitionMinValueToHapi(): Type {
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.instant != Instant.newBuilder().defaultInstanceForType) {
      return (this.instant).toHapi()
    }
    if (this.time != Time.newBuilder().defaultInstanceForType) {
      return (this.time).toHapi()
    }
    if (this.decimal != Decimal.newBuilder().defaultInstanceForType) {
      return (this.decimal).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.unsignedInt != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.unsignedInt).toHapi()
    }
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.minValue[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionMinValueToProto(): ElementDefinition.MinValueX {
    val protoValue = ElementDefinition.MinValueX.newBuilder()
    if (this is DateType) {
      protoValue.date = this.toProto()
    }
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is InstantType) {
      protoValue.instant = this.toProto()
    }
    if (this is TimeType) {
      protoValue.time = this.toProto()
    }
    if (this is DecimalType) {
      protoValue.decimal = this.toProto()
    }
    if (this is IntegerType) {
      protoValue.integer = this.toProto()
    }
    if (this is PositiveIntType) {
      protoValue.positiveInt = this.toProto()
    }
    if (this is UnsignedIntType) {
      protoValue.unsignedInt = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.MaxValueX.elementDefinitionMaxValueToHapi(): Type {
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.instant != Instant.newBuilder().defaultInstanceForType) {
      return (this.instant).toHapi()
    }
    if (this.time != Time.newBuilder().defaultInstanceForType) {
      return (this.time).toHapi()
    }
    if (this.decimal != Decimal.newBuilder().defaultInstanceForType) {
      return (this.decimal).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.unsignedInt != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.unsignedInt).toHapi()
    }
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.maxValue[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionMaxValueToProto(): ElementDefinition.MaxValueX {
    val protoValue = ElementDefinition.MaxValueX.newBuilder()
    if (this is DateType) {
      protoValue.date = this.toProto()
    }
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is InstantType) {
      protoValue.instant = this.toProto()
    }
    if (this is TimeType) {
      protoValue.time = this.toProto()
    }
    if (this is DecimalType) {
      protoValue.decimal = this.toProto()
    }
    if (this is IntegerType) {
      protoValue.integer = this.toProto()
    }
    if (this is PositiveIntType) {
      protoValue.positiveInt = this.toProto()
    }
    if (this is UnsignedIntType) {
      protoValue.unsignedInt = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun ElementDefinition.toHapi(): org.hl7.fhir.r4.model.ElementDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasPath()) {
      hapiValue.pathElement = path.toHapi()
    }
    representationList.forEach {
      hapiValue.addRepresentation(
        org.hl7.fhir.r4.model.ElementDefinition.PropertyRepresentation.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasSliceName()) {
      hapiValue.sliceNameElement = sliceName.toHapi()
    }
    if (hasSliceIsConstraining()) {
      hapiValue.sliceIsConstrainingElement = sliceIsConstraining.toHapi()
    }
    if (hasLabel()) {
      hapiValue.labelElement = label.toHapi()
    }
    if (codeCount > 0) {
      hapiValue.code = codeList.map { it.toHapi() }
    }
    if (hasSlicing()) {
      hapiValue.slicing = slicing.toHapi()
    }
    if (hasShort()) {
      hapiValue.shortElement = short.toHapi()
    }
    if (hasDefinition()) {
      hapiValue.definitionElement = definition.toHapi()
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    if (hasRequirements()) {
      hapiValue.requirementsElement = requirements.toHapi()
    }
    if (aliasCount > 0) {
      hapiValue.alias = aliasList.map { it.toHapi() }
    }
    if (hasMin()) {
      hapiValue.minElement = min.toHapi()
    }
    if (hasMax()) {
      hapiValue.maxElement = max.toHapi()
    }
    if (hasBase()) {
      hapiValue.base = base.toHapi()
    }
    if (hasContentReference()) {
      hapiValue.contentReferenceElement = contentReference.toHapi()
    }
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (hasDefaultValue()) {
      hapiValue.defaultValue = defaultValue.elementDefinitionDefaultValueToHapi()
    }
    if (hasMeaningWhenMissing()) {
      hapiValue.meaningWhenMissingElement = meaningWhenMissing.toHapi()
    }
    if (hasOrderMeaning()) {
      hapiValue.orderMeaningElement = orderMeaning.toHapi()
    }
    if (hasFixed()) {
      hapiValue.fixed = fixed.elementDefinitionFixedToHapi()
    }
    if (hasPattern()) {
      hapiValue.pattern = pattern.elementDefinitionPatternToHapi()
    }
    if (exampleCount > 0) {
      hapiValue.example = exampleList.map { it.toHapi() }
    }
    if (hasMinValue()) {
      hapiValue.minValue = minValue.elementDefinitionMinValueToHapi()
    }
    if (hasMaxValue()) {
      hapiValue.maxValue = maxValue.elementDefinitionMaxValueToHapi()
    }
    if (hasMaxLength()) {
      hapiValue.maxLengthElement = maxLength.toHapi()
    }
    if (conditionCount > 0) {
      hapiValue.condition = conditionList.map { it.toHapi() }
    }
    if (constraintCount > 0) {
      hapiValue.constraint = constraintList.map { it.toHapi() }
    }
    if (hasMustSupport()) {
      hapiValue.mustSupportElement = mustSupport.toHapi()
    }
    if (hasIsModifier()) {
      hapiValue.isModifierElement = isModifier.toHapi()
    }
    if (hasIsModifierReason()) {
      hapiValue.isModifierReasonElement = isModifierReason.toHapi()
    }
    if (hasIsSummary()) {
      hapiValue.isSummaryElement = isSummary.toHapi()
    }
    if (hasBinding()) {
      hapiValue.binding = binding.toHapi()
    }
    if (mappingCount > 0) {
      hapiValue.mapping = mappingList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.ElementDefinition.toProto(): ElementDefinition {
    val protoValue = ElementDefinition.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPath()) {
      protoValue.path = pathElement.toProto()
    }
    protoValue.addAllRepresentation(
      representation.map {
        ElementDefinition.RepresentationCode.newBuilder()
          .setValue(
            PropertyRepresentationCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    if (hasSliceName()) {
      protoValue.sliceName = sliceNameElement.toProto()
    }
    if (hasSliceIsConstraining()) {
      protoValue.sliceIsConstraining = sliceIsConstrainingElement.toProto()
    }
    if (hasLabel()) {
      protoValue.label = labelElement.toProto()
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasSlicing()) {
      protoValue.slicing = slicing.toProto()
    }
    if (hasShort()) {
      protoValue.short = shortElement.toProto()
    }
    if (hasDefinition()) {
      protoValue.definition = definitionElement.toProto()
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    if (hasRequirements()) {
      protoValue.requirements = requirementsElement.toProto()
    }
    if (hasAlias()) {
      protoValue.addAllAlias(alias.map { it.toProto() })
    }
    if (hasMin()) {
      protoValue.min = minElement.toProto()
    }
    if (hasMax()) {
      protoValue.max = maxElement.toProto()
    }
    if (hasBase()) {
      protoValue.base = base.toProto()
    }
    if (hasContentReference()) {
      protoValue.contentReference = contentReferenceElement.toProto()
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasDefaultValue()) {
      protoValue.defaultValue = defaultValue.elementDefinitionDefaultValueToProto()
    }
    if (hasMeaningWhenMissing()) {
      protoValue.meaningWhenMissing = meaningWhenMissingElement.toProto()
    }
    if (hasOrderMeaning()) {
      protoValue.orderMeaning = orderMeaningElement.toProto()
    }
    if (hasFixed()) {
      protoValue.fixed = fixed.elementDefinitionFixedToProto()
    }
    if (hasPattern()) {
      protoValue.pattern = pattern.elementDefinitionPatternToProto()
    }
    if (hasExample()) {
      protoValue.addAllExample(example.map { it.toProto() })
    }
    if (hasMinValue()) {
      protoValue.minValue = minValue.elementDefinitionMinValueToProto()
    }
    if (hasMaxValue()) {
      protoValue.maxValue = maxValue.elementDefinitionMaxValueToProto()
    }
    if (hasMaxLength()) {
      protoValue.maxLength = maxLengthElement.toProto()
    }
    if (hasCondition()) {
      protoValue.addAllCondition(condition.map { it.toProto() })
    }
    if (hasConstraint()) {
      protoValue.addAllConstraint(constraint.map { it.toProto() })
    }
    if (hasMustSupport()) {
      protoValue.mustSupport = mustSupportElement.toProto()
    }
    if (hasIsModifier()) {
      protoValue.isModifier = isModifierElement.toProto()
    }
    if (hasIsModifierReason()) {
      protoValue.isModifierReason = isModifierReasonElement.toProto()
    }
    if (hasIsSummary()) {
      protoValue.isSummary = isSummaryElement.toProto()
    }
    if (hasBinding()) {
      protoValue.binding = binding.toProto()
    }
    if (hasMapping()) {
      protoValue.addAllMapping(mapping.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionSlicingComponent.toProto():
    ElementDefinition.Slicing {
    val protoValue = ElementDefinition.Slicing.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasDiscriminator()) {
      protoValue.addAllDiscriminator(discriminator.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasOrdered()) {
      protoValue.ordered = orderedElement.toProto()
    }
    protoValue.rules =
      ElementDefinition.Slicing.RulesCode.newBuilder()
        .setValue(
          SlicingRulesCode.Value.valueOf(
            rules.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionSlicingDiscriminatorComponent.toProto():
    ElementDefinition.Slicing.Discriminator {
    val protoValue =
      ElementDefinition.Slicing.Discriminator.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    protoValue.type =
      ElementDefinition.Slicing.Discriminator.TypeCode.newBuilder()
        .setValue(
          DiscriminatorTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasPath()) {
      protoValue.path = pathElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionBaseComponent.toProto():
    ElementDefinition.Base {
    val protoValue = ElementDefinition.Base.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasPath()) {
      protoValue.path = pathElement.toProto()
    }
    if (hasMin()) {
      protoValue.min = minElement.toProto()
    }
    if (hasMax()) {
      protoValue.max = maxElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ElementDefinition.TypeRefComponent.toProto():
    ElementDefinition.TypeRef {
    val protoValue = ElementDefinition.TypeRef.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasProfile()) {
      protoValue.addAllProfile(profile.map { it.toProto() })
    }
    if (hasTargetProfile()) {
      protoValue.addAllTargetProfile(targetProfile.map { it.toProto() })
    }
    protoValue.addAllAggregation(
      aggregation.map {
        ElementDefinition.TypeRef.AggregationCode.newBuilder()
          .setValue(
            AggregationModeCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    protoValue.versioning =
      ElementDefinition.TypeRef.VersioningCode.newBuilder()
        .setValue(
          ReferenceVersionRulesCode.Value.valueOf(
            versioning.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionExampleComponent.toProto():
    ElementDefinition.Example {
    val protoValue = ElementDefinition.Example.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasLabel()) {
      protoValue.label = labelElement.toProto()
    }
    if (hasValue()) {
      protoValue.value = value.elementDefinitionExampleValueToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionConstraintComponent.toProto():
    ElementDefinition.Constraint {
    val protoValue =
      ElementDefinition.Constraint.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasKey()) {
      protoValue.key = keyElement.toProto()
    }
    if (hasRequirements()) {
      protoValue.requirements = requirementsElement.toProto()
    }
    protoValue.severity =
      ElementDefinition.Constraint.SeverityCode.newBuilder()
        .setValue(
          ConstraintSeverityCode.Value.valueOf(
            severity.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasHuman()) {
      protoValue.human = humanElement.toProto()
    }
    if (hasExpression()) {
      protoValue.expression = expressionElement.toProto()
    }
    if (hasXpath()) {
      protoValue.xpath = xpathElement.toProto()
    }
    if (hasSource()) {
      protoValue.source = sourceElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionBindingComponent.toProto():
    ElementDefinition.ElementDefinitionBinding {
    val protoValue =
      ElementDefinition.ElementDefinitionBinding.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    protoValue.strength =
      ElementDefinition.ElementDefinitionBinding.StrengthCode.newBuilder()
        .setValue(
          BindingStrengthCode.Value.valueOf(
            strength.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasValueSet()) {
      protoValue.valueSet = valueSetElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionMappingComponent.toProto():
    ElementDefinition.Mapping {
    val protoValue = ElementDefinition.Mapping.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasIdentity()) {
      protoValue.identity = identityElement.toProto()
    }
    protoValue.language =
      ElementDefinition.Mapping.LanguageCode.newBuilder()
        .setValue(language.protoCodeCheck())
        .build()
    if (hasMap()) {
      protoValue.map = mapElement.toProto()
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.Slicing.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionSlicingComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionSlicingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (discriminatorCount > 0) {
      hapiValue.discriminator = discriminatorList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasOrdered()) {
      hapiValue.orderedElement = ordered.toHapi()
    }
    hapiValue.rules =
      org.hl7.fhir.r4.model.ElementDefinition.SlicingRules.valueOf(
        rules.value.name.hapiCodeCheck().replace("_", "")
      )
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.Slicing.Discriminator.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionSlicingDiscriminatorComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionSlicingDiscriminatorComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    hapiValue.type =
      org.hl7.fhir.r4.model.ElementDefinition.DiscriminatorType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPath()) {
      hapiValue.pathElement = path.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.Base.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionBaseComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionBaseComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasPath()) {
      hapiValue.pathElement = path.toHapi()
    }
    if (hasMin()) {
      hapiValue.minElement = min.toHapi()
    }
    if (hasMax()) {
      hapiValue.maxElement = max.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.TypeRef.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.TypeRefComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.TypeRefComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (profileCount > 0) {
      hapiValue.profile = profileList.map { it.toHapi() }
    }
    if (targetProfileCount > 0) {
      hapiValue.targetProfile = targetProfileList.map { it.toHapi() }
    }
    aggregationList.forEach {
      hapiValue.addAggregation(
        org.hl7.fhir.r4.model.ElementDefinition.AggregationMode.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    hapiValue.versioning =
      org.hl7.fhir.r4.model.ElementDefinition.ReferenceVersionRules.valueOf(
        versioning.value.name.hapiCodeCheck().replace("_", "")
      )
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.Example.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionExampleComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionExampleComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasLabel()) {
      hapiValue.labelElement = label.toHapi()
    }
    if (hasValue()) {
      hapiValue.value = value.elementDefinitionExampleValueToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.Constraint.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionConstraintComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionConstraintComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasKey()) {
      hapiValue.keyElement = key.toHapi()
    }
    if (hasRequirements()) {
      hapiValue.requirementsElement = requirements.toHapi()
    }
    hapiValue.severity =
      org.hl7.fhir.r4.model.ElementDefinition.ConstraintSeverity.valueOf(
        severity.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasHuman()) {
      hapiValue.humanElement = human.toHapi()
    }
    if (hasExpression()) {
      hapiValue.expressionElement = expression.toHapi()
    }
    if (hasXpath()) {
      hapiValue.xpathElement = xpath.toHapi()
    }
    if (hasSource()) {
      hapiValue.sourceElement = source.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.ElementDefinitionBinding.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionBindingComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionBindingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    hapiValue.strength =
      Enumerations.BindingStrength.valueOf(strength.value.name.hapiCodeCheck().replace("_", ""))
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasValueSet()) {
      hapiValue.valueSetElement = valueSet.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.Mapping.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionMappingComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionMappingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasIdentity()) {
      hapiValue.identityElement = identity.toHapi()
    }
    hapiValue.language = language.value.hapiCodeCheck()
    if (hasMap()) {
      hapiValue.mapElement = map.toHapi()
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    return hapiValue
  }
}
