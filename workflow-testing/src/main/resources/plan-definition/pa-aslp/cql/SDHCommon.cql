library SDHCommon

using FHIR version '4.0.1'

include FHIRHelpers version '4.1.000'
include FHIRCommon version '1.1.000' called FC

context Patient

define function Official(identifiers List<Identifier>):
  singleton from ( identifiers I
      where I.use = 'official'
  )

define function Official(names List<HumanName>):
  singleton from ( names N
      where N.use = 'official'
  )

define function Only(observations List<FHIR.Observation>):
  singleton from observations

define function Earliest(observations List<FHIR.Observation>):
  First(observations O
      sort by issued
  )

define function Latest(observations List<FHIR.Observation>):
  Last(observations O
      sort by issued
  )

define function MostRecent(observations List<FHIR.Observation>):
  Last(observations O
      sort by issued
  )

define function MostRecent(conditions List<FHIR.Condition>):
  Last(conditions O
      sort by recordedDate
  )

define function Lowest(observations List<FHIR.Observation>):
  First(observations O
      sort by(value as FHIR.Quantity)
  )

define function Highest(observations List<FHIR.Observation>):
  Last(observations O
      sort by(value as FHIR.Quantity)
  )

define function Has(value List<System.Any>):
  if exists value then true 
    else null