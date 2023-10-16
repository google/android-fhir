library immunizationreview version '1.0.0'
using FHIR version '4.0.1'
include FHIRHelpers version '4.0.1'

context Patient

// Patient Operations
define "Get Patient Reference": Reference {
  reference: string { value: 'Patient/' + Patient.id}
}


// Immunization Review Screening
define "Get Immunization Review Description": 'Immunization Review'
define "Get Immunization Review Questionnaire Reference": Reference {
  reference: string { value: 'Questionnaire/Questionnaire-IMMZD1ClientHistory'}
}
define "Eligible for Immunization Review": true
