# Compile CQL

The `FhirOperator` allows the user to load, compile and run a CQL library completely off-line. This example uses CQL to evaluate if a given patient has completed an immunization schedule.

With a CQL file

```cql
val cqlLibStr =
 """
// Declare the name and version of the Library of functions
library DDCCPass version '1.0.0'

// The version of FHIR we are using
using FHIR version '4.0.1'

// Execute all business rules relative to a specific Patient content
context Patient

// Define boolean valued business rule to check if there is an immunization that was completed
define "Completed Immunization": exists ("Completed Immunizations")

// Define a list of completed immunizations for which the dose number is the same as the series dose
// Immunization resources are queried from those that reference the Patient we are executing against
define "Completed Immunizations":
 [Immunization] I
   where (I.protocolApplied.doseNumber.value = I.protocolApplied.seriesDoses.value)
""".trimIndent()
```

Assemble a FHIR Library and add the CQL as an Attachment:

```kotlin
val library = Library().apply {
 id = "DDCCPass-1.0.0"
 name = "DDCCPass"
 version = "1.0.0"
 status = Enumerations.PublicationStatus.ACTIVE
 experimental = true
 url = "http://localhost/Library/DDCCPass|1.0.0"
 addContent(
   Attachment().apply {
     contentType = "text/cql"
     data = cqlLibStr.toByteArray()
   }
 )
}
```

Keep in mind that Library `id`, `name` and `version` in the JSON must match the library `name` and `version` definitions in the CQL file.

By only adding the original CQL source code into the Library object, the FHIR Operator will compile the CQL before running the evaluate operation. The compiled version will remain in memory for subsequent calls.

Then with a Patient and Immunization records

```kotlin
val patientStr =
 """
 {
   "resourceType": "Patient",
   "id": "d4d35004-24f8-40e4-8084-1ad75924514f",
   "name": [
    {
      "family": "Fellhauer",
      "given": [ "Monika" ]
    }
   ],
   "birthDate": "1984-02-24"
 }
 """

val immunizationStr =
 """
   {
   "resourceType": "Immunization",
   "id": "8aa553e8-8847-482a-8bcf-2eca4e9598ef",
   "status": "completed",
   "patient": {
    "reference": "Patient/d4d35004-24f8-40e4-8084-1ad75924514f"
   },
   "occurrenceDateTime": "2021-05-27",
   "protocolApplied": [
    {
      "targetDisease": [
        {
          "coding": [
            {
              "system": "http://snomed.info/sct",
              "code": "840539006"
            }
          ]
        }
      ],
      "doseNumberPositiveInt": 2,
      "seriesDosesPositiveInt": 2
    }
   ]
 }
 """.trimIndent()
```

Add the data objects to the Engine:

```kotlin
val jsonParser = MyApplication.fhirContext(this).newJsonParser()

MyApplication.fhirEngine(this).apply {
 create(jsonParser.parseResource(patientStr) as Patient)
 create(jsonParser.parseResource(immunizationStr) as Immunization)
}
```

Add the library to the Fhir Operator

```kotlin
MyApplication.fhirOperator(this).loadLib(library)
```

And call `FhirOperator.evaluateLibrary` with the library URL, the Patient ID and a set of functions to evaluate.

```kotlin
val results = FhirApplication.fhirOperator(requireContext()).evaluateLibrary(
   "http://localhost/Library/DDCCPass|1.0.0",
   "d4d35004-24f8-40e4-8084-1ad75924514f",
   setOf("Completed Immunization")) as Parameters

val isImmunized = results.getParameterBool("Completed Immunization")
```
