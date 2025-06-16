# FhirEngine Benchmark Results

## Device Spec

**_Panther - Google Pixel 7_**

**RAM** - 8GB

**CPU** - Octa-core (2x2.85 GHz Cortex-X1 & 2x2.35 GHz Cortex-A78 & 4x1.80 GHz Cortex-A55)

API 33

### Data Access API

Results were generated from execution of FhirEngineCrudBenchmark test in the `engine:benchmarks:macrobenchmark` module located at `engine/benchmarks/macrobenchmark/src/main/java/com/google/android/fhir/engine/macrobenchmark/FhirEngineCrudBenchmark.kt`

| API    | Average duration (ms) | Notes                                 |
|:-------|----------------------:|---------------------------------------|
| create |                  ~4.7 | Takes ~47s for population size of 10k |
| update |                ~12.29 |                                       |
| get    |                 ~3.83 |                                       |
| delete |                 ~8.08 |                                       |
