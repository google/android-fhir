package com.google.android.fhir.hapiprotoconverter.generated

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
import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.HumanName
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Identifier
import com.google.fhir.r4.core.Instant
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Markdown
import com.google.fhir.r4.core.Meta
import com.google.fhir.r4.core.Money
import com.google.fhir.r4.core.Oid
import com.google.fhir.r4.core.ParameterDefinition
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

private fun Extension.ValueX.extensionValueToHapi(): Type {
  if (this.getBase64Binary() != Base64Binary.newBuilder().defaultInstanceForType ) {
    return (this.getBase64Binary()).toHapi()
  }
  if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
    return (this.getBoolean()).toHapi()
  }
  if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType ) {
    return (this.getCanonical()).toHapi()
  }
  if (this.getCode() != Code.newBuilder().defaultInstanceForType ) {
    return (this.getCode()).toHapi()
  }
  if (this.getDate() != Date.newBuilder().defaultInstanceForType ) {
    return (this.getDate()).toHapi()
  }
  if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
    return (this.getDateTime()).toHapi()
  }
  if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType ) {
    return (this.getDecimal()).toHapi()
  }
  if (this.getId() != Id.newBuilder().defaultInstanceForType ) {
    return (this.getId()).toHapi()
  }
  if (this.getInstant() != Instant.newBuilder().defaultInstanceForType ) {
    return (this.getInstant()).toHapi()
  }
  if (this.getInteger() != Integer.newBuilder().defaultInstanceForType ) {
    return (this.getInteger()).toHapi()
  }
  if (this.getMarkdown() != Markdown.newBuilder().defaultInstanceForType ) {
    return (this.getMarkdown()).toHapi()
  }
  if (this.getOid() != Oid.newBuilder().defaultInstanceForType ) {
    return (this.getOid()).toHapi()
  }
  if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType ) {
    return (this.getPositiveInt()).toHapi()
  }
  if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
    return (this.getStringValue()).toHapi()
  }
  if (this.getTime() != Time.newBuilder().defaultInstanceForType ) {
    return (this.getTime()).toHapi()
  }
  if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType ) {
    return (this.getUnsignedInt()).toHapi()
  }
  if (this.getUri() != Uri.newBuilder().defaultInstanceForType ) {
    return (this.getUri()).toHapi()
  }
  if (this.getUrl() != Url.newBuilder().defaultInstanceForType ) {
    return (this.getUrl()).toHapi()
  }
  if (this.getUuid() != Uuid.newBuilder().defaultInstanceForType ) {
    return (this.getUuid()).toHapi()
  }
  if (this.getAddress() != Address.newBuilder().defaultInstanceForType ) {
    return (this.getAddress()).toHapi()
  }
  if (this.getAge() != Age.newBuilder().defaultInstanceForType ) {
    return (this.getAge()).toHapi()
  }
  if (this.getAnnotation() != Annotation.newBuilder().defaultInstanceForType ) {
    return (this.getAnnotation()).toHapi()
  }
  if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType ) {
    return (this.getAttachment()).toHapi()
  }
  if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
    return (this.getCodeableConcept()).toHapi()
  }
  if (this.getCoding() != Coding.newBuilder().defaultInstanceForType ) {
    return (this.getCoding()).toHapi()
  }
  if (this.getContactPoint() != ContactPoint.newBuilder().defaultInstanceForType ) {
    return (this.getContactPoint()).toHapi()
  }
  if (this.getCount() != Count.newBuilder().defaultInstanceForType ) {
    return (this.getCount()).toHapi()
  }
  if (this.getDistance() != Distance.newBuilder().defaultInstanceForType ) {
    return (this.getDistance()).toHapi()
  }
  if (this.getDuration() != Duration.newBuilder().defaultInstanceForType ) {
    return (this.getDuration()).toHapi()
  }
  if (this.getHumanName() != HumanName.newBuilder().defaultInstanceForType ) {
    return (this.getHumanName()).toHapi()
  }
  if (this.getIdentifier() != Identifier.newBuilder().defaultInstanceForType ) {
    return (this.getIdentifier()).toHapi()
  }
  if (this.getMoney() != Money.newBuilder().defaultInstanceForType ) {
    return (this.getMoney()).toHapi()
  }
  if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
    return (this.getPeriod()).toHapi()
  }
  if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
    return (this.getQuantity()).toHapi()
  }
  if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
    return (this.getRange()).toHapi()
  }
  if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType ) {
    return (this.getRatio()).toHapi()
  }
  if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
    return (this.getReference()).toHapi()
  }
  if (this.getSampledData() != SampledData.newBuilder().defaultInstanceForType ) {
    return (this.getSampledData()).toHapi()
  }
  if (this.getSignature() != Signature.newBuilder().defaultInstanceForType ) {
    return (this.getSignature()).toHapi()
  }
  if (this.getTiming() != Timing.newBuilder().defaultInstanceForType ) {
    return (this.getTiming()).toHapi()
  }
  if (this.getContactDetail() != ContactDetail.newBuilder().defaultInstanceForType ) {
    return (this.getContactDetail()).toHapi()
  }
  if (this.getContributor() != Contributor.newBuilder().defaultInstanceForType ) {
    return (this.getContributor()).toHapi()
  }
  if (this.getDataRequirement() != DataRequirement.newBuilder().defaultInstanceForType ) {
    return (this.getDataRequirement()).toHapi()
  }
  if (this.getExpression() != Expression.newBuilder().defaultInstanceForType ) {
    return (this.getExpression()).toHapi()
  }
  if (this.getParameterDefinition() != ParameterDefinition.newBuilder().defaultInstanceForType ) {
    return (this.getParameterDefinition()).toHapi()
  }
  if (this.getRelatedArtifact() != RelatedArtifact.newBuilder().defaultInstanceForType ) {
    return (this.getRelatedArtifact()).toHapi()
  }
  if (this.getTriggerDefinition() != TriggerDefinition.newBuilder().defaultInstanceForType ) {
    return (this.getTriggerDefinition()).toHapi()
  }
  if (this.getUsageContext() != UsageContext.newBuilder().defaultInstanceForType ) {
    return (this.getUsageContext()).toHapi()
  }
  if (this.getDosage() != Dosage.newBuilder().defaultInstanceForType ) {
    return (this.getDosage()).toHapi()
  }
  //if (this.getMeta() != Meta.newBuilder().defaultInstanceForType ) {
    //return (this.getMeta()).toHapi()
  //}
  throw IllegalArgumentException("Invalid Type for Extension.value[x]")
}

private fun Type.extensionValueToProto(): Extension.ValueX {
  val protoValue = Extension.ValueX.newBuilder()
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
  if (this is org.hl7.fhir.r4.model.Meta) {
    //protoValue.setMeta(this.toProto())
  }
  return protoValue.build()
}

public fun Extension.toHapi(): org.hl7.fhir.r4.model.Extension {
  val hapiValue = org.hl7.fhir.r4.model.Extension()
  hapiValue.id = id.value 
  hapiValue.setUrlElement(url.toHapi())
  hapiValue.setValue(value.extensionValueToHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Extension.toProto(): Extension {
  val protoValue = Extension.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .setUrl(urlElement.toProto())
  .setValue(value.extensionValueToProto())
  .build()
  return protoValue
}
