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
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Address
import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.ClaimProcessingCode
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.ExplanationOfBenefit
import com.google.fhir.r4.core.ExplanationOfBenefitStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Money
import com.google.fhir.r4.core.NoteTypeCode
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.UnsignedInt
import com.google.fhir.r4.core.UseCode
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UnsignedIntType

public object ExplanationOfBenefitConverter {
  public
      fun ExplanationOfBenefit.SupportingInfo.TimingX.explanationOfBenefitSupportingInfoTimingToHapi():
      Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType ) {
      return (this.getDate()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ExplanationOfBenefit.supportingInfo.timing[x]")
  }

  public fun Type.explanationOfBenefitSupportingInfoTimingToProto():
      ExplanationOfBenefit.SupportingInfo.TimingX {
    val protoValue = ExplanationOfBenefit.SupportingInfo.TimingX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  public
      fun ExplanationOfBenefit.SupportingInfo.ValueX.explanationOfBenefitSupportingInfoValueToHapi():
      Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType ) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ExplanationOfBenefit.supportingInfo.value[x]")
  }

  public fun Type.explanationOfBenefitSupportingInfoValueToProto():
      ExplanationOfBenefit.SupportingInfo.ValueX {
    val protoValue = ExplanationOfBenefit.SupportingInfo.ValueX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public
      fun ExplanationOfBenefit.Diagnosis.DiagnosisX.explanationOfBenefitDiagnosisDiagnosisToHapi():
      Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ExplanationOfBenefit.diagnosis.diagnosis[x]")
  }

  public fun Type.explanationOfBenefitDiagnosisDiagnosisToProto():
      ExplanationOfBenefit.Diagnosis.DiagnosisX {
    val protoValue = ExplanationOfBenefit.Diagnosis.DiagnosisX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public
      fun ExplanationOfBenefit.Procedure.ProcedureX.explanationOfBenefitProcedureProcedureToHapi():
      Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ExplanationOfBenefit.procedure.procedure[x]")
  }

  public fun Type.explanationOfBenefitProcedureProcedureToProto():
      ExplanationOfBenefit.Procedure.ProcedureX {
    val protoValue = ExplanationOfBenefit.Procedure.ProcedureX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun ExplanationOfBenefit.Accident.LocationX.explanationOfBenefitAccidentLocationToHapi():
      Type {
    if (this.getAddress() != Address.newBuilder().defaultInstanceForType ) {
      return (this.getAddress()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ExplanationOfBenefit.accident.location[x]")
  }

  public fun Type.explanationOfBenefitAccidentLocationToProto():
      ExplanationOfBenefit.Accident.LocationX {
    val protoValue = ExplanationOfBenefit.Accident.LocationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.setAddress(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun ExplanationOfBenefit.Item.ServicedX.explanationOfBenefitItemServicedToHapi(): Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType ) {
      return (this.getDate()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ExplanationOfBenefit.item.serviced[x]")
  }

  public fun Type.explanationOfBenefitItemServicedToProto(): ExplanationOfBenefit.Item.ServicedX {
    val protoValue = ExplanationOfBenefit.Item.ServicedX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  public fun ExplanationOfBenefit.Item.LocationX.explanationOfBenefitItemLocationToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getAddress() != Address.newBuilder().defaultInstanceForType ) {
      return (this.getAddress()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ExplanationOfBenefit.item.location[x]")
  }

  public fun Type.explanationOfBenefitItemLocationToProto(): ExplanationOfBenefit.Item.LocationX {
    val protoValue = ExplanationOfBenefit.Item.LocationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.setAddress(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun ExplanationOfBenefit.AddItem.ServicedX.explanationOfBenefitAddItemServicedToHapi():
      Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType ) {
      return (this.getDate()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ExplanationOfBenefit.addItem.serviced[x]")
  }

  public fun Type.explanationOfBenefitAddItemServicedToProto():
      ExplanationOfBenefit.AddItem.ServicedX {
    val protoValue = ExplanationOfBenefit.AddItem.ServicedX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  public fun ExplanationOfBenefit.AddItem.LocationX.explanationOfBenefitAddItemLocationToHapi():
      Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getAddress() != Address.newBuilder().defaultInstanceForType ) {
      return (this.getAddress()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ExplanationOfBenefit.addItem.location[x]")
  }

  public fun Type.explanationOfBenefitAddItemLocationToProto():
      ExplanationOfBenefit.AddItem.LocationX {
    val protoValue = ExplanationOfBenefit.AddItem.LocationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.setAddress(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public
      fun ExplanationOfBenefit.BenefitBalance.Financial.AllowedX.explanationOfBenefitBenefitBalanceFinancialAllowedToHapi():
      Type {
    if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType ) {
      return (this.getUnsignedInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getMoney() != Money.newBuilder().defaultInstanceForType ) {
      return (this.getMoney()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for ExplanationOfBenefit.benefitBalance.financial.allowed[x]")
  }

  public fun Type.explanationOfBenefitBenefitBalanceFinancialAllowedToProto():
      ExplanationOfBenefit.BenefitBalance.Financial.AllowedX {
    val protoValue = ExplanationOfBenefit.BenefitBalance.Financial.AllowedX.newBuilder()
    if (this is UnsignedIntType) {
      protoValue.setUnsignedInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.setMoney(this.toProto())
    }
    return protoValue.build()
  }

  public
      fun ExplanationOfBenefit.BenefitBalance.Financial.UsedX.explanationOfBenefitBenefitBalanceFinancialUsedToHapi():
      Type {
    if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType ) {
      return (this.getUnsignedInt()).toHapi()
    }
    if (this.getMoney() != Money.newBuilder().defaultInstanceForType ) {
      return (this.getMoney()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for ExplanationOfBenefit.benefitBalance.financial.used[x]")
  }

  public fun Type.explanationOfBenefitBenefitBalanceFinancialUsedToProto():
      ExplanationOfBenefit.BenefitBalance.Financial.UsedX {
    val protoValue = ExplanationOfBenefit.BenefitBalance.Financial.UsedX.newBuilder()
    if (this is UnsignedIntType) {
      protoValue.setUnsignedInt(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.setMoney(this.toProto())
    }
    return protoValue.build()
  }

  public fun ExplanationOfBenefit.toHapi(): org.hl7.fhir.r4.model.ExplanationOfBenefit {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.ExplanationOfBenefit.ExplanationOfBenefitStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setType(type.toHapi())
    hapiValue.setSubType(subType.toHapi())
    hapiValue.setUse(org.hl7.fhir.r4.model.ExplanationOfBenefit.Use.valueOf(use.value.name.replace("_","")))
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setBillablePeriod(billablePeriod.toHapi())
    hapiValue.setCreatedElement(created.toHapi())
    hapiValue.setEnterer(enterer.toHapi())
    hapiValue.setInsurer(insurer.toHapi())
    hapiValue.setProvider(provider.toHapi())
    hapiValue.setPriority(priority.toHapi())
    hapiValue.setFundsReserveRequested(fundsReserveRequested.toHapi())
    hapiValue.setFundsReserve(fundsReserve.toHapi())
    hapiValue.setRelatedClaim(relatedClaimList.map{it.toHapi()})
    hapiValue.setPrescription(prescription.toHapi())
    hapiValue.setOriginalPrescription(originalPrescription.toHapi())
    hapiValue.setPayee(payee.toHapi())
    hapiValue.setReferral(referral.toHapi())
    hapiValue.setFacility(facility.toHapi())
    hapiValue.setClaim(claim.toHapi())
    hapiValue.setClaimResponse(claimResponse.toHapi())
    hapiValue.setOutcome(org.hl7.fhir.r4.model.ExplanationOfBenefit.RemittanceOutcome.valueOf(outcome.value.name.replace("_","")))
    hapiValue.setDispositionElement(disposition.toHapi())
    hapiValue.setPreAuthRef(preAuthRefList.map{it.toHapi()})
    hapiValue.setPreAuthRefPeriod(preAuthRefPeriodList.map{it.toHapi()})
    hapiValue.setCareTeam(careTeamList.map{it.toHapi()})
    hapiValue.setSupportingInformation(supportingInformationList.map{it.toHapi()})
    hapiValue.setDiagnosis(diagnosisList.map{it.toHapi()})
    hapiValue.setProcedure(procedureList.map{it.toHapi()})
    hapiValue.setPrecedenceElement(precedence.toHapi())
    hapiValue.setInsurance(insuranceList.map{it.toHapi()})
    hapiValue.setAccident(accident.toHapi())
    hapiValue.setItem(itemList.map{it.toHapi()})
    hapiValue.setAddedItem(addedItemList.map{it.toHapi()})
    hapiValue.setAdjudication(adjudicationList.map{it.toHapi()})
    hapiValue.setTotal(totalList.map{it.toHapi()})
    hapiValue.setPayment(payment.toHapi())
    hapiValue.setFormCode(formCode.toHapi())
    hapiValue.setForm(form.toHapi())
    hapiValue.setNote(noteList.map{it.toHapi()})
    hapiValue.setBenefitPeriod(benefitPeriod.toHapi())
    hapiValue.setBenefitBalance(benefitBalanceList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.toProto(): ExplanationOfBenefit {
    val protoValue = ExplanationOfBenefit.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(ExplanationOfBenefit.StatusCode.newBuilder().setValue(ExplanationOfBenefitStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setType(type.toProto())
    .setSubType(subType.toProto())
    .setUse(ExplanationOfBenefit.UseCode.newBuilder().setValue(UseCode.Value.valueOf(use.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPatient(patient.toProto())
    .setBillablePeriod(billablePeriod.toProto())
    .setCreated(createdElement.toProto())
    .setEnterer(enterer.toProto())
    .setInsurer(insurer.toProto())
    .setProvider(provider.toProto())
    .setPriority(priority.toProto())
    .setFundsReserveRequested(fundsReserveRequested.toProto())
    .setFundsReserve(fundsReserve.toProto())
    .addAllRelatedClaim(relatedClaim.map{it.toProto()})
    .setPrescription(prescription.toProto())
    .setOriginalPrescription(originalPrescription.toProto())
    .setPayee(payee.toProto())
    .setReferral(referral.toProto())
    .setFacility(facility.toProto())
    .setClaim(claim.toProto())
    .setClaimResponse(claimResponse.toProto())
    .setOutcome(ExplanationOfBenefit.OutcomeCode.newBuilder().setValue(ClaimProcessingCode.Value.valueOf(outcome.toCode().replace("-",
        "_").toUpperCase())).build())
    .setDisposition(dispositionElement.toProto())
    .addAllPreAuthRef(preAuthRef.map{it.toProto()})
    .addAllPreAuthRefPeriod(preAuthRefPeriod.map{it.toProto()})
    .addAllCareTeam(careTeam.map{it.toProto()})
    .addAllSupportingInformation(supportingInformation.map{it.toProto()})
    .addAllDiagnosis(diagnosis.map{it.toProto()})
    .addAllProcedure(procedure.map{it.toProto()})
    .setPrecedence(precedenceElement.toProto())
    .addAllInsurance(insurance.map{it.toProto()})
    .setAccident(accident.toProto())
    .addAllItem(item.map{it.toProto()})
    .addAllAddedItem(addedItem.map{it.toProto()})
    .addAllAdjudication(adjudication.map{it.toProto()})
    .addAllTotal(total.map{it.toProto()})
    .setPayment(payment.toProto())
    .setFormCode(formCode.toProto())
    .setForm(form.toProto())
    .addAllNote(note.map{it.toProto()})
    .setBenefitPeriod(benefitPeriod.toProto())
    .addAllBenefitBalance(benefitBalance.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.RelatedClaimComponent.toProto():
      ExplanationOfBenefit.RelatedClaim {
    val protoValue = ExplanationOfBenefit.RelatedClaim.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setClaim(claim.toProto())
    .setRelationship(relationship.toProto())
    .setReference(reference.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.PayeeComponent.toProto():
      ExplanationOfBenefit.Payee {
    val protoValue = ExplanationOfBenefit.Payee.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setParty(party.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.CareTeamComponent.toProto():
      ExplanationOfBenefit.CareTeam {
    val protoValue = ExplanationOfBenefit.CareTeam.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequence(sequenceElement.toProto())
    .setProvider(provider.toProto())
    .setResponsible(responsibleElement.toProto())
    .setRole(role.toProto())
    .setQualification(qualification.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.SupportingInformationComponent.toProto():
      ExplanationOfBenefit.SupportingInformation {
    val protoValue = ExplanationOfBenefit.SupportingInformation.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequence(sequenceElement.toProto())
    .setCategory(category.toProto())
    .setCode(code.toProto())
    .setTiming(timing.explanationOfBenefitSupportingInfoTimingToProto())
    .setValue(value.explanationOfBenefitSupportingInfoValueToProto())
    .setReason(reason.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.DiagnosisComponent.toProto():
      ExplanationOfBenefit.Diagnosis {
    val protoValue = ExplanationOfBenefit.Diagnosis.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequence(sequenceElement.toProto())
    .setDiagnosis(diagnosis.explanationOfBenefitDiagnosisDiagnosisToProto())
    .addAllType(type.map{it.toProto()})
    .setOnAdmission(onAdmission.toProto())
    .setPackageCode(packageCode.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.ProcedureComponent.toProto():
      ExplanationOfBenefit.Procedure {
    val protoValue = ExplanationOfBenefit.Procedure.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequence(sequenceElement.toProto())
    .addAllType(type.map{it.toProto()})
    .setDate(dateElement.toProto())
    .setProcedure(procedure.explanationOfBenefitProcedureProcedureToProto())
    .addAllUdi(udi.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.InsuranceComponent.toProto():
      ExplanationOfBenefit.Insurance {
    val protoValue = ExplanationOfBenefit.Insurance.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setFocal(focalElement.toProto())
    .setCoverage(coverage.toProto())
    .addAllPreAuthRef(preAuthRef.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.AccidentComponent.toProto():
      ExplanationOfBenefit.Accident {
    val protoValue = ExplanationOfBenefit.Accident.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setDate(dateElement.toProto())
    .setType(type.toProto())
    .setLocation(location.explanationOfBenefitAccidentLocationToProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.ItemComponent.toProto():
      ExplanationOfBenefit.Item {
    val protoValue = ExplanationOfBenefit.Item.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequence(sequenceElement.toProto())
    .addAllCareTeamSequence(careTeamSequence.map{it.toProto()})
    .addAllDiagnosisSequence(diagnosisSequence.map{it.toProto()})
    .addAllProcedureSequence(procedureSequence.map{it.toProto()})
    .addAllInformationSequence(informationSequence.map{it.toProto()})
    .setRevenue(revenue.toProto())
    .setCategory(category.toProto())
    .setProductOrService(productOrService.toProto())
    .addAllModifier(modifier.map{it.toProto()})
    .addAllProgramCode(programCode.map{it.toProto()})
    .setServiced(serviced.explanationOfBenefitItemServicedToProto())
    .setLocation(location.explanationOfBenefitItemLocationToProto())
    .setQuantity(( quantity as SimpleQuantity ).toProto())
    .setUnitPrice(unitPrice.toProto())
    .setFactor(factorElement.toProto())
    .setNet(net.toProto())
    .addAllUdi(udi.map{it.toProto()})
    .setBodySite(bodySite.toProto())
    .addAllSubSite(subSite.map{it.toProto()})
    .addAllEncounter(encounter.map{it.toProto()})
    .addAllNoteNumber(noteNumber.map{it.toProto()})
    .addAllAdjudication(adjudication.map{it.toProto()})
    .addAllDetail(detail.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.AdjudicationComponent.toProto():
      ExplanationOfBenefit.Item.Adjudication {
    val protoValue = ExplanationOfBenefit.Item.Adjudication.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCategory(category.toProto())
    .setReason(reason.toProto())
    .setAmount(amount.toProto())
    .setValue(valueElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.DetailComponent.toProto():
      ExplanationOfBenefit.Item.Detail {
    val protoValue = ExplanationOfBenefit.Item.Detail.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequence(sequenceElement.toProto())
    .setRevenue(revenue.toProto())
    .setCategory(category.toProto())
    .setProductOrService(productOrService.toProto())
    .addAllModifier(modifier.map{it.toProto()})
    .addAllProgramCode(programCode.map{it.toProto()})
    .setQuantity(( quantity as SimpleQuantity ).toProto())
    .setUnitPrice(unitPrice.toProto())
    .setFactor(factorElement.toProto())
    .setNet(net.toProto())
    .addAllUdi(udi.map{it.toProto()})
    .addAllNoteNumber(noteNumber.map{it.toProto()})
    .addAllAdjudication(adjudication.map{it.toProto()})
    .addAllSubDetail(subDetail.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.SubDetailComponent.toProto():
      ExplanationOfBenefit.Item.Detail.SubDetail {
    val protoValue = ExplanationOfBenefit.Item.Detail.SubDetail.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequence(sequenceElement.toProto())
    .setRevenue(revenue.toProto())
    .setCategory(category.toProto())
    .setProductOrService(productOrService.toProto())
    .addAllModifier(modifier.map{it.toProto()})
    .addAllProgramCode(programCode.map{it.toProto()})
    .setQuantity(( quantity as SimpleQuantity ).toProto())
    .setUnitPrice(unitPrice.toProto())
    .setFactor(factorElement.toProto())
    .setNet(net.toProto())
    .addAllUdi(udi.map{it.toProto()})
    .addAllNoteNumber(noteNumber.map{it.toProto()})
    .addAllAdjudication(adjudication.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.AddedItemComponent.toProto():
      ExplanationOfBenefit.AddedItem {
    val protoValue = ExplanationOfBenefit.AddedItem.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllItemSequence(itemSequence.map{it.toProto()})
    .addAllDetailSequence(detailSequence.map{it.toProto()})
    .addAllSubDetailSequence(subDetailSequence.map{it.toProto()})
    .addAllProvider(provider.map{it.toProto()})
    .setProductOrService(productOrService.toProto())
    .addAllModifier(modifier.map{it.toProto()})
    .addAllProgramCode(programCode.map{it.toProto()})
    .setServiced(serviced.explanationOfBenefitAddItemServicedToProto())
    .setLocation(location.explanationOfBenefitAddItemLocationToProto())
    .setQuantity(( quantity as SimpleQuantity ).toProto())
    .setUnitPrice(unitPrice.toProto())
    .setFactor(factorElement.toProto())
    .setNet(net.toProto())
    .setBodySite(bodySite.toProto())
    .addAllSubSite(subSite.map{it.toProto()})
    .addAllNoteNumber(noteNumber.map{it.toProto()})
    .addAllAdjudication(adjudication.map{it.toProto()})
    .addAllAddedItemDetail(addedItemDetail.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.AddedItemDetailComponent.toProto():
      ExplanationOfBenefit.AddItem.AddedItemDetail {
    val protoValue = ExplanationOfBenefit.AddItem.AddedItemDetail.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setProductOrService(productOrService.toProto())
    .addAllModifier(modifier.map{it.toProto()})
    .setQuantity(( quantity as SimpleQuantity ).toProto())
    .setUnitPrice(unitPrice.toProto())
    .setFactor(factorElement.toProto())
    .setNet(net.toProto())
    .addAllNoteNumber(noteNumber.map{it.toProto()})
    .addAllAdjudication(adjudication.map{it.toProto()})
    .addAllAddedItemDetailSubDetail(addedItemDetailSubDetail.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.AddedItemDetailSubDetailComponent.toProto():
      ExplanationOfBenefit.AddItem.Detail.AddedItemDetailSubDetail {
    val protoValue = ExplanationOfBenefit.AddItem.Detail.AddedItemDetailSubDetail.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setProductOrService(productOrService.toProto())
    .addAllModifier(modifier.map{it.toProto()})
    .setQuantity(( quantity as SimpleQuantity ).toProto())
    .setUnitPrice(unitPrice.toProto())
    .setFactor(factorElement.toProto())
    .setNet(net.toProto())
    .addAllNoteNumber(noteNumber.map{it.toProto()})
    .addAllAdjudication(adjudication.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.TotalComponent.toProto():
      ExplanationOfBenefit.Total {
    val protoValue = ExplanationOfBenefit.Total.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCategory(category.toProto())
    .setAmount(amount.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.PaymentComponent.toProto():
      ExplanationOfBenefit.Payment {
    val protoValue = ExplanationOfBenefit.Payment.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setAdjustment(adjustment.toProto())
    .setAdjustmentReason(adjustmentReason.toProto())
    .setDate(dateElement.toProto())
    .setAmount(amount.toProto())
    .setIdentifier(identifier.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.NoteComponent.toProto():
      ExplanationOfBenefit.Note {
    val protoValue = ExplanationOfBenefit.Note.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setNumber(numberElement.toProto())
    .setType(ExplanationOfBenefit.ProcessNote.TypeCode.newBuilder().setValue(NoteTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setText(textElement.toProto())
    .setLanguage(language.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.BenefitBalanceComponent.toProto():
      ExplanationOfBenefit.BenefitBalance {
    val protoValue = ExplanationOfBenefit.BenefitBalance.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCategory(category.toProto())
    .setExcluded(excludedElement.toProto())
    .setName(nameElement.toProto())
    .setDescription(descriptionElement.toProto())
    .setNetwork(network.toProto())
    .setUnit(unit.toProto())
    .setTerm(term.toProto())
    .addAllBenefit(benefit.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ExplanationOfBenefit.BenefitComponent.toProto():
      ExplanationOfBenefit.BenefitBalance.Benefit {
    val protoValue = ExplanationOfBenefit.BenefitBalance.Benefit.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setAllowed(allowed.explanationOfBenefitBenefitBalanceFinancialAllowedToProto())
    .setUsed(used.explanationOfBenefitBenefitBalanceFinancialUsedToProto())
    .build()
    return protoValue
  }

  public fun ExplanationOfBenefit.RelatedClaim.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.RelatedClaimComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.RelatedClaimComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setClaim(claim.toHapi())
    hapiValue.setRelationship(relationship.toHapi())
    hapiValue.setReference(reference.toHapi())
    return hapiValue
  }

  public fun ExplanationOfBenefit.Payee.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.PayeeComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.PayeeComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setParty(party.toHapi())
    return hapiValue
  }

  public fun ExplanationOfBenefit.CareTeam.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.CareTeamComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.CareTeamComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setProvider(provider.toHapi())
    hapiValue.setResponsibleElement(responsible.toHapi())
    hapiValue.setRole(role.toHapi())
    hapiValue.setQualification(qualification.toHapi())
    return hapiValue
  }

  public fun ExplanationOfBenefit.SupportingInformation.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.SupportingInformationComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.SupportingInformationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setCategory(category.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setTiming(timing.explanationOfBenefitSupportingInfoTimingToHapi())
    hapiValue.setValue(value.explanationOfBenefitSupportingInfoValueToHapi())
    hapiValue.setReason(reason.toHapi())
    return hapiValue
  }

  public fun ExplanationOfBenefit.Diagnosis.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.DiagnosisComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.DiagnosisComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setDiagnosis(diagnosis.explanationOfBenefitDiagnosisDiagnosisToHapi())
    hapiValue.setType(typeList.map{it.toHapi()})
    hapiValue.setOnAdmission(onAdmission.toHapi())
    hapiValue.setPackageCode(packageCode.toHapi())
    return hapiValue
  }

  public fun ExplanationOfBenefit.Procedure.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.ProcedureComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.ProcedureComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setType(typeList.map{it.toHapi()})
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setProcedure(procedure.explanationOfBenefitProcedureProcedureToHapi())
    hapiValue.setUdi(udiList.map{it.toHapi()})
    return hapiValue
  }

  public fun ExplanationOfBenefit.Insurance.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.InsuranceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.InsuranceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setFocalElement(focal.toHapi())
    hapiValue.setCoverage(coverage.toHapi())
    hapiValue.setPreAuthRef(preAuthRefList.map{it.toHapi()})
    return hapiValue
  }

  public fun ExplanationOfBenefit.Accident.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.AccidentComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.AccidentComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setLocation(location.explanationOfBenefitAccidentLocationToHapi())
    return hapiValue
  }

  public fun ExplanationOfBenefit.Item.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.ItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.ItemComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setCareTeamSequence(careTeamSequenceList.map{it.toHapi()})
    hapiValue.setDiagnosisSequence(diagnosisSequenceList.map{it.toHapi()})
    hapiValue.setProcedureSequence(procedureSequenceList.map{it.toHapi()})
    hapiValue.setInformationSequence(informationSequenceList.map{it.toHapi()})
    hapiValue.setRevenue(revenue.toHapi())
    hapiValue.setCategory(category.toHapi())
    hapiValue.setProductOrService(productOrService.toHapi())
    hapiValue.setModifier(modifierList.map{it.toHapi()})
    hapiValue.setProgramCode(programCodeList.map{it.toHapi()})
    hapiValue.setServiced(serviced.explanationOfBenefitItemServicedToHapi())
    hapiValue.setLocation(location.explanationOfBenefitItemLocationToHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setNet(net.toHapi())
    hapiValue.setUdi(udiList.map{it.toHapi()})
    hapiValue.setBodySite(bodySite.toHapi())
    hapiValue.setSubSite(subSiteList.map{it.toHapi()})
    hapiValue.setEncounter(encounterList.map{it.toHapi()})
    hapiValue.setNoteNumber(noteNumberList.map{it.toHapi()})
    hapiValue.setAdjudication(adjudicationList.map{it.toHapi()})
    hapiValue.setDetail(detailList.map{it.toHapi()})
    return hapiValue
  }

  public fun ExplanationOfBenefit.Item.Adjudication.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.AdjudicationComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.AdjudicationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCategory(category.toHapi())
    hapiValue.setReason(reason.toHapi())
    hapiValue.setAmount(amount.toHapi())
    hapiValue.setValueElement(value.toHapi())
    return hapiValue
  }

  public fun ExplanationOfBenefit.Item.Detail.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.DetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.DetailComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setRevenue(revenue.toHapi())
    hapiValue.setCategory(category.toHapi())
    hapiValue.setProductOrService(productOrService.toHapi())
    hapiValue.setModifier(modifierList.map{it.toHapi()})
    hapiValue.setProgramCode(programCodeList.map{it.toHapi()})
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setNet(net.toHapi())
    hapiValue.setUdi(udiList.map{it.toHapi()})
    hapiValue.setNoteNumber(noteNumberList.map{it.toHapi()})
    hapiValue.setAdjudication(adjudicationList.map{it.toHapi()})
    hapiValue.setSubDetail(subDetailList.map{it.toHapi()})
    return hapiValue
  }

  public fun ExplanationOfBenefit.Item.Detail.SubDetail.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.SubDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.SubDetailComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setRevenue(revenue.toHapi())
    hapiValue.setCategory(category.toHapi())
    hapiValue.setProductOrService(productOrService.toHapi())
    hapiValue.setModifier(modifierList.map{it.toHapi()})
    hapiValue.setProgramCode(programCodeList.map{it.toHapi()})
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setNet(net.toHapi())
    hapiValue.setUdi(udiList.map{it.toHapi()})
    hapiValue.setNoteNumber(noteNumberList.map{it.toHapi()})
    hapiValue.setAdjudication(adjudicationList.map{it.toHapi()})
    return hapiValue
  }

  public fun ExplanationOfBenefit.AddedItem.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.AddedItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.AddedItemComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setItemSequence(itemSequenceList.map{it.toHapi()})
    hapiValue.setDetailSequence(detailSequenceList.map{it.toHapi()})
    hapiValue.setSubDetailSequence(subDetailSequenceList.map{it.toHapi()})
    hapiValue.setProvider(providerList.map{it.toHapi()})
    hapiValue.setProductOrService(productOrService.toHapi())
    hapiValue.setModifier(modifierList.map{it.toHapi()})
    hapiValue.setProgramCode(programCodeList.map{it.toHapi()})
    hapiValue.setServiced(serviced.explanationOfBenefitAddItemServicedToHapi())
    hapiValue.setLocation(location.explanationOfBenefitAddItemLocationToHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setNet(net.toHapi())
    hapiValue.setBodySite(bodySite.toHapi())
    hapiValue.setSubSite(subSiteList.map{it.toHapi()})
    hapiValue.setNoteNumber(noteNumberList.map{it.toHapi()})
    hapiValue.setAdjudication(adjudicationList.map{it.toHapi()})
    hapiValue.setAddedItemDetail(addedItemDetailList.map{it.toHapi()})
    return hapiValue
  }

  public fun ExplanationOfBenefit.AddItem.AddedItemDetail.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.AddedItemDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.AddedItemDetailComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setProductOrService(productOrService.toHapi())
    hapiValue.setModifier(modifierList.map{it.toHapi()})
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setNet(net.toHapi())
    hapiValue.setNoteNumber(noteNumberList.map{it.toHapi()})
    hapiValue.setAdjudication(adjudicationList.map{it.toHapi()})
    hapiValue.setAddedItemDetailSubDetail(addedItemDetailSubDetailList.map{it.toHapi()})
    return hapiValue
  }

  public fun ExplanationOfBenefit.AddItem.Detail.AddedItemDetailSubDetail.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.AddedItemDetailSubDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.AddedItemDetailSubDetailComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setProductOrService(productOrService.toHapi())
    hapiValue.setModifier(modifierList.map{it.toHapi()})
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setNet(net.toHapi())
    hapiValue.setNoteNumber(noteNumberList.map{it.toHapi()})
    hapiValue.setAdjudication(adjudicationList.map{it.toHapi()})
    return hapiValue
  }

  public fun ExplanationOfBenefit.Total.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.TotalComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.TotalComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCategory(category.toHapi())
    hapiValue.setAmount(amount.toHapi())
    return hapiValue
  }

  public fun ExplanationOfBenefit.Payment.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.PaymentComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.PaymentComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setAdjustment(adjustment.toHapi())
    hapiValue.setAdjustmentReason(adjustmentReason.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setAmount(amount.toHapi())
    hapiValue.setIdentifier(identifier.toHapi())
    return hapiValue
  }

  public fun ExplanationOfBenefit.Note.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.NoteComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.NoteComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNumberElement(number.toHapi())
    hapiValue.setType(Enumerations.NoteType.valueOf(type.value.name.replace("_","")))
    hapiValue.setTextElement(text.toHapi())
    hapiValue.setLanguage(language.toHapi())
    return hapiValue
  }

  public fun ExplanationOfBenefit.BenefitBalance.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.BenefitBalanceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.BenefitBalanceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCategory(category.toHapi())
    hapiValue.setExcludedElement(excluded.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setNetwork(network.toHapi())
    hapiValue.setUnit(unit.toHapi())
    hapiValue.setTerm(term.toHapi())
    hapiValue.setBenefit(benefitList.map{it.toHapi()})
    return hapiValue
  }

  public fun ExplanationOfBenefit.BenefitBalance.Benefit.toHapi():
      org.hl7.fhir.r4.model.ExplanationOfBenefit.BenefitComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExplanationOfBenefit.BenefitComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setAllowed(allowed.explanationOfBenefitBenefitBalanceFinancialAllowedToHapi())
    hapiValue.setUsed(used.explanationOfBenefitBenefitBalanceFinancialUsedToHapi())
    return hapiValue
  }
}
