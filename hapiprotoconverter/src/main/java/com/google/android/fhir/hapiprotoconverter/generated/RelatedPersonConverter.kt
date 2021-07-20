package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.HumanNameConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.HumanNameConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.AdministrativeGenderCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.RelatedPerson
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

public object RelatedPersonConverter {
  public fun RelatedPerson.toHapi(): org.hl7.fhir.r4.model.RelatedPerson {
    val hapiValue = org.hl7.fhir.r4.model.RelatedPerson()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setActiveElement(active.toHapi())
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setRelationship(relationshipList.map{it.toHapi()})
    hapiValue.setName(nameList.map{it.toHapi()})
    hapiValue.setTelecom(telecomList.map{it.toHapi()})
    hapiValue.setGender(Enumerations.AdministrativeGender.valueOf(gender.value.name.replace("_","")))
    hapiValue.setBirthDateElement(birthDate.toHapi())
    hapiValue.setAddress(addressList.map{it.toHapi()})
    hapiValue.setPhoto(photoList.map{it.toHapi()})
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setCommunication(communicationList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.RelatedPerson.toProto(): RelatedPerson {
    val protoValue = RelatedPerson.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setActive(activeElement.toProto())
    .setPatient(patient.toProto())
    .addAllRelationship(relationship.map{it.toProto()})
    .addAllName(name.map{it.toProto()})
    .addAllTelecom(telecom.map{it.toProto()})
    .setGender(RelatedPerson.GenderCode.newBuilder().setValue(AdministrativeGenderCode.Value.valueOf(gender.toCode().replace("-",
        "_").toUpperCase())).build())
    .setBirthDate(birthDateElement.toProto())
    .addAllAddress(address.map{it.toProto()})
    .addAllPhoto(photo.map{it.toProto()})
    .setPeriod(period.toProto())
    .addAllCommunication(communication.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.RelatedPerson.RelatedPersonCommunicationComponent.toProto():
      RelatedPerson.Communication {
    val protoValue = RelatedPerson.Communication.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setLanguage(language.toProto())
    .setPreferred(preferredElement.toProto())
    .build()
    return protoValue
  }

  public fun RelatedPerson.Communication.toHapi():
      org.hl7.fhir.r4.model.RelatedPerson.RelatedPersonCommunicationComponent {
    val hapiValue = org.hl7.fhir.r4.model.RelatedPerson.RelatedPersonCommunicationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setLanguage(language.toHapi())
    hapiValue.setPreferredElement(preferred.toHapi())
    return hapiValue
  }
}
