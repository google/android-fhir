package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.CoverageEligibilityRequest
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.EligibilityRequestPurposeCode
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

public object CoverageEligibilityRequestConverter {
  public fun CoverageEligibilityRequest.ServicedX.coverageEligibilityRequestServicedToHapi(): Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType ) {
      return (this.getDate()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("CoverageEligibilityRequest.serviced[x]")
  }

  public fun Type.coverageEligibilityRequestServicedToProto():
      CoverageEligibilityRequest.ServicedX {
    val protoValue = CoverageEligibilityRequest.ServicedX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  public
      fun CoverageEligibilityRequest.Item.Diagnosis.DiagnosisX.coverageEligibilityRequestItemDiagnosisDiagnosisToHapi():
      Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("CoverageEligibilityRequest.item.diagnosis.diagnosis[x]")
  }

  public fun Type.coverageEligibilityRequestItemDiagnosisDiagnosisToProto():
      CoverageEligibilityRequest.Item.Diagnosis.DiagnosisX {
    val protoValue = CoverageEligibilityRequest.Item.Diagnosis.DiagnosisX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun CoverageEligibilityRequest.toHapi(): org.hl7.fhir.r4.model.CoverageEligibilityRequest {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityRequest()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.CoverageEligibilityRequest.EligibilityRequestStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setPriority(priority.toHapi())
    purposeList.map{hapiValue.addPurpose(org.hl7.fhir.r4.model.CoverageEligibilityRequest.EligibilityRequestPurpose.valueOf(it.value.name.replace("_","")))}
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setServiced(serviced.coverageEligibilityRequestServicedToHapi())
    hapiValue.setCreatedElement(created.toHapi())
    hapiValue.setEnterer(enterer.toHapi())
    hapiValue.setProvider(provider.toHapi())
    hapiValue.setInsurer(insurer.toHapi())
    hapiValue.setFacility(facility.toHapi())
    hapiValue.setSupportingInformation(supportingInformationList.map{it.toHapi()})
    hapiValue.setInsurance(insuranceList.map{it.toHapi()})
    hapiValue.setDetails(detailsList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.CoverageEligibilityRequest.toProto():
      CoverageEligibilityRequest {
    val protoValue = CoverageEligibilityRequest.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(CoverageEligibilityRequest.StatusCode.newBuilder().setValue(FinancialResourceStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPriority(priority.toProto())
    .addAllPurpose(purpose.map{CoverageEligibilityRequest.PurposeCode.newBuilder().setValue(EligibilityRequestPurposeCode.Value.valueOf(it.value.toCode().replace("-",
        "_").toUpperCase())).build()})
    .setPatient(patient.toProto())
    .setServiced(serviced.coverageEligibilityRequestServicedToProto())
    .setCreated(createdElement.toProto())
    .setEnterer(enterer.toProto())
    .setProvider(provider.toProto())
    .setInsurer(insurer.toProto())
    .setFacility(facility.toProto())
    .addAllSupportingInformation(supportingInformation.map{it.toProto()})
    .addAllInsurance(insurance.map{it.toProto()})
    .addAllDetails(details.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CoverageEligibilityRequest.SupportingInformationComponent.toProto():
      CoverageEligibilityRequest.SupportingInformation {
    val protoValue = CoverageEligibilityRequest.SupportingInformation.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequence(sequenceElement.toProto())
    .setInformation(information.toProto())
    .setAppliesToAll(appliesToAllElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.CoverageEligibilityRequest.InsuranceComponent.toProto():
      CoverageEligibilityRequest.Insurance {
    val protoValue = CoverageEligibilityRequest.Insurance.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setFocal(focalElement.toProto())
    .setCoverage(coverage.toProto())
    .setBusinessArrangement(businessArrangementElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.CoverageEligibilityRequest.DetailsComponent.toProto():
      CoverageEligibilityRequest.Details {
    val protoValue = CoverageEligibilityRequest.Details.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllSupportingInfoSequence(supportingInfoSequence.map{it.toProto()})
    .setCategory(category.toProto())
    .setProductOrService(productOrService.toProto())
    .addAllModifier(modifier.map{it.toProto()})
    .setProvider(provider.toProto())
    .setQuantity(( quantity as SimpleQuantity ).toProto())
    .setUnitPrice(unitPrice.toProto())
    .setFacility(facility.toProto())
    .addAllDiagnosis(diagnosis.map{it.toProto()})
    .addAllDetail(detail.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.CoverageEligibilityRequest.DiagnosisComponent.toProto():
      CoverageEligibilityRequest.Item.Diagnosis {
    val protoValue = CoverageEligibilityRequest.Item.Diagnosis.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setDiagnosis(diagnosis.coverageEligibilityRequestItemDiagnosisDiagnosisToProto())
    .build()
    return protoValue
  }

  public fun CoverageEligibilityRequest.SupportingInformation.toHapi():
      org.hl7.fhir.r4.model.CoverageEligibilityRequest.SupportingInformationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.CoverageEligibilityRequest.SupportingInformationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setInformation(information.toHapi())
    hapiValue.setAppliesToAllElement(appliesToAll.toHapi())
    return hapiValue
  }

  public fun CoverageEligibilityRequest.Insurance.toHapi():
      org.hl7.fhir.r4.model.CoverageEligibilityRequest.InsuranceComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityRequest.InsuranceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setFocalElement(focal.toHapi())
    hapiValue.setCoverage(coverage.toHapi())
    hapiValue.setBusinessArrangementElement(businessArrangement.toHapi())
    return hapiValue
  }

  public fun CoverageEligibilityRequest.Details.toHapi():
      org.hl7.fhir.r4.model.CoverageEligibilityRequest.DetailsComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityRequest.DetailsComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSupportingInfoSequence(supportingInfoSequenceList.map{it.toHapi()})
    hapiValue.setCategory(category.toHapi())
    hapiValue.setProductOrService(productOrService.toHapi())
    hapiValue.setModifier(modifierList.map{it.toHapi()})
    hapiValue.setProvider(provider.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFacility(facility.toHapi())
    hapiValue.setDiagnosis(diagnosisList.map{it.toHapi()})
    hapiValue.setDetail(detailList.map{it.toHapi()})
    return hapiValue
  }

  public fun CoverageEligibilityRequest.Item.Diagnosis.toHapi():
      org.hl7.fhir.r4.model.CoverageEligibilityRequest.DiagnosisComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityRequest.DiagnosisComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setDiagnosis(diagnosis.coverageEligibilityRequestItemDiagnosisDiagnosisToHapi())
    return hapiValue
  }
}
