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
import com.google.fhir.r4.core.Address
import com.google.fhir.r4.core.Age
import com.google.fhir.r4.core.Annotation
import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.Base64Binary
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Canonical
import com.google.fhir.r4.core.Code
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.ContactDetail
import com.google.fhir.r4.core.ContactPoint
import com.google.fhir.r4.core.Contributor
import com.google.fhir.r4.core.Count
import com.google.fhir.r4.core.DataRequirement
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Decimal
import com.google.fhir.r4.core.Distance
import com.google.fhir.r4.core.Dosage
import com.google.fhir.r4.core.Duration
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
import com.google.fhir.r4.core.Parameters
import com.google.fhir.r4.core.Parameters.Parameter
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.PositiveInt
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RelatedArtifact
import com.google.fhir.r4.core.SampledData
import com.google.fhir.r4.core.Signature
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

object ParametersConverter {
  @JvmStatic
  private fun Parameters.Parameter.ValueX.parametersParameterValueToHapi(): Type {
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
    throw IllegalArgumentException("Invalid Type for Parameters.parameter.value[x]")
  }

  @JvmStatic
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
  fun Parameters.toHapi(): org.hl7.fhir.r4.model.Parameters {
    val hapiValue = org.hl7.fhir.r4.model.Parameters()
    hapiValue.id = id.value
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

  @JvmStatic
  fun org.hl7.fhir.r4.model.Parameters.toProto(): Parameters {
    val protoValue = Parameters.newBuilder().setId(Id.newBuilder().setValue(id))
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

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent.toProto():
    Parameters.Parameter {
    val protoValue = Parameters.Parameter.newBuilder().setId(String.newBuilder().setValue(id))
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

  @JvmStatic
  private fun Parameters.Parameter.toHapi():
    org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent {
    val hapiValue = org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent()
    hapiValue.id = id.value
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
