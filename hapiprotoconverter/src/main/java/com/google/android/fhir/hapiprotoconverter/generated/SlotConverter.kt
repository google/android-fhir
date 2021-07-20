package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Slot
import com.google.fhir.r4.core.SlotStatusCode

public object SlotConverter {
  public fun Slot.toHapi(): org.hl7.fhir.r4.model.Slot {
    val hapiValue = org.hl7.fhir.r4.model.Slot()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setServiceCategory(serviceCategoryList.map{it.toHapi()})
    hapiValue.setServiceType(serviceTypeList.map{it.toHapi()})
    hapiValue.setSpecialty(specialtyList.map{it.toHapi()})
    hapiValue.setAppointmentType(appointmentType.toHapi())
    hapiValue.setSchedule(schedule.toHapi())
    hapiValue.setStatus(org.hl7.fhir.r4.model.Slot.SlotStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setStartElement(start.toHapi())
    hapiValue.setEndElement(end.toHapi())
    hapiValue.setOverbookedElement(overbooked.toHapi())
    hapiValue.setCommentElement(comment.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Slot.toProto(): Slot {
    val protoValue = Slot.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllServiceCategory(serviceCategory.map{it.toProto()})
    .addAllServiceType(serviceType.map{it.toProto()})
    .addAllSpecialty(specialty.map{it.toProto()})
    .setAppointmentType(appointmentType.toProto())
    .setSchedule(schedule.toProto())
    .setStatus(Slot.StatusCode.newBuilder().setValue(SlotStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setStart(startElement.toProto())
    .setEnd(endElement.toProto())
    .setOverbooked(overbookedElement.toProto())
    .setComment(commentElement.toProto())
    .build()
    return protoValue
  }
}
