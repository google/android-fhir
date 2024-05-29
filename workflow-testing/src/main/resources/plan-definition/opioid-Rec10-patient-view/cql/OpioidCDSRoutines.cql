library OpioidCDSRoutines version '2022.1.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2022.1.0' called Common
include OpioidCDSCommonConfig version '2022.1.0' called Config

context Patient

/*
**  Routine #1
**  Opioid Review Useful Routine
**
**  Definition              | Answer to Proceed   | Details                                      | Data (Terminology) Requirement
**  ------------------------------------------------------------------------------------------------------------------------------------
**  Age < 18 years?         | No                  | Calculate age from date of birth; exclude    | Date of birth
**                          |                     | patients with age less than 18 years at the  |
**                          |                     | time of the prescription                     |
**  ------------------------------------------------------------------------------------------------------------------------------------
**  Evidence of             | No                  | Look for documented findings consistent      | Sickle cell disease conditions
**  sickle cell disease?    |                     | with those listed in the sickle cell         |
**                          |                     | value set                                    |
**  ------------------------------------------------------------------------------------------------------------------------------------
**  Limited life            | No                  | Look for documented findings consistent      | Limited life expectancy conditions
**  expectancy conditions   |                     | with those listed in the limited life        |
**  present?                |                     | expectancy value set (terminal illness, bad  |
**                          |                     | prognosis, pre-terminal)                     |
**  ------------------------------------------------------------------------------------------------------------------------------------
**  Order for therapies     | No                  | Look for patients with an existing order for | Therapies indicating end of life care
**  indicating end of life  |                     | therapies indicating end of life care        |
**  care in past 90 days?   |                     | written within past 90 days                  |
**  ------------------------------------------------------------------------------------------------------------------------------------
**  Active cancer           | No                  | See sub-routine 3                            | See sub-routine 3
**  treatment?              |                     |                                              |
**  ------------------------------------------------------------------------------------------------------------------------------------
**  Conditions Likely       | No                  | Look for patients with active conditions in  | Conditions likely terminal for opioid
**  Terminal for opioid     |                     | the value set end-of-life-conditions         | prescribing
**  prescribing present?    |                     |                                              |
**  ------------------------------------------------------------------------------------------------------------------------------------
**
*/


define "Palliative Care Lookback Period":
  Interval[Today() - 90 days, Today()]

define "Is Opioid Review Useful?":
  not "Patient Age Less Than 18"
    and not "Condition Positive for Sickle Cell"
    and not "Conditions Indicating End Of Life Present"
    and not "Order For Palliative Care In Past 90 Days"
    and not "Is Active Cancer Treatment?"
    and not "Conditions Including Cancer To Exclude Opioid Management Indicating End-Stage Disease Present"

define "Patient Age Less Than 18":
  if ( Config."Age Less than 18 Years Is Enabled" ) then AgeInYearsAt(Today()) < 18 
    else false

define "Condition Positive for Sickle Cell":
  if ( Config."Sickle Cell Check Enabled" ) then exists ( Common."Positive Sickle Cell Condition" ) 
    else false

define "Conditions Indicating End Of Life Present":
  exists ( Common."US Core-Categorized Conditions" LLEC
      where LLEC.code in Common."Limited life expectancy conditions"
        and LLEC.clinicalStatus in Common."Active Condition"
  )

define "Order For Palliative Care In Past 90 Days":
  exists ( Common."Service Requests" SR
      where SR.status in { 'active', 'completed' }
        and SR.intent in { 'proposal', 'plan,', 'directive', 'order', 'original-order', 'reflex-order', 'filler-order', 'instance-order' }
        and FHIRHelpers.ToDateTime ( SR.authoredOn ) during day of "Palliative Care Lookback Period"
        and SR.code in Common."Therapies indicating end of life care"
  )

define "Conditions Including Cancer To Exclude Opioid Management Indicating End-Stage Disease Present":
  if ( Config."End-Stage Disease Criteria Enabled" ) then exists ( Common."US Core-Categorized Conditions" EOLC
      where EOLC.code in Common."Conditions likely terminal for opioid prescribing"
        and EOLC.clinicalStatus in Common."Active Condition"
  ) 
    else false

// End of Opioid Review Useful Routine

/*
**  Routine #2
**  Opioid Naive Routine
**
**  Definition                    | Answer to Proceed   | Details                                           | Data (Terminology) Requirement
**  ----------------------------------------------------------------------------------------------------------------------------------------
**  Opioid analgesics with        | No                  | Look for an existing prescription (order) for     | Opioid analgesics with
**  ambulatory misuse potential   |                     | opioid that is in the value set for opioid with   | ambulatory misuse potential
**  prescription in past 90 days  |                     | ambulatory abuse potential authored               |
**  excluding the previous 24     |                     | within the past 90 days (excluding previous 24    |
**  hours                         |                     | hours)                                            |
**  ----------------------------------------------------------------------------------------------------------------------------------------
**  Opioid analgesics with        | No                  | Look for evidence of active medication on         | Opioid analgesics with
**  ambulatory misuse potential   |                     | the medication list that is in the value set      | ambulatory misuse potential
**  reported in past 90 days      |                     | for opioid with ambulatory care abuse             |
**  excluding previous 24 hours*  |                     | potential                                         |
**  ----------------------------------------------------------------------------------------------------------------------------------------
**  Opioid analgesics with        | No                  | Look for evidence of a dispensing event for       | Opioid analgesics with
**  ambulatory misuse potential   |                     | medication that is in the value set for opioid    | ambulatory misuse potential
**  dispensing event in past 90   |                     | with ambulatory use potential occurring           |
**  days excluding previous 24    |                     | within the past 90 days (excluding previous 24    |
**  hours                         |                     | hours)                                            |
**  ----------------------------------------------------------------------------------------------------------------------------------------
**  *Future consideration: Current algorithm addresses only orders (prescriptions).
**
**  Note – orders use RxNorm, but medication lists and dispensed medication will
**  require an NDC value set and/or local mapping of NDC to RxNorm to enable this element
**
*/



define "Prescription/Dispense Inclusion Period":
  Interval[Now() - 90 days, Now() - 24 hours]

define "Report Inclusion Period":
  Interval[Now() - 90 days, Now() - 24 hours )

// NOTE: MedicationStatement-dependent logic comment out due to bug in Publisher not supporting MedicationStatement in AllTypes.


define "Is Opioid Naive?":
  not ( "Has Opioid RX with Ambulatory Abuse Potential In Past 90 Days" )
    /* and not ("Has Report of Opioid with Ambulatory Care Abuse Potential Reported in Past 90 Days") */
    
    and not ( "Has Opioid Dispense with Ambulatory Abuse Potential In Past 90 Days" )

define "Opioid RX with Ambulatory Abuse Potential In Past 90 Days":
  Common."Active Ambulatory Opioid Rx" Rx
    where Rx.authoredOn during day of "Prescription/Dispense Inclusion Period"

define "Has Opioid RX with Ambulatory Abuse Potential In Past 90 Days":
  if ( Config."Opioid Naive Prescription Condition Is Enabled" ) then exists ( "Opioid RX with Ambulatory Abuse Potential In Past 90 Days" ) 
    else false

// NOTE: 2020-02-05: Review direction of MedicationStatement resource (US-Core focusing on MedicationRequest i/o Statement)
// NOTE: 2020-04-21: MedicationStatement a) Shouldn't be use since guidance/direction from USCore is targeting MedicationUse in R5, and b) Breaks in the publisher
/* define "Reports of Opioid with Ambulatory Care Abuse Potential Reported in Past 90 Days":
    [MedicationStatement: Common."Opioid analgesics with ambulatory misuse potential"] Statement
        where Statement.status in {'active', 'completed'}
            and Statement.effective during day of "Report Inclusion Period" */

/* define "Has Report of Opioid with Ambulatory Care Abuse Potential Reported in Past 90 Days":
    Config."Opioid Naive Report Condition Is Enabled"
        and exists ("Reports of Opioid with Ambulatory Care Abuse Potential Reported in Past 90 Days") */





define "Opioid Dispense with Ambulatory Abuse Potential In Past 90 Days":
  [MedicationDispense: Common."Opioid analgesics with ambulatory misuse potential"] OpioidDispense
    where OpioidDispense.whenHandedOver during day of "Prescription/Dispense Inclusion Period"
      and OpioidDispense.status = 'completed'

define "Has Opioid Dispense with Ambulatory Abuse Potential In Past 90 Days":
  if ( Config."Opioid Naive Prescription Condition Is Enabled" ) then exists ( "Opioid Dispense with Ambulatory Abuse Potential In Past 90 Days" ) 
    else false

// End of Opioid Naive Routine

/*
**  Routine #3
**  Active Cancer Treatment Routine
**
**  Definition                    | Answer to Proceed   | Details                                    | Data (Terminology) Requirement
**  ---------------------------------------------------------------------------------------------------------------------------------
**  Two office visits within the  | No                  | Look for a minimum of two distinct         | Office Visit
**  past 12 months                |                     | encounters within 12 months of the date    |
**                                |                     | of the current visit for which each of the |
**                                |                     | following is true:                         |
**                                |                     |   - the encounter diagnosis (primary or    |
**                                |                     |     secondary or co-morbidity diagnosis)   |
**                                |                     |     is listed in the CDC Malignant Cancer  |
**                                |                     |     Conditions value set                   |
**  ---------------------------------------------------------------------------------------------------------------------------------
**  Office visits with an         | No                  | The encounter is performed by an           | Oncology specialty
**  oncology specialist           |                     | oncologist as defined in the oncology      | designations (NUCC)
**  present                       |                     | specialty designations using the           |
**                                |                     | National Uniform Claim Committee           |
**                                |                     | (NUCC) classifications                     |
**  ---------------------------------------------------------------------------------------------------------------------------------
**  Office visits including CDC   | No                  | The encounter diagnosis (primary or        | CDC malignant cancer
**  malignant cancer              |                     | secondary or co-morbidity diagnosis)       | conditions
**  condition                     |                     | is listed in the CDC Malignant Cancer      |
**                                |                     | Conditions value set                       |
**  ----------------------------------------------------------------------------------------------------------------------------------
**
*/



define "Encounter Period":
  Interval[Now() - 12 months, Now() )

define "Is Active Cancer Treatment?":
  if ( Config."Active Cancer Treatment Encounters Condition Is Enabled" ) then "Has Two or More Encounters with Cancer Diagnosis During Encounter Period" 
    else false

define "Active Cancer Diagnoses":
  Common."US Core-Categorized Conditions" C
    where C.code in Common."CDC malignant cancer conditions"

define "Oncologist Practitioner Roles":
  [PractitionerRole: specialty in Common."Oncology specialty designations (NUCC)"] PractitionerRole

define "Encounters with Cancer Diagnosis During Encounter Period":
  [Encounter: Common."Office Visit"] Encounter
    where date from Encounter.period."start".value in day of "Encounter Period"
      and ( exists ( Encounter.diagnosis EncounterDiagnosis
            with "Active Cancer Diagnoses" CancerDiagnosis
              such that EndsWith(EncounterDiagnosis.condition.reference, CancerDiagnosis.id)
        )
          or exists ( Encounter.participant EncounterParticipant
              where EncounterParticipant.type in Common."Oncology specialty designations (NUCC)"
                or ( if ( Config."Active Cancer Treatment PractitionerRole Specialty Is Enabled" ) then exists ( "Oncologist Practitioner Roles" PractitionerRole
                      where EndsWith(EncounterParticipant.individual.reference, PractitionerRole.id)
                  ) 
                    else false
                )
          )
      )

define "Number of Enounters with Cancer Diagnosis During Encounter Period":
  Count("Encounters with Cancer Diagnosis During Encounter Period")

define "Has Two or More Encounters with Cancer Diagnosis During Encounter Period":
  "Number of Enounters with Cancer Diagnosis During Encounter Period" >= 2

// End of Active Cancer Treatment Routine

/*
**  Routine #4
**  For Subacute or Chronic Pain Routine
**
**  Definition                  | Answer to Proceed   | Details                                        | Data (Terminology) Requirement
**  -----------------------------------------------------------------------------------------------------------------------------------
**  Order for opioid analgesic  | Yes                 | Order for opioid analgesics with ambulatory    | Opioid analgesics with
**  with expected supply        |                     | misuse potential with a supply duration of >=  | ambulatory misuse potential
**  duration >= 28 days         |                     | 28 days                                        |
**                              |                     |                                                |
**                              |                     | - Subacute definition = order for opioid       |
**                              |                     |   analgesics with ambulatory misuse            |
**                              |                     |   potential with a supply duration of one to   |
**                              |                     |   two months.                                  |
**                              |                     | - Chronic pain definition = order for opioid   |
**                              |                     |   analgesics with ambulatory misuse            |
**                              |                     |   potential with a supply duration of >= two   |
**                              |                     |   months.                                      |
**  -----------------------------------------------------------------------------------------------------------------------------------
**
*/



define function "Is Subacute or Chronic Pain Prescription?"(prescription FHIR.MedicationRequest):
  ( prescription.dispenseRequest is not null
      and prescription.dispenseRequest.expectedSupplyDuration is not null
      and ( Common.GetDurationInDays ( prescription.dispenseRequest.expectedSupplyDuration ) between 30 days and 60 days
          or Common.GetDurationInDays ( prescription.dispenseRequest.expectedSupplyDuration ) >= 60 days
      )
  )

// End of For Subacute or Chronic Pain Routine

/*
**  Routine #5
**  For Acute Pain Routine
**
**  Definition                  | Answer to Proceed   | Details                                        | Data (Terminology) Requirement
**  -----------------------------------------------------------------------------------------------------------------------------------
**  Order for opioid analgesics | Yes                 | Look for an existing prescription (order) for  | Opioid analgesics with
**  with ambulatory misuse      |                     | opioid analgesics with ambulatory abuse        | ambulatory misuse potential
**  potential of duration < 28  |                     | potential of duration < 28 days                |
**  days                        |                     |                                                |
**  -----------------------------------------------------------------------------------------------------------------------------------
**
*/

// should allow DateTime precision, but not working in the Ruler specifically




define function "Is Acute Pain Prescription?"(prescription FHIR.MedicationRequest):
  prescription.dispenseRequest is not null
    and prescription.dispenseRequest.expectedSupplyDuration is not null
    and Common.GetDurationInDays ( prescription.dispenseRequest.expectedSupplyDuration ) < 28 days

// should allow DateTime precision, but not working in the Ruler specifically


define function "Acute Pain Prescription Greater Than 7 Days?"(prescription FHIR.MedicationRequest):
  prescription.dispenseRequest is not null
    and prescription.dispenseRequest.expectedSupplyDuration is not null
    and Common.GetDurationInDays ( prescription.dispenseRequest.expectedSupplyDuration ) > 7 days
    and Common.GetDurationInDays ( prescription.dispenseRequest.expectedSupplyDuration ) < 28 days
// End of For Acute Pain Routine

// TODO: Include logic for dispensing and patient-reported events

