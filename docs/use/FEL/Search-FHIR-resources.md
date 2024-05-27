# Search FHIR Resources

You can search for resources in a FHIR Engine instance using the Android FHIR SDK's Search DSL or [FHIR search strings](https://www.hl7.org/fhir/search.html). The SDK supports [all defined search parameters](https://www.hl7.org/fhir/searchparameter-registry.html).

See the full list of [FHIR search specification support](FHIR-search-specification-support.md).

## Search using Search DSL

The Search DSL is based on [FHIR search](https://www.hl7.org/fhir/search.html), and most of the capabilities of FHIR search have an equivalent in Search DSL.

### General search syntax

This is the simplest search syntax:

```kotlin
val patients = fhirEngine.search<Patient> {}
```

This search returns a `List` containing all `Patient` resources in the `FhirEngine` instance and is equivalent to the FHIR search:

```
GET [base]/Patient
```

If you're having trouble getting search to work, make sure Android Studio is using the correct version of `search`.

![Search code completion](https://user-images.githubusercontent.com/7772901/197892131-38247b9a-9f3c-4dff-a2aa-f1019d8476d2.png)

In most cases, use a `filter` to search for particular Patient resources.

```kotlin
fhirEngine.search<Patient> {
 filter(Patient.GIVEN, {
   value = "Kiran"
   modifier = StringFilterModifier.MATCHES_EXACTLY
 })
}
```

To do so, first specify a search parameter from the HAPI FHIR resource being searched. For the example above, we recommend you use Android Studio's code completion by first entering the resource followed by a dot, such as `Patient.`, then pressing **ctrl+space** to see valid options. They will be of the type `XClientParam!`.

<img width="426" alt="Search parameter code completion" src="https://user-images.githubusercontent.com/7772901/197892156-e0818dc6-c259-45cf-b8e0-eb11b326a20a.png">

Alternatively, look at the Field Summary for the [HAPI FHIR Patient resource](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Patient.html) and find fields with the description "**Fluent Client** search parameter constant for **field**". These are the Patient resource fields we can search by.

![HAPI FHIR Patient documentation](https://user-images.githubusercontent.com/7772901/197891914-4a5a0e23-ae28-408c-9602-8dcef5f28277.png)

Not every field in a resource has search implemented, for example `Patient.maritalStatus` does not have a search parameter implemented in HAPI FHIR. On the other hand, HAPI FHIR provides some convenience search parameters like [`Patient.NAME`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/org/hl7/fhir/r4/model/Patient.html#NAME) which searches across all name-related fields but is not part of the base FHIR specification.

Next, provide a `value` to search for. The expected type for the value for `value` depends on the type of the search parameter from the HAPI FHIR resource field, or seen in the code completion pop-up. In the example above, `Patient.GIVEN` is a `StringClientParam` so `value` should be a `String`.

In addition, most parameter types have ways to control how the search is performed. In the case of `String` there is a `modifier` field. The `Number`, `Date`, and `Quantity` parameters have a `prefix` field instead, corresponding to the [FHIR Search prefix](https://www.hl7.org/fhir/search.html#prefix).

For more details, see the examples below or [look at the implementation of the specific filter type](https://github.com/google/android-fhir/tree/master/engine/src/main/java/com/google/android/fhir/search/filter).

### Using the OR operator

By default, multiple filters are considered using logical `AND`. You can treat them as an `OR` by setting the `operation` field. The following example returns patients with a given name or family name of exactly "Jay".

```kotlin
fhirEngine.search<Patient> {
 filter(Patient.GIVEN, {
   value = "Jay"
   modifier = StringFilterModifier.MATCHES_EXACTLY
 })

 filter(Patient.FAMILY, {
   value = "Jay"
   modifier = StringFilterModifier.MATCHES_EXACTLY
 })

 operation = Operation.OR
}
```

You can also pass multiple search criteria to the same filter. In this case, they are considered using `OR` by default. (Using `AND` on multiple search criteria is usually not useful.) The following example returns patients with a given name of exactly "Jay" or "Kat".

```kotlin
fhirEngine.search<Patient> {
 filter(Patient.GIVEN, {
   value = "Jay"
   modifier = StringFilterModifier.MATCHES_EXACTLY
 } , {
   value = "Kat"
   modifier = StringFilterModifier.MATCHES_EXACTLY
 })
}
```

### Reverse chaining

The `has` function allows you to search for a resource based on other resources that reference it and functions similarly to [FHIR Search reverse chaining](https://build.fhir.org/search.html#has). In the following example, the search will return a list of Patients that:

1. have a RelatedPerson that references that Patient in the `RelatedPerson.PATIENT` field
2. the RelatedPerson has a name matching "Ariel"

In this case, the results will include a patient resource matching `patient1`, plus any others that were already created in the FhirEngine.

```kotlin
val patient1 = Patient().apply{
  id = "1234567"
  addName(
    HumanName().apply {
      addGiven("Alex")
      family = "Lee"
    }
  )
}

val related1 = RelatedPerson().apply {
  patient.reference = "${patient1.fhirType()}/${patient1.logicalId}"
  addName(
    HumanName().apply {
      addGiven("Ariel")
      family = "Lee"
    }
  )
}

fhirEngine.create(patient1)
fhirEngine.create(related1)

val revinclude = fhirEngine.search<Patient> {
  has<RelatedPerson>(RelatedPerson.PATIENT) {
    filter (RelatedPerson.NAME, {
      value = "Ariel"
    })
  }
}
```

You can use `has` in combination with the other Search DSL features to create [complex searches](#complex-search-queries).

### Search result modification

You can specify several fields to control search results, similar to [FHIR Search results](https://build.fhir.org/search.html#modifyingresults).

* `sort` - specify a Date, Number, or String field to sort the results by
* `count` - limit the results to a certain size
* `from` - start the results from a specified index

```kotlin
val patients = fhirEngine.search<Patient> {
  filter (Patient.NAME, {
    modifier = StringFilterModifier.CONTAINS
    value = "Quinn"
  })
  sort(Patient.FAMILY, Order.ASCENDING)
  count = 100
  from = 0
}
```

### Search filter parameters by type

#### Number

https://www.hl7.org/fhir/search.html#number

Fields

* `prefix` - A [FHIR search prefix](https://build.fhir.org/search.html#prefix), represented by [`ParamPrefixEnum`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/ParamPrefixEnum.html).
* `value` - A `BigDecimal` of the value to search for.

Search for all the Risk Assessments with probability greater than 0.8.

```kotlin
fhirEngine.search<RiskAssessment> {
  filter(RiskAssessment.PROBABILITY, {
    value = BigDecimal(0.8)
    prefix = ParamPrefixEnum.GREATERTHAN
  })
}
```

#### Date

https://www.hl7.org/fhir/search.html#date

* `prefix` - A [FHIR search prefix](https://build.fhir.org/search.html#prefix), represented by [`ParamPrefixEnum`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/ParamPrefixEnum.html).
* `value` - A `DateFilterValues` of the value to search for. Pass a [`DateType`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r5/org/hl7/fhir/r5/model/DateType.html) or [`DateTimeType`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r5/org/hl7/fhir/r5/model/DateTimeType.html) to the `of` function to create.

Search for any patients born after 2013-03-14.

```kotlin
fhirEngine.search<Patient> {
  filter(
    Patient.BIRTHDATE,
    {
      prefix = ParamPrefixEnum.STARTS_AFTER
      value = of(DateType("2013-03-14"))
    }
  )
}
```

#### String

https://www.hl7.org/fhir/search.html#string

* `value` - The `String` to search for.
* `modifier` - An option from [`StringFilterModifier`](https://google.github.io/android-fhir/engine/engine/com.google.android.fhir.search/-string-filter-modifier/index.html), corresponding to a [FHIR string search](https://www.hl7.org/fhir/search.html#string) modifier. Default: `StringFilterModifier.STARTS_WITH`

Search for any patients with a name with a given part containing "eve" at any position. This would include patients with the given name "Eve", "Evelyn", and also "Severine".

```kotlin
val patients = fhirEngine.search<Patient> {
  filter (Patient.NAME, {
    value = "eve"
    modifier = StringFilterModifier.CONTAINS
  })
}
```

#### Token

https://www.hl7.org/fhir/search.html#token

* `value` - A `TokenFilterValue` of the value to search for. Pass the appropriate object type to the `of` function to create: `Boolean`, `String`, `UriType`, `CodeType`, `Coding`, `CodeableConcept`, `Identifier`

Search for any patient with a gender that has the code "male".

```kotlin
fhirEngine.search<Patient> {
  filter(Patient.GENDER, {
    value = of(CodeType("male"))
  })
}
```

Search for any patients that are active.

```kotlin
fhirEngine.search<Patient> {
  filter(Patient.ACTIVE, {
    value = of(true)
  })
}
```

Search for any condition with a code "123" in the code system "http://example.org/codes".

```kotlin
fhirEngine.search<Condition> {
  filter(Condition.CODE, {
    value = of(CodeableConcept(Coding("http://example.org/codes", "123", "")))
  })
}
```

#### Reference

https://www.hl7.org/fhir/search.html#reference

* `value` - a `String` of a resource reference.

Search for all the observations where the subject references "Patient/123".

```kotlin
fhirEngine.search<Observation> {
  filter(Observation.SUBJECT, {
    value = "Patient/123"
  })
}
```

#### Quantity

https://www.hl7.org/fhir/search.html#quantity

* `prefix` - A [FHIR search prefix](https://build.fhir.org/search.html#prefix), represented by [`ParamPrefixEnum`](https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/ca/uhn/fhir/rest/param/ParamPrefixEnum.html).
* `value` - A `BigDecimal` of the value to search for.
* `system` - A `String` of the URI identifying the coding system.
* `unit` - A `String` of the coded form of the unit.

Search for all the observations where the value of is about 5.4 mg, where mg is understood as a UCUM unit.

```kotlin
fhirEngine.search<Observation> {
  filter(Observation.VALUE_QUANTITY, {
    prefix = ParamPrefixEnum.APPROXIMATE
    value = BigDecimal(5.4)
    system = "http://unitsofmeasure.org"
    unit = "mg"
  })
}
```

#### URI

https://www.hl7.org/fhir/search.html#uri

* `value` - a `String` of the URI to search for.

Search for all the value sets with the exact URL "http://example.org/fhir/ValueSet/123".

```kotlin
fhirEngine.search<ValueSet> {
  filter(ValueSet.URL, {
    value = "http://example.org/fhir/ValueSet/123"
  })
}
```

### Search query examples

#### Complex search queries

Search for Patients in India who have completed their Influenza vaccination.

```kotlin
fhirEngine.search<Patient> {
   has<Immunization>(Immunization.PATIENT) {
     filter(
       Immunization.VACCINE_CODE, {
         value = of(Coding("http://hl7.org/fhir/sid/cvx", "140", "Influenza, seasonal, injectable, preservative free"))
       }
     )

     // Follow Immunization.ImmunizationStatus
     filter(Immunization.STATUS, {
         value = of(Coding("http://hl7.org/fhir/event-status", "completed", ""))
       }
     )
   }

   filter(
     Patient.ADDRESS_COUNTRY, {
       modifier = StringFilterModifier.MATCHES_EXACTLY
       value = "IN"
     }
   )
 }
```

#### Search Patient compartment for Encounters and Observations

Search for all Encounters and Observations where the subject is patient ID "123".

```kotlin
val observations =
 fhirEngine.search<Observation> {
   filter(Observation.SUBJECT, { value = "Patient/123" })
 }

val conditions =
 fhirEngine.search<Condition> {
   filter(Condition.SUBJECT, { value = "Patient/123" })
}
```

#### Search Patients with Observation by Code

Search for all Patients where they are the subject of an Observation with the code "http://snomed.info/sct|386661006" or "http://loinc.org|45701-0" (different codes for fever).

```kotlin
val patients = fhirEngine.search<Patient> {
 has<Observation>(Observation.SUBJECT) {
   filter(
     Observation.CODE,
     {
       of(Coding("http://snomed.info/sct", "386661006", "Fever" ))
     }, {
       of(Coding("http://loinc.org", "45701-0", "Fever"))
     }
   )
 }
}
```

## Search using FHIR search queries

You can also search resources in a FHIR Engine instance using [FHIR search queries](https://www.hl7.org/fhir/search.html).

Complex queries including FHIRPath expressions, global common search parameters, modifiers, prefixes, chained parameters are not supported.

```kotlin
val result = fhirEngine.search("Patient?active=true&gender=male&_sort=-name,gender&_count=11")
```

## Adding custom search parameters

You can define custom search parameters, in addition to the ones defined in the [FHIR spec](https://www.hl7.org/fhir/searchparameter-registry.html), add them to FHIR Engine and use them to search for resources. Adding custom search parameters is particularly useful when you want to search for extensions defined in custom profiles.

### Setting up custom search parameters

```kotlin
class FhirApplicationTest : Application(){

  override fun onCreate() {
    super.onCreate()

    FhirEngineProvider.init(
      FhirEngineConfiguration(
        enableEncryptionIfSupported = true,
        RECREATE_AT_OPEN,
        ServerConfiguration("https://hapi.fhir.org/baseR4/"),
        customSearchParameters = listOf(
          SearchParameter().apply {
            url = "http://example.com/SearchParameter/patient-mothersMaidenName"
            addBase("Patient")
            name = "mothers-maiden-name"
            code = "mothers-maiden-name"
            type = Enumerations.SearchParamType.STRING
            expression =
              "Patient.extension('http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName').value.as(String)"
            description = "search on mother's maiden name"
          },
// Additional String Type Search param for Patient.identifier for value matching along with the standard Token Type provided by FHIR.
          SearchParameter().apply {
            url = "http://example.com/SearchParameter/patient-identifierPartial"
            addBase("Patient")
            name = "identifierPartial"
            code = "identifierPartial"
            type = Enumerations.SearchParamType.STRING
            expression = "Patient.identifier.value"
            description = "Search the identifier"
          }
        )
      )
    )
  }
}
```

### Searching with the added custom search parameters

```kotlin
// Identifier partial search returns all Patients whose Patient.identifier.value starts with 5005- .
fhirEngine.search<Patient> {
  filter(
    StringClientParam("identifierPartial"),
    {
      value = "5005-"
      modifier = StringFilterModifier.STARTS_WITH
    }
  )
}
```

```kotlin
// searching with the extension returns all Patients who have Marca as their Maiden name.
// (custom extension http://example.com/SearchParameter/patient-mothersMaidenName).
fhirEngine.search<Patient> {
  filter(
    StringClientParam("mothers-maiden-name"),
    {
      value = "Marca"
      modifier = StringFilterModifier.MATCHES_EXACTLY
    }
  )
}
```

**NOTE:**
The engine doesn't automatically reindex existing resources after a new SearchParameter is added. Resources created after the new SearchParameter is created will be indexed accordingly. Updates to the existing resources will result in the reindexing of the resource.
