library ASLPDataElements

using FHIR version '4.0.1'

include FHIRHelpers version '4.1.000'
include FHIRCommon version '1.1.000' called FC
include SDHCommon called SC
include ASLPConcepts called Cs

parameter "Device Request" List<FHIR.DeviceRequest>
parameter "Device Request Id" List<System.String>
parameter "Medication Request" List<FHIR.MedicationRequest>
parameter "Medication Request Id" List<System.String>
parameter "Nutrition Order" List<FHIR.NutritionOrder>
parameter "Nutrition Order Id" List<System.String>
parameter "Service Request" List<FHIR.ServiceRequest>
parameter "Service Request Id" List<System.String>
parameter "Coverage Id" List<System.String>

context Patient

/*
  @dataElement: ASLP.A1.DE22 BMI
  @activity: ASLP.A1 Adult Sleep Studies
  @description: Body mass index (BMI)
*/


define "BMI":
  convert ( SC.MostRecent ( [Observation: Cs."BMI"] O
      where O.status in { 'final', 'amended', 'corrected' } ).value as FHIR.Quantity
  ) to 'kg/m2' 


/*
  @dataElement: ASLP.A1.DE16 Diagnosis of Obstructive Sleep Apnea
  @activity: ASLP.A1 Adult Sleep Studies
  @description: Diagnosis of Obstructive Sleep Apnea
*/


define "Diagnosis of Obstructive Sleep Apnea":
  SC.MostRecent ( [Condition: Cs."Diagnosis of Obstructive Sleep Apnea Codes"] C
    where C.clinicalStatus in FC."Active Condition"
      and C.verificationStatus ~ FC."confirmed" ).code


/*
  @dataElement: ASLP.A1.DE20 Height
  @activity: ASLP.A1 Adult Sleep Studies
  @description: Height (in inches)
*/


define "Height":
  convert ( SC.MostRecent ( [Observation: Cs."Height"] O
      where O.status in { 'final', 'amended', 'corrected' } ).value as FHIR.Quantity
  ) to '[in_i]'


/*
  @dataElement: ASLP.A1.DE19 History of Diabetes
  @activity: ASLP.A1 Adult Sleep Studies
  @description: History of Diabetes
*/


define "History of Diabetes":
  Coalesce(SC.Has([Condition: Cs."History of Diabetes"] C
      where C.clinicalStatus in FC."Active Condition"
        and C.verificationStatus ~ FC."confirmed"), SC.Has([Observation: Cs."History of Diabetes"] O
      where O.status in { 'final', 'amended', 'corrected' }
        and(convert(O.value as FHIR.Quantity) to 'mmol/L').value > 7.5)
  )


/*
  @dataElement: ASLP.A1.DE18 History of Hypertension
  @activity: ASLP.A1 Adult Sleep Studies
  @description: History of Hypertension
*/


define "History of Hypertension":
  SC.Has ( [Condition: Cs."History of Hypertension"] C
    where C.clinicalStatus in FC."Active Condition"
      and C.verificationStatus ~ FC."confirmed" )


/*
  @dataElement: ASLP.A1.DE20 Neck Circumference
  @activity: ASLP.A1 Adult Sleep Studies
  @description: Neck circumference (in inches)
*/


define "Neck Circumference":
  convert ( SC.MostRecent ( [Observation: Cs."Neck Circumference"] O
      where O.status in { 'final', 'amended', 'corrected' } ).value as FHIR.Quantity
  ) to '[in_i]'   


/*
  @dataElement: ASLP.A1.DE1 Sleep Study
  @activity: ASLP.A1 Adult Sleep Studies
  @description: A sleep study procedure being ordered
*/


define "Sleep Study":
  Coalesce([ServiceRequest] SR
      where SR.id in "Service Request Id", { null as FHIR.ServiceRequest }
  )
    union Coalesce("Service Request", { null as FHIR.ServiceRequest })

define "Sleep Study Code":
  ( "Sleep Study" ).code


/*
  @dataElement: ALSP.A1.DE15 Sleep Study Date
  @activity: ASLP.A1 Adult Sleep Studies
  @description: Date of the procedure
*/


define "Sleep Study Date":
  ( "Sleep Study" ).occurrence


/*
  @dataElement: ASLP.A1.DE21 Weight
  @activity: ASLP.A1 Adult Sleep Studies
  @description: Weight (in pounds)
*/


define "Weight":
  convert ( SC.MostRecent ( [Observation: Cs."Weight"] O
      where O.status in { 'final', 'amended', 'corrected' } ).value as FHIR.Quantity
  ) to '[lb_av]'

define "Is Authorized":
  true

define "Exclusion Detail":
  'I am just a string to test evaluating expressions in extensions!!!!!!'