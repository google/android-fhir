# Manage FHIR Resources Locally

The FHIR Engine Library has a CRUD-like interface for managing local FHIR resources. The FHIR resources are represented using [HAPI FHIR Structures](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/index.html) objects and types.

**Note**: All FHIR Engine functions are `suspend` functions and must be called from a [coroutine](https://developer.android.com/kotlin/coroutines), which is not included in the examples below.

## Create a FHIR resource

A `FhirEngine` instance stores FHIR resources represented as [HAPI FHIR Structures](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/index.html) objects. To create a FHIR resource in a `FhirEngine` instance, first make the resource using the HAPI FHIR Structures API, then use `FhirEngine.create()`.

**NOTE:** The FHIR Engine requires all stored resources to have a logical [`Resource.id`](https://www.hl7.org/fhir/resource-definitions.html#Resource.id). If the `id` is specified in the resource passed to `create()`, the resource created in `FhirEngine` will have the same `id`. If no `id` is specified, `FhirEngine` will generate a UUID as that resource's `id` and include it in the returned list of IDs.

Example: Create a Patient resource

```kotlin
// Create a new Patient resource
val patient = Patient().apply{
  id = "19682646"
  gender = Enumerations.AdministrativeGender.MALE
  addName(
    HumanName().apply {
      addGiven("Lance")
      addGiven("Thomas")
      family = "East"
    }
  )
}

// Create the resource in an existing FhirEngine instance
fhirEngine.create(patient)
```

## Read a FHIR Resource

Read resources from FHIR Engine by providing the type and logical Resource ID.

Example: Read a Patient resource

```kotlin
// Read a Patient resource by ID
try {
 val patient = fhirEngine.get<Patient>("19682646")
} catch (e : ResourceNotFoundException) {
 e.printStackTrace()
}
```

You can also [search for FHIR resources](Search-FHIR-resources.md).

## Update a FHIR resource

Example: Update a Patient resource

```kotlin
val updatedPatient = patient.copy().setActive(false)

fhirEngine.update(updatedPatient)

```

## Delete a FHIR resource

Example: Delete a Patient resource by ID.

```kotlin
fhirEngine.delete<Patient>("19682646")
```

If you already have a copy of the resource you want to delete, you can get the logical ID from it instead.

```kotlin
fhirEngine.delete<Patient>(patient.logicalId)

```
