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
import com.google.fhir.r4.core.Practitioner
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

public object PractitionerConverter {
  public fun Practitioner.toHapi(): org.hl7.fhir.r4.model.Practitioner {
    val hapiValue = org.hl7.fhir.r4.model.Practitioner()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setActiveElement(active.toHapi())
    hapiValue.setName(nameList.map{it.toHapi()})
    hapiValue.setTelecom(telecomList.map{it.toHapi()})
    hapiValue.setAddress(addressList.map{it.toHapi()})
    hapiValue.setGender(Enumerations.AdministrativeGender.valueOf(gender.value.name.replace("_","")))
    hapiValue.setBirthDateElement(birthDate.toHapi())
    hapiValue.setPhoto(photoList.map{it.toHapi()})
    hapiValue.setQualification(qualificationList.map{it.toHapi()})
    hapiValue.setCommunication(communicationList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Practitioner.toProto(): Practitioner {
    val protoValue = Practitioner.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setActive(activeElement.toProto())
    .addAllName(name.map{it.toProto()})
    .addAllTelecom(telecom.map{it.toProto()})
    .addAllAddress(address.map{it.toProto()})
    .setGender(Practitioner.GenderCode.newBuilder().setValue(AdministrativeGenderCode.Value.valueOf(gender.toCode().replace("-",
        "_").toUpperCase())).build())
    .setBirthDate(birthDateElement.toProto())
    .addAllPhoto(photo.map{it.toProto()})
    .addAllQualification(qualification.map{it.toProto()})
    .addAllCommunication(communication.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent.toProto():
      Practitioner.Qualification {
    val protoValue = Practitioner.Qualification.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setCode(code.toProto())
    .setPeriod(period.toProto())
    .setIssuer(issuer.toProto())
    .build()
    return protoValue
  }

  public fun Practitioner.Qualification.toHapi():
      org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setIssuer(issuer.toHapi())
    return hapiValue
  }
}
