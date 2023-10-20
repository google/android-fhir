library WHOCommon

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'
include FHIRCommon called FC

context Patient

define function Official(identifiers List<Identifier>):
  singleton from ( identifiers I
      where I.use = 'official'
  )

define function Official(addresses List<Address>):
  singleton from ( addresses A
      where A.use = 'official'
  )

define function Official(names List<HumanName>):
  singleton from ( names N
      where N.use = 'official'
  )

define function Mobile(contactPoints List<ContactPoint>):
  singleton from ( contactPoints P
      where P.use = 'mobile'
  )

define function Only(allergies List<AllergyIntolerance>):
  singleton from allergies

define function Only(appointments List<Appointment>):
  singleton from appointments

define function Only(careplans List<CarePlan>):
  singleton from careplans

define function Only(conditions List<Condition>):
  singleton from conditions

define function Only(encounters List<Encounter>):
  singleton from encounters

define function Only(immunizations List<Immunization>):
  singleton from immunizations

define function Only(medicationrequests List<MedicationRequest>):
  singleton from medicationrequests

define function Only(observations List<Observation>):
  singleton from observations

define function Only(procedures List<Procedure>):
  singleton from procedures

define function Only(serviceRequests List<ServiceRequest>):
  singleton from serviceRequests

define function Only(dosages List<Dosage>):
  singleton from dosages

define function Only(doses List<FHIR.Dosage.DoseAndRate>):
  singleton from doses

define function Earliest(observations List<Observation>):
  First(observations O
      sort by issued
  )

define function Latest(observations List<Observation>):
  Last(observations O
      sort by issued
  )

define function MostRecent(observations List<Observation>):
  Last(observations O
      sort by issued
  )

define function MostRecent(procedures List<Procedure>):
  Last(procedures P
      sort by start of FC.ToInterval(performed)
  )

define function Lowest(observations List<Observation>):
  First(observations O
      sort by(value as FHIR.Quantity)
  )

define function Highest(observations List<Observation>):
  Last(observations O
      sort by(value as FHIR.Quantity)
  )

/*
@description: Returns any WHO core extensions defined on the given resource with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the
CQL model info.
*/


define function Extensions(domainResource DomainResource, id String):
  domainResource.extension E
    where E.url = ( 'http://fhir.org/guides/who/core/StructureDefinition/' + id )
    return E

/*
@description: Returns the single WHO core extension (if present) on the given resource with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified id.
*/


define function Extension(domainResource DomainResource, id String):
  singleton from "Extensions"(domainResource, id)

/*
@description: Returns any WHO core extensions defined on the given element with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.
*/


define function Extensions(element Element, id String):
  element.extension E
    where E.url = ( 'http://fhir.org/guides/who/core/StructureDefinition/' + id )
    return E

/*
@description: Returns the single WHO core extension (if present) on the given element with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function Extension(element Element, id String):
  singleton from Extensions(element, id)

/*
@description: Returns any WHO core modifier extensions defined on the given resource with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the
CQL model info.
*/


define function ModifierExtensions(domainResource DomainResource, id String):
  domainResource.modifierExtension E
    where E.url = ( 'http://fhir.org/guides/who/core/StructureDefinition/' + id )
    return E

/*
@description: Returns the single WHO core modifier extension (if present) on the given resource with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function ModifierExtension(domainResource DomainResource, id String):
  singleton from ModifierExtensions(domainResource, id)

/*
@description: Returns any WHO core modifier extensions defined on the given element with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.
*/


define function ModifierExtensions(element BackboneElement, id String):
  element.modifierExtension E
    where E.url = ( 'http://fhir.org/guides/who/core/StructureDefinition/' + id )
    return E

/*
@description: Returns the single WHO core modifier extension (if present) on the given element with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function ModifierExtension(element BackboneElement, id String):
  singleton from ModifierExtensions(element, id)