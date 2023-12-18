library OpioidCDSREC10PatientView version '2022.1.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2022.1.0' called Common
include OpioidCDSREC10Common version '2022.1.0' called Rec10Common
include OpioidCDSCommonConfig version '2022.1.0' called Config
include OpioidCDSRoutines version '2022.1.0' called Routines

code "Urine Drug Screening": '310627008' from Common.SNOMED display 'Urine drug screening (procedure)'

/*
**  Recommendation #10
**    When prescribing opioids for subacute or chronic pain, providers should use urine drug
**    testing before starting opioid therapy and consider urine drug testing at
**    least annually to assess for prescribed medications as well as other controlled
**    prescription drugs and illicit drugs (recommendation category: B, evidence type: 4)
**
**  When
**    Provider is prescribing an opioid analgesic with ambulatory misuse potential in the outpatient setting
**    Prescription is for treating subacute or chronic pain.
**    Opioid review is useful for this patient:
**      Patient is 18 or over
**      Patient does not have findings indicating limited life expectancy
**      Patient does not have orders for therapies indicating end of life care
**      Patient is not undergoing active cancer treatment:
**        Patient has had at least 2 encounters within the past year with any diagnosis of cancer
**    Urine drug screening has not been performed in last 12 months
**  Then
**    Recommend urine drug screening
**      Will perform urine screening
**      Not for subacute or chronic pain management, snooze 3 months
**      N/A - see comment, snooze 3 months
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds-r4/PlanDefinition/opioid-cds-10-patient-view

context Patient

define "Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions":
  Common."Active Ambulatory Opioid Rx" AmbulatoryOpioidPrescription
    where Routines."Is Subacute or Chronic Pain Prescription?"(AmbulatoryOpioidPrescription)

define "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential":
  exists ("Chronic Pain Opioid Analgesic with Ambulatory Misuse Potential Prescriptions")

define "Is Perform Drug Screen Recommendation Applicable?":
  "Patient Is Being Prescribed Opioid Analgesic with Ambulatory Misuse Potential"
    and Rec10Common."Inclusion Criteria"
    and not Rec10Common."Exclusion Criteria"

define "Applicable Because of Positive Cocaine or PCP or Opiates":
  Rec10Common."Applicable Because of Positive Cocaine or PCP or Opiates"

define "Urine Drug Screening ProcedureRequest Category":
  Rec10Common."Urine Drug Screening ProcedureRequest Category"

define "Detail":
  Rec10Common."Get Detail" ("Is Perform Drug Screen Recommendation Applicable?")

define "Indicator":
  Rec10Common."Indicator"

define "Summary":
  Rec10Common."Get Summary" ("Is Perform Drug Screen Recommendation Applicable?")

// Service Request - Urine Screening
// Detected Issue - Opioids found in urine screening
// Detected Issue - Illicit drugs found in urine screening

define "Urine Drug Screening Request":
    ServiceRequest {
      //identifier:,
      instantiatesCanonical: { FHIR.canonical { value: 'http://fhir.org/guides/cdc/activitydefinition/urine-screening-request' } },
      status: FHIR.ServiceRequestStatus { value: 'draft' },
      intent: FHIR.ServiceRequestIntent { value: 'proposal' },
      priority: FHIR.ServiceRequestPriority { value: 'routine' },
      code: FHIR.CodeableConcept { coding: { ToCoding("Urine Drug Screening") } },
      subject: FHIR.Reference { reference: FHIR.string { value: 'Patient/' + Patient.id } },
      occurrence: FHIR.Period { start: FHIR.dateTime { value: Today() }, end: FHIR.dateTime { value: Today() + 7 days } },
      authoredOn: FHIR.dateTime { value: Now() },
      reasonCode: { FHIR.CodeableConcept { text: FHIR.string { value: "Detail" } } }
      // doesn't really work, need a relatedArtifact here...
      //reasonCode: { FHIR.CodeableConcept { text: FHIR.string { value: 'https://www.cdc.gov/mmwr/volumes/65/rr/rr6501e1.htm#10_When_prescribing_opioids' } } }
    }

define function ToCoding(code System.Code):
  FHIR.Coding {
    code: FHIR.code { value: code.code },
    system: FHIR.uri { value: code.system },
    version: FHIR.string { value: code.version },
    display: FHIR.string { value: code.display }
  }

