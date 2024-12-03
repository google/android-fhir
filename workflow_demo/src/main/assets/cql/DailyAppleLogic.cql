library DailyAppleLogic version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

valueset "Apple": 'http://fhir.org/guides/cqf/cpg/example/ValueSet/Apple'

context Patient

define "Inclusion Criteria":
  Patient.active

define "Active Daily Apple Order":
  [MedicationRequest: "Apple"] M
    where M.intent = 'order'
      and M.status = 'active'
      and M.doNotPerform is not true

define "Contraindications For Daily Apple Order":
  [MedicationRequest: "Apple"] M
    where M.intent = 'order'
      and M.status = 'active'
      and M.doNotPerform is true

define "No Daily Apple Order":
  not exists ("Active Daily Apple Order")
    and not exists ("Contraindications For Daily Apple Order")
