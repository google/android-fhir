package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SampledDataConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SampledDataConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Hdlcholesterol
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Instant
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Observation
import com.google.fhir.r4.core.ObservationStatusCode
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.SampledData
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Time
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type

public object HdlcholesterolConverter {
  public fun Observation.EffectiveX.observationEffectiveToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType ) {
      return (this.getTiming()).toHapi()
    }
    if (this.getInstant() != Instant.newBuilder().defaultInstanceForType ) {
      return (this.getInstant()).toHapi()
    }
    throw IllegalArgumentException("Observation.effective[x]")
  }

  public fun Type.observationEffectiveToProto(): Observation.EffectiveX {
    val protoValue = Observation.EffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    if (this is InstantType) {
      protoValue.setInstant(this.toProto())
    }
    return protoValue.build()
  }

  public fun Observation.Component.ValueX.observationComponentValueToHapi(): Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType ) {
      return (this.getInteger()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType ) {
      return (this.getRatio()).toHapi()
    }
    if (this.getSampledData() != SampledData.newBuilder().defaultInstanceForType ) {
      return (this.getSampledData()).toHapi()
    }
    if (this.getTime() != Time.newBuilder().defaultInstanceForType ) {
      return (this.getTime()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Observation.component.value[x]")
  }

  public fun Type.observationComponentValueToProto(): Observation.Component.ValueX {
    val protoValue = Observation.Component.ValueX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
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
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
      protoValue.setSampledData(this.toProto())
    }
    if (this is TimeType) {
      protoValue.setTime(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  public fun Hdlcholesterol.toHapi(): org.hl7.fhir.r4.model.Hdlcholesterol {
    val hapiValue = org.hl7.fhir.r4.model.Hdlcholesterol()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setBasedOn(basedOnList.map{it.toHapi()})
    hapiValue.setPartOf(partOfList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Observation.ObservationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setCategory(categoryList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setFocus(focusList.map{it.toHapi()})
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setEffective(effective.observationEffectiveToHapi())
    hapiValue.setIssuedElement(issued.toHapi())
    hapiValue.setPerformer(performerList.map{it.toHapi()})
    hapiValue.setValue[x](value[x].toHapi())
    hapiValue.setValue[x]:valueQuantity(value[x]:valueQuantity.toHapi())
    hapiValue.setDataAbsentReason(dataAbsentReason.toHapi())
    hapiValue.setInterpretation(interpretation.toHapi())
    hapiValue.setNote(noteList.map{it.toHapi()})
    hapiValue.setBodySite(bodySite.toHapi())
    hapiValue.setMethod(method.toHapi())
    hapiValue.setSpecimen(specimen.toHapi())
    hapiValue.setDevice(device.toHapi())
    hapiValue.setReferenceRange(referenceRange.toHapi())
    hapiValue.setComponent(componentList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Hdlcholesterol.toProto(): Hdlcholesterol {
    val protoValue = Hdlcholesterol.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllBasedOn(basedOn.map{it.toProto()})
    .addAllPartOf(partOf.map{it.toProto()})
    .setStatus(Hdlcholesterol.StatusCode.newBuilder().setValue(ObservationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllCategory(category.map{it.toProto()})
    .setCode(code.toProto())
    .setSubject(subject.toProto())
    .addAllFocus(focus.map{it.toProto()})
    .setEncounter(encounter.toProto())
    .setEffective(effective.observationEffectiveToProto())
    .setIssued(issuedElement.toProto())
    .addAllPerformer(performer.map{it.toProto()})
    .setValue[x](value[x].toProto())
    .setValue[x]:valueQuantity(value[x]:valueQuantity.toProto())
    .setDataAbsentReason(dataAbsentReason.toProto())
    .setInterpretation(interpretation.toProto())
    .addAllNote(note.map{it.toProto()})
    .setBodySite(bodySite.toProto())
    .setMethod(method.toProto())
    .setSpecimen(specimen.toProto())
    .setDevice(device.toProto())
    .setReferenceRange(referenceRange.toProto())
    .addAllComponent(component.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Observation.ObservationReferenceRangeComponent.toProto():
      Observation.ReferenceRange {
    val protoValue = Observation.ReferenceRange.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setLow(( low as SimpleQuantity ).toProto())
    .setText(textElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Observation.ObservationComponentComponent.toProto():
      Observation.Component {
    val protoValue = Observation.Component.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(code.toProto())
    .setValue(value.observationComponentValueToProto())
    .setDataAbsentReason(dataAbsentReason.toProto())
    .addAllInterpretation(interpretation.map{it.toProto()})
    .addAllReferenceRange(referenceRange.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun Observation.ReferenceRange.toHapi():
      org.hl7.fhir.r4.model.Observation.ObservationReferenceRangeComponent {
    val hapiValue = org.hl7.fhir.r4.model.Observation.ObservationReferenceRangeComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setLow(low.toHapi())
    hapiValue.setTextElement(text.toHapi())
    return hapiValue
  }

  public fun Observation.Component.toHapi():
      org.hl7.fhir.r4.model.Observation.ObservationComponentComponent {
    val hapiValue = org.hl7.fhir.r4.model.Observation.ObservationComponentComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setValue(value.observationComponentValueToHapi())
    hapiValue.setDataAbsentReason(dataAbsentReason.toHapi())
    hapiValue.setInterpretation(interpretationList.map{it.toHapi()})
    hapiValue.setReferenceRange(referenceRangeList.map{it.toHapi()})
    return hapiValue
  }
}
