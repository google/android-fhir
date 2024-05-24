# Generate a Care Plan

The $Apply operator applies a given PlanDefinition to a patient. The operator will run through the actions defined in the plan, determine applicability by evaluating the applicability conditions defined for the action and convert the applicable actions into the resulting CarePlan activities.

With a Plan Definition and a Patient

```kotlin
val planDefinitionStr =
 """
   {
     "resourceType": "PlanDefinition",
     "id": "Test-PlanDefinition"
     ...
   }
 """.trimIndent()

val patientStr =
 """
   {
     "resourceType": "Patient",
     "id": "Test-Patient"
     ...
   }
 """
```

Add the objects to the Engine:

```kotlin
val jsonParser = MyApplication.fhirContext(this).newJsonParser()

MyApplication.fhirEngine(this).apply {
 create(jsonParser.parseResource(planDefinitionStr) as PlanDefinition)
 create(jsonParser.parseResource(patientStr) as Patient)
}
```

And call FhirOperator.generateCarePlan with the ID of the PlanDefinition and the patient ID desired. An optional Encounter ID can also be provided.

```kotlin
val carePlan = MyApplication.fhirOperator(this).generateCarePlan(
 planDefinitionId = "Test-PlanDefinition",
 patientId = "Test-Patient"
)
```

## Example: Generate a CarePlan with a Medication Request

PlanDefinitions include a set of actions. Each action is linked to an `ActivityDefinition` and the `ActivityDefinition` can be specified as a `MedicationRequest` kind. Here's a simple example.

With a Plan Definition, an Activity Definition, and a Patient

```kotlin
val planDefinitionStr =
 """
   {
        "resourceType": "PlanDefinition",
        "id": "MedRequest-Example",
        "title": "This example illustrates a medication request",
        "status": "active",
        "action": [{
          "id": "medication-action-1",
          "title": "Administer Medication 1",
          "definitionCanonical": "http://localhost/ActivityDefinition/MedicationRequest-1"
        }]
   }
 """.trimIndent()

val activityDefinitionStr =
 """
   {
        "resourceType": "ActivityDefinition",
        "id": "MedicationRequest-1",
        "url": "http://localhost/ActivityDefinition/MedicationRequest-1",
        "status": "active",
        "kind": "MedicationRequest",
        "productCodeableConcept": {
          "text": "Medication 1"
        }
   }
 """.trimIndent()

val patientStr =
 """
   {
        "resourceType": "Patient",
        "id": "Patient-Example",
        "active": true,
        "name": [
          {
            "family": "Hadi",
            "given": [
              "Bareera"
            ]
          }
        ],
        "gender": "female",
        "birthDate": "1999-01-14"
        ...
   }
 """
```

Add the objects to the Engine:

```kotlin
val jsonParser = MyApplication.fhirContext(this).newJsonParser()

MyApplication.fhirEngine(this).apply {
 create(jsonParser.parseResource(planDefinitionStr) as PlanDefinition)
 create(jsonParser.parseResource(activityDefinitionStr) as ActivityDefinition)
 create(jsonParser.parseResource(patientStr) as Patient)
}
```

And call FhirOperator.generateCarePlan with the ID of the PlanDefinition and the patient ID desired.

```kotlin
val carePlan = MyApplication.fhirOperator(this).generateCarePlan(
 planDefinitionId = "MedRequest-Example",
 patientId = "Patient-Example"
)
```

The generated CarePlan is:

```json
{
  "resourceType": "CarePlan",
  "contained": [
    {
      "resourceType": "RequestGroup",
      "id": "1",
      "status": "draft",
      "intent": "proposal",
      "action": [
        {
          "resource": {
            "reference": "#2"
          }
        }
      ]
    },
    {
      "resourceType": "MedicationRequest",
      "id": "2",
      "intent": "order",
      "medicationCodeableConcept": {
        "text": "Medication 1"
      },
      "subject": {
        "reference": "Patient/Patient-Example"
      }
    }
  ],
  "instantiatesCanonical": [
    "MedRequest-Example"
  ],
  "status": "draft",
  "subject": {
    "reference": "Patient/Patient-Example"
  },
  "activity": [
    {
      "reference": {
        "reference": "#1"
      }
    }
  ]
}
```
