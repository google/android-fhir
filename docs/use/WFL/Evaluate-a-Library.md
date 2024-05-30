# Evaluate a Library

Given a pre-compiled FHIR Library, the FhirOperator can evaluate functions defined within it by simply calling FhirOperator.evaluateLibrary.

In this example, the CQL library is a single function called `ActiveProcedureStatus` that always returns true:

```cql
library TestActivityDefinition version '1.0.0'

define "ActiveProcedureStatus": true

```

With a Library object that includes either a `text/CQL`, a `JSON`, or an `XML` formatted as a base64 attachment,

```kotlin
val libraryStr =
 """
{
  "resourceType": "Library",
  "id": "TestActivityDefinition",
  "url": "http://test/fhir/Library/TestActivityDefinition|1.0.0",
  "version": "1.0.0",
  "name": "TestActivityDefinition",
  "parameter": [
    {
      "name": "ActiveProcedureStatus",
      "use": "out",
      "type": "boolean"
    }
  ],
  "content": [
    {
      "contentType": "text/cql",
      "data": "bGlicmFyeSBUZXN0QWN0aXZpdHlEZWZpbml0aW9uIHZlcnNpb24gJzEuMC4wJw0KDQpkZWZpbmUgIkFjdGl2ZVByb2NlZHVyZVN0YXR1cyI6IHRydWUNCg=="
    }
  ]
}
 """.trimIndent()
```

Add a target Patient

```kotlin
val patientStr =
 """
 {
   "resourceType": "Patient",
   "id": "Test-Patient"
   ...
 }
 """.trimIndent()
```

Add the Patient to the Engine and the Library to the FhirOperator:

```kotlin
val jsonParser = MyApplication.fhirContext(this).newJsonParser()

MyApplication.fhirOperator(this).apply {
  loadLib(jsonParser.parseResource(libraryStr) as Library)
}

MyApplication.fhirEngine(this).apply {
  create(jsonParser.parseResource(patientStr) as Patient)
}
```

And call FhirOperator.evaluateLibrary with the library URL, the Patient ID and a set of functions to evaluate

```kotlin
val results = FhirApplication.fhirOperator(requireContext()).evaluateLibrary(
   "http://test/fhir/Library/TestActivityDefinition|1.0.0",
   "Test-Patient",
   setOf("ActiveProcedureStatus")) as Parameters

val isImmunized = results.getParameterBool("ActiveProcedureStatus")
```
