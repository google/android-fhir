# FhirEngine Macrobenchmark

The _FHIR Engine Library_ macrobenchmark tests are located in the module `:engine:benchmark:macrobenchmark`

It uses the _FHIR Engine Benchmark app_ and as a prerequisite it requires that app be [configured with the relevant benchmark data](Benchmark-app.md)

Macrobenchmarks can be run using

```shell
./gradlew :engine:benchmark:macrobenchmark:connectedCheck
```
