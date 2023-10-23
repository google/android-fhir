library OpioidCDSCommon version '2022.1.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommonConfig version '2022.1.0' called Config

codesystem "LOINC": 'http://loinc.org'
codesystem "SNOMED": 'http://snomed.info/sct'

// Expression-based


valueset "Opioid analgesics with ambulatory misuse potential": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/opioid-analgesics-with-ambulatory-misuse-potential'
valueset "Extended release opioid with ambulatory misuse potential": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/extended-release-opioid-with-ambulatory-misuse-potential'
valueset "Buprenorphine and methadone medications": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/buprenorphine-and-methadone-medications'
valueset "Non-synthetic opioid medications": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/non-synthetic-opioid-medications'

// Enumerated-compose

valueset "Limited life expectancy conditions": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/limited-life-expectancy-conditions'
valueset "Therapies indicating end of life care": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/therapies-indicating-end-of-life-care'
valueset "Conditions likely terminal for opioid prescribing": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/conditions-likely-terminal-for-opioid-prescribing'
valueset "CDC malignant cancer conditions": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/cdc-malignant-cancer-conditions'
valueset "Oncology specialty designations (NUCC)": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/oncology-specialty-designations'
valueset "Opioid misuse disorders": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/opioid-misuse-disorders'
valueset "Substance misuse behavioral counseling": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/substance-misuse-behavioral-counseling'
valueset "Conditions documenting substance misuse": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/conditions-documenting-substance-misuse'
// Harvested from VSAC - OID: 2.16.840.1.113883.3.464.1003.101.12.1001
// Reviewed with Terminology, 2020-02-05 - Value set name in VSAC is "Office Visit", need to verify suitability


valueset "Office Visit": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/office-visit'
valueset "Opioid counseling procedure": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/opioid-counseling-procedure'
/* Existing sets for first six recs */

valueset "Benzodiazepine medications": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/benzodiazepine-medications'
valueset "Non-opioid drug urine screening": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/non-opioid-drug-urine-screening'
valueset "Naloxone medications": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/naloxone-medications'
valueset "Opioid misuse assessment procedure": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/opioid-misuse-assessment-procedure'
valueset "Opioid drug urine screening": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/opioid-drug-urine-screening'
valueset "Hospice Disposition": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/hospice-disposition' // Harvested from VSAC - OID: 2.16.840.1.113762.1.4.1108.15

valueset "Hospice Finding Codes": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/hospice-finding'
valueset "Hospice Procedure Codes": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/hospice-procedure'
valueset "Observation Category Laboratory": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory'
valueset "Observation Category Procedure": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-procedure'
valueset "Pain treatment plan": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/pain-treatment-plan'
valueset "Pain management procedure": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/pain-management-procedure'
valueset "PDMP review procedure": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/pdmp-review-procedure'
valueset "PDMP data reviewed finding": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/pdmp-data-reviewed-finding'
valueset "Cocaine Urine Tests": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/cocaine-urine-drug-screening-tests'
valueset "PCP Urine Tests": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/phencyclidine-urine-drug-screening-tests'
valueset "Community": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/medicationrequest-category-community'
valueset "Active Condition": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/condition-clinical-status-active'
valueset "Active MedicationRequest": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/medicationrequest-status-active'
/* valueset "US Core Condition Categories": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/condition-categories' */

valueset "Encounter Diagnosis Condition Category": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/condition-encounter-diagnosis-category'
valueset "Problem List Condition Category": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/condition-problem-list-category'
valueset "US Core Health Concern Condition Category": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/condition-us-core-health-concern-category'
valueset "Sickle Cell Diseases": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/sickle-cell-diseases'

// TODO: code "Nonpharmacologic therapy and nonopioid pharmocologic": 'TODO' from "TODO"


context Patient

// TODO: Capture process decisions for long-term opioid use


define "Opioid Other Than Synthetic Ordered In Last 12 Months":
  if Config."Can the implementing EHR support queries for past medications by date range?" then [MedicationRequest: status in "Active MedicationRequest"] MR
    where date from MR.authoredOn 1 year or less on or before Today()
      and MR.medication is Reference
      or ( MR.medication in "Opioid analgesics with ambulatory misuse potential"
          and not ( MR.medication in "Non-synthetic opioid medications" )
      ) 
    else List < FHIR.MedicationRequest > { }

define "Active Ambulatory Opioid Rx":
  if Config."Can the implementing EHR support queries for past medications by date range?" then ( "Get MedicationRequest Medication as Code"("Get Active Ambulatory Medication Requests"([MedicationRequest])) ) Rx
    where date from Rx.authoredOn 2 years or less on or before Today()
      and Rx.medication in "Opioid analgesics with ambulatory misuse potential" 
    else List < FHIR.MedicationRequest > { }

define "Active Ambulatory Benzodiazepine Rx":
  if Config."Can the implementing EHR support queries for past medications by date range?" then ( "Get MedicationRequest Medication as Code"("Get Active Ambulatory Medication Requests"([MedicationRequest])) ) Rx
    where date from Rx.authoredOn 2 years or less on or before Today()
      and Rx.medication in "Benzodiazepine medications" 
    else List < FHIR.MedicationRequest > { }

define "Active Ambulatory Naloxone Rx":
  if Config."Can the implementing EHR support queries for past medications by date range?" then ( "Get MedicationRequest Medication as Code"("Get Active Ambulatory Medication Requests"([MedicationRequest])) ) Rx
    where date from Rx.authoredOn 2 years or less on or before Today()
      and Rx.medication in "Naloxone medications" 
    else List < FHIR.MedicationRequest > { }

define function "Get Active Ambulatory Medication Requests"(value List<MedicationRequest>) returns List<MedicationRequest>:
  value Rx
    where Rx.status.value = 'active'
      and Rx.category in "Community"

define function "Is Opioid Analgesic with Ambulatory Misuse Potential?"(value List<MedicationRequest>):
  ( "Get MedicationRequest Medication as Code"(value) ) Rx
    where Rx.medication in "Opioid analgesics with ambulatory misuse potential"
      and Rx.category in "Community"

define function "Is Benzodiazepine?"(value List<MedicationRequest>):
  ( "Get MedicationRequest Medication as Code"(value) ) Rx
    where Rx.medication in "Benzodiazepine medications"
      and Rx.category in "Community"

define function "Get MedicationRequest Medication as Code"(value List<MedicationRequest>):
  value Rx
    let Med: [Medication: id in ( Last(Split((Rx.medication as FHIR.Reference).reference, '/')) )]
    return MedicationRequest { id: Rx.id, status: Rx.status, intent: Rx.intent, category: Rx.category, medication: if Rx.medication is Reference then First(Med.code) 
      else Rx.medication as CodeableConcept, subject: Rx.subject, authoredOn: Rx.authoredOn, recorder: Rx.recorder, dosageInstruction: Rx.dosageInstruction, dispenseRequest: Rx.dispenseRequest }

define "End of Life Assessment":
    // 1. Conditions indicating end of life or with limited life expectancy
  
  exists ( "Conditions Indicating End of Life or With Limited Life Expectancy" )
    // 2. Admitted/referred/discharged to hospice care
    
    or exists ( "Admitted/Referred/Discharged to Hospice Care" )
    // 3. Medications indicating end of life
    /* or exists (
      "Medications Indicating End of Life"
    ) */



define "US Core-Categorized Conditions":
  [Condition: category in "Encounter Diagnosis Condition Category"]
    union [Condition: category in "Problem List Condition Category"]
    union [Condition: category in "US Core Health Concern Condition Category"]

define "Conditions Indicating End of Life or With Limited Life Expectancy":
  ( "US Core-Categorized Conditions" C
      where C.code in "Conditions likely terminal for opioid prescribing"
        and C.clinicalStatus in "Active Condition"
  )
    union ( "US Core-Categorized Conditions" C
        where C.code in "Limited life expectancy conditions"
          and C.clinicalStatus in "Active Condition"
    )

define "Admitted/Referred/Discharged to Hospice Care":
  ( [Procedure: code in "Hospice Procedure Codes"] P
      where P.status.value in { 'in-progress', 'completed' }
  )
    union ( "Service Requests" SR
        where SR.code in "Hospice Procedure Codes"
          and SR.status.value in { 'active', 'completed' }
    )
    union ( if ( Config."Hospice Findings Exclusion Enabled" ) then [Observation: code in "Hospice Finding Codes"] O
        where not ( O.status.value in { 'unknown', 'entered-in-error', 'cancelled' } ) 
        else { }
    )
    union ( [Encounter] E
        where date from E.period.start 1 year or less on or before Today()
          and ( if E.hospitalization.dischargeDisposition.coding is null
              or not exists ( E.hospitalization.dischargeDisposition.coding ) then false 
              else E.hospitalization.dischargeDisposition in "Hospice Disposition"
          )
          and E.status.value in { 'planned', 'arrived', 'in-progress', 'finished', 'onleave' }
    )

/*

define "Medications Indicating End of Life":
  (
    [MedicationAdministration: "End Of Life Opioids"] MA
      where MA.status.value in { 'in-progress', 'on-hold', 'completed' }
  )
  union
  (
    [MedicationDispense: "End Of Life Opioids"] MD
      where MD.status.value in { 'preparation', 'in-progress', 'on-hold', 'completed' }
  )
  union
  (
    [MedicationRequest: "End Of Life Opioids"] MR
      where MR.status.value in { 'active', 'completed' }
  )
  union
  (
    [MedicationStatement: "End Of Life Opioids"] MS
      where MS.status.value in { 'active', 'completed', 'intended' }
  )

*/


define "Previous 10 Days Interval":
  Interval[Today() - 10 days, Today()]

define "First Month":
  Interval[Today() - 3 months, Today() - 2 months]

define "Second Month":
  Interval[Today() - 2 months, Today() - 1 months]

define "Third Month":
  Interval[Today() - 1 months, Today()]

define "Prescribed Opioids for 21 or more of 30 Days for each of the past 3 Months":
  "Days on Opioids during Period"("First Month") >= 21
    and "Days on Opioids during Period"("Second Month") >= 21
    and "Days on Opioids during Period"("Third Month") >= 21

define function "Prescription Relevant Period"(prescription FHIR.MedicationRequest):
  if ( prescription.authoredOn is not null
      and prescription.dispenseRequest is not null
      and prescription.dispenseRequest.expectedSupplyDuration is not null
  ) then Interval[date from prescription.authoredOn, date from prescription.authoredOn + GetDurationInDays(prescription.dispenseRequest.expectedSupplyDuration)]
    else null

define function "Days on Opioids during Period"(period Interval<Date>):
  Sum((collapse([MedicationRequest: "Opioid analgesics with ambulatory misuse potential"] OpioidPrescription
        return "Prescription Relevant Period"(OpioidPrescription)
          intersect period
    )) OpioidUseInterval
      return days between start of OpioidUseInterval and 
      end of OpioidUseInterval
  )

define function GetDurationInDays(value FHIR.Duration):
  if value is null then null 
    else case
    when value.code.value ~ 'a' then System.Quantity { value: value.value.value * 365.0, unit: 'days' }
    when value.code.value ~ 'mo' then System.Quantity { value: value.value.value * 30.0, unit: 'days' }
    when value.code.value ~ 'wk' then System.Quantity { value: value.value.value * 7.0, unit: 'days' }
    when value.code.value ~ 'd' then System.Quantity { value: value.value.value, unit: 'days' }
    when value.code.value ~ 'h' then System.Quantity { value: value.value.value / 24.0, unit: 'days' }
    when value.code.value ~ 'min' then System.Quantity { value: value.value.value / 60.0 / 24.0, unit: 'days' }
    when value.code.value ~ 's' then System.Quantity { value: value.value.value / 60.0 / 60.0 / 24.0, unit: 'days' }
    when value.code.value ~ 'ms' then System.Quantity { value: value.value.value / 60.0 / 60.0 / 24.0 / 1000.0, unit: 'days' }
    when value.code.value is null then Message(1000, true, 'Undefined', 'Error', 'Duration unit code is null') 
    else Message(1000, true, 'Undefined', 'Error', 'Unsupported duration unit code: ' + value.code.value) end

/*
*  Conversion Functions
*/


define function CodeableConceptsToString(concepts List<FHIR.CodeableConcept>):
  concepts c
    return CodeableConceptToString(c)

define function CodingToString(coding FHIR.Coding):
  if ( coding is null ) then null 
    else 'Code {' & 'code: ' & coding.code & 'system: ' & coding.system & 'version: ' & coding.version & 'display: ' & coding.display & '}'

define function CodeableConceptToString(concept FHIR.CodeableConcept):
  if ( concept is null
      or concept.coding is null
  ) then null 
    else 'CodeableConcept {' & 'Coding: [' & Combine(concept.coding Coding
      return CodingToString(Coding), ','
  ) & ']' & '}'

define function ToCodes(coding List<FHIR.Coding>):
  coding c
    return FHIRHelpers.ToCode ( c )

define function ToRxNormCode(coding List<FHIR.Coding>):
  singleton from ( coding C
      where C.system = 'http://www.nlm.nih.gov/research/umls/rxnorm'
  )

define "Positive Sickle Cell Condition":
  "US Core-Categorized Conditions" C
    where C.code in "Sickle Cell Diseases"
      and C.clinicalStatus in "Active Condition"

define "Service Requests":
  [ServiceRequest] SR
    return SR

define "Patient 18 or Older?":
  Config."Age Less than 18 Years Is Enabled"
    and AgeInYears() >= 18

define "Patient Age Less Than 18":
  if ( Config."Age Less than 18 Years Is Enabled" ) then AgeInYearsAt(Today()) < 18 
    else false