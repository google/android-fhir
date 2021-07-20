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
import com.google.fhir.r4.core.List
import com.google.fhir.r4.core.ListModeCode
import com.google.fhir.r4.core.ListStatusCode
import com.google.fhir.r4.core.String

public object ListConverter {
  public fun List.toHapi(): org.hl7.fhir.r4.model.List {
    val hapiValue = org.hl7.fhir.r4.model.List()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.List.ListStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setMode(org.hl7.fhir.r4.model.List.ListMode.valueOf(mode.value.name.replace("_","")))
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setSource(source.toHapi())
    hapiValue.setOrderedBy(orderedBy.toHapi())
    hapiValue.setNote(noteList.map{it.toHapi()})
    hapiValue.setEntry(entryList.map{it.toHapi()})
    hapiValue.setEmptyReason(emptyReason.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.List.toProto(): List {
    val protoValue = List.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(List.StatusCode.newBuilder().setValue(ListStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setMode(List.ModeCode.newBuilder().setValue(ListModeCode.Value.valueOf(mode.toCode().replace("-",
        "_").toUpperCase())).build())
    .setTitle(titleElement.toProto())
    .setCode(code.toProto())
    .setSubject(subject.toProto())
    .setEncounter(encounter.toProto())
    .setDate(dateElement.toProto())
    .setSource(source.toProto())
    .setOrderedBy(orderedBy.toProto())
    .addAllNote(note.map{it.toProto()})
    .addAllEntry(entry.map{it.toProto()})
    .setEmptyReason(emptyReason.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.List.ListEntryComponent.toProto(): List.Entry {
    val protoValue = List.Entry.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setFlag(flag.toProto())
    .setDeleted(deletedElement.toProto())
    .setDate(dateElement.toProto())
    .setItem(item.toProto())
    .build()
    return protoValue
  }

  public fun List.Entry.toHapi(): org.hl7.fhir.r4.model.List.ListEntryComponent {
    val hapiValue = org.hl7.fhir.r4.model.List.ListEntryComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setFlag(flag.toHapi())
    hapiValue.setDeletedElement(deleted.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setItem(item.toHapi())
    return hapiValue
  }
}
