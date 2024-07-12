# Evaluate a Measure

Clinical reports in FHIR are represented as a FHIR Measure resource containing terminology, a population criteria section, and at least one FHIR Library resource containing a data criteria section as well as the logic used to define the population criteria. The population criteria section typically contains initial population criteria, denominator criteria, and numerator criteria sub-components, among others. Measures are then scored according to whether a subject (or subjects) are members of the various populations. For example, a Measure for Breast Cancer screening might define an Initial Population (via CQL expressions) of "all women", a Denominator of "women over 35", and a Numerator of "women over 35 who have had breast cancer screenings in the past year". If the Measure is evaluated against a population of 100 women, 50 are over 35, and of those 25 have had breast cancer screenings in the past year, the final score would be 50%1 (total number in numerator / total number in the denominator).

## Measure Evaluate with Population Reporting

This Measure example calculates the proportion of the numerator over a denominator for a given patient population. In this example, we will be measuring the number of patients under 18 using a Measure object and a CQL Library to filter each group.

With a measure:

```kotlin
val measureStr =
 """
{
  "resourceType": "Measure",
  "id": "Under18Measure",
  "url": "http://localhost/Measure/Under18Measure",
  "title": "Measure for Patients Under18",
  "status": "draft",
  "library": [
    "http://localhost/Library/PatientsUnder18|1.0.0"
  ],
  "scoring": {
    "coding": [
      {
        "code": "proportion"
      }
    ]
  },
  "group": [
    {
      "id": "Main",
      "population": [
        {
          "code": {
            "coding": [
              {
                "code": "initial-population"
              }
            ]
          },
          "criteria": {
            "language": "text/cql",
            "expression": "Initial Population"
          }
        },
        {
          "code": {
            "coding": [
              {
                "code": "denominator"
              }
            ]
          },
          "criteria": {
            "language": "text/cql",
            "expression": "Denominator"
          }
        },
        {
          "code": {
            "coding": [
              {
                "code": "numerator"
              }
            ]
          },
          "criteria": {
            "language": "text/cql",
            "expression": "Numerator"
          }
        }
      ]
    }
  ]
}
""".trimIndent()
```

And the respective Fhir Library with a base-64 encoded CQL code:

```kotlin
val libraryStr =
 """
{
  "resourceType": "Library",
  "id": "PatientsUnder18-1.0.0",
  "url": "http://localhost/Library/PatientsUnder18|1.0.0",
  "version": "1.0.0",
  "name": "PatientsUnder18",
  "status": "active",
  "experimental": true,
  "content": [
    {
      "contentType": "text/cql",
      "data": "bGlicmFyeSBQYXRpZW50c1VuZGVyMTggdmVyc2lvbiAnMS4wLjAnCgp1c2luZyBGSElSIHZlcnNpb24gJzQuMC4xJwoKaW5jbHVkZSAiRkhJUkhlbHBlcnMiIHZlcnNpb24gJzQuMC4xJyBjYWxsZWQgRkhJUkhlbHBlcnMKCmNvbnRleHQgUGF0aWVudAoKZGVmaW5lICJJbml0aWFsIFBvcHVsYXRpb24iOiBbUGF0aWVudF0gUApkZWZpbmUgIk51bWVyYXRvciI6ICJJbml0aWFsIFBvcHVsYXRpb24iIFAgd2hlcmUgQ2FsY3VsYXRlQWdlSW5ZZWFycyhQLmJpcnRoRGF0ZSkgPCAxOApkZWZpbmUgIkRlbm9taW5hdG9yIjogIkluaXRpYWwgUG9wdWxhdGlvbiIK"
    }
  ]
}
 """.trimIndent()
```

Add the Measure to the Engine:

```kotlin
val jsonParser = MyApplication.fhirContext(this).newJsonParser()

MyApplication.fhirOperator(this).apply {
  loadLib(jsonParser.parseResource(libraryStr) as Library)
}

MyApplication.fhirEngine(this).apply {
  create(jsonParser.parseResource(measureStr) as Measure)
}
```

For a given number of Patients:

```kotlin
val patientBundleStr =
 """
{
 "resourceType": "Bundle",
 "entry": [
   {
     "resource": {
       "resourceType": "Patient",
       "id": "1",
       "active": true,
       "birthDate": "2015-04-16"
     }
   }, {
     "resource": {
       "resourceType": "Patient",
       "id": "2",
       "active": true,
       "birthDate": "2005-04-16"
     }
   }, {
     "resource": {
       "resourceType": "Patient",
       "id": "3",
       "active": true,
       "birthDate": "1995-04-16"
     }
   }
 ]
}
""".trimIndent()
```

Add Patients to the Engine:

```kotlin
MyApplication.fhirEngine(this).apply {
  val bundle = jsonParser.parseResource(patientBundleStr) as Bundle
  for (entry in bundle.entry) {
    create(entry.resource)
  }
}
```

And call FhirOperator.evaluateMeasure with the measure URL, the dates of the start and end of the report.

```kotlin
val measureReport =
 fhirOperator.evaluateMeasure(
   measureUrl = "http://localhost/Measure/Under18Measure",
   start = "1990-01-01",
   end = "2022-12-31",
   reportType = MeasureEvalType.POPULATION.toCode(),
   subject = null,
   practitioner = null,
   lastReceivedOn = null
 )
```

ReportType accepts multiple options:

* `MeasureEvalType.SUBJECT`: An evaluation generating an individual report that provides information on the performance for a given measure with respect to a single subject
* `MeasureEvalType.SUBJECTLIST`: An evaluation generating a subject list report that includes a listing of subjects that satisfied each population criteria in the measure.
* `MeasureEvalType.PATIENT`: An evaluation generating an individual report that provides information on the performance for a given measure with respect to a single patient
* `MeasureEvalType.PATIENTLIST`:  An evaluation generating a patient list report that includes a listing of patients that satisfied each population criteria in the measure
* `MeasureEvalType.POPULATION`:  An evaluation generating a summary report that returns the number of subjects in each population criteria for the measure

Subject and Practitioner filter the initial population to the patient or practitioner desired. The LastReceivedOn is not yet implemented.

The returning Measure Report should be:

```json
{
 "resourceType": "MeasureReport",
 "status": "complete",
 "type": "summary",
 "measure": "http://localhost/Measure/Under18Measure",
 "period": {
   "start": "1990-01-01T00:00:00+00:00",
   "end": "2022-12-31T23:59:59+00:00"
 },
 "group": [ {
   "id": "Main",
   "population": [ {
     "code": {
       "coding": [ {
         "code": "initial-population"
       } ]
     },
     "count": 3
   }, {
     "code": {
       "coding": [ {
         "code": "denominator"
       } ]
     },
     "count": 3
   }, {
     "code": {
       "coding": [ {
         "code": "numerator"
       } ]
     },
     "count": 2
   } ],
   "measureScore": {
     "value": 0.6666666666666666
   }
 } ]
}
```
