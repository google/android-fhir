library ANCCommon

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

/*
@description: Returns any ANC extensions defined on the given resource with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the
CQL model info.
*/


define function Extensions(domainResource DomainResource, id String):
  domainResource.extension E
    where E.url = ( 'http://fhir.org/guides/who/anc-cds/StructureDefinition/' + id )
    return E

/*
@description: Returns the single ANC extension (if present) on the given resource with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified id.
*/


define function Extension(domainResource DomainResource, id String):
  singleton from "Extensions"(domainResource, id)

/*
@description: Returns any ANC extensions defined on the given element with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.
*/


define function Extensions(element Element, id String):
  element.extension E
    where E.url = ( 'http://fhir.org/guides/who/anc-cds/StructureDefinition/' + id )
    return E

/*
@description: Returns the single ANC extension (if present) on the given element with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function Extension(element Element, id String):
  singleton from Extensions(element, id)

/*
@description: Returns any ANC modifier extensions defined on the given resource with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the
CQL model info.
*/


define function ModifierExtensions(domainResource DomainResource, id String):
  domainResource.modifierExtension E
    where E.url = ( 'http://fhir.org/guides/who/anc-cds/StructureDefinition/' + id )
    return E

/*
@description: Returns the single ANC modifier extension (if present) on the given resource with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function ModifierExtension(domainResource DomainResource, id String):
  singleton from ModifierExtensions(domainResource, id)

/*
@description: Returns any ANC modifier extensions defined on the given element with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.
*/


define function ModifierExtensions(element BackboneElement, id String):
  element.modifierExtension E
    where E.url = ( 'http://fhir.org/guides/who/anc-cds/StructureDefinition/' + id )
    return E

/*
@description: Returns the single ANC modifier extension (if present) on the given element with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.
*/


define function ModifierExtension(element BackboneElement, id String):
  singleton from ModifierExtensions(element, id)