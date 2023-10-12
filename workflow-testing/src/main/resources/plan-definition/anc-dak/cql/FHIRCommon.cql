/*
@author: Bryn Rhodes
@description: Common terminologies and functions used in FHIR-based CQL artifacts
*/
library FHIRCommon version '4.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

codesystem "LOINC": 'http://loinc.org'
codesystem "SNOMEDCT": 'http://snomed.info/sct'
codesystem "RoleCode": 'http://terminology.hl7.org/CodeSystem/v3-RoleCode'
codesystem "Diagnosis Role": 'http://terminology.hl7.org/CodeSystem/diagnosis-role'
codesystem "RequestIntent": 'http://terminology.hl7.org/CodeSystem/request-intent'
codesystem "MedicationRequestCategory": 'http://terminology.hl7.org/CodeSystem/medicationrequest-category'
codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'
codesystem "ConditionVerificationStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-ver-status'
codesystem "AllergyIntoleranceClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical'
codesystem "AllergyIntoleranceVerificationStatusCodes": 'http://terminology.hl7.org/CodeSystem/allergyintolerance-verification'

valueset "Active Condition": 'http://fhir.org/guides/cqf/common/ValueSet/active-condition'
valueset "Inactive Condition": 'http://fhir.org/guides/cqf/common/ValueSet/inactive-condition'

code "Birthdate": '21112-8' from "LOINC" display 'Birth date'
code "Dead": '419099009' from "SNOMEDCT" display 'Dead'
code "ER": 'ER' from "RoleCode" display 'Emergency room'
code "ICU": 'ICU' from "RoleCode" display 'Intensive care unit'
code "Billing": 'billing' from "Diagnosis Role" display 'Billing'

// Condition Clinical Status Codes - Consider value sets for these

code "active": 'active' from "ConditionClinicalStatusCodes"
code "recurrence": 'recurrence' from "ConditionClinicalStatusCodes"
code "relapse": 'relapse' from "ConditionClinicalStatusCodes"
code "inactive": 'inactive' from "ConditionClinicalStatusCodes"
code "remission": 'remission' from "ConditionClinicalStatusCodes"
code "resolved": 'resolved' from "ConditionClinicalStatusCodes"

// Condition Verification Status Codes - Consider value sets for these

code "unconfirmed": 'unconfirmed' from ConditionVerificationStatusCodes
code "provisional": 'provisional' from ConditionVerificationStatusCodes
code "differential": 'differential' from ConditionVerificationStatusCodes
code "confirmed": 'confirmed' from ConditionVerificationStatusCodes
code "refuted": 'refuted' from ConditionVerificationStatusCodes
code "entered-in-error": 'entered-in-error' from ConditionVerificationStatusCodes
code "allergy-active": 'active' from "AllergyIntoleranceClinicalStatusCodes"
code "allergy-inactive": 'inactive' from "AllergyIntoleranceClinicalStatusCodes"
code "allergy-resolved": 'resolved' from "AllergyIntoleranceClinicalStatusCodes"

// Allergy/Intolerance Verification Status Codes - Consider value sets for these

code "allergy-unconfirmed": 'unconfirmed' from AllergyIntoleranceVerificationStatusCodes
code "allergy-confirmed": 'confirmed' from AllergyIntoleranceVerificationStatusCodes
code "allergy-refuted": 'refuted' from AllergyIntoleranceVerificationStatusCodes

// MedicationRequest Category Codes

code "Community": 'community' from "MedicationRequestCategory" display 'Community'
code "Discharge": 'discharge' from "MedicationRequestCategory" display 'Discharge'

// Diagnosis Role Codes

code "AD": 'AD' from "Diagnosis Role" display 'Admission diagnosis'
code "DD": 'DD' from "Diagnosis Role" display 'Discharge diagnosis'
code "CC": 'CC' from "Diagnosis Role" display 'Chief complaint'
code "CM": 'CM' from "Diagnosis Role" display 'Comorbidity diagnosis'
code "pre-op": 'pre-op' from "Diagnosis Role" display 'pre-op diagnosis'
code "post-op": 'post-op' from "Diagnosis Role" display 'post-op diagnosis'
code "billing": 'billing' from "Diagnosis Role" display 'billing diagnosis'

context Patient

/*
@description: Normalizes a value that is a choice of timing-valued types to an equivalent interval
@comment: Normalizes a choice type of FHIR.dateTime, FHIR.Period, FHIR.Timing, FHIR.instance, FHIR.string, FHIR.Age, or FHIR.Range types
to an equivalent interval. This selection of choice types is a superset of the majority of choice types that are used as possible
representations for timing-valued elements in FHIR, allowing this function to be used across any resource. NOTE: Due to the
complexity of determining a single interval from a Timing or String type, this function will throw a run-time exception if it is used
with a Timing or String.
*/


define function ToInterval(choice Choice<FHIR.dateTime, FHIR.Period, FHIR.Timing, FHIR.instant, FHIR.string, FHIR.Age, FHIR.Range>):
  case
    when choice is FHIR.dateTime then Interval[FHIRHelpers.ToDateTime ( choice as FHIR.dateTime ), FHIRHelpers.ToDateTime ( choice as FHIR.dateTime )]
    when choice is FHIR.Period then FHIRHelpers.ToInterval ( choice as FHIR.Period )
    when choice is FHIR.instant then Interval[FHIRHelpers.ToDateTime ( choice as FHIR.instant ), FHIRHelpers.ToDateTime ( choice as FHIR.instant )]
    when choice is FHIR.Age then Interval[FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( choice as FHIR.Age ), FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( choice as FHIR.Age ) + 1 year )
    when choice is FHIR.Range then Interval[FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( ( choice as FHIR.Range ).low ), FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( ( choice as FHIR.Range ).high ) + 1 year )
    when choice is FHIR.Timing then Message(null as Interval<DateTime>, true, '1', 'Error', 'Cannot compute a single interval from a Timing type')
    when choice is FHIR.string then Message(null as Interval<DateTime>, true, '1', 'Error', 'Cannot compute an interval from a String value') 
    else null as Interval<DateTime>end

/*
@description: Returns an interval representing the normalized Abatement of a given Condition resource.
@comment: NOTE: Due to the complexity of determining an interval from a String, this function will throw
a run-time exception if used with a Condition instance that has a String as the abatement value.
*/


define function ToAbatementInterval(condition Condition):
  if condition.abatement is FHIR.dateTime then Interval[FHIRHelpers.ToDateTime ( condition.abatement as FHIR.dateTime ), FHIRHelpers.ToDateTime ( condition.abatement as FHIR.dateTime )]
    else if condition.abatement is FHIR.Period then FHIRHelpers.ToInterval ( condition.abatement as FHIR.Period ) 
    else if condition.abatement is FHIR.string then Message(null as Interval<DateTime>, true, '1', 'Error', 'Cannot compute an interval from a String value') 
    else if condition.abatement is FHIR.Age then Interval[FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( condition.abatement as FHIR.Age ), FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( condition.abatement as FHIR.Age ) + 1 year ) 
    else if condition.abatement is FHIR.Range then Interval[FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( ( condition.abatement as FHIR.Range ).low ), FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( ( condition.abatement as FHIR.Range ).high ) + 1 year ) 
    else if condition.abatement is FHIR.boolean then Interval[end of ToInterval(condition.onset), condition.recordedDate ) 
    else null

/*
@description: Returns an interval representing the normalized prevalence period of a given Condition resource.
@comment: Uses the ToInterval and ToAbatementInterval functions to determine the widest potential interval from
onset to abatement as specified in the given Condition.
*/


define function ToPrevalenceInterval(condition Condition):
  if condition.clinicalStatus ~ "active"
    or condition.clinicalStatus ~ "recurrence"
    or condition.clinicalStatus ~ "relapse" then Interval[start of ToInterval(condition.onset), end of ToAbatementInterval(condition)]
    else Interval[start of ToInterval(condition.onset), end of ToAbatementInterval(condition) )

/*
@description: Returns any extensions defined on the given resource with the specified url.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the
CQL model info.
*/


define function Extensions(domainResource DomainResource, url String):
  domainResource.extension E
    where E.url = url
    return E

/*
@description: Returns the single extension (if present) on the given resource with the specified url.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function Extension(domainResource DomainResource, url String):
  singleton from "Extensions"(domainResource, url)

/*
@description: Returns any extensions defined on the given element with the specified url.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.
*/


define function Extensions(element Element, url String):
  element.extension E
    where E.url = url
    return E

/*
@description: Returns the single extension (if present) on the given element with the specified url.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function Extension(element Element, url String):
  singleton from Extensions(element, url)

/*
@description: Returns any modifier extensions defined on the given resource with the specified url.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the
CQL model info.
*/


define function ModifierExtensions(domainResource DomainResource, url String):
  domainResource.modifierExtension E
    where E.url = url
    return E

/*
@description: Returns the single modifier extension (if present) on the given resource with the specified url.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function ModifierExtension(domainResource DomainResource, url String):
  singleton from ModifierExtensions(domainResource, url)

/*
@description: Returns any modifier extensions defined on the given element with the specified url.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.
*/


define function ModifierExtensions(element BackboneElement, url String):
  element.modifierExtension E
    where E.url = url
    return E

/*
@description: Returns the single modifier extension (if present) on the given element with the specified url.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function ModifierExtension(element BackboneElement, url String):
  singleton from ModifierExtensions(element, url)

/*
@description: Returns any base-FHIR extensions defined on the given resource with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.
*/


define function BaseExtensions(domainResource DomainResource, id String):
  domainResource.extension E
    where E.url = ( 'http://hl7.org/fhir/StructureDefinition/' + id )
    return E

/*
@description: Returns the single base-FHIR extension (if present) on the given resource with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function BaseExtension(domainResource DomainResource, id String):
  singleton from BaseExtensions(domainResource, id)

/*
@description: Returns any base-FHIR extensions defined on the given element with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.
*/


define function BaseExtensions(element Element, id String):
  element.extension E
    where E.url = ( 'http://hl7.org/fhir/StructureDefinition/' + id )
    return E

/*
@description: Returns the single base-FHIR extension (if present) on the given element with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function BaseExtension(element Element, id String):
  singleton from BaseExtensions(element, id)

/*
@description: Returns any base-FHIR modifier extensions defined on the given resource with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.
*/


define function BaseModifierExtensions(domainResource DomainResource, id String):
  domainResource.modifierExtension E
    where E.url = ( 'http://hl7.org/fhir/StructureDefinition/' + id )
    return E

/*
@description: Returns the single base-FHIR modifier extension (if present) on the given resource with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function BaseModifierExtension(domainResource DomainResource, id String):
  singleton from BaseModifierExtensions(domainResource, id)

/*
@description: Returns any base-FHIR modifier extensions defined on the given element with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.
*/


define function BaseModifierExtensions(element BackboneElement, id String):
  element.modifierExtension E
    where E.url = ( 'http://hl7.org/fhir/StructureDefinition/' + id )
    return E

/*
@description: Returns the single base-FHIR extension (if present) on the given element with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function BaseModifierExtension(element BackboneElement, id String):
  singleton from BaseModifierExtensions(element, id)