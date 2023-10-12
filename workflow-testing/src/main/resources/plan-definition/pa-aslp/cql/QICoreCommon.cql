/*
@author: Bryn Rhodes
@description: This library defines functions to expose extensions defined
in QICore as functions in CQL, as well as common terminology and functions
used in writing CQL with FHIR and QICore profiles.

NOTE: The source for this is the QICoreCommon library published as part of QICore 4.1.1
That library specified these functions as fluent functions, whereas the functions
specified here are non-fluent versions.

@update: Refactored to use FHIRCommon
*/
library QICoreCommon version '1.0.000'

using FHIR version '4.0.1'

include FHIRHelpers version '4.1.000' called FHIRHelpers
include FHIRCommon version '1.1.000' called FHIRCommon

codesystem "USCoreConditionCategory": 'http://hl7.org/fhir/us/core/CodeSystem/condition-category'
// NOTE: Introduced in USCore 5

codesystem "USCoreObservationCategory": 'http://hl7.org/fhir/us/core/CodeSystem/us-core-observation-category'

// Condition Category Codes


code "problem-list-item": 'problem-list-item' from FHIRCommon."ConditionCategoryCodes" display 'Problem List Item'
code "encounter-diagnosis": 'encounter-diagnosis' from FHIRCommon."ConditionCategoryCodes" display 'Encounter Diagnosis'
code "health-concern": 'health-concern' from "USCoreConditionCategory" display 'Health Concern'

// Observation Category Codes

code "social-history": 'social-history' from FHIRCommon."ObservationCategoryCodes" display 'Social History'
code "vital-signs": 'vital-signs' from FHIRCommon."ObservationCategoryCodes" display 'Vital Signs'
code "imaging": 'imaging' from FHIRCommon."ObservationCategoryCodes" display 'Imaging'
code "laboratory": 'laboratory' from FHIRCommon."ObservationCategoryCodes" display 'Laboratory'
code "procedure": 'procedure' from FHIRCommon."ObservationCategoryCodes" display 'Procedure'
code "survey": 'survey' from FHIRCommon."ObservationCategoryCodes" display 'Survey'
code "exam": 'exam' from FHIRCommon."ObservationCategoryCodes" display 'Exam'
code "therapy": 'therapy' from FHIRCommon."ObservationCategoryCodes" display 'Therapy'
code "activity": 'activity' from FHIRCommon."ObservationCategoryCodes" display 'Activity'
// NOTE: Introduced in USCore 5

code "clinical-test": 'clinical-test' from "USCoreObservationCategory" display 'Clinical Test'

context Patient

/* Candidates for FHIRCommon */

/*
@description: returns true if the given condition has the given category
*/



define function HasCategory(condition Condition, category Code):
  exists ( condition.category C
      where C ~ category
  )

/*
@description: returns true if the given condition is a problem list item
*/


define function IsProblemListItem(condition Condition):
  exists ( condition.category C
      where C ~ "problem-list-item"
  )

/*
@description: returns true if the given condition is an encounter diagnosis
*/


define function IsEncounterDiagnosis(condition Condition):
  exists ( condition.category C
      where C ~ "encounter-diagnosis"
  )

/*
@description: returns true if the given condition is a health concern
*/


define function IsHealthConcern(condition Condition):
  exists ( condition.category C
      where C ~ "health-concern"
  )

/*
@description: returns true if the given observation has the given category
*/


define function HasCategory(observation Observation, category Code):
  exists ( observation.category C
      where C ~ category
  )

/*
@description: returns true if the given observation is a social history
*/


define function IsSocialHistory(observation Observation):
  exists ( observation.category C
      where C ~ "social-history"
  )

/*
@description: returns true if the given observation is a vital-sign
*/


define function IsVitalSign(observation Observation):
  exists ( observation.category C
      where C ~ "vital-signs"
  )

/*
@description: returns true if the given observation is an imaging observation
*/


define function IsImaging(observation Observation):
  exists ( observation.category C
      where C ~ "imaging"
  )

/*
@description: returns true if the given observation is a laboratory observation
*/


define function IsLaboratory(observation Observation):
  exists ( observation.category C
      where C ~ "laboratory"
  )

/*
@description: returns true if the given observation is a procedure observation
*/


define function IsProcedure(observation Observation):
  exists ( observation.category C
      where C ~ "procedure"
  )

/*
@description: returns true if the given observation is a survey observation
*/


define function IsSurvey(observation Observation):
  exists ( observation.category C
      where C ~ "survey"
  )

/*
@description: returns true if the given observation is an exam observation
*/


define function IsExam(observation Observation):
  exists ( observation.category C
      where C ~ "exam"
  )

/*
@description: returns true if the given observation is a therapy observation
*/


define function IsTherapy(observation Observation):
  exists ( observation.category C
      where C ~ "therapy"
  )

/*
@description: returns true if the given observation is an activity observation
*/


define function IsActivity(observation Observation):
  exists ( observation.category C
      where C ~ "activity"
  )

/*
@description: returns true if the given observation is a clinical test
@comment: Introduced in USCore 5
*/


define function IsClinicalTest(observation Observation):
  exists ( observation.category C
      where C ~ "clinical-test"
  )

/*
@description: Returns true if the given MedicationRequest has a category of Community
*/


define function IsCommunity(medicationRequest MedicationRequest):
  exists ( medicationRequest.category C
      where C ~ FHIRCommon.Community
  )

/*
@description: Returns true if the given MedicationRequest has a category of Discharge
*/


define function IsDischarge(medicationRequest MedicationRequest):
  exists ( medicationRequest.category C
      where C ~ FHIRCommon.Discharge
  )

/*
@description: Surfaces the [resolutionAge](http://hl7.org/fhir/StructureDefinition/allergyintolerance-resolutionAge)
extension defined in FHIR.
@comment: This function returns the value of the extension as an Age.
@extension: http://hl7.org/fhir/StructureDefinition/allergyintolerance-resolutionAge
*/


define function ResolutionAge(allergyIntolerance AllergyIntolerance):
  FHIRCommon.Extension ( allergyIntolerance, 'http://hl7.org/fhir/StructureDefinition/allergyintolerance-resolutionAge' ).value as Age

/*
@description: Surfaces the [reasonRefuted](http://hl7.org/fhir/StructureDefinition/allergyintolerance-reasonRefuted)
extension defined in FHIR.
@comment: This function returns the value of the extension as a CodeableConcept.
@extension: http://hl7.org/fhir/StructureDefinition/allergyintolerance-reasonRefuted
*/


define function ReasonRefuted(allergyIntolerance AllergyIntolerance):
  FHIRCommon.Extension ( allergyIntolerance, 'http://hl7.org/fhir/StructureDefinition/allergyintolerance-reasonRefuted' ).value as CodeableConcept

/*
@description: Surfaces the [duration](http://hl7.org/fhir/StructureDefinition/allergyintolerance-duration)
extension defined in FHIR.
@comment: This function returns the value of the extension as a Duration.
@extension: http://hl7.org/fhir/StructureDefinition/allergyintolerance-duration
*/


define function Duration(allergyIntolerance BackboneElement):
  FHIRCommon.Extension ( allergyIntolerance, 'http://hl7.org/fhir/StructureDefinition/allergyintolerance-duration' ).value as Duration

/*
@description: Surfaces the [dueTo](http://hl7.org/fhir/StructureDefinition/condition-dueTo)
extension defined in FHIR.
@comment: This function returns the value of the extension as a choice of CodeableConcept or Reference.
@extension: http://hl7.org/fhir/StructureDefinition/condition-dueTo
*/


define function DueTo(condition Condition):
  FHIRCommon.Extension ( condition, 'http://hl7.org/fhir/StructureDefinition/condition-dueTo' ).value as Choice<CodeableConcept, Reference>

/*
@description: Surfaces the [occurredFollowing](http://hl7.org/fhir/StructureDefinition/condition-occurredFollowing)
extension defined in FHIR.
@comment: This function returns the value of the extension as a choice of CodeableConcept or Reference
@extension: http://hl7.org/fhir/StructureDefinition/condition-occurredFollowing
*/


define function OccurredFollowing(condition Condition):
  FHIRCommon.Extension ( condition, 'http://hl7.org/fhir/StructureDefinition/condition-occurredFollowing' ).value as Choice<CodeableConcept, Reference>

/*
@description: Surfaces the [doNotPerform](http://hl7.org/fhir/5.0/StructureDefinition/extension-DeviceRequest.doNotPerform)
modifier extension defined in FHIR.
@comment: This function returns the value of the modifier extension as a boolean
@extension: http://hl7.org/fhir/StructureDefinition/request-doNotPerform
*/


define function DoNotPerform(requestResource DomainResource):
  FHIRCommon.ModifierExtension ( requestResource, 'http://hl7.org/fhir/5.0/StructureDefinition/extension-DeviceRequest.doNotPerform' ).value as boolean

/*
@description: Surfaces the [statusReason](http://hl7.org/fhir/StructureDefinition/request-statusReason)
extension defined in FHIR.
@comment: This function returns the value of the extension as a CodeableConcept
@extension: http://hl7.org/fhir/StructureDefinition/request-statusReason
*/


define function StatusReason(requestResource DomainResource):
  FHIRCommon.Extension ( requestResource, 'http://hl7.org/fhir/StructureDefinition/request-statusReason' ).value as CodeableConcept

/*
@description: Surfaces the [locationPerformed](http://hl7.org/fhir/StructureDefinition/diagnosticReport-locationPerformed)
extension defined in FHIR.
@comment: This function returns the value of the extension as a Reference
@extension: http://hl7.org/fhir/StructureDefinition/diagnosticReport-locationPerformed
*/


define function LocationPerformed(diagnosticReport DiagnosticReport):
  FHIRCommon.Extension ( diagnosticReport, 'http://hl7.org/fhir/StructureDefinition/diagnosticReport-locationPerformed' ).value as Reference

/*
@description: Surfaces the [abatement](http://hl7.org/fhir/StructureDefinition/familymemberhistory-abatement)
extension defined in FHIR.
@comment: This function returns the value of the extension as a choice of date, Age, or boolean
@extension: http://hl7.org/fhir/StructureDefinition/familymemberhistory-abatement
*/


define function Abatement(familyMemberHistory FamilyMemberHistory):
  FHIRCommon.Extension ( familyMemberHistory, 'http://hl7.org/fhir/StructureDefinition/familymemberhistory-abatement' ).value as Choice<date, Age, boolean>

/*
@description: Surfaces the [severity](http://hl7.org/fhir/StructureDefinition/familymemberhistory-severity)
extension defined in FHIR.
@comment: This function returns the value of the extension as a CodeableConcept
@extension: http://hl7.org/fhir/StructureDefinition/familymemberhistory-severity
*/


define function Severity(familyMemberHistory FamilyMemberHistory):
  FHIRCommon.Extension ( familyMemberHistory, 'http://hl7.org/fhir/StructureDefinition/familymemberhistory-severity' ).value as CodeableConcept

/*
@description: Surfaces the [reasonRejected](http://hl7.org/fhir/StructureDefinition/goal-reasonRejected)
extension defined in FHIR.
@comment: This function returns the value of the extension as a CodeableConcept
@extension: http://hl7.org/fhir/StructureDefinition/goal-reasonRejected
*/


define function ReasonRejected(goal Goal):
  FHIRCommon.Extension ( goal, 'http://hl7.org/fhir/StructureDefinition/goal-reasonRejected' ).value as CodeableConcept

/*
@description: Surfaces the [bodyPosition](http://hl7.org/fhir/StructureDefinition/observation-bodyPosition)
extension defined in FHIR.
@comment: This function returns the value of the extension as a CodeableConcept
@extension: http://hl7.org/fhir/StructureDefinition/observation-bodyPosition
*/


define function BodyPosition(observation Observation):
  FHIRCommon.Extension ( observation, 'http://hl7.org/fhir/StructureDefinition/observation-bodyPosition' ).value as CodeableConcept

/*
@description: Surfaces the [delta](http://hl7.org/fhir/StructureDefinition/observation-delta)
extension defined in FHIR.
@comment: This function returns the value of the extension as a CodeableConcept
@extension: http://hl7.org/fhir/StructureDefinition/observation-delta
*/


define function Delta(observation Observation):
  FHIRCommon.Extension ( observation, 'http://hl7.org/fhir/StructureDefinition/observation-delta' ).value as CodeableConcept

/*
@description: Surfaces the [preferred](http://hl7.org/fhir/StructureDefinition/iso21090-preferred) extension defined in USCore.
@comment: This function returns the value of the direct extension as a boolean
@extension: http://hl7.org/fhir/StructureDefinition/iso21090-preferred
*/


define function Preferred(contactPoint ContactPoint):
  FHIRCommon.Extension ( contactPoint, 'http://hl7.org/fhir/StructureDefinition/iso21090-preferred' ).value as boolean

/*
@description: Surfaces the [approachBodyStructure](http://hl7.org/fhir/StructureDefinition/procedure-approachBodyStructure)
extension defined in FHIR.
@comment: This function returns the value of the extension as a Reference
@extension: http://hl7.org/fhir/StructureDefinition/procedure-approachBodyStructure
*/


define function ApproachBodyStructure(procedure Procedure):
  FHIRCommon.Extension ( procedure, 'http://hl7.org/fhir/StructureDefinition/procedure-approachBodyStructure' ).value as Reference

/*
@description: Surfaces the [incisionDateTime](http://hl7.org/fhir/StructureDefinition/procedure-incisionDateTime)
extension defined in FHIR.
@comment: This function returns the value of the extension as a dateTime
@extension: http://hl7.org/fhir/StructureDefinition/procedure-incisionDateTime
*/


define function IncisionDateTime(procedure Procedure):
  FHIRCommon.Extension ( procedure, 'http://hl7.org/fhir/StructureDefinition/procedure-incisionDateTime' ).value as dateTime

/* USCore Extensions */
/*
@description: Surfaces the [ethnicity](http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity)
extension defined in USCore.
@comment: This function returns a tuple with elements for each subextension (ombCategory, detailed, and text).
@extension: http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity
*/



define function Ethnicity(patient Patient):
  patient P
    let ethnicityEx: FHIRCommon.Extension ( P, 'http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity' )
    return {
      ombCategory: FHIRCommon.Extension ( ethnicityEx, 'ombCategory' ).value as Coding,
      detailed: ( FHIRCommon.Extensions ( ethnicityEx, 'detailed' ) ) E
        return E.value as Coding,
      text: FHIRCommon.Extension ( ethnicityEx, 'text' ).value as string
    }

/*
@description: Surfaces the [race](http://hl7.org/fhir/us/core/StructureDefinition/us-core-race) extension defined in USCore.
@comment: This function returns a tuple with elements for each subextension (ombCategory, detailed, and text).
@extension: http://hl7.org/fhir/us/core/StructureDefinition/us-core-race
*/


define function Race(patient Patient):
  patient P
    let raceEx: FHIRCommon.Extension ( P, 'http://hl7.org/fhir/us/core/StructureDefinition/us-core-race' )
    return {
      ombCategory: ( FHIRCommon.Extensions ( raceEx, 'ombCategory' ) ) E
        return E.value as Coding,
      detailed: ( FHIRCommon.Extensions ( raceEx, 'detailed' ) ) E
        return E.value as Coding,
      text: FHIRCommon.Extension ( raceEx, 'text' ).value as string
    }

/*
@description: Surfaces the [birthsex](http://hl7.org/fhir/us/core/StructureDefinition/us-core-birthsex) extension defined in USCore.
@comment: This function returns the value of the birthsex extension as a code
@extension: http://hl7.org/fhir/us/core/StructureDefinition/us-core-birthsex
*/


define function Birthsex(patient Patient):
  FHIRCommon.Extension ( patient, 'http://hl7.org/fhir/us/core/StructureDefinition/us-core-birthsex' ).value as code

/*
@description: Surfaces the [direct](http://hl7.org/fhir/us/core/StructureDefinition/us-core-direct) extension defined in USCore.
@comment: This function returns the value of the direct extension as a boolean
@extension: http://hl7.org/fhir/us/core/StructureDefinition/us-core-direct
*/


define function Direct(contactPoint ContactPoint):
  FHIRCommon.Extension ( contactPoint, 'http://hl7.org/fhir/us/core/StructureDefinition/us-core-direct' ).value as boolean

/* QICore Extensions */

/*
@description: Surfaces the [isElective](http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-isElective)
modifier extension defined in QICore.
@comment: This function returns the value of the isElective modifier extension as a boolean.
@extension: http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-isElective
*/



define function IsElective(serviceRequest ServiceRequest):
  FHIRCommon.ModifierExtension ( serviceRequest, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-isElective' ).value as boolean

/*
@description: Surfaces the [procedure](http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-encounter-procedure)
extension defined in QICore.
@comment: This function returns a tuple with elements for each subextension (type, rank, and procedure).
@extension: http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-encounter-procedure
*/


define function Procedure(encounter Encounter):
  ( FHIRCommon.Extensions ( encounter, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-encounter-procedure' ) ) E
    return {
      type: FHIRCommon.Extension ( E, 'type' ).value as CodeableConcept,
      rank: FHIRCommon.Extension ( E, 'rank' ).value as positiveInt,
      procedure: FHIRCommon.Extension ( E, 'procedure' ).value as Reference
    }

/*
@description: Surfaces the [doNotPerform](http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-doNotPerformReason)
extension defined in QICore.
@comment: This function returns the value of the extension as a CodeableConcept
@extension: http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-doNotPerformReason
*/


define function DoNotPerformReason(requestResource DomainResource):
  FHIRCommon.Extension ( requestResource, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-doNotPerformReason' ).value as CodeableConcept

/*
@description: Surfaces the [recorded](http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-recorded)
extension defined in QICore.
@comment: This function returns the value of the extension as a dateTime
@extension: http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-recorded
*/


define function Recorded(domainResource DomainResource):
  FHIRCommon.Extension ( domainResource, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-recorded' ).value as dateTime

/*
@description: Surfaces the [notDone](http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDone)
modifier extension defined in QICore.
@comment: This function returns the value of the modifier extension as a boolean
@extension: http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDone
*/


define function NotDone(eventResource DomainResource):
  FHIRCommon.ModifierExtension ( eventResource, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDone' ).value as boolean

/*
@description: Surfaces the [notDoneReason](http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneReason)
extension defined in QICore.
@comment: This function returns the value of the extension as a CodeableConcept
@extension: http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneReason
*/


define function NotDoneReason(eventResource DomainResource):
  FHIRCommon.Extension ( eventResource, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneReason' ).value as CodeableConcept

/*
@description: Surfaces the [notDoneValueSet](http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneValueSet)
extension defined in QICore.
@comment: This function returns the value of the extension as a canonical
@extension: http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneValueSet
*/


define function NotDoneValueSet(concept CodeableConcept):
  FHIRCommon.Extension ( concept, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneValueSet' ).value as canonical

/*
@description: Surfaces the [presentOnAdmission](http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-encounter-diagnosisPresentOnAdmission)
extension defined in QICore.
@comment: This function returns the value of the extension as a CodeableConcept
@extension: http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-encounter-diagnosisPresentOnAdmission
*/


define function PresentOnAdmission(element Element):
  FHIRCommon.Extension ( element, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-encounter-diagnosisPresentOnAdmission' ).value as CodeableConcept