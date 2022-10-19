library TestFHIRPath

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

parameter Patient Patient
parameter normalReportingDuration System.Quantity default 2 weeks
parameter Encounter Encounter

context Patient

define TestToday: today()

define TestNow: now()

define TestTimeOfDay: timeOfDay()

define Encounters: [Encounter]

define TestTodayInWhere: Encounters.where(status = 'in-progress' and period.end <= today() - 72 hours)

define TestNowInWhere: Encounters.where(status = 'in-progress' and period.end <= now() - 72 hours)

define TestNowInWhere2: Encounters.where((status = 'in-progress' and period.start + normalReportingDuration < now()) or period.end <= (now() - 72 hours))

define TestNow2: Patient.birthDate < date from now()

define TestNow3: Patient.birthDate.value < now()

define TestToday2: @1974-12-25 < today()

