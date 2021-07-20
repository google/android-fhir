package com.google.android.fhir.hapiprotoconverter.generated

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
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductAuthorization
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object MedicinalProductAuthorizationConverter {
  public
      fun MedicinalProductAuthorization.Procedure.DateX.medicinalProductAuthorizationProcedureDateToHapi():
      Type {
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    throw IllegalArgumentException("MedicinalProductAuthorization.procedure.date[x]")
  }

  public fun Type.medicinalProductAuthorizationProcedureDateToProto():
      MedicinalProductAuthorization.Procedure.DateX {
    val protoValue = MedicinalProductAuthorization.Procedure.DateX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    return protoValue.build()
  }

  public fun MedicinalProductAuthorization.toHapi():
      org.hl7.fhir.r4.model.MedicinalProductAuthorization {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductAuthorization()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setCountry(countryList.map{it.toHapi()})
    hapiValue.setJurisdiction(jurisdictionList.map{it.toHapi()})
    hapiValue.setStatus(status.toHapi())
    hapiValue.setStatusDateElement(statusDate.toHapi())
    hapiValue.setRestoreDateElement(restoreDate.toHapi())
    hapiValue.setValidityPeriod(validityPeriod.toHapi())
    hapiValue.setDataExclusivityPeriod(dataExclusivityPeriod.toHapi())
    hapiValue.setDateOfFirstAuthorizationElement(dateOfFirstAuthorization.toHapi())
    hapiValue.setInternationalBirthDateElement(internationalBirthDate.toHapi())
    hapiValue.setLegalBasis(legalBasis.toHapi())
    hapiValue.setJurisdictionalAuthorization(jurisdictionalAuthorizationList.map{it.toHapi()})
    hapiValue.setHolder(holder.toHapi())
    hapiValue.setRegulator(regulator.toHapi())
    hapiValue.setProcedure(procedure.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MedicinalProductAuthorization.toProto():
      MedicinalProductAuthorization {
    val protoValue = MedicinalProductAuthorization.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setSubject(subject.toProto())
    .addAllCountry(country.map{it.toProto()})
    .addAllJurisdiction(jurisdiction.map{it.toProto()})
    .setStatus(status.toProto())
    .setStatusDate(statusDateElement.toProto())
    .setRestoreDate(restoreDateElement.toProto())
    .setValidityPeriod(validityPeriod.toProto())
    .setDataExclusivityPeriod(dataExclusivityPeriod.toProto())
    .setDateOfFirstAuthorization(dateOfFirstAuthorizationElement.toProto())
    .setInternationalBirthDate(internationalBirthDateElement.toProto())
    .setLegalBasis(legalBasis.toProto())
    .addAllJurisdictionalAuthorization(jurisdictionalAuthorization.map{it.toProto()})
    .setHolder(holder.toProto())
    .setRegulator(regulator.toProto())
    .setProcedure(procedure.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationJurisdictionalAuthorizationComponent.toProto():
      MedicinalProductAuthorization.JurisdictionalAuthorization {
    val protoValue = MedicinalProductAuthorization.JurisdictionalAuthorization.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setCountry(country.toProto())
    .addAllJurisdiction(jurisdiction.map{it.toProto()})
    .setLegalStatusOfSupply(legalStatusOfSupply.toProto())
    .setValidityPeriod(validityPeriod.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationProcedureComponent.toProto():
      MedicinalProductAuthorization.Procedure {
    val protoValue = MedicinalProductAuthorization.Procedure.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setIdentifier(identifier.toProto())
    .setType(type.toProto())
    .setDate(date.medicinalProductAuthorizationProcedureDateToProto())
    .addAllApplication(application.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun MedicinalProductAuthorization.JurisdictionalAuthorization.toHapi():
      org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationJurisdictionalAuthorizationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationJurisdictionalAuthorizationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setCountry(country.toHapi())
    hapiValue.setJurisdiction(jurisdictionList.map{it.toHapi()})
    hapiValue.setLegalStatusOfSupply(legalStatusOfSupply.toHapi())
    hapiValue.setValidityPeriod(validityPeriod.toHapi())
    return hapiValue
  }

  public fun MedicinalProductAuthorization.Procedure.toHapi():
      org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationProcedureComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationProcedureComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setDate(date.medicinalProductAuthorizationProcedureDateToHapi())
    hapiValue.setApplication(applicationList.map{it.toHapi()})
    return hapiValue
  }
}
