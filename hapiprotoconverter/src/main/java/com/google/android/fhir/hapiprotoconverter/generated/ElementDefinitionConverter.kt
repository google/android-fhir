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

public object ElementDefinitionConverter {
  @JvmStatic
  private fun ElementDefinition.DefaultValueX.elementDefinitionDefaultValueToHapi(): Type {
    if (this.getBase64Binary() != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.getBase64Binary()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType) {
      return (this.getCanonical()).toHapi()
    }
    if (this.getCode() != Code.newBuilder().defaultInstanceForType) {
      return (this.getCode()).toHapi()
    }
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getId() != Id.newBuilder().defaultInstanceForType) {
      return (this.getId()).toHapi()
    }
    if (this.getInstant() != Instant.newBuilder().defaultInstanceForType) {
      return (this.getInstant()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType) {
      return (this.getInteger()).toHapi()
    }
    if (this.getMarkdown() != Markdown.newBuilder().defaultInstanceForType) {
      return (this.getMarkdown()).toHapi()
    }
    if (this.getOid() != Oid.newBuilder().defaultInstanceForType) {
      return (this.getOid()).toHapi()
    }
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType) {
      return (this.getTime()).toHapi()
    }
    if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.getUnsignedInt()).toHapi()
    }
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType) {
      return (this.getUri()).toHapi()
    }
    if (this.getUrl() != Url.newBuilder().defaultInstanceForType) {
      return (this.getUrl()).toHapi()
    }
    if (this.getUuid() != Uuid.newBuilder().defaultInstanceForType) {
      return (this.getUuid()).toHapi()
    }
    if (this.getAddress() != Address.newBuilder().defaultInstanceForType) {
      return (this.getAddress()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType) {
      return (this.getAge()).toHapi()
    }
    if (this.getAnnotation() != Annotation.newBuilder().defaultInstanceForType) {
      return (this.getAnnotation()).toHapi()
    }
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getCoding() != Coding.newBuilder().defaultInstanceForType) {
      return (this.getCoding()).toHapi()
    }
    if (this.getContactPoint() != ContactPoint.newBuilder().defaultInstanceForType) {
      return (this.getContactPoint()).toHapi()
    }
    if (this.getCount() != Count.newBuilder().defaultInstanceForType) {
      return (this.getCount()).toHapi()
    }
    if (this.getDistance() != Distance.newBuilder().defaultInstanceForType) {
      return (this.getDistance()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType) {
      return (this.getDuration()).toHapi()
    }
    if (this.getHumanName() != HumanName.newBuilder().defaultInstanceForType) {
      return (this.getHumanName()).toHapi()
    }
    if (this.getIdentifier() != Identifier.newBuilder().defaultInstanceForType) {
      return (this.getIdentifier()).toHapi()
    }
    if (this.getMoney() != Money.newBuilder().defaultInstanceForType) {
      return (this.getMoney()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType) {
      return (this.getRatio()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    if (this.getSampledData() != SampledData.newBuilder().defaultInstanceForType) {
      return (this.getSampledData()).toHapi()
    }
    if (this.getSignature() != Signature.newBuilder().defaultInstanceForType) {
      return (this.getSignature()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    if (this.getContactDetail() != ContactDetail.newBuilder().defaultInstanceForType) {
      return (this.getContactDetail()).toHapi()
    }
    if (this.getContributor() != Contributor.newBuilder().defaultInstanceForType) {
      return (this.getContributor()).toHapi()
    }
    if (this.getDataRequirement() != DataRequirement.newBuilder().defaultInstanceForType) {
      return (this.getDataRequirement()).toHapi()
    }
    if (this.getExpression() != Expression.newBuilder().defaultInstanceForType) {
      return (this.getExpression()).toHapi()
    }
    if (this.getParameterDefinition() != ParameterDefinition.newBuilder().defaultInstanceForType) {
      return (this.getParameterDefinition()).toHapi()
    }
    if (this.getRelatedArtifact() != RelatedArtifact.newBuilder().defaultInstanceForType) {
      return (this.getRelatedArtifact()).toHapi()
    }
    if (this.getTriggerDefinition() != TriggerDefinition.newBuilder().defaultInstanceForType) {
      return (this.getTriggerDefinition()).toHapi()
    }
    if (this.getUsageContext() != UsageContext.newBuilder().defaultInstanceForType) {
      return (this.getUsageContext()).toHapi()
    }
    if (this.getDosage() != Dosage.newBuilder().defaultInstanceForType) {
      return (this.getDosage()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.defaultValue[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionDefaultValueToProto(): ElementDefinition.DefaultValueX {
    val protoValue = ElementDefinition.DefaultValueX.newBuilder()
    if (this is Base64BinaryType) {
      protoValue.setBase64Binary(this.toProto())
    }
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is CanonicalType) {
      protoValue.setCanonical(this.toProto())
    }
    if (this is CodeType) {
      protoValue.setCode(this.toProto())
    }
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    if (this is IdType) {
      protoValue.setId(this.toProto())
    }
    if (this is InstantType) {
      protoValue.setInstant(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is MarkdownType) {
      protoValue.setMarkdown(this.toProto())
    }
    if (this is OidType) {
      protoValue.setOid(this.toProto())
    }
    if (this is PositiveIntType) {
      protoValue.setPositiveInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is TimeType) {
      protoValue.setTime(this.toProto())
    }
    if (this is UnsignedIntType) {
      protoValue.setUnsignedInt(this.toProto())
    }
    if (this is UriType) {
      protoValue.setUri(this.toProto())
    }
    if (this is UrlType) {
      protoValue.setUrl(this.toProto())
    }
    if (this is UuidType) {
      protoValue.setUuid(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.setAddress(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Annotation) {
      protoValue.setAnnotation(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.setCoding(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ContactPoint) {
      protoValue.setContactPoint(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Count) {
      protoValue.setCount(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Distance) {
      protoValue.setDistance(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.HumanName) {
      protoValue.setHumanName(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Identifier) {
      protoValue.setIdentifier(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.setMoney(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
      protoValue.setSampledData(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Signature) {
      protoValue.setSignature(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ContactDetail) {
      protoValue.setContactDetail(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Contributor) {
      protoValue.setContributor(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.DataRequirement) {
      protoValue.setDataRequirement(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Expression) {
      protoValue.setExpression(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ParameterDefinition) {
      protoValue.setParameterDefinition(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.RelatedArtifact) {
      protoValue.setRelatedArtifact(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.TriggerDefinition) {
      protoValue.setTriggerDefinition(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.UsageContext) {
      protoValue.setUsageContext(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Dosage) {
      protoValue.setDosage(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.FixedX.elementDefinitionFixedToHapi(): Type {
    if (this.getBase64Binary() != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.getBase64Binary()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType) {
      return (this.getCanonical()).toHapi()
    }
    if (this.getCode() != Code.newBuilder().defaultInstanceForType) {
      return (this.getCode()).toHapi()
    }
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getId() != Id.newBuilder().defaultInstanceForType) {
      return (this.getId()).toHapi()
    }
    if (this.getInstant() != Instant.newBuilder().defaultInstanceForType) {
      return (this.getInstant()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType) {
      return (this.getInteger()).toHapi()
    }
    if (this.getMarkdown() != Markdown.newBuilder().defaultInstanceForType) {
      return (this.getMarkdown()).toHapi()
    }
    if (this.getOid() != Oid.newBuilder().defaultInstanceForType) {
      return (this.getOid()).toHapi()
    }
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType) {
      return (this.getTime()).toHapi()
    }
    if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.getUnsignedInt()).toHapi()
    }
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType) {
      return (this.getUri()).toHapi()
    }
    if (this.getUrl() != Url.newBuilder().defaultInstanceForType) {
      return (this.getUrl()).toHapi()
    }
    if (this.getUuid() != Uuid.newBuilder().defaultInstanceForType) {
      return (this.getUuid()).toHapi()
    }
    if (this.getAddress() != Address.newBuilder().defaultInstanceForType) {
      return (this.getAddress()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType) {
      return (this.getAge()).toHapi()
    }
    if (this.getAnnotation() != Annotation.newBuilder().defaultInstanceForType) {
      return (this.getAnnotation()).toHapi()
    }
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getCoding() != Coding.newBuilder().defaultInstanceForType) {
      return (this.getCoding()).toHapi()
    }
    if (this.getContactPoint() != ContactPoint.newBuilder().defaultInstanceForType) {
      return (this.getContactPoint()).toHapi()
    }
    if (this.getCount() != Count.newBuilder().defaultInstanceForType) {
      return (this.getCount()).toHapi()
    }
    if (this.getDistance() != Distance.newBuilder().defaultInstanceForType) {
      return (this.getDistance()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType) {
      return (this.getDuration()).toHapi()
    }
    if (this.getHumanName() != HumanName.newBuilder().defaultInstanceForType) {
      return (this.getHumanName()).toHapi()
    }
    if (this.getIdentifier() != Identifier.newBuilder().defaultInstanceForType) {
      return (this.getIdentifier()).toHapi()
    }
    if (this.getMoney() != Money.newBuilder().defaultInstanceForType) {
      return (this.getMoney()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType) {
      return (this.getRatio()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    if (this.getSampledData() != SampledData.newBuilder().defaultInstanceForType) {
      return (this.getSampledData()).toHapi()
    }
    if (this.getSignature() != Signature.newBuilder().defaultInstanceForType) {
      return (this.getSignature()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    if (this.getContactDetail() != ContactDetail.newBuilder().defaultInstanceForType) {
      return (this.getContactDetail()).toHapi()
    }
    if (this.getContributor() != Contributor.newBuilder().defaultInstanceForType) {
      return (this.getContributor()).toHapi()
    }
    if (this.getDataRequirement() != DataRequirement.newBuilder().defaultInstanceForType) {
      return (this.getDataRequirement()).toHapi()
    }
    if (this.getExpression() != Expression.newBuilder().defaultInstanceForType) {
      return (this.getExpression()).toHapi()
    }
    if (this.getParameterDefinition() != ParameterDefinition.newBuilder().defaultInstanceForType) {
      return (this.getParameterDefinition()).toHapi()
    }
    if (this.getRelatedArtifact() != RelatedArtifact.newBuilder().defaultInstanceForType) {
      return (this.getRelatedArtifact()).toHapi()
    }
    if (this.getTriggerDefinition() != TriggerDefinition.newBuilder().defaultInstanceForType) {
      return (this.getTriggerDefinition()).toHapi()
    }
    if (this.getUsageContext() != UsageContext.newBuilder().defaultInstanceForType) {
      return (this.getUsageContext()).toHapi()
    }
    if (this.getDosage() != Dosage.newBuilder().defaultInstanceForType) {
      return (this.getDosage()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.fixed[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionFixedToProto(): ElementDefinition.FixedX {
    val protoValue = ElementDefinition.FixedX.newBuilder()
    if (this is Base64BinaryType) {
      protoValue.setBase64Binary(this.toProto())
    }
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is CanonicalType) {
      protoValue.setCanonical(this.toProto())
    }
    if (this is CodeType) {
      protoValue.setCode(this.toProto())
    }
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    if (this is IdType) {
      protoValue.setId(this.toProto())
    }
    if (this is InstantType) {
      protoValue.setInstant(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is MarkdownType) {
      protoValue.setMarkdown(this.toProto())
    }
    if (this is OidType) {
      protoValue.setOid(this.toProto())
    }
    if (this is PositiveIntType) {
      protoValue.setPositiveInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is TimeType) {
      protoValue.setTime(this.toProto())
    }
    if (this is UnsignedIntType) {
      protoValue.setUnsignedInt(this.toProto())
    }
    if (this is UriType) {
      protoValue.setUri(this.toProto())
    }
    if (this is UrlType) {
      protoValue.setUrl(this.toProto())
    }
    if (this is UuidType) {
      protoValue.setUuid(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.setAddress(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Annotation) {
      protoValue.setAnnotation(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.setCoding(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ContactPoint) {
      protoValue.setContactPoint(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Count) {
      protoValue.setCount(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Distance) {
      protoValue.setDistance(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.HumanName) {
      protoValue.setHumanName(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Identifier) {
      protoValue.setIdentifier(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.setMoney(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
      protoValue.setSampledData(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Signature) {
      protoValue.setSignature(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ContactDetail) {
      protoValue.setContactDetail(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Contributor) {
      protoValue.setContributor(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.DataRequirement) {
      protoValue.setDataRequirement(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Expression) {
      protoValue.setExpression(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ParameterDefinition) {
      protoValue.setParameterDefinition(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.RelatedArtifact) {
      protoValue.setRelatedArtifact(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.TriggerDefinition) {
      protoValue.setTriggerDefinition(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.UsageContext) {
      protoValue.setUsageContext(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Dosage) {
      protoValue.setDosage(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.PatternX.elementDefinitionPatternToHapi(): Type {
    if (this.getBase64Binary() != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.getBase64Binary()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType) {
      return (this.getCanonical()).toHapi()
    }
    if (this.getCode() != Code.newBuilder().defaultInstanceForType) {
      return (this.getCode()).toHapi()
    }
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getId() != Id.newBuilder().defaultInstanceForType) {
      return (this.getId()).toHapi()
    }
    if (this.getInstant() != Instant.newBuilder().defaultInstanceForType) {
      return (this.getInstant()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType) {
      return (this.getInteger()).toHapi()
    }
    if (this.getMarkdown() != Markdown.newBuilder().defaultInstanceForType) {
      return (this.getMarkdown()).toHapi()
    }
    if (this.getOid() != Oid.newBuilder().defaultInstanceForType) {
      return (this.getOid()).toHapi()
    }
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType) {
      return (this.getTime()).toHapi()
    }
    if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.getUnsignedInt()).toHapi()
    }
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType) {
      return (this.getUri()).toHapi()
    }
    if (this.getUrl() != Url.newBuilder().defaultInstanceForType) {
      return (this.getUrl()).toHapi()
    }
    if (this.getUuid() != Uuid.newBuilder().defaultInstanceForType) {
      return (this.getUuid()).toHapi()
    }
    if (this.getAddress() != Address.newBuilder().defaultInstanceForType) {
      return (this.getAddress()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType) {
      return (this.getAge()).toHapi()
    }
    if (this.getAnnotation() != Annotation.newBuilder().defaultInstanceForType) {
      return (this.getAnnotation()).toHapi()
    }
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getCoding() != Coding.newBuilder().defaultInstanceForType) {
      return (this.getCoding()).toHapi()
    }
    if (this.getContactPoint() != ContactPoint.newBuilder().defaultInstanceForType) {
      return (this.getContactPoint()).toHapi()
    }
    if (this.getCount() != Count.newBuilder().defaultInstanceForType) {
      return (this.getCount()).toHapi()
    }
    if (this.getDistance() != Distance.newBuilder().defaultInstanceForType) {
      return (this.getDistance()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType) {
      return (this.getDuration()).toHapi()
    }
    if (this.getHumanName() != HumanName.newBuilder().defaultInstanceForType) {
      return (this.getHumanName()).toHapi()
    }
    if (this.getIdentifier() != Identifier.newBuilder().defaultInstanceForType) {
      return (this.getIdentifier()).toHapi()
    }
    if (this.getMoney() != Money.newBuilder().defaultInstanceForType) {
      return (this.getMoney()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType) {
      return (this.getRatio()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    if (this.getSampledData() != SampledData.newBuilder().defaultInstanceForType) {
      return (this.getSampledData()).toHapi()
    }
    if (this.getSignature() != Signature.newBuilder().defaultInstanceForType) {
      return (this.getSignature()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    if (this.getContactDetail() != ContactDetail.newBuilder().defaultInstanceForType) {
      return (this.getContactDetail()).toHapi()
    }
    if (this.getContributor() != Contributor.newBuilder().defaultInstanceForType) {
      return (this.getContributor()).toHapi()
    }
    if (this.getDataRequirement() != DataRequirement.newBuilder().defaultInstanceForType) {
      return (this.getDataRequirement()).toHapi()
    }
    if (this.getExpression() != Expression.newBuilder().defaultInstanceForType) {
      return (this.getExpression()).toHapi()
    }
    if (this.getParameterDefinition() != ParameterDefinition.newBuilder().defaultInstanceForType) {
      return (this.getParameterDefinition()).toHapi()
    }
    if (this.getRelatedArtifact() != RelatedArtifact.newBuilder().defaultInstanceForType) {
      return (this.getRelatedArtifact()).toHapi()
    }
    if (this.getTriggerDefinition() != TriggerDefinition.newBuilder().defaultInstanceForType) {
      return (this.getTriggerDefinition()).toHapi()
    }
    if (this.getUsageContext() != UsageContext.newBuilder().defaultInstanceForType) {
      return (this.getUsageContext()).toHapi()
    }
    if (this.getDosage() != Dosage.newBuilder().defaultInstanceForType) {
      return (this.getDosage()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.pattern[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionPatternToProto(): ElementDefinition.PatternX {
    val protoValue = ElementDefinition.PatternX.newBuilder()
    if (this is Base64BinaryType) {
      protoValue.setBase64Binary(this.toProto())
    }
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is CanonicalType) {
      protoValue.setCanonical(this.toProto())
    }
    if (this is CodeType) {
      protoValue.setCode(this.toProto())
    }
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    if (this is IdType) {
      protoValue.setId(this.toProto())
    }
    if (this is InstantType) {
      protoValue.setInstant(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is MarkdownType) {
      protoValue.setMarkdown(this.toProto())
    }
    if (this is OidType) {
      protoValue.setOid(this.toProto())
    }
    if (this is PositiveIntType) {
      protoValue.setPositiveInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is TimeType) {
      protoValue.setTime(this.toProto())
    }
    if (this is UnsignedIntType) {
      protoValue.setUnsignedInt(this.toProto())
    }
    if (this is UriType) {
      protoValue.setUri(this.toProto())
    }
    if (this is UrlType) {
      protoValue.setUrl(this.toProto())
    }
    if (this is UuidType) {
      protoValue.setUuid(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.setAddress(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Annotation) {
      protoValue.setAnnotation(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.setCoding(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ContactPoint) {
      protoValue.setContactPoint(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Count) {
      protoValue.setCount(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Distance) {
      protoValue.setDistance(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.HumanName) {
      protoValue.setHumanName(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Identifier) {
      protoValue.setIdentifier(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.setMoney(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
      protoValue.setSampledData(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Signature) {
      protoValue.setSignature(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ContactDetail) {
      protoValue.setContactDetail(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Contributor) {
      protoValue.setContributor(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.DataRequirement) {
      protoValue.setDataRequirement(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Expression) {
      protoValue.setExpression(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ParameterDefinition) {
      protoValue.setParameterDefinition(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.RelatedArtifact) {
      protoValue.setRelatedArtifact(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.TriggerDefinition) {
      protoValue.setTriggerDefinition(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.UsageContext) {
      protoValue.setUsageContext(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Dosage) {
      protoValue.setDosage(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.Example.ValueX.elementDefinitionExampleValueToHapi(): Type {
    if (this.getBase64Binary() != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.getBase64Binary()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType) {
      return (this.getCanonical()).toHapi()
    }
    if (this.getCode() != Code.newBuilder().defaultInstanceForType) {
      return (this.getCode()).toHapi()
    }
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getId() != Id.newBuilder().defaultInstanceForType) {
      return (this.getId()).toHapi()
    }
    if (this.getInstant() != Instant.newBuilder().defaultInstanceForType) {
      return (this.getInstant()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType) {
      return (this.getInteger()).toHapi()
    }
    if (this.getMarkdown() != Markdown.newBuilder().defaultInstanceForType) {
      return (this.getMarkdown()).toHapi()
    }
    if (this.getOid() != Oid.newBuilder().defaultInstanceForType) {
      return (this.getOid()).toHapi()
    }
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType) {
      return (this.getTime()).toHapi()
    }
    if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.getUnsignedInt()).toHapi()
    }
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType) {
      return (this.getUri()).toHapi()
    }
    if (this.getUrl() != Url.newBuilder().defaultInstanceForType) {
      return (this.getUrl()).toHapi()
    }
    if (this.getUuid() != Uuid.newBuilder().defaultInstanceForType) {
      return (this.getUuid()).toHapi()
    }
    if (this.getAddress() != Address.newBuilder().defaultInstanceForType) {
      return (this.getAddress()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType) {
      return (this.getAge()).toHapi()
    }
    if (this.getAnnotation() != Annotation.newBuilder().defaultInstanceForType) {
      return (this.getAnnotation()).toHapi()
    }
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getCoding() != Coding.newBuilder().defaultInstanceForType) {
      return (this.getCoding()).toHapi()
    }
    if (this.getContactPoint() != ContactPoint.newBuilder().defaultInstanceForType) {
      return (this.getContactPoint()).toHapi()
    }
    if (this.getCount() != Count.newBuilder().defaultInstanceForType) {
      return (this.getCount()).toHapi()
    }
    if (this.getDistance() != Distance.newBuilder().defaultInstanceForType) {
      return (this.getDistance()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType) {
      return (this.getDuration()).toHapi()
    }
    if (this.getHumanName() != HumanName.newBuilder().defaultInstanceForType) {
      return (this.getHumanName()).toHapi()
    }
    if (this.getIdentifier() != Identifier.newBuilder().defaultInstanceForType) {
      return (this.getIdentifier()).toHapi()
    }
    if (this.getMoney() != Money.newBuilder().defaultInstanceForType) {
      return (this.getMoney()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType) {
      return (this.getRatio()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    if (this.getSampledData() != SampledData.newBuilder().defaultInstanceForType) {
      return (this.getSampledData()).toHapi()
    }
    if (this.getSignature() != Signature.newBuilder().defaultInstanceForType) {
      return (this.getSignature()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    if (this.getContactDetail() != ContactDetail.newBuilder().defaultInstanceForType) {
      return (this.getContactDetail()).toHapi()
    }
    if (this.getContributor() != Contributor.newBuilder().defaultInstanceForType) {
      return (this.getContributor()).toHapi()
    }
    if (this.getDataRequirement() != DataRequirement.newBuilder().defaultInstanceForType) {
      return (this.getDataRequirement()).toHapi()
    }
    if (this.getExpression() != Expression.newBuilder().defaultInstanceForType) {
      return (this.getExpression()).toHapi()
    }
    if (this.getParameterDefinition() != ParameterDefinition.newBuilder().defaultInstanceForType) {
      return (this.getParameterDefinition()).toHapi()
    }
    if (this.getRelatedArtifact() != RelatedArtifact.newBuilder().defaultInstanceForType) {
      return (this.getRelatedArtifact()).toHapi()
    }
    if (this.getTriggerDefinition() != TriggerDefinition.newBuilder().defaultInstanceForType) {
      return (this.getTriggerDefinition()).toHapi()
    }
    if (this.getUsageContext() != UsageContext.newBuilder().defaultInstanceForType) {
      return (this.getUsageContext()).toHapi()
    }
    if (this.getDosage() != Dosage.newBuilder().defaultInstanceForType) {
      return (this.getDosage()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.example.value[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionExampleValueToProto(): ElementDefinition.Example.ValueX {
    val protoValue = ElementDefinition.Example.ValueX.newBuilder()
    if (this is Base64BinaryType) {
      protoValue.setBase64Binary(this.toProto())
    }
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is CanonicalType) {
      protoValue.setCanonical(this.toProto())
    }
    if (this is CodeType) {
      protoValue.setCode(this.toProto())
    }
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    if (this is IdType) {
      protoValue.setId(this.toProto())
    }
    if (this is InstantType) {
      protoValue.setInstant(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is MarkdownType) {
      protoValue.setMarkdown(this.toProto())
    }
    if (this is OidType) {
      protoValue.setOid(this.toProto())
    }
    if (this is PositiveIntType) {
      protoValue.setPositiveInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is TimeType) {
      protoValue.setTime(this.toProto())
    }
    if (this is UnsignedIntType) {
      protoValue.setUnsignedInt(this.toProto())
    }
    if (this is UriType) {
      protoValue.setUri(this.toProto())
    }
    if (this is UrlType) {
      protoValue.setUrl(this.toProto())
    }
    if (this is UuidType) {
      protoValue.setUuid(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.setAddress(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Annotation) {
      protoValue.setAnnotation(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.setCoding(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ContactPoint) {
      protoValue.setContactPoint(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Count) {
      protoValue.setCount(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Distance) {
      protoValue.setDistance(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.HumanName) {
      protoValue.setHumanName(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Identifier) {
      protoValue.setIdentifier(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.setMoney(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
      protoValue.setSampledData(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Signature) {
      protoValue.setSignature(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ContactDetail) {
      protoValue.setContactDetail(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Contributor) {
      protoValue.setContributor(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.DataRequirement) {
      protoValue.setDataRequirement(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Expression) {
      protoValue.setExpression(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.ParameterDefinition) {
      protoValue.setParameterDefinition(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.RelatedArtifact) {
      protoValue.setRelatedArtifact(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.TriggerDefinition) {
      protoValue.setTriggerDefinition(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.UsageContext) {
      protoValue.setUsageContext(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Dosage) {
      protoValue.setDosage(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.MinValueX.elementDefinitionMinValueToHapi(): Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getInstant() != Instant.newBuilder().defaultInstanceForType) {
      return (this.getInstant()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType) {
      return (this.getTime()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType) {
      return (this.getInteger()).toHapi()
    }
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.getUnsignedInt()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.minValue[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionMinValueToProto(): ElementDefinition.MinValueX {
    val protoValue = ElementDefinition.MinValueX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is InstantType) {
      protoValue.setInstant(this.toProto())
    }
    if (this is TimeType) {
      protoValue.setTime(this.toProto())
    }
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is PositiveIntType) {
      protoValue.setPositiveInt(this.toProto())
    }
    if (this is UnsignedIntType) {
      protoValue.setUnsignedInt(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.MaxValueX.elementDefinitionMaxValueToHapi(): Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getInstant() != Instant.newBuilder().defaultInstanceForType) {
      return (this.getInstant()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType) {
      return (this.getTime()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType) {
      return (this.getInteger()).toHapi()
    }
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.getUnsignedInt()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ElementDefinition.maxValue[x]")
  }

  @JvmStatic
  private fun Type.elementDefinitionMaxValueToProto(): ElementDefinition.MaxValueX {
    val protoValue = ElementDefinition.MaxValueX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is InstantType) {
      protoValue.setInstant(this.toProto())
    }
    if (this is TimeType) {
      protoValue.setTime(this.toProto())
    }
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is PositiveIntType) {
      protoValue.setPositiveInt(this.toProto())
    }
    if (this is UnsignedIntType) {
      protoValue.setUnsignedInt(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun ElementDefinition.toHapi(): org.hl7.fhir.r4.model.ElementDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasPath()) {
      hapiValue.setPathElement(path.toHapi())
    }
    representationList.forEach {
      hapiValue.addRepresentation(
        org.hl7.fhir.r4.model.ElementDefinition.PropertyRepresentation.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasSliceName()) {
      hapiValue.setSliceNameElement(sliceName.toHapi())
    }
    if (hasSliceIsConstraining()) {
      hapiValue.setSliceIsConstrainingElement(sliceIsConstraining.toHapi())
    }
    if (hasLabel()) {
      hapiValue.setLabelElement(label.toHapi())
    }
    if (codeCount > 0) {
      hapiValue.setCode(codeList.map { it.toHapi() })
    }
    if (hasSlicing()) {
      hapiValue.setSlicing(slicing.toHapi())
    }
    if (hasShort()) {
      hapiValue.setShortElement(short.toHapi())
    }
    if (hasDefinition()) {
      hapiValue.setDefinitionElement(definition.toHapi())
    }
    if (hasComment()) {
      hapiValue.setCommentElement(comment.toHapi())
    }
    if (hasRequirements()) {
      hapiValue.setRequirementsElement(requirements.toHapi())
    }
    if (aliasCount > 0) {
      hapiValue.setAlias(aliasList.map { it.toHapi() })
    }
    if (hasMin()) {
      hapiValue.setMinElement(min.toHapi())
    }
    if (hasMax()) {
      hapiValue.setMaxElement(max.toHapi())
    }
    if (hasBase()) {
      hapiValue.setBase(base.toHapi())
    }
    if (hasContentReference()) {
      hapiValue.setContentReferenceElement(contentReference.toHapi())
    }
    if (typeCount > 0) {
      hapiValue.setType(typeList.map { it.toHapi() })
    }
    if (hasDefaultValue()) {
      hapiValue.setDefaultValue(defaultValue.elementDefinitionDefaultValueToHapi())
    }
    if (hasMeaningWhenMissing()) {
      hapiValue.setMeaningWhenMissingElement(meaningWhenMissing.toHapi())
    }
    if (hasOrderMeaning()) {
      hapiValue.setOrderMeaningElement(orderMeaning.toHapi())
    }
    if (hasFixed()) {
      hapiValue.setFixed(fixed.elementDefinitionFixedToHapi())
    }
    if (hasPattern()) {
      hapiValue.setPattern(pattern.elementDefinitionPatternToHapi())
    }
    if (exampleCount > 0) {
      hapiValue.setExample(exampleList.map { it.toHapi() })
    }
    if (hasMinValue()) {
      hapiValue.setMinValue(minValue.elementDefinitionMinValueToHapi())
    }
    if (hasMaxValue()) {
      hapiValue.setMaxValue(maxValue.elementDefinitionMaxValueToHapi())
    }
    if (hasMaxLength()) {
      hapiValue.setMaxLengthElement(maxLength.toHapi())
    }
    if (conditionCount > 0) {
      hapiValue.setCondition(conditionList.map { it.toHapi() })
    }
    if (constraintCount > 0) {
      hapiValue.setConstraint(constraintList.map { it.toHapi() })
    }
    if (hasMustSupport()) {
      hapiValue.setMustSupportElement(mustSupport.toHapi())
    }
    if (hasIsModifier()) {
      hapiValue.setIsModifierElement(isModifier.toHapi())
    }
    if (hasIsModifierReason()) {
      hapiValue.setIsModifierReasonElement(isModifierReason.toHapi())
    }
    if (hasIsSummary()) {
      hapiValue.setIsSummaryElement(isSummary.toHapi())
    }
    if (hasBinding()) {
      hapiValue.setBinding(binding.toHapi())
    }
    if (mappingCount > 0) {
      hapiValue.setMapping(mappingList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ElementDefinition.toProto(): ElementDefinition {
    val protoValue = ElementDefinition.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPath()) {
      protoValue.setPath(pathElement.toProto())
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
      protoValue.setSliceName(sliceNameElement.toProto())
    }
    if (hasSliceIsConstraining()) {
      protoValue.setSliceIsConstraining(sliceIsConstrainingElement.toProto())
    }
    if (hasLabel()) {
      protoValue.setLabel(labelElement.toProto())
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasSlicing()) {
      protoValue.setSlicing(slicing.toProto())
    }
    if (hasShort()) {
      protoValue.setShort(shortElement.toProto())
    }
    if (hasDefinition()) {
      protoValue.setDefinition(definitionElement.toProto())
    }
    if (hasComment()) {
      protoValue.setComment(commentElement.toProto())
    }
    if (hasRequirements()) {
      protoValue.setRequirements(requirementsElement.toProto())
    }
    if (hasAlias()) {
      protoValue.addAllAlias(alias.map { it.toProto() })
    }
    if (hasMin()) {
      protoValue.setMin(minElement.toProto())
    }
    if (hasMax()) {
      protoValue.setMax(maxElement.toProto())
    }
    if (hasBase()) {
      protoValue.setBase(base.toProto())
    }
    if (hasContentReference()) {
      protoValue.setContentReference(contentReferenceElement.toProto())
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasDefaultValue()) {
      protoValue.setDefaultValue(defaultValue.elementDefinitionDefaultValueToProto())
    }
    if (hasMeaningWhenMissing()) {
      protoValue.setMeaningWhenMissing(meaningWhenMissingElement.toProto())
    }
    if (hasOrderMeaning()) {
      protoValue.setOrderMeaning(orderMeaningElement.toProto())
    }
    if (hasFixed()) {
      protoValue.setFixed(fixed.elementDefinitionFixedToProto())
    }
    if (hasPattern()) {
      protoValue.setPattern(pattern.elementDefinitionPatternToProto())
    }
    if (hasExample()) {
      protoValue.addAllExample(example.map { it.toProto() })
    }
    if (hasMinValue()) {
      protoValue.setMinValue(minValue.elementDefinitionMinValueToProto())
    }
    if (hasMaxValue()) {
      protoValue.setMaxValue(maxValue.elementDefinitionMaxValueToProto())
    }
    if (hasMaxLength()) {
      protoValue.setMaxLength(maxLengthElement.toProto())
    }
    if (hasCondition()) {
      protoValue.addAllCondition(condition.map { it.toProto() })
    }
    if (hasConstraint()) {
      protoValue.addAllConstraint(constraint.map { it.toProto() })
    }
    if (hasMustSupport()) {
      protoValue.setMustSupport(mustSupportElement.toProto())
    }
    if (hasIsModifier()) {
      protoValue.setIsModifier(isModifierElement.toProto())
    }
    if (hasIsModifierReason()) {
      protoValue.setIsModifierReason(isModifierReasonElement.toProto())
    }
    if (hasIsSummary()) {
      protoValue.setIsSummary(isSummaryElement.toProto())
    }
    if (hasBinding()) {
      protoValue.setBinding(binding.toProto())
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
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasOrdered()) {
      protoValue.setOrdered(orderedElement.toProto())
    }
    protoValue.setRules(
      ElementDefinition.Slicing.RulesCode.newBuilder()
        .setValue(
          SlicingRulesCode.Value.valueOf(
            rules.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
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
    protoValue.setType(
      ElementDefinition.Slicing.Discriminator.TypeCode.newBuilder()
        .setValue(
          DiscriminatorTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasPath()) {
      protoValue.setPath(pathElement.toProto())
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
      protoValue.setPath(pathElement.toProto())
    }
    if (hasMin()) {
      protoValue.setMin(minElement.toProto())
    }
    if (hasMax()) {
      protoValue.setMax(maxElement.toProto())
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
      protoValue.setCode(codeElement.toProto())
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
    protoValue.setVersioning(
      ElementDefinition.TypeRef.VersioningCode.newBuilder()
        .setValue(
          ReferenceVersionRulesCode.Value.valueOf(
            versioning.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
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
      protoValue.setLabel(labelElement.toProto())
    }
    if (hasValue()) {
      protoValue.setValue(value.elementDefinitionExampleValueToProto())
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
      protoValue.setKey(keyElement.toProto())
    }
    if (hasRequirements()) {
      protoValue.setRequirements(requirementsElement.toProto())
    }
    protoValue.setSeverity(
      ElementDefinition.Constraint.SeverityCode.newBuilder()
        .setValue(
          ConstraintSeverityCode.Value.valueOf(
            severity.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasHuman()) {
      protoValue.setHuman(humanElement.toProto())
    }
    if (hasExpression()) {
      protoValue.setExpression(expressionElement.toProto())
    }
    if (hasXpath()) {
      protoValue.setXpath(xpathElement.toProto())
    }
    if (hasSource()) {
      protoValue.setSource(sourceElement.toProto())
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
    protoValue.setStrength(
      ElementDefinition.ElementDefinitionBinding.StrengthCode.newBuilder()
        .setValue(
          BindingStrengthCode.Value.valueOf(
            strength.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasValueSet()) {
      protoValue.setValueSet(valueSetElement.toProto())
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
      protoValue.setIdentity(identityElement.toProto())
    }
    protoValue.setLanguage(
      ElementDefinition.Mapping.LanguageCode.newBuilder()
        .setValue(language.protoCodeCheck())
        .build()
    )
    if (hasMap()) {
      protoValue.setMap(mapElement.toProto())
    }
    if (hasComment()) {
      protoValue.setComment(commentElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ElementDefinition.Slicing.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionSlicingComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionSlicingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (discriminatorCount > 0) {
      hapiValue.setDiscriminator(discriminatorList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasOrdered()) {
      hapiValue.setOrderedElement(ordered.toHapi())
    }
    hapiValue.setRules(
      org.hl7.fhir.r4.model.ElementDefinition.SlicingRules.valueOf(
        rules.value.name.hapiCodeCheck().replace("_", "")
      )
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
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    hapiValue.setType(
      org.hl7.fhir.r4.model.ElementDefinition.DiscriminatorType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasPath()) {
      hapiValue.setPathElement(path.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.Base.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionBaseComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionBaseComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (hasPath()) {
      hapiValue.setPathElement(path.toHapi())
    }
    if (hasMin()) {
      hapiValue.setMinElement(min.toHapi())
    }
    if (hasMax()) {
      hapiValue.setMaxElement(max.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.TypeRef.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.TypeRefComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.TypeRefComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (hasCode()) {
      hapiValue.setCodeElement(code.toHapi())
    }
    if (profileCount > 0) {
      hapiValue.setProfile(profileList.map { it.toHapi() })
    }
    if (targetProfileCount > 0) {
      hapiValue.setTargetProfile(targetProfileList.map { it.toHapi() })
    }
    aggregationList.forEach {
      hapiValue.addAggregation(
        org.hl7.fhir.r4.model.ElementDefinition.AggregationMode.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    hapiValue.setVersioning(
      org.hl7.fhir.r4.model.ElementDefinition.ReferenceVersionRules.valueOf(
        versioning.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.Example.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionExampleComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionExampleComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (hasLabel()) {
      hapiValue.setLabelElement(label.toHapi())
    }
    if (hasValue()) {
      hapiValue.setValue(value.elementDefinitionExampleValueToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.Constraint.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionConstraintComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionConstraintComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (hasKey()) {
      hapiValue.setKeyElement(key.toHapi())
    }
    if (hasRequirements()) {
      hapiValue.setRequirementsElement(requirements.toHapi())
    }
    hapiValue.setSeverity(
      org.hl7.fhir.r4.model.ElementDefinition.ConstraintSeverity.valueOf(
        severity.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasHuman()) {
      hapiValue.setHumanElement(human.toHapi())
    }
    if (hasExpression()) {
      hapiValue.setExpressionElement(expression.toHapi())
    }
    if (hasXpath()) {
      hapiValue.setXpathElement(xpath.toHapi())
    }
    if (hasSource()) {
      hapiValue.setSourceElement(source.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.ElementDefinitionBinding.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionBindingComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionBindingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    hapiValue.setStrength(
      Enumerations.BindingStrength.valueOf(strength.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasValueSet()) {
      hapiValue.setValueSetElement(valueSet.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ElementDefinition.Mapping.toHapi():
    org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionMappingComponent {
    val hapiValue = org.hl7.fhir.r4.model.ElementDefinition.ElementDefinitionMappingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (hasIdentity()) {
      hapiValue.setIdentityElement(identity.toHapi())
    }
    hapiValue.setLanguage(language.value.hapiCodeCheck())
    if (hasMap()) {
      hapiValue.setMapElement(map.toHapi())
    }
    if (hasComment()) {
      hapiValue.setCommentElement(comment.toHapi())
    }
    return hapiValue
  }
}
