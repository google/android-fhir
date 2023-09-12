# Android FHIR SDK Workflow Benchmark module

Contains test cases that evaluate the performance of individual tasks executed for the first time directly on hardware. 

The test cases are designed to run in sequence of their alphabetic order to make sure larger tasks do not build cache for smaller ones. Their class names are prefixed by an extra letter to inform their position relative to others in the list.

# How to run the benchmark

In Android Studio, set your build variants to `release` and run your benchmark as you would any `@Test` using the gutter action next to your test class or method.

![gutter test action](https://developer.android.com/static/topic/performance/images/benchmark_images/microbenchmark_run.png)

The results will be similar to this:
```
   48,093,078   ns    trace    A_JacksonMapperBenchmark.loadJsonMapper
2,261,548,715   ns    trace    B_FhirContextLoaderBenchmark.loadR4
2,928,293,365   ns    trace    B_FhirContextLoaderBenchmark.loadR5
  337,669,721   ns    trace    B_FhirContextLoaderBenchmark.loadDstu2
1,744,938,507   ns    trace    B_FhirContextLoaderBenchmark.loadDstu3
6,817,953,752   ns    trace    C_CqlEngineFhirContextLoaderBenchmark.loadDstu2FhirModelResolver
3,704,489,380   ns    trace    C_CqlEngineFhirContextLoaderBenchmark.loadR4FhirModelResolver
2,814,451,999   ns    trace    C_CqlEngineFhirContextLoaderBenchmark.loadDstu3FhirModelResolver
  675,991,839   ns    trace    D_FhirJsonParserBenchmark.parseLightFhirLibrary
1,238,212,883   ns    trace    D_FhirJsonParserBenchmark.parseLightFhirBundle
2,785,964,288   ns    trace    E_ElmJsonLibraryLoaderBenchmark.parseImmunityCheckCqlFromFhirLibrary
  713,779,915   ns    trace    E_ElmJsonLibraryLoaderBenchmark.parseFhirHelpersCqlFromFhirLibrary
9,833,892,387   ns    trace    F_CqlEvaluatorBenchmark.evaluatesLibrary
```

Alternatively, from the command line, run the connectedCheck to run all of the tests from specified Gradle module:

```bash
./gradlew workflow:benchmark:connectedReleaseAndroidTest
```

In this case, results will be saved to the `outputs/androidTest-results/connected/<device>/test-result.pb`. To visualize on Android Studio, click Run / Import Tests From File and find the `.pb` file