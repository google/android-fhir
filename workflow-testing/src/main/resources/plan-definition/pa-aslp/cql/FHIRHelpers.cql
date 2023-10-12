/*
@author: Bryn Rhodes
@description: This library defines functions to convert between FHIR
 data types and CQL system-defined types, as well as functions to support
 FHIRPath implementation. For more information, the FHIRHelpers wiki page:
 https://github.com/cqframework/clinical_quality_language/wiki/FHIRHelpers
@allowFluent: true

@update: Refresh based on AU 2022 content
*/
library FHIRHelpers version '4.1.000'

using FHIR version '4.0.1'

/*
@description: Converts the given [Period](https://hl7.org/fhir/datatypes.html#Period)
value to a CQL DateTime Interval
@comment: If the start value of the given period is unspecified, the starting
boundary of the resulting interval will be open (meaning the start of the interval
is unknown, as opposed to interpreted as the beginning of time).
*/


define function ToInterval(period FHIR.Period):
  if period is null then null 
    else if period."start" is null then Interval ( period."start".value, period."end".value]
    else Interval[period."start".value, period."end".value]

/*
@description: Converts a UCUM definite duration unit to a CQL calendar duration
unit using conversions specified in the [quantities](https://cql.hl7.org/02-authorsguide.html#quantities) 
topic of the CQL specification.
@comment: Note that for durations above days (or weeks), the conversion is understood to be approximate
*/


define function ToCalendarUnit(unit System.String):
  case unit
    when 'ms' then 'millisecond'
    when 's' then 'second'
    when 'min' then 'minute'
    when 'h' then 'hour'
    when 'd' then 'day'
    when 'wk' then 'week'
    when 'mo' then 'month'
    when 'a' then 'year' 
    else unit end

/*
@description: Converts the given FHIR [Quantity](https://hl7.org/fhir/datatypes.html#Quantity) 
value to a CQL Quantity
@comment: If the given quantity has a comparator specified, a runtime error is raised. If the given quantity
has a system other than UCUM (i.e. `http://unitsofmeasure.org`) or CQL calendar units (i.e. `http://hl7.org/fhirpath/CodeSystem/calendar-units`)
an error is raised. For UCUM to calendar units, the `ToCalendarUnit` function is used.
@seealso: ToCalendarUnit
*/


define function ToQuantity(quantity FHIR.Quantity):
  case
    when quantity is null then null
    when quantity.value is null then null
    when quantity.comparator is not null then Message(null, true, 'FHIRHelpers.ToQuantity.ComparatorQuantityNotSupported', 'Error', 'FHIR Quantity value has a comparator and cannot be converted to a System.Quantity value.')
    when quantity.system is null
      or quantity.system.value = 'http://unitsofmeasure.org'
      or quantity.system.value = 'http://hl7.org/fhirpath/CodeSystem/calendar-units' then System.Quantity { value: quantity.value.value, unit: ToCalendarUnit(Coalesce(quantity.code.value, quantity.unit.value, '1')) } 
    else Message(null, true, 'FHIRHelpers.ToQuantity.InvalidFHIRQuantity', 'Error', 'Invalid FHIR Quantity code: ' & quantity.unit.value & ' (' & quantity.system.value & '|' & quantity.code.value & ')') end

/*
@description: Converts the given FHIR [Quantity](https://hl7.org/fhir/datatypes.html#Quantity) value to a CQL Quantity, ignoring
the comparator element. This function should only be used when an application is justified in ignoring the comparator value (i.e. the
context is looking for boundary).
@comment: If the given quantity has a system other than UCUM (i.e. `http://unitsofmeasure.org`) or CQL calendar units 
(i.e. `http://hl7.org/fhirpath/CodeSystem/calendar-units`) an error is raised. For UCUM to calendar units, the `ToCalendarUnit` function 
is used.
@seealso: ToCalendarUnit
*/


define function ToQuantityIgnoringComparator(quantity FHIR.Quantity):
  case
    when quantity is null then null
    when quantity.value is null then null
    when quantity.system is null
      or quantity.system.value = 'http://unitsofmeasure.org'
      or quantity.system.value = 'http://hl7.org/fhirpath/CodeSystem/calendar-units' then System.Quantity { value: quantity.value.value, unit: ToCalendarUnit(Coalesce(quantity.code.value, quantity.unit.value, '1')) } 
    else Message(null, true, 'FHIRHelpers.ToQuantity.InvalidFHIRQuantity', 'Error', 'Invalid FHIR Quantity code: ' & quantity.unit.value & ' (' & quantity.system.value & '|' & quantity.code.value & ')') end

/*
@description: Converts the given FHIR [Quantity](https://hl7.org/fhir/datatypes.html#Quantity) value to a CQL Interval of Quantity.
@comment: If the given quantity has a comparator, it is used to construct an interval based on the value of the comparator. If the comparator
is less than, the resulting interval will start with a null closed boundary and end with an open boundary on the quantity. If the comparator
is less than or equal, the resulting interval will start with a null closed boundary and end with a closed boundary on the quantity. If the 
comparator is greater or equal, the resulting interval will start with a closed boundary on the quantity and end with a closed null boundary.
If the comparator is greatter than, the resulting interval will start with an open boundary on the quantity and end with a closed null boundary.
If no comparator is specified, the resulting interval will start and end with a closed boundary on the quantity.
*/


define function ToInterval(quantity FHIR.Quantity):
  if quantity is null then null 
    else case quantity.comparator.value
    when '<' then Interval[null, ToQuantityIgnoringComparator(quantity) )
    when '<=' then Interval[null, ToQuantityIgnoringComparator(quantity)]
    when '>=' then Interval[ToQuantityIgnoringComparator(quantity), null]
    when '>' then Interval ( ToQuantityIgnoringComparator(quantity), null]
    else Interval[ToQuantity(quantity), ToQuantity(quantity)]end

/*
@description: Converts the given FHIR [Ratio](https://hl7.org/fhir/datatypes.html#Ratio) value to a CQL Ratio.
*/


define function ToRatio(ratio FHIR.Ratio):
  if ratio is null then null 
    else System.Ratio { numerator: ToQuantity(ratio.numerator), denominator: ToQuantity(ratio.denominator) }

/*
@description: Converts the given FHIR [Range](https://hl7.org/fhir/datatypes.html#Range) value to a CQL Interval of Quantity
*/


define function ToInterval(range FHIR.Range):
  if range is null then null 
    else Interval[ToQuantity(range.low), ToQuantity(range.high)]

/*
@description: Converts the given FHIR [Coding](https://hl7.org/fhir/datatypes.html#Coding) value to a CQL Code.
*/


define function ToCode(coding FHIR.Coding):
  if coding is null then null 
    else System.Code { code: coding.code.value, system: coding.system.value, version: coding.version.value, display: coding.display.value }

/*
@description: Converts the given FHIR [CodeableConcept](https://hl7.org/fhir/datatypes.html#CodeableConcept) value to a CQL Concept.
*/


define function ToConcept(concept FHIR.CodeableConcept):
  if concept is null then null 
    else System.Concept { codes: concept.coding C
    return ToCode(C), display: concept.text.value }

/*
@description: Converts the given value (assumed to be a URI) to a CQL [ValueSet](https://cql.hl7.org/09-b-cqlreference.html#valueset)
*/


define function ToValueSet(uri String):
  if uri is null then null 
    else System.ValueSet { id: uri }

/*
@description: Constructs a FHIR [Reference](https://hl7.org/fhir/datatypes.html#Reference) from the given reference (assumed to be a FHIR resource URL)
*/


define function reference(reference String):
  if reference is null then null 
    else Reference { reference: string { value: reference } }

/*
@description: Converts the given value to a CQL value using the appropriate accessor or conversion function.
@comment: TODO: document conversion
*/


define function ToValue(value Choice<base64Binary, boolean, canonical, code, date, dateTime, decimal, id, instant, integer, markdown, oid, positiveInt, string, time, unsignedInt, uri, url, uuid, Address, Age, Annotation, Attachment, CodeableConcept, Coding, ContactPoint, Count, Distance, Duration, HumanName, Identifier, Money, Period, Quantity, Range, Ratio, Reference, SampledData, Signature, Timing, ContactDetail, Contributor, DataRequirement, Expression, ParameterDefinition, RelatedArtifact, TriggerDefinition, UsageContext, Dosage, Meta>):
  case
    when value is base64Binary then ( value as base64Binary ).value
    when value is boolean then ( value as boolean ).value
    when value is canonical then ( value as canonical ).value
    when value is code then ( value as code ).value
    when value is date then ( value as date ).value
    when value is dateTime then ( value as dateTime ).value
    when value is decimal then ( value as decimal ).value
    when value is id then ( value as id ).value
    when value is instant then ( value as instant ).value
    when value is integer then ( value as integer ).value
    when value is markdown then ( value as markdown ).value
    when value is oid then ( value as oid ).value
    when value is positiveInt then ( value as positiveInt ).value
    when value is string then ( value as string ).value
    when value is time then ( value as time ).value
    when value is unsignedInt then ( value as unsignedInt ).value
    when value is uri then ( value as uri ).value
    when value is url then ( value as url ).value
    when value is uuid then ( value as uuid ).value
    when value is Age then ToQuantity(value as Age)
    when value is CodeableConcept then ToConcept(value as CodeableConcept)
    when value is Coding then ToCode(value as Coding)
    when value is Count then ToQuantity(value as Count)
    when value is Distance then ToQuantity(value as Distance)
    when value is Duration then ToQuantity(value as Duration)
    when value is Quantity then ToQuantity(value as Quantity)
    when value is Range then ToInterval(value as Range)
    when value is Period then ToInterval(value as Period)
    when value is Ratio then ToRatio(value as Ratio) 
    else value as Choice<Address, Annotation, Attachment, ContactPoint, HumanName, Identifier, Money, Reference, SampledData, Signature, Timing, ContactDetail, Contributor, DataRequirement, Expression, ParameterDefinition, RelatedArtifact, TriggerDefinition, UsageContext, Dosage, Meta>end

/*
@description: Resolve the given reference as a url to a resource. If the item resolves, the Resource is returned, otherwise the result is null.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function resolve(reference String) returns Resource:
  external
/*
@description: Resolve the reference element of the given Reference. If the item resolves, the Resource is returned, otherwise the result is null.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function resolve(reference Reference) returns Resource:
  external
/*
@description: Constructs a Reference to the given Resource. The resulting reference will typically be relative, but implementations may provide a base URL if one can be unambiguously determined.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function reference(resource Resource) returns Reference:
  external
/*
@description: Returns any extensions with the given url defined on the given element.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function extension(element Element, url String) returns List<Extension>:
  external
/*
@description: Returns any extensions with the given url defined on the given resource.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function extension(resource DomainResource, url String) returns List<Extension>:
  external
/*
@description: Returns any modifier extensions with the given url defined on the given element.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function modifierExtension(element BackboneElement, url String) returns List<Extension>:
  external
/*
@description: Returns any modifier extensions with the given url defined on the given resource.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function modifierExtension(resource DomainResource, url String) returns List<Extension>:
  external
/*
@description: Returns true if the element is a FHIR primitive type with a value element (as opposed to having only extensions); false otherwise
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function hasValue(element Element) returns Boolean:
  external
/*
@description: Returns the value of the FHIR primitive; null otherwise
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function getValue(element Element) returns Any:
  external
/*
@description: Returns a list containing only those elements in the input that are of the given type, specified as a string.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function ofType(identifier String) returns List<Any>:
  external
/*
@description: Returns true if the input is of the given type; false otherwise
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function is(identifier String) returns Boolean:
  external
/*
@description: If the input is of the given type; returns the value as that type; null otherwise.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function as(identifier String) returns Any:
  external
/*
@description: Returns the FHIR element definition for the given element
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function elementDefinition(element Element) returns ElementDefinition:
  external
/*
@description: Returns the given slice as defined in the given structure definition. The structure argument is a uri that resolves to the structure definition, and the name must be the name of a slice within that structure definition. If the structure cannot be resolved, or the name of the slice within the resolved structure is not present, an error is thrown.
@comment: For every element in the input collection, if the resolved slice is present on the element, it will be returned. If the slice does not match any element in the input collection, or if the input collection is empty, the result is an empty collection ({ }).
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function slice(element Element, url String, name String) returns List<Element>:
  external
/*
@description: For each element in the input collection, verifies that there are no modifying extensions defined other than the ones given by the modifier argument. If the check passes, the input collection is returned. Otherwise, an error is thrown.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function checkModifiers(resource Resource) returns Resource:
  external
/*
@description: For each element in the input collection, verifies that there are no modifying extensions defined other than the ones given by the modifier argument. If the check passes, the input collection is returned. Otherwise, an error is thrown.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function checkModifiers(resource Resource, modifier String) returns Resource:
  external
/*
@description: For each element in the input collection, verifies that there are no modifying extensions defined other than the ones given by the modifier argument. If the check passes, the input collection is returned. Otherwise, an error is thrown.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function checkModifiers(element Element) returns Element:
  external
/*
@description: For each element in the input collection, verifies that there are no modifying extensions defined other than the ones given by the modifier argument. If the check passes, the input collection is returned. Otherwise, an error is thrown.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function checkModifiers(element Element, modifier String) returns Element:
  external
/*
@description: Returns true if the single input element conforms to the profile specified by the structure argument, and false otherwise. If the structure cannot be resolved to a valid profile, an error is thrown. If the input contains more than one element, an error is thrown. If the input is empty, the result is empty.
@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function conformsTo(resource Resource, structure String) returns Boolean:
  external
/*
@description: Returns true if the given code is equal to a code in the valueset, so long as the valueset only contains one codesystem. If the valueset contains more than one codesystem, an error is thrown.
@comment: If the valueset cannot be resolved as a uri to a value set, an error is thrown.

Note that implementations are encouraged to make use of a terminology service to provide this functionality.

For example:

```fhirpath
Observation.component.where(code.memberOf('http://hl7.org/fhir/ValueSet/observation-vitalsignresult'))
```

This expression returns components that have a code that is a member of the observation-vitalsignresult valueset.

@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function memberOf(code code, valueSet String) returns Boolean:
  external
/*
@description: Returns true if the code is a member of the given valueset.
@comment: If the valueset cannot be resolved as a uri to a value set, an error is thrown.

Note that implementations are encouraged to make use of a terminology service to provide this functionality.

For example:

```fhirpath
Observation.component.where(code.memberOf('http://hl7.org/fhir/ValueSet/observation-vitalsignresult'))
```

This expression returns components that have a code that is a member of the observation-vitalsignresult valueset.

@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function memberOf(coding Coding, valueSet String) returns Boolean:
  external
/*
@description: Returns true if any code in the concept is a member of the given valueset.
@comment: If the valueset cannot be resolved as a uri to a value set, an error is thrown.

Note that implementations are encouraged to make use of a terminology service to provide this functionality.

For example:

```fhirpath
Observation.component.where(code.memberOf('http://hl7.org/fhir/ValueSet/observation-vitalsignresult'))
```

This expression returns components that have a code that is a member of the observation-vitalsignresult valueset.

@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function memberOf(concept CodeableConcept, valueSet String) returns Boolean:
  external
/*
@description: Returns true if the source code is equivalent to the given code, or if the source code subsumes the given code (i.e. the source code is an ancestor of the given code in a subsumption hierarchy), and false otherwise.

@comment: If the Codings are from different code systems, the relationships between the code systems must be well-defined or a run-time error is thrown.

Note that implementations are encouraged to make use of a terminology service to provide this functionality.

@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function subsumes(coding Coding, subsumedCoding Coding) returns Boolean:
  external
/*
@description: Returns true if any Coding in the source or given elements is equivalent to or subsumes the given code.

@comment: If the Codings are from different code systems, the relationships between the code systems must be well-defined or a run-time error is thrown.

Note that implementations are encouraged to make use of a terminology service to provide this functionality.

@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function subsumes(concept CodeableConcept, subsumedConcept CodeableConcept) returns Boolean:
  external
/*
@description: Returns true if the source code is equivalent to the given code, or if the source code is subsumed by the given code (i.e. the source code is a descendant of the given code in a subsumption hierarchy), and false otherwise.

@comment: If the Codings are from different code systems, the relationships between the code systems must be well-defined or a run-time error is thrown.

Note that implementations are encouraged to make use of a terminology service to provide this functionality.

@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function subsumedBy(coding Coding, subsumingCoding Coding) returns Boolean:
  external
/*
@description: Returns true if any Coding in the source or given elements is equivalent to or subsumed by the given code.

@comment: If the Codings are from different code systems, the relationships between the code systems must be well-defined or a run-time error is thrown.

Note that implementations are encouraged to make use of a terminology service to provide this functionality.

@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function subsumedBy(concept CodeableConcept, subsumingConcept CodeableConcept) returns Boolean:
  external
/*
@description: When invoked on an xhtml element, returns true if the rules around HTML usage are met, and false if they are not. The return value is undefined (null) on any other kind of element.

@seealso: https://hl7.org/fhir/fhirpath.html#functions
*/


define function htmlChecks(element Element) returns Boolean:
  external

define function ToString(value AccountStatus):
  value.value

define function ToString(value ActionCardinalityBehavior):
  value.value

define function ToString(value ActionConditionKind):
  value.value

define function ToString(value ActionGroupingBehavior):
  value.value

define function ToString(value ActionParticipantType):
  value.value

define function ToString(value ActionPrecheckBehavior):
  value.value

define function ToString(value ActionRelationshipType):
  value.value

define function ToString(value ActionRequiredBehavior):
  value.value

define function ToString(value ActionSelectionBehavior):
  value.value

define function ToString(value ActivityDefinitionKind):
  value.value

define function ToString(value ActivityParticipantType):
  value.value

define function ToString(value AddressType):
  value.value

define function ToString(value AddressUse):
  value.value

define function ToString(value AdministrativeGender):
  value.value

define function ToString(value AdverseEventActuality):
  value.value

define function ToString(value AggregationMode):
  value.value

define function ToString(value AllergyIntoleranceCategory):
  value.value

define function ToString(value AllergyIntoleranceCriticality):
  value.value

define function ToString(value AllergyIntoleranceSeverity):
  value.value

define function ToString(value AllergyIntoleranceType):
  value.value

define function ToString(value AppointmentStatus):
  value.value

define function ToString(value AssertionDirectionType):
  value.value

define function ToString(value AssertionOperatorType):
  value.value

define function ToString(value AssertionResponseTypes):
  value.value

define function ToString(value AuditEventAction):
  value.value

define function ToString(value AuditEventAgentNetworkType):
  value.value

define function ToString(value AuditEventOutcome):
  value.value

define function ToString(value BindingStrength):
  value.value

define function ToString(value BiologicallyDerivedProductCategory):
  value.value

define function ToString(value BiologicallyDerivedProductStatus):
  value.value

define function ToString(value BiologicallyDerivedProductStorageScale):
  value.value

define function ToString(value BundleType):
  value.value

define function ToString(value CapabilityStatementKind):
  value.value

define function ToString(value CarePlanActivityKind):
  value.value

define function ToString(value CarePlanActivityStatus):
  value.value

define function ToString(value CarePlanIntent):
  value.value

define function ToString(value CarePlanStatus):
  value.value

define function ToString(value CareTeamStatus):
  value.value

define function ToString(value CatalogEntryRelationType):
  value.value

define function ToString(value ChargeItemDefinitionPriceComponentType):
  value.value

define function ToString(value ChargeItemStatus):
  value.value

define function ToString(value ClaimResponseStatus):
  value.value

define function ToString(value ClaimStatus):
  value.value

define function ToString(value ClinicalImpressionStatus):
  value.value

define function ToString(value CodeSearchSupport):
  value.value

define function ToString(value CodeSystemContentMode):
  value.value

define function ToString(value CodeSystemHierarchyMeaning):
  value.value

define function ToString(value CommunicationPriority):
  value.value

define function ToString(value CommunicationRequestStatus):
  value.value

define function ToString(value CommunicationStatus):
  value.value

define function ToString(value CompartmentCode):
  value.value

define function ToString(value CompartmentType):
  value.value

define function ToString(value CompositionAttestationMode):
  value.value

define function ToString(value CompositionStatus):
  value.value

define function ToString(value ConceptMapEquivalence):
  value.value

define function ToString(value ConceptMapGroupUnmappedMode):
  value.value

define function ToString(value ConditionalDeleteStatus):
  value.value

define function ToString(value ConditionalReadStatus):
  value.value

define function ToString(value ConsentDataMeaning):
  value.value

define function ToString(value ConsentProvisionType):
  value.value

define function ToString(value ConsentState):
  value.value

define function ToString(value ConstraintSeverity):
  value.value

define function ToString(value ContactPointSystem):
  value.value

define function ToString(value ContactPointUse):
  value.value

define function ToString(value ContractPublicationStatus):
  value.value

define function ToString(value ContractStatus):
  value.value

define function ToString(value ContributorType):
  value.value

define function ToString(value CoverageStatus):
  value.value

define function ToString(value CurrencyCode):
  value.value

define function ToString(value DayOfWeek):
  value.value

define function ToString(value DaysOfWeek):
  value.value

define function ToString(value DetectedIssueSeverity):
  value.value

define function ToString(value DetectedIssueStatus):
  value.value

define function ToString(value DeviceMetricCalibrationState):
  value.value

define function ToString(value DeviceMetricCalibrationType):
  value.value

define function ToString(value DeviceMetricCategory):
  value.value

define function ToString(value DeviceMetricColor):
  value.value

define function ToString(value DeviceMetricOperationalStatus):
  value.value

define function ToString(value DeviceNameType):
  value.value

define function ToString(value DeviceRequestStatus):
  value.value

define function ToString(value DeviceUseStatementStatus):
  value.value

define function ToString(value DiagnosticReportStatus):
  value.value

define function ToString(value DiscriminatorType):
  value.value

define function ToString(value DocumentConfidentiality):
  value.value

define function ToString(value DocumentMode):
  value.value

define function ToString(value DocumentReferenceStatus):
  value.value

define function ToString(value DocumentRelationshipType):
  value.value

define function ToString(value EligibilityRequestPurpose):
  value.value

define function ToString(value EligibilityRequestStatus):
  value.value

define function ToString(value EligibilityResponsePurpose):
  value.value

define function ToString(value EligibilityResponseStatus):
  value.value

define function ToString(value EnableWhenBehavior):
  value.value

define function ToString(value EncounterLocationStatus):
  value.value

define function ToString(value EncounterStatus):
  value.value

define function ToString(value EndpointStatus):
  value.value

define function ToString(value EnrollmentRequestStatus):
  value.value

define function ToString(value EnrollmentResponseStatus):
  value.value

define function ToString(value EpisodeOfCareStatus):
  value.value

define function ToString(value EventCapabilityMode):
  value.value

define function ToString(value EventTiming):
  value.value

define function ToString(value EvidenceVariableType):
  value.value

define function ToString(value ExampleScenarioActorType):
  value.value

define function ToString(value ExplanationOfBenefitStatus):
  value.value

define function ToString(value ExposureState):
  value.value

define function ToString(value ExtensionContextType):
  value.value

define function ToString(value FHIRAllTypes):
  value.value

define function ToString(value FHIRDefinedType):
  value.value

define function ToString(value FHIRDeviceStatus):
  value.value

define function ToString(value FHIRResourceType):
  value.value

define function ToString(value FHIRSubstanceStatus):
  value.value

define function ToString(value FHIRVersion):
  value.value

define function ToString(value FamilyHistoryStatus):
  value.value

define function ToString(value FilterOperator):
  value.value

define function ToString(value FlagStatus):
  value.value

define function ToString(value GoalLifecycleStatus):
  value.value

define function ToString(value GraphCompartmentRule):
  value.value

define function ToString(value GraphCompartmentUse):
  value.value

define function ToString(value GroupMeasure):
  value.value

define function ToString(value GroupType):
  value.value

define function ToString(value GuidanceResponseStatus):
  value.value

define function ToString(value GuidePageGeneration):
  value.value

define function ToString(value GuideParameterCode):
  value.value

define function ToString(value HTTPVerb):
  value.value

define function ToString(value IdentifierUse):
  value.value

define function ToString(value IdentityAssuranceLevel):
  value.value

define function ToString(value ImagingStudyStatus):
  value.value

define function ToString(value ImmunizationEvaluationStatus):
  value.value

define function ToString(value ImmunizationStatus):
  value.value

define function ToString(value InvoicePriceComponentType):
  value.value

define function ToString(value InvoiceStatus):
  value.value

define function ToString(value IssueSeverity):
  value.value

define function ToString(value IssueType):
  value.value

define function ToString(value LinkType):
  value.value

define function ToString(value LinkageType):
  value.value

define function ToString(value ListMode):
  value.value

define function ToString(value ListStatus):
  value.value

define function ToString(value LocationMode):
  value.value

define function ToString(value LocationStatus):
  value.value

define function ToString(value MeasureReportStatus):
  value.value

define function ToString(value MeasureReportType):
  value.value

define function ToString(value MediaStatus):
  value.value

define function ToString(value MedicationAdministrationStatus):
  value.value

define function ToString(value MedicationDispenseStatus):
  value.value

define function ToString(value MedicationKnowledgeStatus):
  value.value

define function ToString(value MedicationRequestIntent):
  value.value

define function ToString(value MedicationRequestPriority):
  value.value

define function ToString(value MedicationRequestStatus):
  value.value

define function ToString(value MedicationStatementStatus):
  value.value

define function ToString(value MedicationStatus):
  value.value

define function ToString(value MessageSignificanceCategory):
  value.value

define function ToString(value Messageheader_Response_Request):
  value.value

define function ToString(value MimeType):
  value.value

define function ToString(value NameUse):
  value.value

define function ToString(value NamingSystemIdentifierType):
  value.value

define function ToString(value NamingSystemType):
  value.value

define function ToString(value NarrativeStatus):
  value.value

define function ToString(value NoteType):
  value.value

define function ToString(value NutritiionOrderIntent):
  value.value

define function ToString(value NutritionOrderStatus):
  value.value

define function ToString(value ObservationDataType):
  value.value

define function ToString(value ObservationRangeCategory):
  value.value

define function ToString(value ObservationStatus):
  value.value

define function ToString(value OperationKind):
  value.value

define function ToString(value OperationParameterUse):
  value.value

define function ToString(value OrientationType):
  value.value

define function ToString(value ParameterUse):
  value.value

define function ToString(value ParticipantRequired):
  value.value

define function ToString(value ParticipantStatus):
  value.value

define function ToString(value ParticipationStatus):
  value.value

define function ToString(value PaymentNoticeStatus):
  value.value

define function ToString(value PaymentReconciliationStatus):
  value.value

define function ToString(value ProcedureStatus):
  value.value

define function ToString(value PropertyRepresentation):
  value.value

define function ToString(value PropertyType):
  value.value

define function ToString(value ProvenanceEntityRole):
  value.value

define function ToString(value PublicationStatus):
  value.value

define function ToString(value QualityType):
  value.value

define function ToString(value QuantityComparator):
  value.value

define function ToString(value QuestionnaireItemOperator):
  value.value

define function ToString(value QuestionnaireItemType):
  value.value

define function ToString(value QuestionnaireResponseStatus):
  value.value

define function ToString(value ReferenceHandlingPolicy):
  value.value

define function ToString(value ReferenceVersionRules):
  value.value

define function ToString(value ReferredDocumentStatus):
  value.value

define function ToString(value RelatedArtifactType):
  value.value

define function ToString(value RemittanceOutcome):
  value.value

define function ToString(value RepositoryType):
  value.value

define function ToString(value RequestIntent):
  value.value

define function ToString(value RequestPriority):
  value.value

define function ToString(value RequestStatus):
  value.value

define function ToString(value ResearchElementType):
  value.value

define function ToString(value ResearchStudyStatus):
  value.value

define function ToString(value ResearchSubjectStatus):
  value.value

define function ToString(value ResourceType):
  value.value

define function ToString(value ResourceVersionPolicy):
  value.value

define function ToString(value ResponseType):
  value.value

define function ToString(value RestfulCapabilityMode):
  value.value

define function ToString(value RiskAssessmentStatus):
  value.value

define function ToString(value SPDXLicense):
  value.value

define function ToString(value SearchComparator):
  value.value

define function ToString(value SearchEntryMode):
  value.value

define function ToString(value SearchModifierCode):
  value.value

define function ToString(value SearchParamType):
  value.value

define function ToString(value SectionMode):
  value.value

define function ToString(value SequenceType):
  value.value

define function ToString(value ServiceRequestIntent):
  value.value

define function ToString(value ServiceRequestPriority):
  value.value

define function ToString(value ServiceRequestStatus):
  value.value

define function ToString(value SlicingRules):
  value.value

define function ToString(value SlotStatus):
  value.value

define function ToString(value SortDirection):
  value.value

define function ToString(value SpecimenContainedPreference):
  value.value

define function ToString(value SpecimenStatus):
  value.value

define function ToString(value Status):
  value.value

define function ToString(value StrandType):
  value.value

define function ToString(value StructureDefinitionKind):
  value.value

define function ToString(value StructureMapContextType):
  value.value

define function ToString(value StructureMapGroupTypeMode):
  value.value

define function ToString(value StructureMapInputMode):
  value.value

define function ToString(value StructureMapModelMode):
  value.value

define function ToString(value StructureMapSourceListMode):
  value.value

define function ToString(value StructureMapTargetListMode):
  value.value

define function ToString(value StructureMapTransform):
  value.value

define function ToString(value SubscriptionChannelType):
  value.value

define function ToString(value SubscriptionStatus):
  value.value

define function ToString(value SupplyDeliveryStatus):
  value.value

define function ToString(value SupplyRequestStatus):
  value.value

define function ToString(value SystemRestfulInteraction):
  value.value

define function ToString(value TaskIntent):
  value.value

define function ToString(value TaskPriority):
  value.value

define function ToString(value TaskStatus):
  value.value

define function ToString(value TestReportActionResult):
  value.value

define function ToString(value TestReportParticipantType):
  value.value

define function ToString(value TestReportResult):
  value.value

define function ToString(value TestReportStatus):
  value.value

define function ToString(value TestScriptRequestMethodCode):
  value.value

define function ToString(value TriggerType):
  value.value

define function ToString(value TypeDerivationRule):
  value.value

define function ToString(value TypeRestfulInteraction):
  value.value

define function ToString(value UDIEntryType):
  value.value

define function ToString(value UnitsOfTime):
  value.value

define function ToString(value Use):
  value.value

define function ToString(value VariableType):
  value.value

define function ToString(value VisionBase):
  value.value

define function ToString(value VisionEyes):
  value.value

define function ToString(value VisionStatus):
  value.value

define function ToString(value XPathUsageType):
  value.value

define function ToString(value base64Binary):
  value.value

define function ToBoolean(value boolean):
  value.value

define function ToDate(value date):
  value.value

define function ToDateTime(value dateTime):
  value.value

define function ToDecimal(value decimal):
  value.value

define function ToDateTime(value instant):
  value.value

define function ToInteger(value integer):
  value.value

define function ToString(value string):
  value.value

define function ToTime(value time):
  value.value

define function ToString(value uri):
  value.value

define function ToString(value xhtml):
  value.value