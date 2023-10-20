library ANCContactDataElements

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'
include FHIRCommon called FC

include WHOCommon called WC
include ANCCommon called AC
include ANCConcepts called Cx

parameter encounter String

context Patient

/*
  @dataElement: ANC.A.DE4 Contact date
  @activity: ANC.A4 Gather client details
  @description: The date and time of the client's contact
*/
define "Contact date":
  WC.Only(
  [Encounter] E
    where E.id = Last(Split(encounter, '/'))
  ).period.start


/*
  @dataElement: ANC.B5.DE1 Reason for coming to facility
  @activity: ANC.A4 Gather client details
  @description: Records the reason why the woman came to the health-care facility today
*/
define "Reason for coming to facility":
  WC.Only(
  [Encounter: Cx."Reason for coming to facility"] E
    where E.id = Last(Split(encounter, '/'))
  ).reasonCode


/*
  @dataElement: ANC.A.DE12 ANC contact number
  @activity: ANC.A4 Gather client details
  @description: The ANC contact or visit number – recommended minimum is 8 contacts
*/
define "ANC contact number":
  AC.Extension(WC.Only(
  [Encounter] E
    where E.id = Last(Split(encounter, '/'))
  ), 'contactNumber').value as integer


/*
  @dataElement: ANC.A.DE1 Unique identification
  @activity: ANC.A4 Gather client details
  @description: Unique identifier generated for new clients or a universal ID, if used in the country
*/
define "Unique identification":
  singleton from ([Patient] P return WC.Official(P.identifier).value)


/*
  @dataElement: ANC.A.DE2 First name
  @activity: ANC.A4 Gather client details
  @description: Client's first name
*/
define "First name":
  singleton from ([Patient] P return First(WC.Official(P.name).given))


/*
  @dataElement: ANC.A.DE3 Last name
  @activity: ANC.A4 Gather client details
  @description: Client's family name or last name
*/
define "Last name":
  singleton from ([Patient] P return WC.Official(P.name).family)


/*
  @dataElement: ANC.A.DE5 Date of birth
  @activity: ANC.A4 Gather client details
  @description: The client's date of birth (DOB), if known
*/
define "Date of birth":
  singleton from ([Patient] P return P.birthDate)


/*
  @dataElement: ANC.A.DE7 Address
  @activity: ANC.A4 Gather client details
  @description: Client's home address or address that the client is consenting to disclose
*/
define "Address":
  singleton from ([Patient] P return WC.Official(P.address).text)


/*
  @dataElement: ANC.A.DE8 Mobile phone number
  @activity: ANC.A4 Gather client details
  @description: Client's mobile phone number
*/
define "Mobile phone number":
  singleton from ([Patient] P return WC.Mobile(P.telecom).value)


/*
  @dataElement: ANC.A.DE10 Alternative contact's name
  @activity: ANC.A4 Gather client details
  @description: Name of an alternative contact, which could be next of kin (e.g. partner, mother, sibling); the alternative contact would be used in the case of an emergency situation
*/
define "Alternative contact's name":
  WC.Official(First([Patient] P return P.contact).name).text


/*
  @dataElement: ANC.A.DE11 Alternative contact's phone number
  @activity: ANC.A4 Gather client details
  @description: Phone number of the alternative contact
*/
define "Alternative contact's phone number":
  First(First([Patient] P return P.contact).telecom).value


/*
  @dataElement: ANC.A.DE9 Woman wants to receive reminders during pregnancy
  @activity: ANC.A4 Gather client details
  @description: Whether or not the woman wants to receive SMS or other messages regarding her ANC contacts and health status during pregnancy
*/
define "Woman wants to receive reminders during pregnancy":
  singleton from ([Patient] P return AC.Extension(P, 'reminder').value as boolean)


/*
  @dataElement: ANC.B6.DE1 Highest level of education achieved
  @activity: ANC.A4 Gather client details
  @description: The highest level of schooling the woman has reached
*/
define "Highest level of education achieved":
  singleton from (
  [Patient] P
    return AC.Extension(P, 'eductaionLevel').value as FHIR.CodeableConcept
  )


/*
  @dataElement: ANC.B6.DE7 Occupation
  @activity: ANC.A4 Gather client details
  @description: The woman's occupation (select all that apply)
*/
define "Occupation":
  singleton from ([Patient] P
    return AC.Extension(P, 'occupation').value as FHIR.CodeableConcept
  )


/*
  @dataElement: ANC.A.DE13 Co-habitants
  @activity: ANC.A7 Create client record?OR?ANC.A8. Validate client details
  @description: Who does the client live with? It is important to know whether client lives with parents, other family members, a partner, friends, etc.
*/
define "Co-habitants":
  [Observation: Cx."Co-habitants"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B4.DE1 Pregnancy confirmed
  @activity: ANC.B4 Confirm pregnancy
  @description: Pregnancy has been confirmed
*/
define "Pregnancy confirmed":
  WC.Only(
  [Observation: Cx."Pregnancy confirmed"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B5.DE48 Danger signs
  @activity: ANC.B5 Quick check
  @description: Before each contact, the health worker should check whether the woman has any of the danger signs listed here – if yes, she should refer to the hospital urgently; if no, she should continue to the normal contact
*/
define "Danger signs":
  [Observation: Cx."Danger signs"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B5.DE5 Specific health concern(s)
  @activity: ANC.B5 Quick check
  @description: If the woman came to the facility with a specific health concern, select the health concern(s) from the list
*/
define "Specific health concern(s)":
  [Observation: Cx."Specific health concern(s)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE100 Tetanus toxoid-containing vaccine (TTCV) immunization history
  @activity: ANC.B6 Profile
  @description: The woman's history of receiving tetanus toxoid-containing vaccine (TTCV)
*/
define "Tetanus toxoid-containing vaccine (TTCV) immunization history":
  WC.MostRecent(
  [Observation: Cx."Tetanus toxoid-containing vaccine (TTCV) immunization history"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE105 Flu immunization history
  @activity: ANC.B6 Profile
  @description: Whether or not this year's seasonal flu vaccine has been provided
*/
define "Flu immunization history":
  WC.MostRecent(
  [Observation: Cx."Flu immunization history"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE109 Current medications
  @activity: ANC.B6 Profile
  @description: Select all of the medications the woman is currently taking
*/
define "Current medications":
  [Observation: Cx."Current medications"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE139 Daily caffeine intake
  @activity: ANC.B6 Profile
  @description: Assesses whether the woman consumes more than 300 mg of caffeine per day
*/
define "Daily caffeine intake":
  [Observation: Cx."Daily caffeine intake"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE14 Last menstrual period (LMP) date
  @activity: ANC.B6 Profile
  @description: The woman's last menstrual period (LMP) date; this is defined as the first day of her most recent period
*/
define "Last menstrual period (LMP) date":
  WC.MostRecent(
  [Observation: Cx."Last menstrual period (LMP) date"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.dateTime


/*
  @dataElement: ANC.B6.DE145 Clinical enquiry for alcohol and other substance use done
  @activity: ANC.B6 Profile
  @description: Whether or not the health worker has performed a clinical enquiry into alcohol and substance use
*/
define "Clinical enquiry for alcohol and other substance use done":
  WC.Only(
  [Observation: Cx."Clinical enquiry for alcohol and other substance use done"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE146 Current alcohol and/or other substance use
  @activity: ANC.B6 Profile
  @description: Whether or not the woman currently consumes any alcohol or substances
*/
define "Current alcohol and/or other substance use":
  [Observation: Cx."Current alcohol and/or other substance use"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE15 Ultrasound done
  @activity: ANC.B6 Profile
  @description: Whether or not the woman has had an ultrasound scan done at any point in this pregnancy
*/
define "Ultrasound done":
  WC.MostRecent(
  [Observation: Cx."Ultrasound done"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B6.DE153 Tobacco use
  @activity: ANC.B6 Profile
  @description: Whether the woman uses tobacco products
*/
define "Tobacco use":
  WC.Only(
  [Observation: Cx."Tobacco use"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B6.DE154 Recently quit tobacco products
  @activity: ANC.B6 Profile
  @description: Whether the woman has recently quit using any tobacco products
*/
define "Recently quit tobacco products":
  WC.Only(
  [Observation: Cx."Recently quit tobacco products"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B6.DE155 Exposure to second-hand smoke
  @activity: ANC.B6 Profile
  @description: Anyone in the household smokes tobacco products – whether the woman is exposed to second-hand smoke from anyone in her household
*/
define "Exposure to second-hand smoke":
  WC.Only(
  [Observation: Cx."Exposure to second-hand smoke"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B6.DE156 Partner HIV status (reported)
  @activity: ANC.B6 Profile
  @description: The HIV status of the woman's partner
*/
define "Partner HIV status (reported)":
  WC.Only(
  [Observation: Cx."Partner HIV status (reported)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE160 Contraceptive use of male condoms
  @activity: ANC.B6 Profile
  @description: Whether or not the woman (and her partner) use male condoms during sex
*/
define "Contraceptive use of male condoms":
  WC.Only(
  [Observation: Cx."Contraceptive use of male condoms"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B6.DE161 Contraceptive use of female condoms
  @activity: ANC.B6 Profile
  @description: Whether or not the woman (and her partner) use female condoms during sex
*/
define "Contraceptive use of female condoms":
  WC.Only(
  [Observation: Cx."Contraceptive use of female condoms"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B6.DE17 Gestational age
  @activity: ANC.B6 Profile
  @description: Gestational age in weeks and/or days depending on the source of gestational age
*/
define "Gestational age":
  WC.MostRecent(
  [Observation: Cx."Gestational age"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B6.DE18 Source of gestational age
  @activity: ANC.B6 Profile
  @description: Gestational age can be calculated multiple ways – this data element describes where the gestational age above has been calculated from
*/
define "Source of gestational age":
  WC.MostRecent(
  [Observation: Cx."Source of gestational age"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE22 Expected date of delivery (EDD)
  @activity: ANC.B6 Profile
  @description: Expected date of delivery based on gestational age
*/
define "Expected date of delivery (EDD)":
  WC.Only(
  [Observation: Cx."Expected date of delivery (EDD)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.dateTime


/*
  @dataElement: ANC.B6.DE24 Number of pregnancies (gravida)
  @activity: ANC.B6 Profile
  @description: Total number of times the woman has been pregnant (including this pregnancy); also referred to as gravida
*/
define "Number of pregnancies (gravida)":
  WC.Only(
  [Observation: Cx."Number of pregnancies (gravida)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.integer


/*
  @dataElement: ANC.B6.DE25 Number of miscarriages and/or abortions
  @activity: ANC.B6 Profile
  @description: Total number of pregnancies lost/ended due to miscarriages and/or abortions before 22 weeks / 5 months
*/
define "Number of miscarriages and/or abortions":
  WC.Only(
  [Observation: Cx."Number of miscarriages and/or abortions"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.integer


/*
  @dataElement: ANC.B6.DE26 Number of live births
  @activity: ANC.B6 Profile
  @description: Total number of live births after 22 weeks
*/
define "Number of live births":
  WC.Only(
  [Observation: Cx."Number of live births"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.integer


/*
  @dataElement: ANC.B6.DE27 Whether last live birth was preterm
  @activity: ANC.B6 Profile
  @description: Was the last live birth preterm? Whether the last live birth was preterm (i.e. less than 37 weeks gestation at the time of delivery)
*/
define "Whether last live birth was preterm":
  WC.MostRecent(
  [Observation: Cx."Whether last live birth was preterm"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE31 Number of stillbirths
  @activity: ANC.B6 Profile
  @description: Total number of stillbirths after 22 weeks
*/
define "Number of stillbirths":
  WC.MostRecent(
  [Observation: Cx."Number of stillbirths"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.integer


/*
  @dataElement: ANC.B6.DE32 Parity
  @activity: ANC.B6 Profile
  @description: Calculated parity is the total number of live and stillbirths
*/
define "Parity":
  WC.MostRecent(
  [Observation: Cx."Parity"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.integer


/*
  @dataElement: ANC.B6.DE33 Number of caesarian sections
  @activity: ANC.B6 Profile
  @description: Total number of caesarean sections
*/
define "Number of caesarian sections":
  WC.MostRecent(
  [Observation: Cx."Number of caesarian sections"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.integer


/*
  @dataElement: ANC.B6.DE34 Past pregnancy complications
  @activity: ANC.B6 Profile
  @description: Mark whether the woman has had any complications or problems in any previous pregnancy
*/
define "Past pregnancy complications":
  [Observation: Cx."Past pregnancy complications"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE51 Substance use during past pregnancy specification
  @activity: ANC.B6 Profile
  @description: If selected substance use, specify and indicate what type of substances used during past pregnancies
*/
define "Substance use during past pregnancy specification":
  [Observation: Cx."Substance use during past pregnancy specification"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE57 Allergies
  @activity: ANC.B6 Profile
  @description: Does the woman have any allergies?
*/
define "Allergies":
  [Observation: Cx."Allergies"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE72 Past surgeries
  @activity: ANC.B6 Profile
  @description: Has the woman had any prior surgeries?
*/
define "Past surgeries":
  [Observation: Cx."Past surgeries"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE83 Existing chronic health conditions
  @activity: ANC.B6 Profile
  @description: Does the woman have any current chronic health conditions or problems?
*/
define "Existing chronic health conditions":
  [Observation: Cx."Existing chronic health conditions"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B6.DE99 HIV diagnosis date
  @activity: ANC.B6 Profile
  @description: If woman knows she's HIV+, record the date of her diagnosis
*/
define "HIV diagnosis date":
  [Observation: Cx."HIV diagnosis date"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B6.DE16 Ultrasound date
  @activity: ANC.B6 Profile
  @description: Date that the ultrasound was done
*/
define "Ultrasound date":
  WC.MostRecent(
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).performed as FHIR.dateTime


/*
  @dataElement: ANC.B9.DE3 Ultrasound scan conducted
  @activity: ANC.B6 Profile
  @description: Whether an ultrasound scan was conducted during a specific contact
*/
define "Ultrasound scan conducted":
  WC.Only(
  [Procedure: Cx."Ultrasound scan conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B7.DE1 Medication follow-up
  @activity: ANC.B7 Symptoms & Follow-up
  @description: Which of the medications(including supplements and vitamins) is the woman taking and any side effects?
*/
define "Medication follow-up":
  [Observation: Cx."Medication follow-up"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B7.DE18 Persistent physiological symptoms
  @activity: ANC.B7 Symptoms & Follow-up
  @description: Which of the following physiological symptoms persist? Select which of the following physiological symptoms persist after being reported during last contact
*/
define "Persistent physiological symptoms":
  [Observation: Cx."Persistent physiological symptoms"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B7.DE43 Current physiological symptoms
  @activity: ANC.B7 Symptoms & Follow-up
  @description: The physiological symptoms that the woman is experiencing right now: select "None" if she is experiencing none of these symptoms
*/
define "Current physiological symptoms":
  [Observation: Cx."Current physiological symptoms"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B7.DE71 Presenting signs and symptoms that trigger suspicion of IPV and clinical inquiry
  @activity: ANC.B7 Symptoms & Follow-up
  @description: Presenting signs and symptoms that trigger clinical enquiry of IPV. The Input Options listed below are based on Health care for women subjected to intimate partner violence or sexual violence – a clinical handbook (3)
*/
define "Presenting signs and symptoms that trigger suspicion of IPV and clinical inquiry":
  [Observation: Cx."Presenting signs and symptoms that trigger suspicion of IPV and clinical inquiry"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B7.DE97 Woman discloses or is suspected to be subjected to intimate partner violence
  @activity: ANC.B7 Symptoms & Follow-up
  @description: Woman discloses or is suspected   to be subjected to intimate partner violence
*/
define "Woman discloses or is suspected to be subjected to intimate partner violence":
  WC.Only(
  [Observation: Cx."Woman discloses or is suspected to be subjected to intimate partner violence"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B7.DE98 Fetal movement
  @activity: ANC.B7 Symptoms & Follow-up
  @description: Whether the woman has felt the baby move or not or if the baby's movements have decreased
*/
define "Fetal movement":
  WC.Only(
  [Observation: Cx."Fetal movement"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B7.DED9 Persistent behaviours
  @activity: ANC.B7 Symptoms & Follow-up
  @description: Which of the following behaviours persist? Select which of the following behaviours persist after being reported during last contact
*/
define "Persistent behaviours":
  [Observation: Cx."Persistent behaviours"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE128 Has the woman  been subjected to intimate partner violence
  @activity: ANC.B7 Symptoms & Follow-up
  @description: Whether the woman has been subjected to intimate partner violence
*/
define "Has the woman  been subjected to intimate partner violence":
  WC.Only(
  [Observation: Cx."Has the woman  been subjected to intimate partner violence"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B8.DE1 Height
  @activity: ANC.B8 Physical exam
  @description: The woman's current height in centimetres
*/
define "Height":
  WC.Only(
  [Observation: Cx."Height"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE10 Expected weight gain
  @activity: ANC.B8 Physical exam
  @description: Expected weight gain during pregnancy is based on the woman's weight category
*/
define "Expected weight gain":
  WC.Only(
  [Observation: Cx."Expected weight gain"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE100 Oedema severity
  @activity: ANC.B8 Physical exam
  @description: Severity of the oedema
*/
define "Oedema severity":
  WC.Only(
  [Observation: Cx."Oedema severity"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE105 Symphysis-fundal height (SFH)
  @activity: ANC.B8 Physical exam
  @description: The symphysis-fundal height (SFH) in centimetres
*/
define "Symphysis-fundal height (SFH)":
  WC.Only(
  [Observation: Cx."Symphysis-fundal height (SFH)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE106 Fetal heartbeat present
  @activity: ANC.B8 Physical exam
  @description: Whether or not the health worker observes a fetal heartbeat
*/
define "Fetal heartbeat present":
  WC.Only(
  [Observation: Cx."Fetal heartbeat present"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B8.DE107 Fetal heart rate
  @activity: ANC.B8 Physical exam
  @description: The fetal heart rate in beats per minute (bpm)
*/
define "Fetal heart rate":
  WC.Earliest(
  [Observation: Cx."Fetal heart rate"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE108 Second fetal heart rate
  @activity: ANC.B8 Physical exam
  @description: The second fetal heart rate reading in beats per minute (bpm)
*/
define "Second fetal heart rate":
  WC.MostRecent(
  [Observation: Cx."Second fetal heart rate"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE109 Number of fetuses
  @activity: ANC.B8 Physical exam
  @description: Indicate the number of fetuses the woman is carrying
*/
define "Number of fetuses":
  WC.Only(
  [Observation: Cx."Number of fetuses"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE110 Number of fetuses unknown
  @activity: ANC.B8 Physical exam
  @description: Number of fetuses cannot be determined at this time, thus the number of fetuses is unknown
*/
define "Number of fetuses unknown":
  WC.Only(
  [Observation: Cx."Number of fetuses unknown"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE111 Fetal presentation
  @activity: ANC.B8 Physical exam
  @description: If a single fetus only, indicate the presentation of the fetus in the uterus
*/
define "Fetal presentation":
  WC.Only(
  [Observation: Cx."Fetal presentation"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE117 Presenting signs/conditions for IPV
  @activity: ANC.B8 Physical exam
  @description: Signs or conditions that are due to or trigger suspicion of  intimate partner violence?
*/
define "Presenting signs/conditions for IPV":
  [Observation: Cx."Presenting signs/conditions for IPV"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE121 Clinical enquiry for IPV
  @activity: ANC.B8 Physical exam
  @description: Whether or not clinical enquiry for IPV was conducted based on presenting signs and symptoms and conditions
*/
define "Clinical enquiry for IPV":
  WC.Only(
  [Observation: Cx."Clinical enquiry for IPV"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B8.DE122 Reason clinical enquiry not done
  @activity: ANC.B8 Physical exam
  @description: Reason clinical enquiry not done
*/
define "Reason clinical enquiry not done":
  WC.Only(
  [Observation: Cx."Reason clinical enquiry not done"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE129 Types of IPV
  @activity: ANC.B8 Physical exam
  @description: What type(s) of violence has the woman been subjected to?
*/
define "Types of IPV":
  [Observation: Cx."Types of IPV"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE15 Average weight gain per week
  @activity: ANC.B8 Physical exam
  @description: Average weight gain per week since last contact (kg); this would only display starting at the 2nd contact
*/
define "Average weight gain per week":
  WC.Only(
  [Observation: Cx."Average weight gain per week"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE16 Total weight gain (kg)
  @activity: ANC.B8 Physical exam
  @description: Total weight gain in pregnancy so far (kg)
*/
define "Total weight gain (kg)":
  WC.Only(
  [Observation: Cx."Total weight gain (kg)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE17 Systolic blood pressure
  @activity: ANC.B8 Physical exam
  @description: Systolic blood pressure (SBP) in mmHg
*/
define "Systolic blood pressure":
  WC.Earliest(
  [Observation: Cx."Systolic blood pressure"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE19 Diastolic blood pressure
  @activity: ANC.B8 Physical exam
  @description: Diastolic blood pressure (DBP) in mmHg
*/
define "Diastolic blood pressure":
  WC.Earliest(
  [Observation: Cx."Diastolic blood pressure"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE2 Pre-gestational weight
  @activity: ANC.B8 Physical exam
  @description: The woman's pre-gestational weight in kilograms
*/
define "Pre-gestational weight":
  WC.Only(
  [Observation: Cx."Pre-gestational weight"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE20 Repeat systolic blood pressure
  @activity: ANC.B8 Physical exam
  @description: Repeat measurement of the woman's systolic blood pressure in mmHg after 10–15 minutes rest
*/
define "Repeat systolic blood pressure":
  WC.MostRecent(
  [Observation: Cx."Repeat systolic blood pressure"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE21 Repeat diastolic blood pressure
  @activity: ANC.B8 Physical exam
  @description: Repeat measurement of the woman's diastolic blood pressure in mmHg after 10–15 minutes rest
*/
define "Repeat diastolic blood pressure":
  WC.MostRecent(
  [Observation: Cx."Repeat diastolic blood pressure"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE22 Blood pressure cannot be taken
  @activity: ANC.B8 Physical exam
  @description: If the woman's blood pressure cannot be taken, this should be indicated here; otherwise, blood pressure should be measured
*/
define "Blood pressure cannot be taken":
  WC.Only(
  [Observation: Cx."Blood pressure cannot be taken"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B8.DE23 Reason blood pressure cannot be taken
  @activity: ANC.B8 Physical exam
  @description: The reason why the health worker is unable to record the woman's BP during this contact visit
*/
define "Reason blood pressure cannot be taken":
  WC.Only(
  [Observation: Cx."Reason blood pressure cannot be taken"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE27 Symptoms of severe pre-eclampsia
  @activity: ANC.B8 Physical exam
  @description: Check if the woman has any of the following symptoms of severe pre-eclampsia
*/
define "Symptoms of severe pre-eclampsia":
  [Observation: Cx."Symptoms of severe pre-eclampsia"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE3 Current  weight
  @activity: ANC.B8 Physical exam
  @description: The woman's current weight in kilograms
*/
define "Current  weight":
  WC.Only(
  [Observation: Cx."Current  weight"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE34 Body temperature
  @activity: ANC.B8 Physical exam
  @description: The woman's body temperature in degrees Celsius  (°C) (first reading)
*/
define "Body temperature":
  WC.Earliest(
  [Observation: Cx."Body temperature"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE35 Second body temperature
  @activity: ANC.B8 Physical exam
  @description: The woman's body temperature in degrees Celsius (°C) (second reading)
*/
define "Second body temperature":
  WC.MostRecent(
  [Observation: Cx."Second body temperature"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE36 Pulse rate
  @activity: ANC.B8 Physical exam
  @description: The woman's pulse rate in beats per minute (bpm)
*/
define "Pulse rate":
  WC.Earliest(
  [Observation: Cx."Pulse rate"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE37 Second pulse rate
  @activity: ANC.B8 Physical exam
  @description: The woman's second pulse rate in beats per minute (bpm)
*/
define "Second pulse rate":
  WC.MostRecent(
  [Observation: Cx."Second pulse rate"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE38 Pallor present
  @activity: ANC.B8 Physical exam
  @description: Whether or not the woman has pallor
*/
define "Pallor present":
  WC.Only(
  [Observation: Cx."Pallor present"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B8.DE39 Respiratory exam result
  @activity: ANC.B8 Physical exam
  @description: The woman's respiratory condition based on respiratory exam
*/
define "Respiratory exam result":
  WC.Only(
  [Observation: Cx."Respiratory exam result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE4 BMI
  @activity: ANC.B8 Physical exam
  @description: Body mass index (BMI): calculated by taking weight in kg divided by the squared height in meters, i.e. kg/(m^2)
*/
define "BMI":
  WC.Only(
  [Observation: Cx."BMI"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE49 Oximetry
  @activity: ANC.B8 Physical exam
  @description: The measure of the oxygen saturation (%) in the woman's blood: this is usually measured using a pulse oximeter medical device
*/
define "Oximetry":
  WC.Only(
  [Observation: Cx."Respiratory exam result - Oximetry"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE5 Weight category
  @activity: ANC.B8 Physical exam
  @description: Weight category is calculated from BMI
*/
define "Weight category":
  WC.Only(
  [Observation: Cx."Weight category"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE50 Cardiac exam result
  @activity: ANC.B8 Physical exam
  @description: The woman's cardiac condition
*/
define "Cardiac exam result":
  WC.Only(
  [Observation: Cx."Cardiac exam result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE61 Breast exam result
  @activity: ANC.B8 Physical exam
  @description: Whether or not the result of the breast exam is normal
*/
define "Breast exam result":
  WC.Only(
  [Observation: Cx."Breast exam result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE71 Abdominal exam result
  @activity: ANC.B8 Physical exam
  @description: Whether or not the result of the abdominal exam is normal
*/
define "Abdominal exam result":
  WC.Only(
  [Observation: Cx."Abdominal exam result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE79 Pelvic exam result (visual)
  @activity: ANC.B8 Physical exam
  @description: Whether or not the result of the pelvic exam is normal
*/
define "Pelvic exam result (visual)":
  WC.Only(
  [Observation: Cx."Pelvic exam result (visual)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B8.DE92 Cervical exam conducted
  @activity: ANC.B8 Physical exam
  @description: Whether or not the health worker performed a cervical exam
*/
define "Cervical exam conducted":
  WC.Only(
  [Observation: Cx."Cervical exam conducted"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B8.DE93 Cervical dilation
  @activity: ANC.B8 Physical exam
  @description: If a cervical exam was done, how many centimeters is the cervix dilated?
*/
define "Cervical dilation":
  WC.Only(
  [Observation: Cx."Cervical dilation"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B8.DE94 Oedema present
  @activity: ANC.B8 Physical exam
  @description: Whether or not the woman has oedema
*/
define "Oedema present":
  WC.Only(
  [Observation: Cx."Oedema present"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B8.DE95 Oedema type
  @activity: ANC.B8 Physical exam
  @description: Select the type of oedema the woman has
*/
define "Oedema type":
  [Observation: Cx."Oedema type"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE1 Ultrasound required
  @activity: ANC.B9 Lab tests & imaging
  @description: Woman requires an ultrasound scan
*/
define "Ultrasound required":
  WC.Only(
  [Observation: Cx."Ultrasound required"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B9.DE102 Reason syphilis test not done
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the reason why a syphilis test was not done
*/
define "Reason syphilis test not done":
  [Observation: Cx."Reason syphilis test not done"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE107 Syphilis test result
  @activity: ANC.B9 Lab tests & imaging
  @description: Syphilis test result
*/
define "Syphilis test result":
  WC.Only(
  [Observation: Cx."Syphilis test result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE110 Syphilis diagnosis
  @activity: ANC.B9 Lab tests & imaging
  @description: Calculation for whether woman has syphilis (1) or not (0)
*/
define "Syphilis diagnosis":
  WC.Only(
  [Observation: Cx."Syphilis diagnosis"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE113 Urine test required
  @activity: ANC.B9 Lab tests & imaging
  @description: Urine test is required
*/
define "Urine test required":
  WC.Only(
  [Observation: Cx."Urine test required"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B9.DE115 Urine test ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: Urine test has been ordered
*/
define "Urine test ordered":
  WC.Only(
  [ServiceRequest: Cx."Urine test ordered"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE116 Urine test type
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the type of urine test that was done. Of the three types of tests, the midstream urine culture is the preferred method. If culture is not available, then midstream Gram-staining test is preferred over dipstick. Urine dipstick test is required if need to check urine for protein.
*/
define "Urine test type":
  [Observation: Cx."Urine test type"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return O.value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE120 Reason urine test not done
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the reason why a urine test was not done at this contact
*/
define "Reason urine test not done":
  [Procedure: Cx."Reason urine test not done"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B9.DE124 Midstream urine culture (recommended)
  @activity: ANC.B9 Lab tests & imaging
  @description: Midstream urine culture is the recommended method for diagnosing asymptomatic bacteriuria (ASB) in pregnancy
*/
define "Midstream urine culture (recommended)":
  WC.Only(
  [Observation: Cx."Midstream urine culture (recommended)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE129 Midstream urine Gram-staining
  @activity: ANC.B9 Lab tests & imaging
  @description: On-site midstream urine Gram-staining is recommended over the use of dipstick tests as the method for diagnosing ASB in pregnancy if midstream culture is not possible
*/
define "Midstream urine Gram-staining":
  WC.Only(
  [Observation: Cx."Midstream urine Gram-staining"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE13 Placenta location
  @activity: ANC.B9 Lab tests & imaging
  @description: Location of the placenta detected during the ultrasound
*/
define "Placenta location":
  WC.Only(
  [Observation: Cx."Placenta location"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE132 Urine dipstick result - nitrites
  @activity: ANC.B9 Lab tests & imaging
  @description: Dipstick test results – nitrites
*/
define "Urine dipstick result - nitrites":
  WC.Only(
  [Observation: Cx."Urine dipstick result - nitrites"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE138 Urine dipstick result - leukocytes
  @activity: ANC.B9 Lab tests & imaging
  @description: Dipstick test results – leukocytes
*/
define "Urine dipstick result - leukocytes":
  WC.Only(
  [Observation: Cx."Urine dipstick result - leukocytes"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE144 Urine dipstick result - protein
  @activity: ANC.B9 Lab tests & imaging
  @description: Enter the result for the dipstick test – protein
*/
define "Urine dipstick result - protein":
  WC.Only(
  [Observation: Cx."Urine dipstick result - protein"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE150 Urine dipstick result - glucose
  @activity: ANC.B9 Lab tests & imaging
  @description: Dipstick test results – glucose
*/
define "Urine dipstick result - glucose":
  WC.Only(
  [Observation: Cx."Urine dipstick result - glucose"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE156 Asymptomatic bacteriuria (ASB) diagnosis
  @activity: ANC.B9 Lab tests & imaging
  @description: A woman is considered to have ASB if she has one of the following test results:?– Positive culture (> 100,000 bacteria/mL)?– Gram-staining positive?– Urine dipstick test positive (nitrites or leukocytes)
*/
define "Asymptomatic bacteriuria (ASB) diagnosis":
  WC.Only(
  [Observation: Cx."Asymptomatic bacteriuria (ASB) diagnosis"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE160 Blood glucose test ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: Blood glucose test has been ordered
*/
define "Blood glucose test ordered":
  WC.Only(
  [ServiceRequest: Cx."Blood glucose test ordered"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE162 Blood glucose test type
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the type of blood glucose test done
*/
define "Blood glucose test type":
  WC.Only(
  [Observation: Cx."Blood glucose test type"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE163 Fasting plasma glucose
  @activity: ANC.B9 Lab tests & imaging
  @description: Fasting plasma glucose
*/
define "Fasting plasma glucose":
  WC.Only(
  [Observation: Cx."Fasting plasma glucose"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B9.DE164 75 g oral glucose tolerance test (OGTT) - fasting glucose
  @activity: ANC.B9 Lab tests & imaging
  @description: 75 g oral glucose tolerance test (OGTT) – fasting glucose
*/
define "75 g oral glucose tolerance test (OGTT) - fasting glucose":
  WC.Only(
  [Observation: Cx."75 g oral glucose tolerance test (OGTT) - fasting glucose"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B9.DE165 75 g oral glucose tolerance test (OGTT) - 1 hour
  @activity: ANC.B9 Lab tests & imaging
  @description: 75 g oral glucose tolerance test (OGTT) –  1 hour
*/
define "75 g oral glucose tolerance test (OGTT) - 1 hour":
  WC.Only(
  [Observation: Cx."75 g oral glucose tolerance test (OGTT) - 1 hour"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B9.DE166 75 g oral glucose tolerance test (OGTT) - 2 hours
  @activity: ANC.B9 Lab tests & imaging
  @description: 75 g oral glucose tolerance test (OGTT) – 2 hours
*/
define "75 g oral glucose tolerance test (OGTT) - 2 hours":
  WC.Only(
  [Observation: Cx."75 g oral glucose tolerance test (OGTT) - 2 hours"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B9.DE167 Random plasma glucose
  @activity: ANC.B9 Lab tests & imaging
  @description: Random plasma glucose
*/
define "Random plasma glucose":
  WC.Only(
  [Observation: Cx."Random plasma glucose"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B9.DE168 Blood glucose test result
  @activity: ANC.B9 Lab tests & imaging
  @description: Enter the result for the blood glucose test (mg/dl)
*/
define "Blood glucose test result":
  WC.Only(
  [Observation: Cx."Blood glucose test result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B9.DE169 Gestational diabetes mellitus (GDM) diagnosis
  @activity: ANC.B9 Lab tests & imaging
  @description: Whether the woman has gestational diabetes mellitus (GDM)
*/
define "Gestational diabetes mellitus (GDM) diagnosis":
  WC.Only(
  [Observation: Cx."Gestational diabetes mellitus (GDM) diagnosis"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE172 Diabetes mellitus (DM) during pregnancy diagnosis
  @activity: ANC.B9 Lab tests & imaging
  @description: Whether the woman has developed diabetes mellitus during pregnancy
*/
define "Diabetes mellitus (DM) during pregnancy diagnosis":
  WC.Only(
  [Observation: Cx."Diabetes mellitus (DM) during pregnancy diagnosis"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE176 Blood haemoglobin test ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: Whether or not the blood haemoglobin test (Hb test) has been ordered
*/
define "Blood haemoglobin test ordered":
  WC.Only(
  [ServiceRequest: Cx."Blood haemoglobin test ordered"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE177 Blood haemoglobin test type
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the type of Hb test that was done
*/
define "Blood haemoglobin test type":
  WC.Only(
  [Observation: Cx."Blood haemoglobin test type"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE181 Reason blood haemoglobin test not done
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the reason why a Hb test was not done
*/
define "Reason blood haemoglobin test not done":
  [Procedure: Cx."Reason blood haemoglobin test not done"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B9.DE186 Blood haemoglobin test result
  @activity: ANC.B9 Lab tests & imaging
  @description: Blood haemoglobin test result in g/dl
*/
define "Blood haemoglobin test result":
  WC.Only(
  [Observation: Cx."Blood haemoglobin test result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B9.DE187 Anaemia diagnosis
  @activity: ANC.B9 Lab tests & imaging
  @description: Calculated field to determine woman's anaemia status
*/
define "Anaemia diagnosis":
  WC.Only(
  [Observation: Cx."Anaemia diagnosis"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE190 Hematocrit (Ht)
  @activity: ANC.B9 Lab tests & imaging
  @description: Hematocrit percentage
*/
define "Hematocrit (Ht)":
  WC.Only(
  [Observation: Cx."Hematocrit (Ht)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B9.DE191 White blood cell (WBC) count
  @activity: ANC.B9 Lab tests & imaging
  @description: Full white blood cells count
*/
define "White blood cell (WBC) count":
  WC.Only(
  [Observation: Cx."White blood cell (WBC) count"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B9.DE192 Platelet count
  @activity: ANC.B9 Lab tests & imaging
  @description: Full platelets count
*/
define "Platelet count":
  WC.Only(
  [Observation: Cx."Platelet count"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.Quantity


/*
  @dataElement: ANC.B9.DE193 TB screening recommended
  @activity: ANC.B9 Lab tests & imaging
  @description: TB screening is recommended
*/
define "TB screening recommended":
  WC.Only(
  [ServiceRequest] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE195 TB screening ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: TB screening has been ordered
*/
define "TB screening ordered":
  WC.Only(
  [ServiceRequest: Cx."TB screening ordered"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE196 Reason TB screening not done
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the reason why TB screening was not done
*/
define "Reason TB screening not done":
  [Procedure: Cx."Reason TB screening not done"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B9.DE2 Ultrasound scan ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: An ultrasound scan has been ordered
*/
define "Ultrasound scan ordered":
  WC.Only(
  [ServiceRequest: Cx."Ultrasound scan ordered"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE206 TB screening result
  @activity: ANC.B9 Lab tests & imaging
  @description: Record the result of the TB screening
*/
define "TB screening result":
  WC.Only(
  [Observation: Cx."TB screening result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE21 Blood type test ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: Blood type test has been ordered
*/
define "Blood type test ordered":
  WC.Only(
  [ServiceRequest] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE214 Other test(s) result(s)
  @activity: ANC.B9 Lab tests & imaging
  @description: Input the result from the test(s)
*/
define "Other test(s) result(s)":
  WC.Only(
  [Observation: Cx."Other test(s) result(s)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE24 Blood type
  @activity: ANC.B9 Lab tests & imaging
  @description: The client's blood type
*/
define "Blood type":
  WC.Only(
  [Observation: Cx."Blood type"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE29 Rh factor
  @activity: ANC.B9 Lab tests & imaging
  @description: The client's Rhesus (Rh) factor
*/
define "Rh factor":
  WC.Only(
  [Observation: Cx."Rh factor"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE33 HIV test required
  @activity: ANC.B9 Lab tests & imaging
  @description: HIV test is required
*/
define "HIV test required":
  WC.Only(
  [Observation: Cx."HIV test required"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B9.DE34 HIV test ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: HIV test has been ordered
*/
define "HIV test ordered":
  WC.Only(
  [ServiceRequest: Cx."HIV test ordered"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE36 Reason HIV test not done
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the reason why an HIV test was not done
*/
define "Reason HIV test not done":
  [Procedure: Cx."Reason HIV test not done"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B9.DE41 HIV test result
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the result of the HIV test
*/
define "HIV test result":
  WC.Only(
  [Observation: Cx."HIV test result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE45 HIV status
  @activity: ANC.B9 Lab tests & imaging
  @description: Calculated field for woman's HIV status
*/
define "HIV status":
  WC.Only(
  [Observation: Cx."HIV status"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE48 Partner HIV test result
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the result of her partner's HIV test
*/
define "Partner HIV test result":
  WC.Only(
  [Observation: Cx."Partner HIV test result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE49 Partner HIV test ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: An HIV test for the woman's partner has been ordered
*/
define "Partner HIV test ordered":
  WC.Only(
  [ServiceRequest: Cx."Partner HIV test ordered"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE5 Reason ultrasound scan not done
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the reason why an ultrasound scan was not done
*/
define "Reason ultrasound scan not done":
  WC.Only(
  [Procedure: Cx."Reason ultrasound scan not done"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE55 Partner HIV status (confirmed)
  @activity: ANC.B9 Lab tests & imaging
  @description: Partner's confirmed HIV status
*/
define "Partner HIV status (confirmed)":
  WC.Only(
  [Observation: Cx."Partner HIV status (confirmed)"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE58 High risk for HIV
  @activity: ANC.B9 Lab tests & imaging
  @description: If the woman lives in a setting with a generalized HIV epidemic, or her partner is HIV positive, or she injects drugs, or she has an employment at increased risk for HIV, then she is at high risk for HIV
*/
define "High risk for HIV":
  WC.Only(
  [Observation: Cx."High risk for HIV"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B9.DE59 Hepatitis B test required
  @activity: ANC.B9 Lab tests & imaging
  @description: Hepatitis B test is required
*/
define "Hepatitis B test required":
  WC.Only(
  [Observation: Cx."Hepatitis B test required"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B9.DE61 Hepatitis B test ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: Hepatitis B test has been ordered
*/
define "Hepatitis B test ordered":
  WC.Only(
  [ServiceRequest: Cx."Hepatitis B test ordered"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE62 Hepatitis B test type
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the type of Hep B test done
*/
define "Hepatitis B test type":
  WC.Only(
  [Observation: Cx."Hepatitis B test type"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE66 Hepatitis B test result
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the reason why a Hep B test was not done
*/
define "Hepatitis B test result":
  [Procedure: Cx."Hepatitis B test result"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B9.DE74 Hepatitis B diagnosis
  @activity: ANC.B9 Lab tests & imaging
  @description: This calculates the woman's Hep B infection status as positive (1) or negative (0), taking into account any of the three methods for Hep B testing
*/
define "Hepatitis B diagnosis":
  WC.Only(
  [Observation: Cx."Hepatitis B diagnosis"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE77 Hepatitis C test required
  @activity: ANC.B9 Lab tests & imaging
  @description: Hepatitis C test is required
*/
define "Hepatitis C test required":
  WC.Only(
  [Observation: Cx."Hepatitis C test required"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B9.DE79 Hepatitis C test ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: Hepatitis C test has been ordered
*/
define "Hepatitis C test ordered":
  [ServiceRequest: Cx."Hepatitis C test ordered"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B9.DE80 Hepatitis C test type
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the type of Hep C test that was done
*/
define "Hepatitis C test type":
  WC.Only(
  [Observation: Cx."Hepatitis C test type"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE84 Reason Hepatitis C test not done
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the reason why a Hep C test was not done
*/
define "Reason Hepatitis C test not done":
  [Procedure: Cx."Reason Hepatitis C test not done"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B9.DE89 Hepatitis C test result
  @activity: ANC.B9 Lab tests & imaging
  @description: Hepatitis C test result
*/
define "Hepatitis C test result":
  WC.Only(
  [Observation: Cx."Hepatitis C test result"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE9 Amniotic fluid level
  @activity: ANC.B9 Lab tests & imaging
  @description: Amniotic fluid level detected during ultrasound
*/
define "Amniotic fluid level":
  WC.Only(
  [Observation: Cx."Amniotic fluid level"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE92 Hepatitis C diagnosis
  @activity: ANC.B9 Lab tests & imaging
  @description: This calculates the woman's Hep C infection status as positive (1) or negative (0), taking into account any of the three methods for Hep C testing
*/
define "Hepatitis C diagnosis":
  WC.Only(
  [Observation: Cx."Hepatitis C diagnosis"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE97 Syphilis test ordered
  @activity: ANC.B9 Lab tests & imaging
  @description: Syphilis test has been ordered
*/
define "Syphilis test ordered":
  WC.Only(
  [ServiceRequest: Cx."Syphilis test ordered"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE98 Syphilis test type
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the type of syphilis test that was done
*/
define "Syphilis test type":
  WC.Only(
  [Observation: Cx."Syphilis test type"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B9.DE161 Blood glucose test date
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the date of the glucose test
*/
define "Blood glucose test date":
  WC.Only(
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).performed as FHIR.dateTime


/*
  @dataElement: ANC.B9.DE175 Blood haemoglobin test conducted
  @activity: ANC.B9 Lab tests & imaging
  @description: A blood haemoglobin test (Hb test) is required for all women multiple times during pregnancy to determine whether she's anaemic
*/
define "Blood haemoglobin test conducted":
  WC.Only(
  [Procedure: Cx."Blood haemoglobin test conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE185 Blood haemoglobin test date
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the date of the Hb test
*/
define "Blood haemoglobin test date":
  WC.Only(
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).performed as FHIR.dateTime


/*
  @dataElement: ANC.B9.DE22 Blood type test conducted
  @activity: ANC.B9 Lab tests & imaging
  @description: Blood type test has been conducted
*/
define "Blood type test conducted":
  WC.Only(
  [Procedure: Cx."Blood type test conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE23 Blood type test date
  @activity: ANC.B9 Lab tests & imaging
  @description: Date of the blood type test
*/
define "Blood type test date":
  WC.Only(
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).performed as FHIR.dateTime


/*
  @dataElement: ANC.B9.DE60 Hepatitis B test conducted
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the status of the Hep B test – record result, ordered (pending) or not done
*/
define "Hepatitis B test conducted":
  WC.Only(
  [Procedure: Cx."Hepatitis B test conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE70 Hepatitis B test date
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the date of the hepatitis B test
*/
define "Hepatitis B test date":
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B9.DE78 Hepatitis C test conducted
  @activity: ANC.B9 Lab tests & imaging
  @description: In a setting where the proportion of HCV antibody seroprevalence in the general population is ≥ 2% or the woman is HIV positive, injects drugs or is a sex worker, then a Hep C test is required
*/
define "Hepatitis C test conducted":
  WC.Only(
  [Procedure: Cx."Hepatitis C test conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE88 Hepatitis C test date
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the date of the hepatitis C test
*/
define "Hepatitis C test date":
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B9.DE35 HIV test conducted
  @activity: ANC.B9 Lab tests & imaging
  @description: An HIV test is required for all pregnant women at the first contact in pregnancy and again at the first contact of the 3rd trimester if the HIV prevalence in the pregnant woman population is ≥ 5%. A test is not required if the woman is already confirmed HIV+.
*/
define "HIV test conducted":
  WC.Only(
  [Procedure: Cx."HIV test conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE40 HIV test date
  @activity: ANC.B9 Lab tests & imaging
  @description: Date of the HIV test
*/
define "HIV test date":
  WC.Only(
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).performed as FHIR.dateTime


/*
  @dataElement: ANC.B9.DE211 Other tests conducted
  @activity: ANC.B9 Lab tests & imaging
  @description: If the health worker performed other tests on the woman that are not explicitly listed in the application, select "yes" here and fill in the details below
*/
define "Other tests conducted":
  WC.Only(
  [Procedure: Cx."Other tests conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE212 Other test(s) name
  @activity: ANC.B9 Lab tests & imaging
  @description: Input the name of other test(s) that were done
*/
define "Other test(s) name":
  WC.Only(
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code.text


/*
  @dataElement: ANC.B9.DE213 Other test(s) date
  @activity: ANC.B9 Lab tests & imaging
  @description: Input the date of other test(s) that were done
*/
define "Other test(s) date":
  WC.Only(
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).performed as FHIR.dateTime


/*
  @dataElement: ANC.B9.DE50 Partner HIV test conducted
  @activity: ANC.B9 Lab tests & imaging
  @description: If the woman does not know the HIV status of her partner(s), offer to test and add results here
*/
define "Partner HIV test conducted":
  WC.Only(
  [Procedure: Cx."Partner HIV test conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE51 Partner HIV test date
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the date of her partner's HIV test
*/
define "Partner HIV test date":
  WC.Only(
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).performed as FHIR.dateTime


/*
  @dataElement: ANC.B9.DE96 Syphilis test conducted
  @activity: ANC.B9 Lab tests & imaging
  @description: A syphilis test is recommended for all pregnant women at the first contact and again at the first contact of 3rd trimester; women who are already confirmed positive for syphilis do not need to be tested
*/
define "Syphilis test conducted":
  WC.Only(
  [Procedure: Cx."Syphilis test conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE106 Syphilis test date
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the date of the syphilis test
*/
define "Syphilis test date":
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B9.DE194 TB screening conducted
  @activity: ANC.B9 Lab tests & imaging
  @description: In settings where the tuberculosis (TB) prevalence in the general population is 100/100,000 population or higher or in settings with subpopulations that have very poor access to health care, or if the woman is HIV positive, TB screening is recommended
*/
define "TB screening conducted":
  WC.Only(
  [Procedure: Cx."TB screening conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE205 TB screening date
  @activity: ANC.B9 Lab tests & imaging
  @description: Record the date of the TB screening
*/
define "TB screening date":
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B9.DE114 Urine test conducted
  @activity: ANC.B9 Lab tests & imaging
  @description: A urine test is required at the first contact, last contact in 2nd trimester, and 2nd contact in 3rd trimester OR anytime the woman reports pain during urination (dysuria). A dipstick test is required if the woman has a repeat high BP reading (140/90 or higher). Otherwise, a urine test is optional. The urine test checks for bacterial or other infections that can lead to adverse outcomes for the neonate. The urine dipstick test can check for proteins in the urine, which can be a sign of pre-eclampsia.
*/
define "Urine test conducted":
  WC.Only(
  [Procedure: Cx."Urine test conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B9.DE125 Urine test date
  @activity: ANC.B9 Lab tests & imaging
  @description: Select the date of the urine test
*/
define "Urine test date":
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B10.DE1 Counselling conducted on caffeine reduction
  @activity: ANC.B10.1 Behaviour counselling
  @description: Whether or not counselling was done on lowering daily caffeine intake
*/
define "Counselling conducted on caffeine reduction":
  WC.Only(
  [Procedure: Cx."Counselling on Caffeine Reduction"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE10 Reason counselling on second-hand smoke was not conducted
  @activity: ANC.B10.1 Behaviour counselling
  @description: Select the reason why counselling on second-hand smoke cessation was not done
*/
define "Reason counselling on second-hand smoke was not conducted":
  [Procedure: Cx."Counselling on Second-Hand Smoke"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B10.DE13 Counselling conducted on condom use
  @activity: ANC.B10.1 Behaviour counselling
  @description: Whether or not counselling was done on condom use
*/
define "Counselling conducted on condom use":
  WC.Only(
  [Procedure: Cx."Counselling on Condom Use"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE14 Reason counselling on condom use was not conducted
  @activity: ANC.B10.1 Behaviour counselling
  @description: Select the reason why counselling on condom use was not done
*/
define "Reason counselling on condom use was not conducted":
  [Procedure: Cx."Counselling on Condom Use"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B10.DE17 Counselling conducted on alcohol / substance use
  @activity: ANC.B10.1 Behaviour counselling
  @description: Whether or not counselling was done on alcohol and substance use cessation
*/
define "Counselling conducted on alcohol / substance use":
  WC.Only(
  [Procedure: Cx."Counselling on Alcohol Substance Abuse"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE18 Reason counselling on alcohol / substance use was not conducted
  @activity: ANC.B10.1 Behaviour counselling
  @description: Select the reason why counselling on alcohol and substance use was not done
*/
define "Reason counselling on alcohol / substance use was not conducted":
  [Procedure: Cx."Counselling on Alcohol Substance Abuse"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B10.DE2 Reason counselling on caffeine reduction was not done
  @activity: ANC.B10.1 Behaviour counselling
  @description: The reason why the health worker did not counsel on caffeine reduction
*/
define "Reason counselling on caffeine reduction was not done":
  [Procedure: Cx."Counselling on Caffeine Reduction"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B10.DE5 Counselling conducted on tobacco cessation
  @activity: ANC.B10.1 Behaviour counselling
  @description: Whether or not counselling was done on tobacco cessation
*/
define "Counselling conducted on tobacco cessation":
  WC.Only(
  [Procedure: Cx."Counselling on Tobacco Cessation"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE6 Reason counselling on tobacco cessation was not conducted
  @activity: ANC.B10.1 Behaviour counselling
  @description: Select the reason why counselling on tobacco cessation was not done
*/
define "Reason counselling on tobacco cessation was not conducted":
  [Procedure: Cx."Counselling on Tobacco Cessation"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B10.DE9 Counselling conducted on second-hand smoke
  @activity: ANC.B10.1 Behaviour counselling
  @description: Whether or not counselling was done on second-hand smoke cessation
*/
define "Counselling conducted on second-hand smoke":
  WC.Only(
  [Procedure: Cx."Counselling on Second-Hand Smoke"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE21 Counselling conducted on non-pharma measures to relieve nausea and vomiting
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Whether or not counselling was conducted on nausea and vomiting
*/
define "Counselling conducted on non-pharma measures to relieve nausea and vomiting":
  WC.Only(
  [Procedure: Cx."Counselling on Non-Pharma Measures to Relieve Nausea and Vomiting"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE22 Reason counselling on non-pharma measures to relieve nausea and vomiting was not conducted
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Select the reason why non-pharma measures to relieve nausea and vomiting counselling was not done
*/
define "Reason counselling on non-pharma measures to relieve nausea and vomiting was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling on Non-Pharma Measures to Relieve Nausea and Vomiting"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE25 Counselling conducted on pharmacological treatments for nausea and vomiting
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Whether or not counselling for the repeat symptom "nausea and vomiting" was done
*/
define "Counselling conducted on pharmacological treatments for nausea and vomiting":
  WC.Only(
  [Procedure: Cx."Counselling on Pharmacological Measures to Relieve Nausea and Vomiting"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE26 Reason counselling on pharmacological treatments for nausea and vomiting was not conducted
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Select the reason why counselling on pharmacological treatments to relieve nausea and vomiting was not done
*/
define "Reason counselling on pharmacological treatments for nausea and vomiting was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling on Pharmacological Measures to Relieve Nausea and Vomiting"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE29 Counselling conducted on diet and lifestyle changes to prevent and relieve heartburn
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Whether or not counselling for the symptom "heartburn" was done
*/
define "Counselling conducted on diet and lifestyle changes to prevent and relieve heartburn":
  WC.Only(
  [Procedure: Cx."Counselling on Diet and Lifestyle Changes to Prevent and Relieve Heartburn"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE30 Reason counselling on diet and lifestyle changes to prevent and relieve heartburn was not conducted
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Select the reason why counselling for the symptom "Heartburn" was not done
*/
define "Reason counselling on diet and lifestyle changes to prevent and relieve heartburn was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling on Diet and Lifestyle Changes to Prevent and Relieve Heartburn"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE33 Counselling conducted on antacid preparations to relieve heartburn
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Whether or not counselling for the repeat symptom "Heartburn" was done
*/
define "Counselling conducted on antacid preparations to relieve heartburn":
  WC.Only(
  [Procedure: Cx."Counselling on Antacid Preparations to Relieve Heartburn"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE34 Reason counselling on antacid preparations to relieve heartburn was not conducted
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Select the reason why counselling for the repeat symptom "Heartburn" was not done
*/
define "Reason counselling on antacid preparations to relieve heartburn was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling on Antacid Preparations to Relieve Heartburn"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE37 Counselling on non-pharmacological treatment for the relief of leg cramps
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Whether or not counselling for the symptom "Leg cramps" was done
*/
define "Counselling on non-pharmacological treatment for the relief of leg cramps":
  WC.Only(
  [Procedure: Cx."Counselling on Non-Pharmacological Treatment for Relief of Leg Cramps"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE38 Reason counselling on non-pharmacological treatment for the relief of leg cramps was not conducted
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Select the reason why counselling for the symptom "Leg cramps" was not done
*/
define "Reason counselling on non-pharmacological treatment for the relief of leg cramps was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling on Non-Pharmacological Treatment for Relief of Leg Cramps"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE41 Counselling conducted on use of magnesium and calcium to relieve leg cramps
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Whether or not counselling for the repeat symptom "Leg cramps" was done
*/
define "Counselling conducted on use of magnesium and calcium to relieve leg cramps":
  WC.Only(
  [Procedure: Cx."Counselling on Use of Magnesium-and Calcium to Relieve Leg Cramps"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE42 Reason counselling on use of magnesium and calcium to relieve leg cramps was not conducted
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Select the reason why counselling for the repeat symptom "Leg cramps" was not done
*/
define "Reason counselling on use of magnesium and calcium to relieve leg cramps was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling on Use of Magnesium-and Calcium to Relieve Leg Cramps"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE45 Counselling conducted on dietary modifications to relieve constipation
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Whether or not counselling for the symptom "Constipation" was done
*/
define "Counselling conducted on dietary modifications to relieve constipation":
  WC.Only(
  [Procedure: Cx."Counselling on Dietary Modifications to Relieve Constipation"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE46 Reason counselling on dietary modifications to relieve constipation was not conducted
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Select the reason why counselling for the symptom "Constipation" was not done
*/
define "Reason counselling on dietary modifications to relieve constipation was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling on Dietary Modifications to Relieve Constipation"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE49 Counselling conducted on use of wheat bran or other fibre supplements to relieve constipation
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Whether or not counselling for the repeat symptom "Constipation" was done
*/
define "Counselling conducted on use of wheat bran or other fibre supplements to relieve constipation":
  WC.Only(
  [Procedure: Cx."Counselling on Use of Wheat Bran or Other Fibre Supplements to Relieve Constipation"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE50 Reason counselling on use of wheat bran or other fibre supplements to relieve constipation was not conducted
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Select the reason why counselling for the repeat symptom "Constipation" was not done
*/
define "Reason counselling on use of wheat bran or other fibre supplements to relieve constipation was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling on Use of Wheat Bran or Other Fibre Supplements to Relieve Constipation"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE53 Counselling conducted on regular exercise, physiotherapy, support belts and acupuncture to relieve low back and pelvic pain
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Whether or not counselling for the symptom "low back and pelvic pain" was done
*/
define "Counselling conducted on regular exercise, physiotherapy, support belts and acupuncture to relieve low back and pelvic pain":
  WC.Only(
  [Procedure: Cx."Counselling on Regular Exercise Physiotherapy Support Belts and Acupuncture to Relieve Low Back and Pelvic Pain"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE54 Reason counselling on regular exercise, physiotherapy, support belts and acupuncture to relieve low back and pelvic pain was not conducted
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Select the reason why counselling for the symptom "low back and pelvic pain" was not done
*/
define "Reason counselling on regular exercise, physiotherapy, support belts and acupuncture to relieve low back and pelvic pain was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling on Regular Exercise Physiotherapy Support Belts and Acupuncture to Relieve Low Back and Pelvic Pain"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE57 Counselling conducted on non-pharmacological options for varicose veins and oedema
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Whether or not counselling for the symptom "Varicose veins or oedema" was done
*/
define "Counselling conducted on non-pharmacological options for varicose veins and oedema":
  WC.Only(
  [Procedure: Cx."Counselling on Non-Pharmacological Options for Varicose Veins and Oedema"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE58 Reason counselling on non-pharmacological options for varicose veins and oedema was not conducted
  @activity: ANC.B10.2 Physiological symptoms counselling
  @description: Select the reason why counselling for the symptom "Varicose veins or oedema" was not done
*/
define "Reason counselling on non-pharmacological options for varicose veins and oedema was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling on Non-Pharmacological Options for Varicose Veins and Oedema"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE61 Counselling conducted on healthy eating and keeping physically active
  @activity: ANC.B10.3 Diet counselling
  @description: Whether or not counselling was done on healthy eating and keeping physically active during pregnancy
*/
define "Counselling conducted on healthy eating and keeping physically active":
  WC.Only(
  [Procedure: Cx."Counselling on Healthy Eating and Keeping Physically Active"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE62 Reason counselling on healthy eating and keeping physically active was not conducted
  @activity: ANC.B10.3 Diet counselling
  @description: Select the reason why counselling on healthy eating and exercise during pregnancy was not done
*/
define "Reason counselling on healthy eating and keeping physically active was not conducted":
  [Procedure: Cx."Counselling on Healthy Eating and Keeping Physically Active"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B10.DE65 Counselling conducted on increasing daily energy and protein intake
  @activity: ANC.B10.3 Diet counselling
  @description: Whether or not counselling was done on increasing energy and protein intake
*/
define "Counselling conducted on increasing daily energy and protein intake":
  WC.Only(
  [Procedure: Cx."Counselling on Increasing Daily Energy Protein Intake"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE66 Reason counselling on increasing daily energy and protein intake was not conducted
  @activity: ANC.B10.3 Diet counselling
  @description: Select the reason why counselling on increasing daily energy intake was not done
*/
define "Reason counselling on increasing daily energy and protein intake was not conducted":
  [Procedure: Cx."Counselling on Increasing Daily Energy Protein Intake"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B10.DE69 Counselling conducted on balanced energy and protein dietary supplementation
  @activity: ANC.B10.3 Diet counselling
  @description: Whether or not counselling was done on balanced energy and protein dietary supplementation
*/
define "Counselling conducted on balanced energy and protein dietary supplementation":
  WC.Only(
  [Procedure: Cx."Counselling on Balanced Energy and Protein Dietary Supplementation"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE70 Reason counselling on balanced energy and protein dietary supplementation was not conducted
  @activity: ANC.B10.3 Diet counselling
  @description: Select the reason why counselling on balanced energy and protein intake was not done
*/
define "Reason counselling on balanced energy and protein dietary supplementation was not conducted":
  [Procedure: Cx."Counselling on Balanced Energy and Protein Dietary Supplementation"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return PND.code


/*
  @dataElement: ANC.B10.DE73 Hypertension
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether the client has developed hypertension associated with pregnancy
*/
define "Hypertension":
  WC.Only(
  [Condition: Cx."Hypertension"] C
    where C.clinicalStatus in FC."Active Condition"
      and C.verificationStatus ~ FC."confirmed"
      and Last(Split(C.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE74 Severe hypertension
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether the client has developed severe hypertension associated with pregnancy, as defined in the decision logic (If SBP is 160 mmHg or higher and/or DBP is 110 mmHg or higher)
*/
define "Severe hypertension":
  WC.Only(
  [Condition: Cx."Severe hypertension"] C
    where C.clinicalStatus in FC."Active Condition"
      and C.verificationStatus ~ FC."confirmed"
      and Last(Split(C.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE75 Pre-eclampsia
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether the client has pre-eclampsia
*/
define "Pre-eclampsia":
  WC.Only(
  [Condition: Cx."Pre-eclampsia"] C
    where C.clinicalStatus in FC."Active Condition"
      and C.verificationStatus ~ FC."confirmed"
      and Last(Split(C.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE76 Severe pre-eclampsia
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether the client has severe pre-eclampsia as defined by decision logic and reported symptoms
*/
define "Severe pre-eclampsia":
  WC.Only(
  [Condition: Cx."Severe pre-eclampsia"] C
    where C.clinicalStatus in FC."Active Condition"
      and C.verificationStatus ~ FC."confirmed"
      and Last(Split(C.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE77 Counselling conducted on hypertension
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether counselling was provided to a client who has been diagnosed with hypertension
*/
define "Counselling conducted on hypertension":
  WC.Only(
  [Procedure: Cx."Counselling conducted on hypertension"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE78 Counselling conducted on HIV positive
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether counselling was provided to a client who has been diagnosed with HIV
*/
define "Counselling conducted on HIV positive":
  WC.Only(
  [Procedure: Cx."Counselling conducted on HIV positive"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE79 Counselling conducted on hepatitis B positive
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether counselling was provided to a client who has been diagnosed with Hepatitis B
*/
define "Counselling conducted on hepatitis B positive":
  WC.Only(
  [Procedure: Cx."Counselling conducted on hepatitis B positive"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE80 Counselling conducted on hepatitis C positive
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether counselling was provided to a client who has been diagnosed with Hepatitis C
*/
define "Counselling conducted on hepatitis C positive":
  WC.Only(
  [Procedure: Cx."Counselling conducted on hepatitis C positive"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE81 Syphilis counselling and treatment
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether counselling and treatment was provided to a client who has been diagnosed with syphilis
*/
define "Syphilis counselling and treatment":
  WC.Only(
  [Procedure: Cx."Syphilis counselling and treatment"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE82 Syphilis counselling, treatment and further testing
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether counselling and treatment was provided to a client who has been diagnosed with syphilis – additional testing (RPR test) recommended
*/
define "Syphilis counselling, treatment and further testing":
  WC.Only(
  [Procedure: Cx."Syphilis counselling, treatment and further testing"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE83 Seven-day antibiotic regimen for asymptomatic bacteriuria (ASB) provided
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether seven-day antibiotic regimen was provided to a client who has been diagnosed with ASB
*/
define "Seven-day antibiotic regimen for asymptomatic bacteriuria (ASB) provided":
  WC.Only(
  [Procedure: Cx."Seven-day antibiotic regimen for asymptomatic bacteriuria (ASB) provided"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE84 Reason seven-day antibiotic regimen for asymptomatic bacteriuria (ASB) not provided
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Please explain why woman was not provided treatment
*/
define "Reason seven-day antibiotic regimen for asymptomatic bacteriuria (ASB) not provided":
  WC.Only(
  [Procedure: Cx."Reason seven-day antibiotic regimen for asymptomatic bacteriuria (ASB) not provided"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE87 Counselling conducted on TB screening positive
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Counselling provided to woman when TB screening result is positive
*/
define "Counselling conducted on TB screening positive":
  WC.Only(
  [Procedure: Cx."Counselling conducted on TB screening positive"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE88 Counselling conducted on gestational diabetes mellitus (GDM)
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether counselling was provided to a woman who has been diagnosed with GDM
*/
define "Counselling conducted on gestational diabetes mellitus (GDM)":
  WC.Only(
  [Procedure: Cx."Counselling conducted on gestational diabetes mellitus (GDM)"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE89 Counselling conducted on diabetes mellitus (DM)
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether counselling was provided to a woman who has been diagnosed with DM during pregnancy
*/
define "Counselling conducted on diabetes mellitus (DM)":
  WC.Only(
  [Procedure: Cx."Counselling conducted on diabetes mellitus (DM)"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE90 Counselling conducted on anaemia
  @activity: ANC.B10.4 Diagnosis & treatment
  @description: Whether counselling was provided to a woman who has been diagnosed with anaemia
*/
define "Counselling conducted on anaemia":
  WC.Only(
  [Procedure] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE104 Vitamin A supplementation not provided
  @activity: ANC.B10.5 Nutrition supplementation
  @description: Select the reason why the supplement was not given
*/
define "Vitamin A supplementation not provided":
  [MedicationRequest: Cx."Vitamin A supplementation not provided"] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and MR.doNotPerform is true
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return MR.medication as FHIR.CodeableConcept


/*
  @dataElement: ANC.B10.DE107 Amount of daily calcium supplements provided
  @activity: ANC.B10.5 Nutrition supplementation
  @description: Amount in grams (g) of calcium supplements provided
*/
define "Amount of daily calcium supplements provided":
  singleton from (WC.Only(
  [MedicationRequest] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(MR.doNotPerform, false) is false
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).dosageInstruction.doseAndRate.dose) as FHIR.SimpleQuantity


/*
  @dataElement: ANC.B10.DE108 Reason calcium not prescribed
  @activity: ANC.B10.5 Nutrition supplementation
  @description: Select the reason why calcium supplementation was not prescribed
*/
define "Reason calcium not prescribed":
  WC.Only(
  [MedicationRequest: Cx."Reason calcium not prescribed"] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and MR.doNotPerform is true
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).medication as FHIR.CodeableConcept


/*
  @dataElement: ANC.B10.DE95 Amount of daily dose of folic acid prescribed
  @activity: ANC.B10.5 Nutrition supplementation
  @description: Amount of folic acid supplements prescribed in milligrams for daily intake
*/
define "Amount of daily dose of folic acid prescribed":
  singleton from (WC.Only(
  [MedicationRequest] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(MR.doNotPerform, false) is false
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).dosageInstruction.doseAndRate.dose) as FHIR.SimpleQuantity


/*
  @dataElement: ANC.B10.DE96 Reason iron and folic acid not prescribed
  @activity: ANC.B10.5 Nutrition supplementation
  @description: Select the reason why the supplement was not prescribed
*/
define "Reason iron and folic acid not prescribed":
  WC.Only(
  [MedicationRequest: Cx."Reason iron and folic acid not prescribed"] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and MR.doNotPerform is true
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).medication as FHIR.CodeableConcept


/*
  @dataElement: ANC.B10.DE91 Amount of iron prescribed
  @activity: ANC.B10.5 Nutrition supplementation
  @description: Amount of iron supplements prescribed in milligrams for intake
*/
define "Amount of iron prescribed":
  singleton from (WC.Only(
  [MedicationRequest] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(MR.doNotPerform, false) is false
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).dosageInstruction.doseAndRate.dose) as FHIR.SimpleQuantity


/*
  @dataElement: ANC.B10.DE92 Type of iron supplement dosage provided
  @activity: ANC.B10.5 Nutrition supplementation
  @description: Whether the amount of iron prescribed is for daily or weekly intake
*/
define "Type of iron supplement dosage provided":
  WC.Only(
  [MedicationRequest] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(MR.doNotPerform, false) is false
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).dosageInstruction.timing.repeat.periodUnit


/*
  @dataElement: ANC.B10.DE100 Amount of vitamin A supplementation provided
  @activity: ANC.B10.5 Nutrition supplementation
  @description: Amount of vitamin A supplementation provided in IU
*/
define "Amount of vitamin A supplementation provided":
  singleton from (WC.Only(
  [MedicationRequest] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(MR.doNotPerform, false) is false
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).dosageInstruction.doseAndRate.dose) as FHIR.SimpleQuantity


/*
  @dataElement: ANC.B10.DE101 Type of vitamin A supplement dosage provided
  @activity: ANC.B10.5 Nutrition supplementation
  @description: Whether the amount of vitamin A prescribed is for daily or weekly intake
*/
define "Type of vitamin A supplement dosage provided":
  WC.Only(
  [MedicationRequest] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(MR.doNotPerform, false) is false
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).dosageInstruction.timing.repeat.periodUnit


/*
  @dataElement: ANC.B10.DE113 Risk of pre-eclampsia
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: The woman is at risk for developing pre-eclampsia
*/
define "Risk of pre-eclampsia":
  WC.Only(
  [Condition: Cx."Risk of pre-eclampsia"] C
    where C.clinicalStatus in FC."Active Condition"
      and C.verificationStatus ~ FC."confirmed"
      and Last(Split(C.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE114 Pre-eclampsia risk counselling provided
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Whether or not counselling on pre-eclampsia was provided
*/
define "Pre-eclampsia risk counselling provided":
  WC.Only(
  [Procedure: Cx."Pre-eclampsia risk counselling provided"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE115 Amount of daily aspirin prescribed until delivery
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Amount in milligrams (mg) of aspirin prescribed
*/
define "Amount of daily aspirin prescribed until delivery":
  singleton from (WC.Only(
  [MedicationRequest] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(MR.doNotPerform, false) is false
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).dosageInstruction.doseAndRate.dose) as FHIR.SimpleQuantity


/*
  @dataElement: ANC.B10.DE116 Reason aspirin not prescribed
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Select the reason why the aspirin was not prescribed
*/
define "Reason aspirin not prescribed":
  WC.Only(
  [MedicationRequest: Cx."Reason aspirin not prescribed"] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and MR.doNotPerform is true
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).medication as FHIR.CodeableConcept


/*
  @dataElement: ANC.B10.DE121 Risk of gestational diabetes mellitus (GDM)
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: The woman is at risk for developing gestational diabetes mellitus (GDM)
*/
define "Risk of gestational diabetes mellitus (GDM)":
  WC.Only(
  [Condition: Cx."Risk of gestational diabetes mellitus (GDM)"] C
    where C.clinicalStatus in FC."Active Condition"
      and C.verificationStatus ~ FC."confirmed"
      and Last(Split(C.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE124 Gestational diabetes mellitus (GDM) risk counselling conducted
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Gestational diabetes mellitus (GDM) risk counselling conducted
*/
define "Gestational diabetes mellitus (GDM) risk counselling conducted":
  WC.Only(
  [Procedure: Cx."Gestational diabetes mellitus (GDM) risk counselling conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE125 Counselling conducted on HIV risk
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Whether counselling on HIV risk was conducted for a client who is at a high risk of HIV
*/
define "Counselling conducted on HIV risk":
  WC.Only(
  [Procedure: Cx."Counselling conducted on HIV risk"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE126 PrEP for HIV prevention provided
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Whether or not the client has been counselled on use of oral pre-exposure prophylaxis (PrEP) as a means to reduce risk of HIV transmission and provided with oral PrEP
*/
define "PrEP for HIV prevention provided":
  WC.Only(
  [Procedure: Cx."PrEP for HIV prevention provided"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE127 Reason PrEP for HIV prevention was not provided
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Why the client was not provided with PrEP
*/
define "Reason PrEP for HIV prevention was not provided":
  [MedicationRequest: Cx."Reason PrEP for HIV prevention was not provided"] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and MR.doNotPerform is true
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return MR.medication as FHIR.CodeableConcept


/*
  @dataElement: ANC.B10.DE132 Amount of oral PrEP provided
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Amount of oral PrEP provided to the woman
*/
define "Amount of oral PrEP provided":
  singleton from (WC.Only(
  [MedicationRequest] MR
    where MR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(MR.doNotPerform, false) is false
      and Last(Split(MR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).dosageInstruction.doseAndRate.dose) as FHIR.SimpleQuantity


/*
  @dataElement: ANC.B10.DE133 Counselling conducted on seeking care when danger signs occur
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Counselling provided on when and how to seek care when danger signs are present
*/
define "Counselling conducted on seeking care when danger signs occur":
  WC.Only(
  [Procedure: Cx."Counselling conducted on seeking care when danger signs occur"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE134 Counsel to immediately go to the hospital if severe danger signs are present
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Counselling on going immediately to the hospital if severe danger signs
*/
define "Counsel to immediately go to the hospital if severe danger signs are present":
  WC.Only(
  [Procedure: Cx."Counsel to immediately go to the hospital if severe danger signs are present"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE135 Counselling on ANC contact schedule conducted
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Counselling on ANC contact schedule conducted
*/
define "Counselling on ANC contact schedule conducted":
  WC.Only(
  [Procedure: Cx."Counselling on ANC contact schedule conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE136 Counselling conducted on birth preparedness and complications readiness
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Counselling on birth preparedness and complications readiness conducted
*/
define "Counselling conducted on birth preparedness and complications readiness":
  WC.Only(
  [Procedure: Cx."Counselling conducted on birth preparedness and complications readiness"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE137 Planned birth place
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Place where woman plans to give birth
*/
define "Planned birth place":
  WC.Only(
  [Observation: Cx."Planned birth place"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B10.DE142 Counselling conducted on Rh factor negative
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Counselling conducted on Rh factor negative
*/
define "Counselling conducted on Rh factor negative":
  WC.Only(
  [Procedure: Cx."Counselling conducted on Rh factor negative"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE143 Counselling conducted on intrapartum antibiotic to prevent early neonatal Group B Streptococcus (GBS) infection
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Counselling conducted on intrapartum antibiotic to prevent early neonatal Group B Streptococcus (GBS) infection
*/
define "Counselling conducted on intrapartum antibiotic to prevent early neonatal Group B Streptococcus (GBS) infection":
  WC.Only(
  [Procedure: Cx."Counselling conducted on intrapartum antibiotic to prevent early neonatal Group B Streptococcus (GBS) infection"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE144 Counselling on postpartum family planning conducted
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Counselling on postpartum family planning conducted
*/
define "Counselling on postpartum family planning conducted":
  WC.Only(
  [Procedure: Cx."Counselling on postpartum family planning conducted"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE145 Family planning method selected
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Type of postpartum family planning method selected
*/
define "Family planning method selected":
  WC.Only(
  [Observation: Cx."Family planning method selected"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B10.DE164 Counselling conducted on breastfeeding
  @activity: ANC.B10.6 Risk reduction & general counselling
  @description: Counselling on breastfeeding was conducted
*/
define "Counselling conducted on breastfeeding":
  WC.Only(
  [Procedure: Cx."Counselling conducted on breastfeeding"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE175 Reason tetanus toxoid (TT) vaccination not provided
  @activity: ANC.B10.7 Immunizations
  @description: Select the reason why the TT dose was not given
*/
define "Reason tetanus toxoid (TT) vaccination not provided":
  WC.Only(
  [Immunization: Cx."Reason tetanus toxoid (TT) vaccination not provided"] IND
    where IND.status = 'not-done'
      and Last(Split(IND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).statusReason


/*
  @dataElement: ANC.B10.DE181 Counselling conducted on Hep B negative
  @activity: ANC.B10.7 Immunizations
  @description: Counselling conducted on Hep B negative
*/
define "Counselling conducted on Hep B negative":
  WC.Only(
  [Procedure: Cx."Counselling conducted on Hep B negative"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE188 No doses of hepatitis B
  @activity: ANC.B10.7 Immunizations
  @description: No dose, or zero doses, of hepatitis B immunization has ever been provided
*/
define "No doses of hepatitis B":
  WC.Only(
  [Observation: Cx."No doses of hepatitis B"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B10.DE189 Reason Hep B vaccination not provided
  @activity: ANC.B10.7 Immunizations
  @description: Select the reason why the Hep B dose was not given
*/
define "Reason Hep B vaccination not provided":
  WC.Only(
  [Immunization: Cx."Reason Hep B vaccination not provided"] IND
    where IND.status = 'not-done'
      and Last(Split(IND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE197 Reason flu vaccine not provided
  @activity: ANC.B10.7 Immunizations
  @description: Select the reason why the flu dose was not given
*/
define "Reason flu vaccine not provided":
  WC.Only(
  [Immunization: Cx."Reason flu vaccine not provided"] IND
    where IND.status = 'not-done'
      and Last(Split(IND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).statusReason


/*
  @dataElement: ANC.B10.DE195 Flu immunization provided
  @activity: ANC.B10.7 Immunizations
  @description: Whether or not the seasonal flu immunization was provided
*/
define "Flu immunization provided":
  [Immunization: Cx."Flu immunization provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B10.DE196 Date flu immunization was received
  @activity: ANC.B10.7 Immunizations
  @description: Date on which this year's seasonal flu dose has been given
*/
define "Date flu immunization was received":
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B10.DE182 Hepatitis B-1 immunization provided
  @activity: ANC.B10.7 Immunizations
  @description: Whether the first dose of hepatitis B immunization was provided
*/
define "Hepatitis B-1 immunization provided":
  WC.Only(
  [Immunization: Cx."Hepatitis B-1 immunization provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE183 Date hepatitis B-1 immunization was received
  @activity: ANC.B10.7 Immunizations
  @description: Date on which the first dose of hepatitis B immunization was provided
*/
define "Date hepatitis B-1 immunization was received":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE184 Hepatitis B-2 immunization provided
  @activity: ANC.B10.7 Immunizations
  @description: Whether the second dose of hepatitis B immunization was provided
*/
define "Hepatitis B-2 immunization provided":
  WC.Only(
  [Immunization: Cx."Hepatitis B-2 immunization provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE185 Date hepatitis B-2 immunization was received
  @activity: ANC.B10.7 Immunizations
  @description: Date on which the second dose of hepatitis B immunization was provided
*/
define "Date hepatitis B-2 immunization was received":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE186 Hepatitis B-3 immunization provided
  @activity: ANC.B10.7 Immunizations
  @description: Whether the third dose of hepatitis B immunization was provided
*/
define "Hepatitis B-3 immunization provided":
  WC.Only(
  [Immunization: Cx."Hepatitis B-3 immunization provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE187 Date hepatitis B-3 immunization was received
  @activity: ANC.B10.7 Immunizations
  @description: Date on which the third dose of hepatitis B immunization was provided
*/
define "Date hepatitis B-3 immunization was received":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE165 Tetanus toxoid (TT) 1 immunization provided
  @activity: ANC.B10.7 Immunizations
  @description: Whether or not the first tetanus toxoid-containing vaccine (TT1) dose was provided
*/
define "Tetanus toxoid (TT) 1 immunization provided":
  WC.Only(
  [Immunization: Cx."Tetanus toxoid (TT) 1 immunization provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE166 Date tetanus toxoid 1 immunization was received
  @activity: ANC.B10.7 Immunizations
  @description: Date on which TT1 was provided
*/
define "Date tetanus toxoid 1 immunization was received":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE167 Tetanus toxoid (TT) 2 immunization provided
  @activity: ANC.B10.7 Immunizations
  @description: Whether or not the second tetanus toxoid-containing vaccine (TT2) dose was provided
*/
define "Tetanus toxoid (TT) 2 immunization provided":
  WC.Only(
  [Immunization: Cx."Tetanus toxoid (TT) 2 immunization provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE168 Date tetanus toxoid 2 immunization was received
  @activity: ANC.B10.7 Immunizations
  @description: Date on which TT2 was provided
*/
define "Date tetanus toxoid 2 immunization was received":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE169 Tetanus toxoid (TT) 3 immunization provided
  @activity: ANC.B10.7 Immunizations
  @description: Whether or not the third tetanus toxoid-containing vaccine (TT3) dose was provided
*/
define "Tetanus toxoid (TT) 3 immunization provided":
  WC.Only(
  [Immunization: Cx."Tetanus toxoid (TT) 3 immunization provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE170 Date tetanus toxoid 3 immunization was received
  @activity: ANC.B10.7 Immunizations
  @description: Date on which TT3 was provided
*/
define "Date tetanus toxoid 3 immunization was received":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE171 Tetanus toxoid (TT) 4 immunization provided
  @activity: ANC.B10.7 Immunizations
  @description: Whether or not the fourth tetanus toxoid-containing vaccine (TT4) dose was provided
*/
define "Tetanus toxoid (TT) 4 immunization provided":
  WC.Only(
  [Immunization: Cx."Tetanus toxoid (TT) 4 immunization provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE172 Date tetanus toxoid 4 immunization was received
  @activity: ANC.B10.7 Immunizations
  @description: Date on which TT4 was provided
*/
define "Date tetanus toxoid 4 immunization was received":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE173 Tetanus toxoid (TT) 5 immunization provided
  @activity: ANC.B10.7 Immunizations
  @description: Whether or not the fifth tetanus toxoid-containing vaccine (TT5) dose was provided
*/
define "Tetanus toxoid (TT) 5 immunization provided":
  WC.Only(
  [Immunization: Cx."Tetanus toxoid (TT) 5 immunization provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE174 Date tetanus toxoid 5 immunization was received
  @activity: ANC.B10.7 Immunizations
  @description: Date on which TT5 was provided
*/
define "Date tetanus toxoid 5 immunization was received":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE203 IPV first-line support provided
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: First-line support includes basic counselling or psychosocial support using LIVES, which involves the following steps: Listen, Inquire, Validate, Enhance safety and Support
*/
define "IPV first-line support provided":
  WC.Only(
  [Procedure: Cx."IPV first-line support provided"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE204 Reason IPV first line support not done
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: Reason IPV first line support not done
*/
define "Reason IPV first line support not done":
  WC.Only(
  [Procedure: Cx."Reason IPV first line support not done"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE210 What additional type of care provided
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: What care was provided for survivor/woman subject to intimate partner violence
*/
define "What additional type of care provided":
  WC.Only(
  [Procedure: Cx."What additional type of care provided"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE216 Safety assessement
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: Assess immediate risk of violence/danger
*/
define "Safety assessement":
  WC.Only(
  [Observation: Cx."Safety assessement"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B10.DE217 Has the physical violence happened more often or gotten worse over the past 6 months
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: Has the physical violence happened more often or gotten worse over the past 6 months?
*/
define "Has the physical violence happened more often or gotten worse over the past 6 months":
  WC.Only(
  [Observation: Cx."Has the physical violence happened more often or gotten worse over the past 6 months"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B10.DE218 Has he ever used a weapon or threatened you with a weapon
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: Has he ever used a weapon or threatened you with a weapon?
*/
define "Has he ever used a weapon or threatened you with a weapon":
  WC.Only(
  [Observation: Cx."Has he ever used a weapon or threatened you with a weapon"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B10.DE219 Has he ever tried to strangle you
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: Has he ever tried to strangle you?
*/
define "Has he ever tried to strangle you":
  WC.Only(
  [Observation: Cx."Has he ever tried to strangle you"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B10.DE220 Has he ever beaten you when you were pregnant
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: Has he ever beaten you when you were pregnant?
*/
define "Has he ever beaten you when you were pregnant":
  WC.Only(
  [Observation: Cx."Has he ever beaten you when you were pregnant"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B10.DE221 Is he violently and constantly jealous of you
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: Is he violently and constantly jealous of you?
*/
define "Is he violently and constantly jealous of you":
  WC.Only(
  [Observation: Cx."Is he violently and constantly jealous of you"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B10.DE222 Do you believe he could kill you
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: Do you believe he could kill you?
*/
define "Do you believe he could kill you":
  WC.Only(
  [Observation: Cx."Do you believe he could kill you"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.boolean


/*
  @dataElement: ANC.B10.DE223 Referrals made as part of first-line support and care
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: Referrals made as part of first-line support and care
*/
define "Referrals made as part of first-line support and care":
  WC.Only(
  [ServiceRequest: Cx."Referrals made as part of first-line support and care"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE224 Was client referred
  @activity: ANC.B10.8 Intimate partner violence first-line support and care
  @description: Whether the client was referred to external services
*/
define "Was client referred":
  [Observation: Cx."Was client referred"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B10.DE233 Preventive antihelminthic treatment provided
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Preventive antihelminthic treatment was provided
*/
define "Preventive antihelminthic treatment provided":
  WC.Only(
  [Observation: Cx."Preventive antihelminthic treatment provided"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).value as FHIR.CodeableConcept


/*
  @dataElement: ANC.B10.DE237 Reason no preventative treatment provided
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Select the reason why the treatment was not given
*/
define "Reason no preventative treatment provided":
  WC.Only(
  [ServiceRequest: Cx."Reason no preventative treatment provided"] SNR
    where SNR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and SNR.doNotPerform is true
      and Last(Split(SNR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE241 Counselling conducted on malaria prevention
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Counselling provided on how to prevent malaria
*/
define "Counselling conducted on malaria prevention":
  [Procedure: Cx."Counselling conducted on malaria prevention"] P
    where P.status in { 'preparation', 'in-progress', 'on-hold', 'completed' }
      and Last(Split(P.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.B10.DE242 Reason counselling on malaria prevention was not conducted
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Select the reason why the counselling was not given
*/
define "Reason counselling on malaria prevention was not conducted":
  WC.Only(
  [Procedure: Cx."Counselling conducted on malaria prevention"] PND
    where PND.status = 'not-done'
      and Last(Split(PND.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE251 Reason malaria prophylaxis not provided
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Select the reason why the treatment was not given
*/
define "Reason malaria prophylaxis not provided":
  [Immunization: Cx."Malaria Prophylaxis"] IND
    where IND.status = 'not-done'
      and Last(Split(IND.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return IND.vaccineCode


/*
  @dataElement: ANC.B10.DE245 IPTp-SP dose 1 provided
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Whether or not IPTp-SP dose 1 was provided
*/
define "IPTp-SP dose 1 provided":
  WC.Only(
  [Immunization: Cx."IPTp-SP dose 1 provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE246 IPTp-SP dose 1 date
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Date IPTp-SP dose 1 given
*/
define "IPTp-SP dose 1 date":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE247 IPTp-SP dose 2 provided
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Whether or not IPTp-SP dose 2 was provided
*/
define "IPTp-SP dose 2 provided":
  WC.Only(
  [Immunization: Cx."IPTp-SP dose 2 provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE248 IPTp-SP dose 2 date
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Date IPTp-SP dose 2 given
*/
define "IPTp-SP dose 2 date":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE249 IPTp-SP dose 3 provided
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Whether or not IPTp-SP dose 3 was provided
*/
define "IPTp-SP dose 3 provided":
  WC.Only(
  [Immunization: Cx."IPTp-SP dose 3 provided"] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).vaccineCode


/*
  @dataElement: ANC.B10.DE250 IPTp-SP dose 3 date
  @activity: ANC.B10.9 Deworming & malaria prophylaxis
  @description: Date IPTp-SP dose 3 given
*/
define "IPTp-SP dose 3 date":
  WC.Only(
  [Immunization] I
    where I.status = 'completed'
      and Last(Split(I.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).occurrence as FHIR.dateTime


/*
  @dataElement: ANC.B10.DE256 Referred to hospital
  @activity: ANC.B11 Urgent referral needed?
  @description: Whether or not the health worker referred the woman to a hospital or higher-level facility
*/
define "Referred to hospital":
  WC.Only(
  [ServiceRequest: Cx."Referral to Hospital"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))
  ).code


/*
  @dataElement: ANC.B10.DE257 Reason referral not done
  @activity: ANC.B11 Urgent referral needed?
  @description: The reason why the health worker did not refer the woman to a hospital or higher-level facility
*/
define "Reason referral not done":
  [ServiceRequest: Cx."Referral to Hospital"] SNR
    where SNR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and SNR.doNotPerform is true
      and Last(Split(SNR.encounter.reference, '/')) = Last(Split(encounter, '/'))
    return SNR.code


/*
  @dataElement: ANC.C.DE1 Emergency referral
  @activity: ANC.C1 Emergency referral?
  @description: Referral for urgent care
*/
define "Emergency referral":
  [ServiceRequest: Cx."Emergency referral"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.C.DE6 Date of scheduled referral
  @activity: ANC.C1 Emergency referral?
  @description: When the referral is scheduled
*/
define "Date of scheduled referral":
  [ServiceRequest] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.C.DE7 Location of referral
  @activity: ANC.C1 Emergency referral?
  @description: Where the client is being referred to
*/
define "Location of referral":
  [ServiceRequest] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.C.DE8 Date referral was made
  @activity: ANC.C1 Emergency referral?
  @description: The date the referral was made
*/
define "Date referral was made":
  [ServiceRequest] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.C.DE9 Provider who made referral
  @activity: ANC.C1 Emergency referral?
  @description: The name of the provider who made the referral
*/
define "Provider who made referral":
  [ServiceRequest] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.C.DE12 Referral notes
  @activity: ANC.C1 Emergency referral?
  @description: Any additional relevant details of clinical significance for the referral facility to provide continuity of care
*/
define "Referral notes":
  [ServiceRequest] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.C.DE13 Client history summary
  @activity: ANC.C1 Emergency referral?
  @description: With interoperable systems, the provider receiving the referral should be able to access the client's health record digitally. However, in the absence of this, the referral provider should receive a summary of the client's health records that includes the client's history, reported issues and concerns, and any other relevant clinical information the health-care provider had already obtained
*/
define "Client history summary":
  [ServiceRequest] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.C.DE2 Reason for referral
  @activity: ANC.C6 Provide information to referral facility
  @description: Reason why the client is being referred
*/
define "Reason for referral":
  [ServiceRequest: Cx."Reason for referral"] SR
    where SR.status in { 'draft', 'active', 'on-hold', 'completed' }
      and Coalesce(SR.doNotPerform, false) is false
      and Last(Split(SR.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.C.DE5 Any treatment given before referral
  @activity: ANC.C6 Provide information to referral facility
  @description: If woman was referred, was any treatment provided before referral?
*/
define "Any treatment given before referral":
  [Observation: Cx."Any treatment given before referral"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.C.DE10 Provider's facility
  @activity: ANC.C6 Provide information to referral facility
  @description: Facility client is being referred from
*/
define "Provider's facility":
  [PractitionerRole]

/*
  @dataElement: ANC.C.DE11 Provider's telephone number
  @activity: ANC.C6 Provide information to referral facility
  @description: The contact details of the provider making the referral
*/
define "Provider's telephone number":
  [PractitionerRole]

/*
  @dataElement: ANC.End.1 Reason for closing ANC record
  @activity: ANC.End End
  @description: Select the reason why you are closing the woman's ANC record
*/
define "Reason for closing ANC record":
  [Observation: Cx."Reason for closing ANC record"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.End.12 Delivery date
  @activity: ANC.End End
  @description: Date on which the woman delivered
*/
define "Delivery date":
  [Observation: Cx."Delivery date"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.End.13 Place of delivery
  @activity: ANC.End End
  @description: Place where the woman delivered
*/
define "Place of delivery":
  [Encounter: Cx."Place of delivery"]

/*
  @dataElement: ANC.End.17 Preterm Birth
  @activity: ANC.End End
  @description: The woman gave birth when the gestational age was less than 37 weeks
*/
define "Preterm Birth":
  [Observation: Cx."Preterm Birth"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.End.18 Delivery mode
  @activity: ANC.End End
  @description: How the woman gave birth/delivered
*/
define "Delivery mode":
  [Observation: Cx."Delivery mode"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.End.23 Birth weight
  @activity: ANC.End End
  @description: Enter the birth weight of the baby in kg
*/
define "Birth weight":
  [Observation: Cx."Birth weight"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.End.24 Exclusively breastfeeding
  @activity: ANC.End End
  @description: Specify whether the woman is exclusively breastfeeding
*/
define "Exclusively breastfeeding":
  [Observation: Cx."Exclusively breastfeeding"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.End.25 Delivery complications
  @activity: ANC.End End
  @description: Any complications that occurred during delivery
*/
define "Delivery complications":
  [Observation: Cx."Delivery complications"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.End.38 Date of miscarriage or abortion
  @activity: ANC.End End
  @description: Date of the miscarriage/abortion
*/
define "Date of miscarriage or abortion":
  [Observation: Cx."Date of miscarriage or abortion"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.End.39 Date of death
  @activity: ANC.End End
  @description: Date that the woman died
*/
define "Date of death":
  [Observation: Cx."Date of death"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))


/*
  @dataElement: ANC.End.40 Cause of death
  @activity: ANC.End End
  @description: Cause of the woman's death
*/
define "Cause of death":
  [Observation: Cx."Cause of death"] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false
      and Last(Split(O.encounter.reference, '/')) = Last(Split(encounter, '/'))

