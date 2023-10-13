library ANCDT17

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'
include ANCConfig called Config
include ANCConcepts called Cx
include ANCDataElements called PatientData
include ANCContactDataElements called ContactData

context Patient

/*
(((((((((((((((("Systolic blood pressure" ≥ 140 mmHg)
  AND ("Repeat systolic blood pressure" ≥ 140 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "Severe headache"))
  OR (("Diastolic blood pressure" ≥ 90 mmHg)
  AND ("Repeat diastolic blood pressure" ≥ 90 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "Severe headache")))
  OR (("Systolic blood pressure" ≥ 140 mmHg)
  AND ("Repeat systolic blood pressure" ≥ 140 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "Blurred vision")))
  OR (("Diastolic blood pressure" ≥ 90 mmHg)
  AND ("Repeat diastolic blood pressure" ≥ 90 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "Blurred vision")))
  OR (("Systolic blood pressure" ≥ 140 mmHg)
  AND ("Repeat systolic blood pressure" ≥ 140 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "Epigastric pain")))
  OR (("Diastolic blood pressure" ≥ 90 mmHg)
  AND ("Repeat diastolic blood pressure" ≥ 90 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "Epigastric pain")))
  OR (("Systolic blood pressure" ≥ 140 mmHg)
  AND ("Repeat systolic blood pressure" ≥ 140 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "Dizziness")))
  OR (("Diastolic blood pressure" ≥ 90 mmHg)
  AND ("Repeat diastolic blood pressure" ≥ 90 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "Dizziness")))
  OR (("Systolic blood pressure" ≥ 140 mmHg)
  AND ("Repeat systolic blood pressure" ≥ 140 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "Vomiting")))
  OR (("Diastolic blood pressure" ≥ 90 mmHg)
  AND ("Repeat diastolic blood pressure" ≥ 90 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "Vomiting")))
  OR (("Systolic blood pressure" ≥ 140 mmHg)
  AND ("Repeat systolic blood pressure" ≥ 140 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "None")
  AND ("Urine dipstick result – protein" = "++")))
  OR (("Diastolic blood pressure" ≥ 90 mmHg)
  AND ("Repeat diastolic blood pressure" ≥ 90 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "None")
  AND ("Urine dipstick result – protein" = "++")))
  OR (("Systolic blood pressure" ≥ 140 mmHg)
  AND ("Repeat systolic blood pressure" ≥ 140 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "None")
  AND ("Urine dipstick result – protein" = "+++")))
  OR (("Diastolic blood pressure" ≥ 90 mmHg)
  AND ("Repeat diastolic blood pressure" ≥ 90 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "None")
  AND ("Urine dipstick result – protein" = "+++")))
  OR (("Systolic blood pressure" ≥ 140 mmHg)
  AND ("Repeat systolic blood pressure" ≥ 140 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "None")
  AND ("Urine dipstick result – protein" = "++++")))
  OR (("Diastolic blood pressure" ≥ 90 mmHg)
  AND ("Repeat diastolic blood pressure" ≥ 90 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "None")
  AND ("Urine dipstick result – protein" = "++++"))
*/


define "Should Refer urgently to a hospital":
  ( ContactData."Systolic blood pressure" >= 140 'mm[Hg]'
      and ContactData."Repeat systolic blood pressure" >= 140 'mm[Hg]'
      and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia Choices"
  )
    or ( ContactData."Diastolic blood pressure" >= 90 'mm[Hg]'
        and ContactData."Repeat diastolic blood pressure" >= 90 'mm[Hg]'
        and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia Choices"
    )
    or ( ContactData."Systolic blood pressure" >= 140 'mm[Hg]'
        and ContactData."Repeat systolic blood pressure" >= 140 'mm[Hg]'
        and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia - No symptoms Choices"
        and ( ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 2 plus Choices"
            or ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 3 plus Choices"
            or ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 4 plus Choices"
        )
    )
    or ( ContactData."Diastolic blood pressure" >= 90 'mm[Hg]'
        and ContactData."Repeat diastolic blood pressure" >= 90 'mm[Hg]'
        and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia - No symptoms Choices"
        and ( ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 2 plus Choices"
            or ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 3 plus Choices"
            or ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 4 plus Choices"
        )
    )

/*
((((((("Systolic blood pressure" ≤ 140 mmHg)
  AND ("Repeat systolic blood pressure" ≤ 140 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "No symptoms of severe pre-eclampsia")
  AND ("Urine dipstick result – protein" = "++"))
  OR (("Systolic blood pressure" > 160 mmHg)
  AND ("Repeat systolic blood pressure" > 160 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "No symptoms of severe pre-eclampsia")
  AND ("Urine dipstick result – protein" = "++")))
  OR ((90 mmHg ≤ "Diastolic blood pressure" < 110 mmHg)
  AND (90 mmHg ≤ "Repeat diastolic blood pressure" < 110 mmHg)
  AND (Symptoms of severe pre-eclampsia" = "No symptoms of severe pre-eclampsia")
  AND ("Urine dipstick result – protein" = "++")))
  OR ((140 mmHg ≤ "Systolic blood pressure" < 160 mmHg)
  AND (140 mmHg ≤ "Repeat systolic blood pressure" < 160 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "No symptoms of severe pre-eclampsia")
  AND ("Urine dipstick result – protein" = "+++")))
  OR ((90 mmHg ≤ "Diastolic blood pressure" < 110 mmHg)
  AND (90 mmHg – "Repeat diastolic blood pressure" < 110 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "No symptoms of severe pre-eclampsia")
  AND ("Urine dipstick result – protein" = "+++")))
  OR ((140 mmHg ≤ "Systolic blood pressure" < 160 mmHg)
  AND (140 mmHg ≤ "Repeat systolic blood pressure" < 160 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "No symptoms of severe pre-eclampsia")
  AND ("Urine dipstick result – protein" = "++++")))
  OR ((90 mmHg ≤ "Diastolic blood pressure" < 110 mmHg)
  AND (90 mmHg ≤ "Repeat diastolic blood pressure" < 110 mmHg)
  AND ("Symptoms of severe pre-eclampsia" = "No symptoms of severe pre-eclampsia")
  AND ("Urine dipstick result – protein" = "++++"))
*/


define "Should Refer urgently to a hospital 2":
  ( ContactData."Systolic blood pressure" <= 140 'mm[Hg]'
      and ContactData."Repeat systolic blood pressure" <= 140 'mm[Hg]'
      and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia - No symptoms Choices"
      and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 2 plus Choices"
  )
    or ( ContactData."Systolic blood pressure" > 160 'mm[Hg]'
        and ContactData."Repeat systolic blood pressure" > 160 'mm[Hg]'
        and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia - No symptoms Choices"
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 2 plus Choices"
    )
    or ( ContactData."Diastolic blood pressure" in Interval[90 'mm[Hg]', 110 'mm[Hg]']
        and ContactData."Repeat diastolic blood pressure" in Interval[90 'mm[Hg]', 110 'mm[Hg]']
        and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia - No symptoms Choices"
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 2 plus Choices"
    )
    or ( ContactData."Systolic blood pressure" in Interval[140 'mm[Hg]', 160 'mm[Hg]']
        and ContactData."Repeat systolic blood pressure" in Interval[140 'mm[Hg]', 160 'mm[Hg]']
        and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia - No symptoms Choices"
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 3 plus Choices"
    )
    or ( ContactData."Diastolic blood pressure" in Interval[90 'mm[Hg]', 110 'mm[Hg]']
        and ContactData."Repeat diastolic blood pressure" in Interval[90 'mm[Hg]', 110 'mm[Hg]']
        and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia - No symptoms Choices"
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 3 plus Choices"
    )
    or ( ContactData."Systolic blood pressure" in Interval[140 'mm[Hg]', 160 'mm[Hg]']
        and ContactData."Repeat systolic blood pressure" in Interval[140 'mm[Hg]', 160 'mm[Hg]']
        and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia - No symptoms Choices"
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 4 plus Choices"
    )
    or ( ContactData."Diastolic blood pressure" in Interval[90 'mm[Hg]', 110 'mm[Hg]']
        and ContactData."Repeat diastolic blood pressure" in Interval[90 'mm[Hg]', 110 'mm[Hg]']
        and ContactData."Symptoms of severe pre-eclampsia" in Cx."Symptoms of severe pre-eclampsia - No symptoms Choices"
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 4 plus Choices"
    )

/*
(((("Systolic blood pressure" ≥ 160 mmHg)
  AND ("Repeat systolic blood pressure" ≥ 160 mmHg)
  AND ("Urine dipstick result – protein" = "+"))
  OR (("Diastolic blood pressure" ≥ 110 mmHg)
  AND ("Repeat diastolic blood pressure" ≥ 110 mmHg)
  AND ("Urine dipstick result – protein" = "+")))
  OR (("Systolic blood pressure" ≥ 160 mmHg)
  AND ("Repeat systolic blood pressure" ≥ 160 mmHg)
  AND ("Urine dipstick result – protein" = "None")))
  OR (("Diastolic blood pressure" ≥ 110 mmHg)
  AND ("Repeat diastolic blood pressure" ≥ 110 mmHg)
  AND ("Urine dipstick result – protein" = "None"))
*/


define "Should Refer urgently to a hospital 3":
  ( ContactData."Systolic blood pressure" >= 160 'mm[Hg]'
      and ContactData."Repeat systolic blood pressure" >= 160 'mm[Hg]'
      and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 1 plus Choices"
  )
    or ( ContactData."Diastolic blood pressure" >= 110 'mm[Hg]'
        and ContactData."Repeat diastolic blood pressure" >= 110 'mm[Hg]'
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 1 plus Choices"
    )
    or ( ContactData."Systolic blood pressure" >= 160 'mm[Hg]'
        and ContactData."Repeat systolic blood pressure" value >= 160 'mm[Hg]'
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - None Choices"
    )
    or ( ContactData."Diastolic blood pressure" >= 110 'mm[Hg]'
        and ContactData."Repeat diastolic blood pressure" >= 110 'mm[Hg]'
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - None Choices"
    )

/*
(((("Existing chronic health conditions" = "Hypertension")
  OR ((140 mmHg ≤ "Systolic blood pressure" < 160 mmHg)
  AND (140 mmHg ≤ "Repeat systolic blood pressure" < 160 mmHg)
  AND ("Urine dipstick result – protein" = "+")))
  OR ((90 mmHg ≤ "Diastolic blood pressure" < 110 mmHg)
  AND (90 mmHg ≤ "Repeat diastolic blood pressure" < 110 mmHg)
  AND ("Urine dipstick result – protein" = "+")))
  OR ((140 mmHg ≤ "Systolic blood pressure" < 160 mmHg)
  AND (140 mmHg ≤ "Repeat systolic blood pressure" < 160 mmHg)
  AND (Urine dipstick result – protein" = "None")))
  OR ((90 mmHg ≤ "Diastolic blood pressure" < 110 mmHg)
  AND (90 mmHg ≤ "Repeat diastolic blood pressure" < 110 mmHg)
  AND ("Urine dipstick result – protein" = "None"))
*/


define "Should Conduct hypertension counselling":
  ContactData."Existing chronic health conditions" in Cx."Existing chronic health conditions - Hypertension Choices"
    or ( ContactData."Systolic blood pressure" in Interval[140 'mm[Hg]', 160 'mm[Hg]']
        and ContactData."Repeat systolic blood pressure" in Interval[140 'mm[Hg]', 160 'mm[Hg]']
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 1 plus Choices"
    )
    or ( ContactData."Diastolic blood pressure" in Interval[90 'mm[Hg]', 110 'mm[Hg]']
        and ContactData."Repeat diastolic blood pressure" in Interval[90 'mm[Hg]', 110 'mm[Hg]']
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - 1 plus Choices"
    )
    or ( ContactData."Systolic blood pressure" in Interval[140 'mm[Hg]', 160 'mm[Hg]']
        and ContactData."Repeat systolic blood pressure" in Interval[140 'mm[Hg]', 160 'mm[Hg]']
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - None Choices"
    )
    or ( ContactData."Diastolic blood pressure" in Interval[90 'mm[Hg]', 110 'mm[Hg]']
        and ContactData."Repeat diastolic blood pressure" in Interval[90 'mm[Hg]', 110 'mm[Hg]']
        and ContactData."Urine dipstick result - protein" in Cx."Urine dipstick result - protein - None Choices"
    )