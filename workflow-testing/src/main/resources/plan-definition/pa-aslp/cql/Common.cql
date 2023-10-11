library Common version '0.0.001'

using FHIR version '4.0.1'

include FHIRHelpers version '4.1.000' called FHIRHelpers

codesystem "ObservationStatusCodes": 'http://hl7.org/fhir/observation-status'

code "final": 'final' from "ObservationStatusCodes"
code "amended": 'amended' from "ObservationStatusCodes"
code "corrected": 'corrected' from "ObservationStatusCodes"
code "preliminary": 'preliminary' from "ObservationStatusCodes"

parameter "Asserted Lookback Duration" System.Quantity default 6 months

context Patient

define function MostRecent(value List<FHIR.Observation>):
  First(value Observation
      sort by effective descending
  )

define function QualifiedObservations(value List<FHIR.Observation>):
  value Observation
    where ( Observation.status ~ "final".code
        or Observation.status ~ "amended".code
        or Observation.status ~ "corrected".code
    )

define function QualifiedCaseFeatureObservations(value List<FHIR.Observation>):
  value Observation
    where ( Observation.status ~ "final".code
        or Observation.status ~ "amended".code
        or Observation.status ~ "corrected".code
        or Observation.status ~ "preliminary".code
    )
      and WithinAssertedPeriod(Observation.effective)

define function WithinAssertedPeriod(value Choice<FHIR.date, FHIR.dateTime>):
  if ( value is FHIR.dateTime ) then value as FHIR.dateTime on or after ( ToDateTime(Today()) ) - "Asserted Lookback Duration" 
    else ToDateTime(value as FHIR.date) on or after ( ToDateTime(Today()) ) - "Asserted Lookback Duration"

define function ReferenceTo(patient Patient):
  Reference { reference: string { value: 'Patient/' + patient.id } }

/* 
Getting error with this overload:
Cannot resolve reference to expression or function CaseFeatureObservation() because it results in a circular reference

define function CaseFeatureObservation(code CodeableConcept, value System.Quantity, profile String):
    CaseFeatureObservation(code, value, Now(), profile)
*/


define function CaseFeatureObservation(code CodeableConcept, value System.Quantity, effective DateTime, profile String):
  Observation { meta: Meta { profile: { canonical { value: profile } } }, code: code, effective: dateTime { value: Coalesce(effective, Now()) }, issued: instant { value: Now() }, subject: ReferenceTo(Patient), status: ObservationStatus { value: 'preliminary' }, value: Quantity { value: decimal { value: value.value }, unit: string { value: value.unit }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: value.unit } }
        //other elements
  }