//Declare name and version of lib
library PatientGroups version '1.0.0'

//FHIR as a data model and version of FHIR
using FHIR version '4.0.1'

//functions to help FHIR vs CQL primitives
include "FHIRHelpers" version '4.0.1' called FHIRHelpers

context Patient

define "All Groups": [Group] G
    with [Patient] P
        such that P.active = true

define "All Person Groups": "All Groups" G where G.type = 'person'
define "All Group Members": distinct(flatten("All Person Groups" G return (G.member M return M.entity)))

define "All Patients": [Patient] P
    with "All Group Members" G
        such that P.id = Split(G.reference, '/')[1] and P.active = true

define "Males": "All Patients" M where M.gender='male' and M.active = true
define "Females": "All Patients" F where F.gender='female' and F.active = true

define "Age": CalculateAgeInYears(Patient.birthDate)

define "Age Stratifier":
  case
    when "Age" < 1 then 'P0Y'
    when "Age" in Interval[1,5] then 'P1-5Y'
    when "Age" in Interval[6, 14] then 'P6-14Y'
    when "Age" in Interval[15, 49] then 'P15-49Y'
    when "Age" >= 50 then 'P50Y'
    else 'Unspecified'
  end
