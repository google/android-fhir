library example version '1.0.0'
using FHIR version '4.0.1'
include FHIRHelpers version '4.0.1'

context Patient

define "Patient is Female": Patient.gender = 'female'

define "Get Task Description": 'Example Task'
