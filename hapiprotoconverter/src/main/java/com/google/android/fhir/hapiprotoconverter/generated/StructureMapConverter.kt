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
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
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
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.PositiveInt
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RelatedArtifact
import com.google.fhir.r4.core.SampledData
import com.google.fhir.r4.core.Signature
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.StructureMap
import com.google.fhir.r4.core.StructureMap.Group
import com.google.fhir.r4.core.StructureMap.Group.Input
import com.google.fhir.r4.core.StructureMap.Group.Rule
import com.google.fhir.r4.core.StructureMap.Group.Rule.Source
import com.google.fhir.r4.core.StructureMap.Group.Rule.Target
import com.google.fhir.r4.core.StructureMap.Group.Rule.Target.Parameter
import com.google.fhir.r4.core.StructureMap.Structure
import com.google.fhir.r4.core.StructureMapContextTypeCode
import com.google.fhir.r4.core.StructureMapGroupTypeModeCode
import com.google.fhir.r4.core.StructureMapInputModeCode
import com.google.fhir.r4.core.StructureMapModelModeCode
import com.google.fhir.r4.core.StructureMapSourceListModeCode
import com.google.fhir.r4.core.StructureMapTargetListModeCode
import com.google.fhir.r4.core.StructureMapTransformCode
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

public object StructureMapConverter {
  @JvmStatic
  private fun StructureMap.Group.Rule.Source.DefaultValueX.structureMapGroupRuleSourceDefaultValueToHapi():
    Type {
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
    throw IllegalArgumentException(
      "Invalid Type for StructureMap.group.rule.source.defaultValue[x]"
    )
  }

  @JvmStatic
  private fun Type.structureMapGroupRuleSourceDefaultValueToProto():
    StructureMap.Group.Rule.Source.DefaultValueX {
    val protoValue = StructureMap.Group.Rule.Source.DefaultValueX.newBuilder()
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
  private fun StructureMap.Group.Rule.Target.Parameter.ValueX.structureMapGroupRuleTargetParameterValueToHapi():
    Type {
    if (this.getId() != Id.newBuilder().defaultInstanceForType) {
      return (this.getId()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType) {
      return (this.getInteger()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType) {
      return (this.getDecimal()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for StructureMap.group.rule.target.parameter.value[x]"
    )
  }

  @JvmStatic
  private fun Type.structureMapGroupRuleTargetParameterValueToProto():
    StructureMap.Group.Rule.Target.Parameter.ValueX {
    val protoValue = StructureMap.Group.Rule.Target.Parameter.ValueX.newBuilder()
    if (this is IdType) {
      protoValue.setId(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun StructureMap.toHapi(): org.hl7.fhir.r4.model.StructureMap {
    val hapiValue = org.hl7.fhir.r4.model.StructureMap()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setStatus(
      Enumerations.PublicationStatus.valueOf(
        status
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map { it.toHapi() })
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setStructure(structureList.map { it.toHapi() })
    hapiValue.setImport(importList.map { it.toHapi() })
    hapiValue.setGroup(groupList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.StructureMap.toProto(): StructureMap {
    val protoValue =
      StructureMap.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .addAllIdentifier(identifier.map { it.toProto() })
        .setVersion(versionElement.toProto())
        .setName(nameElement.toProto())
        .setTitle(titleElement.toProto())
        .setStatus(
          StructureMap.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(
                status
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setExperimental(experimentalElement.toProto())
        .setDate(dateElement.toProto())
        .setPublisher(publisherElement.toProto())
        .addAllContact(contact.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllUseContext(useContext.map { it.toProto() })
        .addAllJurisdiction(jurisdiction.map { it.toProto() })
        .setPurpose(purposeElement.toProto())
        .setCopyright(copyrightElement.toProto())
        .addAllStructure(structure.map { it.toProto() })
        .addAllImport(import.map { it.toProto() })
        .addAllGroup(group.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureMap.StructureMapStructureComponent.toProto():
    StructureMap.Structure {
    val protoValue =
      StructureMap.Structure.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .setMode(
          StructureMap.Structure.ModeCode.newBuilder()
            .setValue(
              StructureMapModelModeCode.Value.valueOf(
                mode
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setAlias(aliasElement.toProto())
        .setDocumentation(documentationElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureMap.StructureMapGroupComponent.toProto():
    StructureMap.Group {
    val protoValue =
      StructureMap.Group.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setExtends(extendsElement.toProto())
        .setTypeMode(
          StructureMap.Group.TypeModeCode.newBuilder()
            .setValue(
              StructureMapGroupTypeModeCode.Value.valueOf(
                typeMode
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setDocumentation(documentationElement.toProto())
        .addAllInput(input.map { it.toProto() })
        .addAllRule(rule.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureMap.StructureMapGroupInputComponent.toProto():
    StructureMap.Group.Input {
    val protoValue =
      StructureMap.Group.Input.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setType(typeElement.toProto())
        .setMode(
          StructureMap.Group.Input.ModeCode.newBuilder()
            .setValue(
              StructureMapInputModeCode.Value.valueOf(
                mode
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setDocumentation(documentationElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleComponent.toProto():
    StructureMap.Group.Rule {
    val protoValue =
      StructureMap.Group.Rule.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .addAllSource(source.map { it.toProto() })
        .addAllTarget(target.map { it.toProto() })
        .addAllDependent(dependent.map { it.toProto() })
        .setDocumentation(documentationElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleSourceComponent.toProto():
    StructureMap.Group.Rule.Source {
    val protoValue =
      StructureMap.Group.Rule.Source.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setContext(contextElement.toProto())
        .setMin(minElement.toProto())
        .setMax(maxElement.toProto())
        .setType(typeElement.toProto())
        .setDefaultValue(defaultValue.structureMapGroupRuleSourceDefaultValueToProto())
        .setElement(elementElement.toProto())
        .setListMode(
          StructureMap.Group.Rule.Source.ListModeCode.newBuilder()
            .setValue(
              StructureMapSourceListModeCode.Value.valueOf(
                listMode
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setVariable(variableElement.toProto())
        .setCondition(conditionElement.toProto())
        .setCheck(checkElement.toProto())
        .setLogMessage(logMessageElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleTargetComponent.toProto():
    StructureMap.Group.Rule.Target {
    val protoValue =
      StructureMap.Group.Rule.Target.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setContext(contextElement.toProto())
        .setContextType(
          StructureMap.Group.Rule.Target.ContextTypeCode.newBuilder()
            .setValue(
              StructureMapContextTypeCode.Value.valueOf(
                contextType
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setElement(elementElement.toProto())
        .setVariable(variableElement.toProto())
        .addAllListMode(
          listMode.map {
            StructureMap.Group.Rule.Target.ListModeCode.newBuilder()
              .setValue(
                StructureMapTargetListModeCode.Value.valueOf(
                  it.value
                    .toCode()
                    .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                    .replace("-", "_")
                    .toUpperCase()
                )
              )
              .build()
          }
        )
        .setListRuleId(listRuleIdElement.toProto())
        .setTransform(
          StructureMap.Group.Rule.Target.TransformCode.newBuilder()
            .setValue(
              StructureMapTransformCode.Value.valueOf(
                transform
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .addAllParameter(parameter.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleTargetParameterComponent.toProto():
    StructureMap.Group.Rule.Target.Parameter {
    val protoValue =
      StructureMap.Group.Rule.Target.Parameter.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setValue(value.structureMapGroupRuleTargetParameterValueToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleDependentComponent.toProto():
    StructureMap.Group.Rule.Dependent {
    val protoValue =
      StructureMap.Group.Rule.Dependent.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .addAllVariable(variable.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun StructureMap.Structure.toHapi():
    org.hl7.fhir.r4.model.StructureMap.StructureMapStructureComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureMap.StructureMapStructureComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setMode(
      org.hl7.fhir.r4.model.StructureMap.StructureMapModelMode.valueOf(
        mode
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setAliasElement(alias.toHapi())
    hapiValue.setDocumentationElement(documentation.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun StructureMap.Group.toHapi():
    org.hl7.fhir.r4.model.StructureMap.StructureMapGroupComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureMap.StructureMapGroupComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setExtendsElement(extends.toHapi())
    hapiValue.setTypeMode(
      org.hl7.fhir.r4.model.StructureMap.StructureMapGroupTypeMode.valueOf(
        typeMode
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setDocumentationElement(documentation.toHapi())
    hapiValue.setInput(inputList.map { it.toHapi() })
    hapiValue.setRule(ruleList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun StructureMap.Group.Input.toHapi():
    org.hl7.fhir.r4.model.StructureMap.StructureMapGroupInputComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureMap.StructureMapGroupInputComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTypeElement(type.toHapi())
    hapiValue.setMode(
      org.hl7.fhir.r4.model.StructureMap.StructureMapInputMode.valueOf(
        mode
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setDocumentationElement(documentation.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun StructureMap.Group.Rule.toHapi():
    org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setSource(sourceList.map { it.toHapi() })
    hapiValue.setTarget(targetList.map { it.toHapi() })
    hapiValue.setDependent(dependentList.map { it.toHapi() })
    hapiValue.setDocumentationElement(documentation.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun StructureMap.Group.Rule.Source.toHapi():
    org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleSourceComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleSourceComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setContextElement(context.toHapi())
    hapiValue.setMinElement(min.toHapi())
    hapiValue.setMaxElement(max.toHapi())
    hapiValue.setTypeElement(type.toHapi())
    hapiValue.setDefaultValue(defaultValue.structureMapGroupRuleSourceDefaultValueToHapi())
    hapiValue.setElementElement(element.toHapi())
    hapiValue.setListMode(
      org.hl7.fhir.r4.model.StructureMap.StructureMapSourceListMode.valueOf(
        listMode
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setVariableElement(variable.toHapi())
    hapiValue.setConditionElement(condition.toHapi())
    hapiValue.setCheckElement(check.toHapi())
    hapiValue.setLogMessageElement(logMessage.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun StructureMap.Group.Rule.Target.toHapi():
    org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleTargetComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleTargetComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setContextElement(context.toHapi())
    hapiValue.setContextType(
      org.hl7.fhir.r4.model.StructureMap.StructureMapContextType.valueOf(
        contextType
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setElementElement(element.toHapi())
    hapiValue.setVariableElement(variable.toHapi())
    listModeList.forEach {
      hapiValue.addListMode(
        org.hl7.fhir.r4.model.StructureMap.StructureMapTargetListMode.valueOf(
          it.value
            .name
            .apply {
              if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
              else this
            }
            .replace("_", "")
        )
      )
    }
    hapiValue.setListRuleIdElement(listRuleId.toHapi())
    hapiValue.setTransform(
      org.hl7.fhir.r4.model.StructureMap.StructureMapTransform.valueOf(
        transform
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setParameter(parameterList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun StructureMap.Group.Rule.Target.Parameter.toHapi():
    org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleTargetParameterComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleTargetParameterComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setValue(value.structureMapGroupRuleTargetParameterValueToHapi())
    return hapiValue
  }

  @JvmStatic
  private fun StructureMap.Group.Rule.Dependent.toHapi():
    org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleDependentComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureMap.StructureMapGroupRuleDependentComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setVariable(variableList.map { it.toHapi() })
    return hapiValue
  }
}
