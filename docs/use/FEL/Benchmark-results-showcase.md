# FhirEngine Benchmark Results

## Device Spec

**_Panther - Google Pixel 7_**

**RAM** - 8GB

**CPU** - Octa-core (2x2.85 GHz Cortex-X1 & 2x2.35 GHz Cortex-A78 & 4x1.80 GHz Cortex-A55)

API 33

### Data Access API

Results were generated from execution of the [FhirEngineCrudBenchmark](../../../engine/benchmarks/macrobenchmark/src/main/java/com/google/android/fhir/engine/macrobenchmark/FhirEngineCrudBenchmark.kt) test

| API    | Average duration (ms) | Notes                                 |
|:-------|----------------------:|---------------------------------------|
| create |                  ~4.7 | Takes ~47s for population size of 10k |
| update |                ~12.29 |                                       |
| get    |                 ~3.83 |                                       |
| delete |                 ~8.08 |                                       |


### Search DSL API

Results were generated from the execution of the [FhirEngineSearchApiBenchmark](../../../engine/benchmarks/macrobenchmark/src/main/java/com/google/android/fhir/engine/macrobenchmark/FhirEngineSearchApiBenchmark.kt) test

|                                             |  Population size | Average duration (ms) | Notes                                                                                                                 |
|---------------------------------------------|-----------------:|----------------------:|-----------------------------------------------------------------------------------------------------------------------|
| searchDisjunctPatientGivenName              |              10k |                ~34.47 | Preloaded patients of population size and their associated resources (Encounters/Practitioners/Organization/Location) |
| searchEncounterLocalLastUpdated             |              10k |               ~428.07 |                                                                                                                       |
| searchPatientHasEncounter                   |              10k |               ~104.66 |                                                                                                                       |
| searchPatientSortedByBirthDate              |              10k |               ~612.62 |                                                                                                                       |
| searchPatientSortedByName                   |              10k |               ~497.06 |                                                                                                                       |
| searchPatientWithIncludeGeneralPractitioner |              10k |                 ~9.53 |                                                                                                                       |
| searchPatientWithRevIncludeConditions       |              10k |                 ~8.32 |                                                                                                                       |
| searchPatientWithTokenIdentifier            |              10k |                ~11.11 |                                                                                                                       |
| searchWithPatientGivenNamesDisjunct         |              10k |                ~31.89 |                                                                                                                       |
| searchWithTypeDateSearchParameter           |              10k |                 ~5.65 |                                                                                                                       |
| searchWithTypeNumberSearchParameter         |              10k |                 ~1.68 |                                                                                                                       |
| searchWithTypeQuantitySearchParameter       |              10k |                 ~3.12 |                                                                                                                       |
| searchWithTypeStringSearchParameter         |              10k |                ~24.49 |                                                                                                                       |
