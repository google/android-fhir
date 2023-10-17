library OutpatientPriorAuthorizationPrepopulation version '1.0.0'

using FHIR version '4.0.1'

// include FHIRCommon version '4.0.1' called FHIRCommon
// include FHIRHelpers version '4.0.1' called FHIRHelpers

// include FHIRCommon version '4.0.1' called FHIRCommon
// include FHIRHelpers version '4.0.1' called FHIRHelpers





include

// include FHIRCommon version '4.0.1' called FHIRCommon
// include FHIRHelpers version '4.0.1' called FHIRHelpers

FHIRHelpers

// include FHIRCommon version '4.0.1' called FHIRCommon
// include FHIRHelpers version '4.0.1' called FHIRHelpers

version

// include FHIRCommon version '4.0.1' called FHIRCommon
// include FHIRHelpers version '4.0.1' called FHIRHelpers

'4.0.001'

// include FHIRCommon version '4.0.1' called FHIRCommon
// include FHIRHelpers version '4.0.1' called FHIRHelpers

called

// include FHIRCommon version '4.0.1' called FHIRCommon
// include FHIRHelpers version '4.0.1' called FHIRHelpers

FHIRHelpers

// include FHIRCommon version '4.0.1' called FHIRCommon
// include FHIRHelpers version '4.0.1' called FHIRHelpers
// valueset "AAN MCI Encounters Outpt and Care Plan": 'https://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.11.1040'

/* parameter EncounterId String */





parameter
// valueset "AAN MCI Encounters Outpt and Care Plan": 'https://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.11.1040'

/* parameter EncounterId String */

ClaimId String default '14703'

context Patient

define "Claim Is Applicable":
  true

define ClaimResource:
  First([Claim] C
      where C.id = ClaimId
  )

define OrganizationFacility:
  First([Organization] O
      where EndsWith(ClaimResource.provider.reference, O.id)
  )

define "FacilityName":
  OrganizationFacility.name.value

define "FacilityNPI":
  ( OrganizationFacility.identifier I
      where I.system = 'http://hl7.org.fhir/sid/us-npi'
  ).value.value

define function FindPractitioner(myCode String, mySequence Integer):
  //If we can't find a Primary practicioner by code, use the sequence as a fallback.
  
  Coalesce(First([Practitioner] P //Practitioner who is on the careteam and contains a code that equals supplied code
        
        where EndsWith(First(ClaimResource.careTeam CT
              where exists(CT.role.coding CTCode
                  where CTCode.code.value = myCode
              )
          ).provider.reference, P.id
        )
    ), First([Practitioner] P //Practitioner who is on the careteam and is of the supplied sequence
        
        where EndsWith(First(ClaimResource.careTeam CT
              where CT.sequence = mySequence
          ).provider.reference, P.id
        )
    )
  )

define PractitionerOperatingPhysician:
  FindPractitioner('primary', 1)

define PractitionerAttendingPhysician:
  FindPractitioner('assist', 2)

// Should probably be seperate, Utiltiy files; but just for practice:

//PATIENT INFO



define OfficialName:
  First(Patient.name name
      where name.use.value = 'official'
  )

define FirstName:
  Patient.name[0]

define BeneficiaryName:
  Coalesce(OfficialName, FirstName)

define "BeneficiaryFirstName":
  BeneficiaryName.given[0].value

define "BeneficiaryFirstNames":
  BeneficiaryName.given

define "BeneficiaryLastName":
  BeneficiaryName.family.value

define "BeneficiaryDOB":
  Patient.birthDate.value

define "BeneficiaryGender":
  Patient.gender.value

define RequestCoverage:
  ClaimResource.insurance

define CoverageResource:
  First([Coverage] coverage
    // pull coverage resource id from the service request insurance extension
      
      where EndsWith(RequestCoverage[0].coverage.reference, coverage.id)
  )

define "BeneficiaryMedicareID":
  CoverageResource.subscriberId.value

// OPERATING PHYSICIAN INFO


define "OperatingPhysicianFirstName":
  PractitionerOperatingPhysician.name.given[0].value

define "OperatingPhysicianLastName":
  PractitionerOperatingPhysician.name.family.value

define "OperatingPhysicianNPI":
  ( PractitionerOperatingPhysician.identifier I
      where I.system = 'http://hl7.org.fhir/sid/us-npi'
  ).value.value

define OperatingPhysicianAddress:
  First(PractitionerOperatingPhysician.address address
      where address.use.value = 'work'
  )

define "OperatingPhysicianAddress1":
  OperatingPhysicianAddress.line[0].value

define "OperatingPhysicianAddress2":
  OperatingPhysicianAddress.line[1].value

define "OperatingPhysicianAddressCity":
  OperatingPhysicianAddress.city.value

define "OperatingPhysicianAddressState":
  OperatingPhysicianAddress.state.value

define "OperatingPhysicianAddressZip":
  OperatingPhysicianAddress.postalCode.value

// Attending PHYSICIAN INFO


define "AttendingPhysicianSame":
  case
    when PractitionerAttendingPhysician is not null then false 
    else true end

define "AttendingPhysicianFirstName":
  PractitionerAttendingPhysician.name.given[0].value

define "AttendingPhysicianLastName":
  PractitionerAttendingPhysician.name.family.value

define "AttendingPhysicianNPI":
  ( PractitionerAttendingPhysician.identifier I
      where I.system = 'http://hl7.org.fhir/sid/us-npi'
  ).value.value

define AttendingPhysicianAddressWork:
  First(PractitionerAttendingPhysician.address address
      where address.use.value = 'work'
  )

define AttendingPhysicianAddressHome:
  First(PractitionerAttendingPhysician.address address
      where address.use.value = 'home'
  )

define AttendingPhysicianAddress:
  Coalesce(AttendingPhysicianAddressWork, AttendingPhysicianAddressHome)

define "AttendingPhysicianAddress1":
  AttendingPhysicianAddress.line[0].value

define "AttendingPhysicianAddress2":
  AttendingPhysicianAddress.line[1].value

define "AttendingPhysicianAddressCity":
  AttendingPhysicianAddress.city.value

define "AttendingPhysicianAddressState":
  AttendingPhysicianAddress.state.value

define "AttendingPhysicianAddressZip":
  AttendingPhysicianAddress.postalCode.value

//CLAIM INFORMATION


define ClaimDiagnosisReferenced:
  First([Condition] C
      where //First condition referenced by the Claim
      exists(ClaimResource.diagnosis.diagnosis Condition
          where EndsWith(Condition.reference, C.id)
      )
  ).code.coding[0].code.value

define ClaimDiagnosisCode:
  ClaimResource.diagnosis.diagnosis.coding[0].code.value //TODO: Check for primary vs. secondary?


define "RequestDetailsPrimaryDiagnosisCode":
  Coalesce(ClaimDiagnosisCode, ClaimDiagnosisReferenced)

//PROCEDURE INFORMATION


define RelevantReferencedProcedures:
  [Procedure] P
    where P.status.value != 'completed'
      and exists ( ClaimResource.procedure Procedure
          where EndsWith(Procedure.procedure.reference, P.id)
      )

define function FindProcedure(proc String):
  exists ( ClaimResource.procedure.procedure.coding P
      where P.code.value = proc
  )
    or exists ( RelevantReferencedProcedures.code.coding coding
        where coding.code.value = proc
    )

define "RequestDetailsProcedureCode64612":
  FindProcedure('64612')

define "RequestDetailsProcedureCodeJ0586":
  FindProcedure('J0586')

define "RequestDetailsProcedureCode64615":
  FindProcedure('64615')

define "RequestDetailsProcedureCode20912":
  FindProcedure('20912')

define "RequestDetailsProcedureCode36478":
  FindProcedure('36478')

define "RequestDetailsProcedureCode22551":
  FindProcedure('22551')