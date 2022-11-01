library ANCVISIT version '1'

using FHIR version '4.0.1'

include "FHIRHelpers" version '4.0.1' called FHIRHelpers

codesystem "SNOMED": 'http://snomed.info/sct'

code "Birth code": '281050002' from "SNOMED"
code "Birth Place code": '366344009' from "SNOMED"
code "Birth Facility code": '169815003' from "SNOMED"

parameter "depends-on" List<Resource>

context Patient

define "Male": Patient.gender = 'male'
define "Female": Patient.gender = 'female'

define "QR": "depends-on".where($this is QuestionnaireResponse).first() as QuestionnaireResponse

define "QR132152": "QR".questionnaire.value = 'Questionnaire/132152'

define "QR132170": "QR".questionnaire.value = 'Questionnaire/132170'

define "QRChildReferred": CalculateAgeInYears(Patient.birthDate) <= 5 'years'
    and "QR".children().where((linkId='1b625d5c-d0bc-44d5-8eed-3efc37c51f84' or linkId='9b486fa3-5323-47db-c8eb-f725fb50e8d6') and answer.first().value.code.value='yes').exists()

define "TaskIntent": 'plan'
define "TaskExecutionPeriod": Period {
    start: dateTime {value: Now() }, end: dateTime {value: Now() }
}
define "TaskPriority": 'routine'
define "TaskFor": "QR".subject
// TODO CarePlan reference define "TaskBasedOn": 'routine'
define "TaskAuthoredOn": Now()
define "TaskRequester": Patient.generalPractitioner.first()
define "TaskOwner": Patient.generalPractitioner.first()
define "TaskReasonReference": Reference {
    reference: string {value: 'Questionnaire/132155' }
}