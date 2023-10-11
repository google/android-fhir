library OpioidCDSREC10Common version '2022.1.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OpioidCDSCommon version '2022.1.0' called Common
include OpioidCDSRoutines version '2022.1.0' called Routines
include OpioidCDSCommonConfig version '2022.1.0' called Config

/*
**  Recommendation #10
**    When prescribing opioids for subacute or chronic pain, providers should use urine drug
**    testing before starting opioid therapy and consider urine drug testing at
**    least annually to assess for prescribed medications as well as other controlled
**    prescription drugs and illicit drugs (recommendation category: B, evidence type: 4)
**
**  When
**    Provider is prescribing an opioid analgesic with ambulatory misuse potential in the outpatient setting
**    Prescription is for treating chronic pain.
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

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds/PlanDefinition/opioid-cds-10-patient-view



context Patient

define "Lookback Year":
  Interval[Today() - 12 months - 1 days, Today() - 1 day]

define "Applicable Because of Positive Cocaine or PCP or Opiates":
  if exists ( Common."Opioid Other Than Synthetic Ordered In Last 12 Months" ) then "Has Positive Screening for Cocaine in Last 12 Months"
    or "Has Positive Screening for PCP in Last 12 Months" 
    else "Has Positive Screening for Cocaine in Last 12 Months"
    or "Has Positive Screening for PCP in Last 12 Months"
    or if Config."Opiate Urine Screening Check Enabled" then "Has Positive Screening for Opiates in Last 12 Months" 
      else false

define "Inclusion Criteria":
  Routines."Is Opioid Review Useful?"
    and ( not "Patient had Urine Screening in Last 12 Months" )

define "Exclusion Criteria":
  Common."End of Life Assessment"

define "Patient had Urine Screening in Last 12 Months":
  ( exists ( "Non-opioid Screenings" )
      or exists ( "Cocaine Screenings" )
      or exists ( "PCP Screenings" )
  )
    or if Config."Opiate Urine Screening Check Enabled" then exists ( "Opiate Screenings" ) 
      else false

define "Laboratory Observations":
  [Observation: "category" in Common."Observation Category Laboratory"]

define "Non-opioid Screenings":
  GetRelevantScreenings("Laboratory Observations" LabObservations
      where(LabObservations.code in Common."Non-opioid drug urine screening")
  )

define "Opiate Screenings":
  GetRelevantScreenings("Laboratory Observations" LabObservations
      where(LabObservations.code in Common."Opioid drug urine screening")
  )

define "Cocaine Screenings":
  GetRelevantScreenings("Laboratory Observations" LabObservations
      where(LabObservations.code in Common."Cocaine Urine Tests")
  )

define "PCP Screenings":
  GetRelevantScreenings("Laboratory Observations" LabObservations
      where(LabObservations.code in Common."PCP Urine Tests")
  )

define function "GetRelevantScreenings"(obsList List<Observation>):
  obsList LabObservations
    where date from LabObservations.effective in day of "Lookback Year"
      and not ( LabObservations.status.value in { 'unknown', 'entered-in-error', 'cancelled' } )

// Returns a text representation of a dateTime using the CQL `ToString` function.
// @param d - a FHIR dateTime to get text for
// @returns {System.String} the text representation of the dateTime




define function "DateTimeText"(d FHIR.dateTime):
  ToString(d.value)

define "Positive Opiate Screenings":
  "Opiate Screenings" Opiate
    where StartsWith(Lower(Opiate.value as FHIR.string), 'pos')

define "Negative Opiate Screenings":
  "Opiate Screenings" Opiate
    where StartsWith(Lower(Opiate.value as FHIR.string), 'neg')

define "Positive PCP Screenings":
  "PCP Screenings" PCP
    where StartsWith(Lower(PCP.value as FHIR.string), 'pos')

define "Negative PCP Screenings":
  "PCP Screenings" PCP
    where StartsWith(Lower(PCP.value as FHIR.string), 'neg')

define "Positive Cocaine Screenings":
  "Cocaine Screenings" Cocaine
    where StartsWith(Lower(Cocaine.value as FHIR.string), 'pos')

define "Negative Cocaine Screenings":
  "Cocaine Screenings" Cocaine
    where StartsWith(Lower(Cocaine.value as FHIR.string), 'neg')

/*
  borrowed from CDS4CPM CDS_Connect_Commons_for_FHIRv400
  Should this go into OpioidCDSCommon?
*/


define function "MostRecent"(ObsList List<Observation>):
  Last(ObsList O
      sort by Coalesce((effective as FHIR.dateTime).value,(effective as FHIR.Period)."end".value,(effective as FHIR.Period)."start".value, issued.value)
  )

define "Negative PCP Screenings Count Since Last Positive Screening":
  Count("Negative PCP Screenings" N
      where DateTimeText(N.effective) > DateTimeText("MostRecent"("Positive PCP Screenings").effective)
  )

define "Negative Opiate Screenings Count Since Last Positive Screening":
  Count("Negative Opiate Screenings" N
      where DateTimeText(N.effective) > DateTimeText("MostRecent"("Positive Opiate Screenings").effective)
  )

define "Negative Cocaine Screenings Count Since Last Positive Screening":
  Count("Negative Cocaine Screenings" N
      where DateTimeText(N.effective) > DateTimeText("MostRecent"("Positive Cocaine Screenings").effective)
  )

define "Positive Opiate Dates in Lookback Period":
  "Positive Opiate Screenings" Opiate
    return DateTimeText(Opiate.effective)

define "Positive Cocaine Dates in Lookback Period":
  "Positive Cocaine Screenings" CS
    return DateTimeText(CS.effective)

define "Positive PCP Dates in Lookback Period":
  "Positive PCP Screenings" PS
    return DateTimeText(PS.effective)

define "Has Positive Screening for Cocaine in Last 12 Months":
  exists ( "Cocaine Screenings" CS
      where StartsWith(Lower(CS.value as FHIR.string), 'pos')
  )

define "Has Positive Screening for PCP in Last 12 Months":
  exists ( "PCP Screenings" PCP
      where StartsWith(Lower(PCP.value as FHIR.string), 'pos')
  )

define "Has Positive Screening for Opiates in Last 12 Months":
  if Config."Opiate Urine Screening Check Enabled" then exists ( "Opiate Screenings" Opioid
      where StartsWith(Lower(Opioid.value as FHIR.string), 'pos')
  ) 
    else false

define "Cocaine Detail":
  if "Has Positive Screening for Cocaine in Last 12 Months" then "Cocaine Summary" 
    else null

define "Opiates Detail":
  if "Has Positive Screening for Opiates in Last 12 Months" then "Opiates Summary" 
    else null

define "PCP Detail":
  if "Has Positive Screening for PCP in Last 12 Months" then "PCP Summary" 
    else null

define function "Get Detail"(isScreeningRecommended Boolean):
  if isScreeningRecommended then 'Patients on opioid therapy should have a urine drug test performed every 12 months. For guidance on counseling patients regarding the purpose of urine toxicology test, review the draft CDC 2022 Opioid Prescribing Guideline Recommendation 10 page 140 lines 3441 - 3446.'
    else if "Applicable Because of Positive Cocaine or PCP or Opiates" then "Cocaine Detail" & "Opiates Detail" & "PCP Detail" & '<br/>Note: result may be false positive result or indicate patient is occasional user or addicted to the illicit drug. For guidance on counseling patients regarding the purpose of urine toxicology test, review the draft CDC 2022 Opioid Prescribing Guideline Recommendation 10 page 140 lines 3441 - 3446.'
    else null

define "Indicator":
  'warning'

    /*
        Used only in PlanDefinition
     */


define function "Get Summary"(isScreeningRecommended Boolean):
  if isScreeningRecommended then 'Annual Urine Screening Check' 
    else if "Applicable Because of Positive Cocaine or PCP or Opiates" then if Config."Opiate Urine Screening Check Enabled" then 'Positive Cocaine or PCP or Opiates in Urine Screening' 
    else 'Positive Cocaine or PCP in Urine Screening' 
    else null

define "Urine Drug Screening ProcedureRequest Category":
  FHIR.CodeableConcept { coding: { FHIR.Coding { system: FHIR.uri { value: 'http://terminology.hl7.org/CodeSystem/medicationrequest-category' }, code: FHIR.code { value: 'outpatient' }, display: FHIR.string { value: 'Outpatient' } } } }
  /* singleton from (First(
    [Encounter] E
      where E.period starts same day as Today()
      sort by start of period desc
  ).type) */


define "Cocaine And PCP Summary":
  'Positive for Cocaine AND PCP <br/><br/>' + "Cocaine Summary" + '<br/>' + "PCP Summary"

define "Opiates Summary":
  if not exists Common."Opioid Other Than Synthetic Ordered In Last 12 Months" then if "Negative Opiate Screenings Count Since Last Positive Screening" > 0 then '<br/>*Positive for Opiates: ' + Combine("Positive Opiate Dates in Lookback Period", ', ') + '  (' + ToString("Negative Opiate Screenings Count Since Last Positive Screening") + ' negative since)' 
    else '<br/>*Positive for Opiates: ' + Combine("Positive Opiate Dates in Lookback Period", ', ') 
    else null

define "PCP Summary":
  if "Negative PCP Screenings Count Since Last Positive Screening" > 0 then '<br/>*Positive for PCP: <br/>' + Combine("Positive PCP Dates in Lookback Period", ', ') + '  (' + ToString("Negative PCP Screenings Count Since Last Positive Screening") + ' negative since)' 
    else '<br/>*Positive for PCP: <br/>' + Combine("Positive PCP Dates in Lookback Period", ', ')

define "Cocaine Summary":
  if "Negative Cocaine Screenings Count Since Last Positive Screening" > 0 then '<br/>*Positive for Cocaine: <br/>' + Combine("Positive Cocaine Dates in Lookback Period", ', ') + '  (' + ToString("Negative Cocaine Screenings Count Since Last Positive Screening") + ' negative since)' 
    else '<br/>*Positive for Cocaine: <br/>' + Combine("Positive Cocaine Dates in Lookback Period", ', ')