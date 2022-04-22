library DDCCPass version '0.0.1'

using FHIR version '4.0.0'

include "FHIRHelpers" version '4.0.0' called FHIRHelpers

context Immunization

define "CompletedImmunization":
  exists(GetFinalDose) or exists(GetSingleDose) or ModernaProtocol or PfizerProtocol

define "GetFinalDose":
  [Immunization] I
    where exists(I.protocolApplied)
      and I.protocolApplied.doseNumber.value = I.protocolApplied.seriesDoses.value

define "GetSingleDose":
  [Immunization] I
    where exists(I.protocolApplied)
      and exists(I.protocolApplied.doseNumber.value)
      and not exists(I.protocolApplied.seriesDoses.value)

define "ModernaProtocol":
  Count(GetAllModerna) > 1

define "PfizerProtocol":
  Count(GetAllPfizer) > 1

define "GetAllModerna":
  [Immunization] I
    where I.vaccineCode.coding.code.value contains '207'

define "GetAllPfizer":
  [Immunization] I
    where I.vaccineCode.coding.code.value contains '208'