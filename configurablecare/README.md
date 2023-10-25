# Configurable Care Codelab

## Set-up

1. Pull the latest changes in the [android-fhir](https://github.com/google/android-fhir) repo
2. Switch to the `configurable-care-codelab` branch.
3. Open `android-fhir` as the root project.
4. Build the `configurablecare` module in Android Studio. 

## Exercises

### Exercise 1
1. Launch the `configurablecare` app in Android Studio with a connected Android device
2. Open logcat tab in Android Studio and wait until you see this message: `com.google.android.fhir.configurablecare  I  init`
3. Click on “Register new patient” and complete the questionnaire to create new patient (infant) with basic information. [Suggested DOB: 02-02-2023]
4. Navigate to “Patient list” and choose your newly created patient
5. Click on “Careplan Activities” to see the list of pending and completed activities.
6. Begin the Immunization Review activity. You do not need to fill this form. Click on Submit on the top right.
7. Wait for “Updating Activities” to change to “Activities Updated”. Click on the “Completed Activities” tab to see the recently completed task.
8. A new Measles Vaccine Medication Request activity proposal should appear in the “Pending Activities” tab. Click on this Activity to continue.
9. A questionnaire will open up that asks you whether you want to accept this proposal. Click on “Yes” and submit.
10. Notice that the Medication Request activity has now changed from “proposal” to “plan”.
11. Click on the above activity. You will see a questionnaire with options for contraindications. Do NOT select anything and submit the empty questionnaire.
12. Notice that the Medication Request activity has now changed from “plan” to “order”.
13. Click on the above activity. This will be the questionnaire for administering the measles vaccine.
14. Provide “yes” for “consent given” and “stock available” questions and answer the rest of the questions any way you like. Click on submit when you are done.
15. Now you will notice that all pending activities have been completed. Observe the completed activities as well and take note of the transition from proposal -> plan -> order -> completed
16. Go back to the Patient card and notice that the “Immunization Records” section now has a new item added with the details of this vaccine.


### Exercise 2
1. Follow steps 1-10 from Exercise 1
2. Instead of submitting an empty questionnaire, select the following contraindications: “Severely immunosuppressed”, “History of anaphylactic reactions” and “Severe allergic reactions” and click on submit.
3. You will notice that there are no more pending activities. If you take a look at the completed activities, you will notice a MedicationRequest with the status “Do Not Perform”.
4. Go back to the Patient card and notice that the “Alerts” section now has a record that the measles vaccine should not be administered to this patient.


### Exercise 3
1. Observe the care-config.json file within `configurablecare/src/main/assets` directory.
2. Go to line 49 and change it to: `"condition": “automatic”`. The “condition” references the action to be taken for a MedicationRequest resource to transition from one state to another. In this case, the “condition” represents the transition from “proposal” to “plan” which was done in steps 8-10.
3. Launch the application again and follow steps 1-7 from Exercise 1.
4. You will notice that the Medication Request created is now a “plan” instead of a “proposal”. You have skipped step 9 in Exercise 1. This change is an example of a case where no intervention is needed to “accept” a proposal.
